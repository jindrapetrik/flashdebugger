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
import com.jpexs.debugger.flash.messages.in.InFrame;
import com.jpexs.debugger.flash.messages.in.InNumScript;
import com.jpexs.debugger.flash.messages.in.InScript;
import com.jpexs.debugger.flash.messages.in.InSwfInfo.SwfInfo;
import java.io.IOException;
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
public class Debugger extends Thread {

    private final List<DebuggerConnection> connections;
    public static final int DEBUG_PORT = 7935;
    private final ServerSocket ssock;

    private List<DebugConnectionListener> listeners = new ArrayList<>();

    public void addConnectionListener(DebugConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionListener(DebugConnectionListener listener) {
        listeners.remove(listener);
    }

    public Debugger() throws IOException {
        ssock = new ServerSocket(DEBUG_PORT);
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "Starting server");
        while (!this.isInterrupted()) {
            try {
                Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "Waiting for a client...");
                Socket s = ssock.accept();
                Logger.getLogger(Debugger.class.getName()).log(Level.FINE, "New client connected");
                s.setTcpNoDelay(true);
                DebuggerConnection dc = new DebuggerConnection(s);
                connections.add(dc);
                dc.start();
                for (DebugConnectionListener listener : listeners) {
                    listener.connected(dc);
                }
            } catch (IOException ex) {
                Logger.getLogger(Debugger.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {

        Level level = Level.INFO;

        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(level);
        rootLog.getHandlers()[0].setLevel(level);

        Debugger d = new Debugger();
        d.addConnectionListener(new DebugConnectionListener() {

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
                    int numScript = con.getMessage(InNumScript.class).num;
                    Map<Integer, String> moduleNames = new HashMap<>();
                    for (int i = 0; i < numScript; i++) {
                        InScript sc = con.getMessage(InScript.class);
                        moduleNames.put(sc.module, sc.name);
                    }
                    con.getMessage(InAskBreakpoints.class);
                    /*dc.addBreakPoint(13, 15);

                     con.addMessageListener(new DebugMessageListener<InBreakAt>() {

                     @Override
                     public void message(InBreakAt message) {
                     Logger.getLogger(Debugger.class.getName()).log(Level.INFO, "break " + message.file + ":" + message.line);
                     dc.sendContinue();
                     }
                     });*/
                    InFrame f = dc.getFrame(0);
                    System.out.println("" + f);
                    //dc.sendContinue();
                } catch (IOException ex) {
                    //error:-(
                    System.err.println("FAILED to communicate");
                }

            }
        });
        d.start();
    }
}
