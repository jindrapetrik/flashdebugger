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

import com.jpexs.debugger.flash.messages.in.InProcessTag;
import com.jpexs.debugger.flash.messages.out.OutProcessedTag;
import com.jpexs.debugger.flash.messages.out.OutSetActiveIsolate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
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
public class DebuggerConnection extends Thread {

    private final InputStream is;
    private final OutputStream os;
    private final Socket s;

    private static final String DEBUG_MESSAGES = "$debug_messages";
    private static final String DEBUG_MESSAGE_SIZE = "$debug_message_size";
    private static final String DEBUG_MESSAGE_FILE = "$debug_message_file";
    private static final String DEBUG_MESSAGE_FILE_SIZE = "$debug_message_file_size";

    private static final String CONSOLE_ERRORS = "$console_errors";

    private static final String FLASH_PREFIX = "$flash_";

    public int playerVersion = 0;
    public int sizeOfPtr = 4;
    public boolean squelchEnabled = false;
    public Map<String, String> parameters = new HashMap<>();
    public Map<String, String> options = new HashMap<>();
    public boolean wideLines = false;

    protected List<DebugMessageListener> messageListeners = new ArrayList<>();

    public static final int DEFAULT_ISOLATE_ID = 1;

    public int activeIsolateId = -1;

    public void addMessageListener(DebugMessageListener l) {
        messageListeners.add(l);
    }

    public void removeMessageListener(DebugMessageListener l) {
        messageListeners.remove(l);
    }

    final DebuggerCommands dc;

