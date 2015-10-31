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
 * License along with this library. */
package com.jpexs.debugger.flash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JPEXS
 */
public abstract class OutDebuggerMessage extends DebuggerMessage {

    public static int ID;
    protected int type;

    public static final int DEFAULT_ISOLATE_ID = 1;

    public int targetIsolate;

    private ByteArrayOutputStream os;

    protected DebuggerConnection connection;

    protected static boolean supportsWideLineNumbers = true;

    public OutDebuggerMessage(DebuggerConnection c, int type, byte[] data) {
        this.type = type;
        connection = c;
        targetIsolate = c.activeIsolateId;

        os = new ByteArrayOutputStream();
        if (data.length > 0) {
            try {
                os.write(data);
            } catch (IOException ex) {
                Logger.getLogger(OutDebuggerMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public byte[] getData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writeTo(baos);
        } catch (IOException ex) {
            Logger.getLogger(OutDebuggerMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baos.toByteArray();

    }

    public void writeTo(OutputStream os) throws IOException {

    }

    public OutDebuggerMessage(DebuggerConnection c, int type) {
        this(c, type, new byte[0]);
    }

    public void writeDWord(OutputStream os, long val) throws IOException {
        int b1 = (int) (val & 0xff);
        int b2 = (int) ((val >> 8) & 0xff);
        int b3 = (int) ((val >> 16) & 0xff);
        int b4 = (int) ((val >> 24) & 0xff);
        os.write(b1);
        os.write(b2);
        os.write(b3);
        os.write(b4);
    }

    public void writeLong(OutputStream os, long val) throws IOException {
        writeDWord(os, val & 0xffffffff);
        writeDWord(os, (val >> 32) & 0xffffffff);
    }

    public void writeByte(OutputStream os, int val) throws IOException {
        os.write(val & 0xff);
    }

    public void writeBytes(OutputStream os, byte[] data) {
        try {
            os.write(data);
        } catch (IOException ex) {
            //ignore
        }
    }

    public void writeWord(OutputStream os, long val) throws IOException {
        int b1 = (int) (val & 0xff);
        int b2 = (int) ((val >> 8) & 0xff);
        os.write(b1);
        os.write(b2);
    }

    public void writeString(OutputStream os, String val) throws IOException {
        try {
            os.write(val.getBytes("UTF-8"));
        } catch (IOException ex) {
            //ignore
        }
        os.write(0);
    }

    @Override
    public int getType() {
        return type;
    }

    public void writePtr(OutputStream os, long ptr) throws IOException {
        if (connection.sizeOfPtr == 4) {
            writeDWord(os, ptr);
        } else if (connection.sizeOfPtr == 8) {
            writeLong(os, ptr);
        }
    }

    public void writeId(OutputStream os, int file, int line) throws IOException {
        writeDWord(os, (file & 0xffff) + ((line & 0xffff) << 16));
    }

}
