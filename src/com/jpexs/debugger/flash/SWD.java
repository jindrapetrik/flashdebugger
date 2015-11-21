/*
 * Copyright (C) 2015 JPEXS, All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

/**
 * Flash Player Debug Info file - .swd file
 *
 * @author JPEXS
 */
public class SWD {

    /*public static final int bitmapMainMxmlFile = 1; //Main MXML file for application
     public static final int bitmapOtherFile = 2; //Other file, presumably an MXML or AS file
     public static final int bitmapFrameworkFile = 3; //Framework file
     public static final int bitmapOtherSyntheticFiles = 4;//Other per-component synthetic files produced by MXML compiler
     public static final int bitmapActions = 5; //Actions
     */
    //These seems to bu used by AS1/2 compiler
    public static final int bitmapAction = 1;
    public static final int bitmapOnAction = 2;
    public static final int bitmapOnClipAction = 3;

    private static final String SYNTHETIC = "synthetic: ";
    private static final String SYNTHETIC_OBJ = "synthetic: Object.registerClass() for ";
    private static final String ACTIONS_FOR = "Actions for ";

    public int swfVersion;

    public final List<DebugItem> objects;

    public abstract static class DebugItem {

        public final int tagId;

        public DebugItem(int tagId) {
            this.tagId = tagId;
        }

        protected abstract void writeTo(OutputStream os) throws IOException;