    public DebuggerConnection(Socket s) throws IOException {
        this.s = s;
        this.is = new BufferedInputStream(s.getInputStream());
        this.os = new BufferedOutputStream(s.getOutputStream());

        dc = new DebuggerCommands(this);

        //Respon to InProcessTag with OutProcessedTag
        addMessageListener(new DebugMessageListener<InProcessTag>() {
            @Override
            public void message(InProcessTag message) {
                try {
                    writeMessage(new OutProcessedTag(DebuggerConnection.this));
                } catch (IOException ex) {
                    //ignore
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private boolean handle(DebugMessageListener<? extends InDebuggerMessage> l, InDebuggerMessage msg) {
        Class actualType = (Class) ((ParameterizedType) l.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        boolean canHandle = (actualType.isAssignableFrom(msg.getClass()));
        if (!canHandle) {
            return false;
        }
        try {
            l.getClass().getMethod("message", InDebuggerMessage.class).invoke(l, msg);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DebuggerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /*
    
     debugger -> flash
     <- 2a	InSWFinfo
     <- 02 	InExit
     <- 3e  InIsolate
     <- 0c InParam
     <- 14 InNumScript
     <- 0e InScript
     <- 0e InScript
     ...
     <- 0e InScript
     -> 1c SetOption disable_script_stuck_dialog
     <- 13 InSetBreakPoint
     <- 0f InAskBreakPoints
     <- 10 InBreakAt
     <- 20 InOption disable_script_stuck_dialog - true
     -> 1c OutSetOption disable_script_stuck
     <- 20 InOption disable_script_stuck false
     -> 1c OutSetOption break_on_fault on
     <- 20 InOption break_on_fault true
     -> 1c OutSetOption enumerate_override on
     <- 20 InOption enumerate_over true
     -> 1c OutSetOption notify_on_failure on
     <- 20 InOption notify_on_failure true
     -> 1c OutSetOption invoke_setters om
     <- 20 InOption invoke_setters true
     -> 1c OutSetOption swf_load_messages on
     <- 20 InOption swf_load_messafes true
     -> 1c OutSetOption getter_timeout 1500
     <- 20 InOption getter_timeout 1500
     -> 1c OutSetOption setter_timeout 5000
     <- 20 InOption setter_timeout 5000
     -> 18 OutSetSquelch 1
     <- 1d InSquelch
     -> 26 OutSwfInfo
     <- 2a InSwfInfo
     -> 0f OutContinue
     <- 11 InContinue
     <- 05 InTrace
     <- 05 InTrace
     <- 00 InSetMenuState
     <- 19 InProcessTag
     -> 17 OutProcessedTag
     <- 00 InSetMenuState
     <- 19 InProcessTag
     -> 17 OutProcessedTag
     */
    private List<InDebuggerMessage> received = new ArrayList<>();

    private List<Class<? extends InDebuggerMessage>> dropped = new ArrayList<>();

    private final Object receivedLock = new Object();

    public <E extends InDebuggerMessage> void dropMessage(Class<E> msgType) {
        synchronized (receivedLock) {
            dropped.add(msgType);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends InDebuggerMessage> E getMessage(Class<E> msgType) {
        synchronized (receivedLock) {
            while (true) {
                loopi:
                for (int i = 0; i < received.size(); i++) {
                    InDebuggerMessage msg = received.get(i);
                    for (int d = 0; d < dropped.size(); d++) {
                        Class<? extends InDebuggerMessage> dc = dropped.get(d);
                        if (msgType.isAssignableFrom(dc)) {
                            received.remove(i);
                            dropped.remove(d);
                            i--;
                            continue loopi;
                        }
                    }
                    if (msgType.isAssignableFrom(msg.getClass())) {
                        received.remove(i);
                        return (E) msg;
                    }
                }
                try {
                    receivedLock.wait();
                } catch (InterruptedException ex) {
                    return null;
                }
            }
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                InDebuggerMessage msg = readMessage();
                Logger.getLogger(Debugger.class.getName()).log(Level.FINER, "Received: {0}", msg);
                for (DebugMessageListener listener : messageListeners) {
                    handle(listener, msg);
                }
                msg.exec();
                synchronized (receivedLock) {
                    received.add(msg);
                    receivedLock.notifyAll();
                }
            } catch (IOException ex) {
                Logger.getLogger(DebuggerConnection.class.getName()).log(Level.FINE, "Disconnected");
                break;
            }
        }
    }

    protected InDebuggerMessage readMessage() throws IOException {
        int size = (int) readDword();
        if (size < 0) {
            throw new IOException("Socket closed");
        }
        int type = (int) readDword();
        byte buf[] = new byte[size];
        int cnt;
        int offset = 0;
        while (offset < size && (cnt = is.read(buf, offset, size - offset)) > 0) {
            offset += cnt;
        }
        return InDebuggerMessage.getInstance(this, type, buf);
    }

    /**
     * Writes message
     *
     * @param v
     * @throws IOException
     */
    public synchronized void writeMessage(OutDebuggerMessage v) throws IOException {
        Logger.getLogger(DebuggerConnection.class.getName()).log(Level.FINER, "Sending: {0}", v);
        if (v.type != OutSetActiveIsolate.ID) {
            int targetIsolate = v.targetIsolate;
            if (targetIsolate != activeIsolateId) {
                activeIsolateId = targetIsolate;
                writeMessage(new OutSetActiveIsolate(this, targetIsolate));
            }
        }
        byte[] data = v.getData();

        writeDword(data.length);
        writeDword(v.type);
        os.write(data);
        os.flush();

        Logger.getLogger(DebuggerConnection.class.getName()).log(Level.FINEST, "Sent: {0}", v);
    }

    /**
     * Writes message and waits for result
     *
     * @param <E>
     * @param v
     * @param cls
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public <E extends InDebuggerMessage> E sendMessage(OutDebuggerMessage v, Class<E> cls) throws IOException {
        writeMessage(v);
        return getMessage(cls);
    }

    /**
     * Writes message and ignores result
     *
     * @param <E>
     * @param v
     * @param cls
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public <E extends InDebuggerMessage> void sendMessageIgnoreResult(OutDebuggerMessage v, Class<E> cls) throws IOException {
        writeMessage(v);
        dropMessage(cls);
    }

    protected long readDword() throws IOException {
        int b1 = is.read();
        int b2 = is.read();
        int b3 = is.read();
        int b4 = is.read();
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + b1;
    }

    protected void writeDword(long val) throws IOException {
        int b1 = (int) (val & 0xff);
        int b2 = (int) ((val >> 8) & 0xff);
        int b3 = (int) ((val >> 16) & 0xff);
        int b4 = (int) ((val >> 24) & 0xff);
        os.write(b1);
        os.write(b2);
        os.write(b3);
        os.write(b4);
    }
}
