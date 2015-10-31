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
package com.jpexs.debugger.flash.messages.in;

import com.jpexs.debugger.flash.DebuggerConnection;

import com.jpexs.debugger.flash.InDebuggerMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JPEXS
 */
public class InSwfInfo extends InDebuggerMessage {

    public static final int ID = 42;

    public List<SwfInfo> swfInfos;

    public static class SwfInfo {

        public long index;
        public long id;
        public boolean debugComing;
        public int vmVersion;
        public int rsvd1;
        public long swfSize;
        public long swdSize;
        public long scriptCount;
        public long offsetCount;
        public long breakpointCount;
        public long port;
        public String path;
        public String url;
        public String host;

        public Map<Long, Integer> local2global = new HashMap<>();

        @Override
        public String toString() {
            return "SwfInfo(index=" + index + ", id=" + id + ", vmVersion=" + vmVersion + ", swfSize=" + swfSize + ", swdSize=" + swdSize + ", scriptCount=" + scriptCount + ", path=" + path + ", url=" + url + ", host=" + host + ")";
        }

    }

    @Override
    public String toString() {
        return super.toString() + "(swfInfo.count=" + swfInfos.size() + ")";
    }

    public InSwfInfo(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        int count = readWord();
        swfInfos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SwfInfo s = new SwfInfo();
            s.index = readDWord();

            s.id = readPtr(c);
            if (s.id != 0) {
                s.debugComing = readByte() == 0 ? false : true;
                s.vmVersion = readByte();
                s.rsvd1 = readWord();
                s.swfSize = readDWord();
                s.swdSize = readDWord();
                s.scriptCount = readDWord();
                s.offsetCount = readDWord();
                s.breakpointCount = readDWord();

                s.port = readDWord();
                s.path = readString();
                s.url = readString();
                s.host = readString();

                s.local2global = new HashMap<>();
                if (s.swdSize > 0) {
                    long num = readDWord();
                    for (int j = 0; j < num; j++) {
                        if (available() < c.sizeOfPtr) {
                            break;
                        }
                        long local = readPtr(c);
                        int global = (int) readDWord();
                        s.local2global.put(local, global);
                    }
                }
            }
            swfInfos.add(s);
        }
    }

}