        public byte[] getBytes() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                writeUI32(baos, tagId);
                writeTo(baos);
                return baos.toByteArray();
            } catch (IOException ex) {
                return null;
            }
        }
    }

    public static class DebugScript extends DebugItem {

        public int module;
        public int bitmap;
        public String name;
        public String text;

        public static final int ID = 0;

        @Override
        public String toString() {
            return "DebugScript[module=" + module + ",bitmap=" + bitmap + ",name=" + name + "]";
        }

        public DebugScript(InputStream is) throws IOException {
            super(ID);
            module = readUI32(is);
            bitmap = readUI32(is);
            name = readString(is);
            text = readString(is);
        }

        public DebugScript(int id, int bitmap, String name, String text) {
            super(ID);

            this.module = id;
            this.bitmap = bitmap;
            this.name = name;
            this.text = text;
        }

        public DebugScript() {
            super(ID);

        }

        @Override
        protected void writeTo(OutputStream os) throws IOException {
            writeUI32(os, module);
            writeUI32(os, bitmap);
            writeString(os, name);
            writeString(os, text);
        }

    }

    public static class DebugOffset extends DebugItem {

        public int module;
        public int line;
        public int offset;

        public static final int ID = 1;

        @Override
        public String toString() {
            return "DebugOffset[module=" + module + ", line=" + line + ", offset=" + offset + "]";
        }

        public DebugOffset(InputStream is) throws IOException {
            super(ID);
            module = readUI32(is);
            line = readUI32(is);
            offset = readUI32(is);
        }

        public DebugOffset() {
            super(ID);

        }

        public DebugOffset(int id, int lineno, int offset) {
            super(ID);

            this.module = id;
            this.line = lineno;
            this.offset = offset;
        }

        @Override
        protected void writeTo(OutputStream os) throws IOException {
            writeUI32(os, module);
            writeUI32(os, line);
            writeUI32(os, offset);
        }

    }

    public static class DebugRegisters extends DebugItem {

        public int offset;
        public List<Integer> regnums = new ArrayList<>();
        public List<String> names = new ArrayList<>();

        public static final int ID = 5;

        @Override
        public String toString() {
            return "DebugRegisters[offset=" + offset + ", count=" + regnums.size() + "]";
        }

        public DebugRegisters() {
            super(ID);

        }

        public DebugRegisters(int offset, List<Integer> regnums, List<String> names) {
            super(ID);

            this.offset = offset;
            this.regnums = regnums;
            this.names = names;
        }

        public DebugRegisters(InputStream is) throws IOException {
            super(ID);

            offset = readUI32(is);
            int size = readUI8(is);
            for (int i = 0; i < size; i++) {
                regnums.add(readUI8(is));
                names.add(readString(is));
            }
        }

        @Override
        protected void writeTo(OutputStream os) throws IOException {
            writeUI32(os, offset);
            writeUI8(os, regnums.size());
            for (int i = 0; i < regnums.size(); i++) {
                writeUI8(os, regnums.get(i));
                writeString(os, names.get(i));
            }
        }

    }

    public static class DebugBreakpoint extends DebugItem {

        public int module;
        public int line;

        public static final int ID = 2;

        @Override
        public String toString() {
            return "DebugBreakpoint[module=" + module + ", line=" + line + "]";
        }

        public DebugBreakpoint() {
            super(ID);

        }

        public DebugBreakpoint(InputStream is) throws IOException {
            super(ID);
            int bp = readUI32(is);
            module = bp & 0xffff;
            line = (bp >> 16) & 0xffff;
        }

        public DebugBreakpoint(int module, int line) {
            super(ID);

            this.module = module;
            this.line = line;
        }

        @Override
        protected void writeTo(OutputStream os) throws IOException {
            long bp = ((line << 16) & 0xffff0000) | (module);
            writeUI32(os, bp);
        }

    }

    public static class DebugId extends DebugItem {

        byte[] uid;

        public static final int ID = 3;

        @Override
        public String toString() {
            return "DebugId[" + DatatypeConverter.printHexBinary(uid) + "]";
        }

        public DebugId() {
            super(ID);

        }

        public DebugId(InputStream is) throws IOException {
            super(ID);

            uid = new byte[16];
            for (int i = 0; i < uid.length; i++) {
                uid[i] = (byte) readUI8(is);
            }
        }

        public DebugId(byte[] uid) {
            super(ID);

            this.uid = uid;
        }

        @Override
        protected void writeTo(OutputStream os) throws IOException {
            os.write(uid);
        }

    }

    private static boolean isFrameworkClass(String n) {
        return (n.startsWith("mx.") && n.indexOf(":") != -1 && n.endsWith(".as")) || (n.indexOf("/mx/") > -1);
    }

    public static String fixName(String name) {

        if (name.startsWith(SYNTHETIC)) {

            if (name.startsWith(SYNTHETIC_OBJ)) {
                String componentName = name.substring(SYNTHETIC_OBJ.length());
                name = "<" + componentName + ".2>";
                return name;
            }
        }
        return name;
    }
    /*
     public static int getBitmap(String name, String mainDebugScriptName) {
     if (isFrameworkClass(name)) {
     return bitmapFrameworkFile;
     }
     if (name.startsWith(SYNTHETIC)) {
     return bitmapOtherSyntheticFiles;
     }
     if (name.startsWith(ACTIONS_FOR)) {
     return bitmapActions;
     }
     if (name.equals(mainDebugScriptName)) {
     return bitmapMainMxmlFile;
     }
     return bitmapOtherFile;
     }*/

    public SWD(int swfVersion, List<DebugItem> items) {
        this.objects = items;
        this.swfVersion = swfVersion;
    }

    public void saveTo(OutputStream os) throws IOException {
        os.write("FWD".getBytes());
        os.write(swfVersion);
        for (DebugItem di : objects) {
            os.write(di.getBytes());
        }

    }

    public SWD(byte[] data) throws IOException {
        this(new ByteArrayInputStream(data));
    }

    public SWD(InputStream is) throws IOException {
        objects = new ArrayList<>();
        byte[] hdr = readBytes(is, 3);
        if (hdr[0] != 'F' || hdr[1] != 'W' || hdr[2] != 'D') {
            throw new IOException("Invalid SWD file - header not found");
        }
        swfVersion = readUI8(is);
        if (swfVersion < 6) {
            throw new IOException("SWD file < 6 unsupported");
        }

        do {
            int tag = readUI32(is);
            switch (tag) {
                case DebugScript.ID:
                    objects.add(new DebugScript(is));
                    break;
                case DebugOffset.ID:
                    objects.add(new DebugOffset(is));
                    break;
                case DebugBreakpoint.ID:
                    objects.add(new DebugBreakpoint(is));
                    break;
                case DebugId.ID:
                    objects.add(new DebugId(is));
                    break;
                case DebugRegisters.ID:
                    objects.add(new DebugRegisters(is));
                    break;
            }
        } while (is.available() > 0);
    }

    private static void writeUI8(OutputStream os, int val) throws IOException {
        os.write(val);
    }

    private static void writeString(OutputStream os, String val) throws IOException {
        os.write(val.getBytes("UTF-8"));
        os.write(0);
    }

    private static void writeUI32(OutputStream os, long val) throws IOException {
        int b0 = (int) (val & 0xff);
        int b1 = (int) ((val >> 8) & 0xff);
        int b2 = (int) ((val >> 16) & 0xff);
        int b3 = (int) ((val >> 24) & 0xff);

        os.write(b0);
        os.write(b1);
        os.write(b2);
        os.write(b3);
    }

    private static String readString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r;
        while (true) {
            r = readUI8(is);
            if (r == 0) {
                try {
                    return new String(baos.toByteArray(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    //ignore
                }
            }
            baos.write(r);
        }
    }

    private static byte[] readBytes(InputStream is, int length) throws IOException {
        byte[] buf = new byte[length];
        for (int i = 0; i < length; i++) {
            buf[i] = (byte) is.read();
        }
        return buf;
    }

    private static int readUI8(InputStream is) throws IOException {
        int v = is.read();
        if (v == -1) {
            throw new IOException("End of stream reached");
        }
        return v;
    }

    private static int readUI32(InputStream is) throws IOException {
        return (readUI8(is) + (readUI8(is) << 8) + (readUI8(is) << 16) + (readUI8(is) << 24)) & 0xffffffff;
    }

}
