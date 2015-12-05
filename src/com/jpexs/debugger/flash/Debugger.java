/*
 *  Copyright (C) 2015 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.debugger.flash;

import com.jpexs.debugger.flash.messages.in.InAskBreakpoints;
import com.jpexs.debugger.flash.messages.in.InBreakAt;
import com.jpexs.debugger.flash.messages.in.InBreakReason;
import com.jpexs.debugger.flash.messages.in.InNumScript;
import com.jpexs.debugger.flash.messages.in.InScript;
import com.jpexs.debugger.flash.messages.in.InSwfInfo.SwfInfo;
import com.jpexs.debugger.flash.messages.out.OutGetBreakReason;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JPEXS
 */
public class Debugger {

    private final List<DebuggerConnection> connections;
    public static final int DEBUG_PORT = 7935;
    private ServerSocket ssock;
    private boolean stopped = true;
    private Thread thread;

    private List<DebugConnectionListener> listeners = new ArrayList<>();

    public void addConnectionListener(DebugConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionListener(DebugConnectionListener listener) {
        listeners.remove(listener);
    }

    private void initThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (Debugger.this) {
                        ssock = new ServerSocket(DEBUG_PORT, 0, InetAddress.getByName("localhost"));
                    }
                    while (!isStopped() && !this.isInterrupted()) {
                        DebuggerConnection dc;

                        Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "Waiting for a client...");
                        Socket s = ssock.accept();
                        Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "New client connected");
                        s.setTcpNoDelay(true);
                        dc = new DebuggerConnection(s);
                        connections.add(dc);
                        dc.start();
                        for (DebugConnectionListener listener : listeners) {
                            listener.connected(dc);
                        }
                    }
                } catch (IOException ex) {
                    if (!isStopped()) {
                        Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "Cannot listen: " + ex.getMessage());

                        stopDebugger();
                        for (DebugConnectionListener listener : listeners) {
                            listener.failedListen(ex);
                        }

                    }
                    return;
                }
            }
        };
    }

    public Debugger() throws IOException {
        connections = new ArrayList<>();
    }

    public void startDebugger() {
        if (!isStopped()) {
            stopDebugger();

        }
        Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "Starting server");
        initThread();

        thread.start();

        synchronized (this) {
            stopped = false;
        }
    }

    public synchronized boolean isStopped() {
        return stopped;
    }

    public void stopDebugger() {

        boolean stopNow = false;
        synchronized (this) {
            if (!stopped) {
                stopNow = true;
            }
            stopped = true;
        }
        if (stopNow && ssock != null) {
            try {
                ssock.close();
            } catch (IOException ex) {
                //ignore
            }
            ssock = null;
        }
        thread = null;
    }

    public static void main(String[] args) throws IOException {

        Level level = Level.FINEST;

        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(level);
        rootLog.getHandlers()[0].setLevel(level);

        Debugger d = new Debugger();
        d.addConnectionListener(new DebugConnectionListener() {

            @Override
            public void failedListen(IOException ex) {
            }

            @Override
            public void connected(DebuggerConnection con) {

                DebuggerCommands dc = new DebuggerCommands(con);
                try {
                    dc.stopWarning();
                    dc.setStopOnFault();
                    dc.setEnumerateOverride();
                    dc.setNotifyFailure();
                    dc.setInvokeSetters();
                    dc.setSwfLoadNotify();
                    dc.setGetterTimeout(1500);
                    dc.setSetterTimeout(5000);
                    dc.squelch(true);
                    List<SwfInfo> swfs = dc.getSwfInfo(1);
                    /*InGetSwf isf = con.sendMessage(new OutGetSwf(con, 0), InGetSwf.class);
                     InGetSwd isd = con.sendMessage(new OutGetSwd(con, 0), InGetSwd.class);
                     if (isd.swdData.length > 0) {
                     SWD swd = new SWD(isd.data);
                     for (SWD.DebugItem it : swd.objects) {
                     System.out.println(it);
                     if (it instanceof SWD.DebugScript) {
                     SWD.DebugScript s = (SWD.DebugScript) it;
                     }
                     }
                     }*/

                    final Map<Integer, String> moduleNames = new HashMap<>();
                    con.addMessageListener(new DebugMessageListener<InNumScript>() {

                        @Override
                        public void message(InNumScript message) {

                        }
                    });
                    con.addMessageListener(new DebugMessageListener<InScript>() {

                        @Override
                        public void message(InScript sc) {
                            moduleNames.put(sc.module, sc.name);
                        }
                    });

                    con.addMessageListener(new DebugMessageListener<InAskBreakpoints>() {

                        @Override
                        public void message(InAskBreakpoints message) {
                            con.dropMessage(message);
                        }

                    });

                    con.addMessageListener(new DebugMessageListener<InBreakAt>() {

                        @Override
                        public
                                void message(InBreakAt bra) {
                            try {

                                InBreakReason ibr = con.sendMessage(new OutGetBreakReason(con), InBreakReason.class
                                );
                                if (ibr.reason != InBreakReason.REASON_SCRIPT_LOADED) {
                                    if (moduleNames.containsKey(bra.file)) {
                                        System.out.println(moduleNames.get(bra.file) + ":" + bra.line);
                                    } else {
                                        System.out.println("unknown:" + bra.line);
                                    }
                                }

                                dc.stepInto();
                            } catch (IOException ex) {
                                //ignore
                            }
                        }
                    });
                } catch (IOException ex) {
                    //error:-(
                    System.err.println("FAILED to communicate");
                }

            }
        }
        );
        d.startDebugger();
    }
}
