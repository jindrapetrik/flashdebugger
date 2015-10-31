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
package com.jpexs.debugger.flash.messages.in;

import com.jpexs.debugger.flash.DebuggerConnection;

import com.jpexs.debugger.flash.InDebuggerMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class InGetFncNames extends InDebuggerMessage {

    public static final int ID = 45;

    public long module;

    public static class FunctionName {

        public int offset;
        public int firstLine;
        public int lastLine;
        public String name;
    }

    public List<FunctionName> names;

    @Override
    public String toString() {
        return super.toString() + "(names.count=" + names.size() + ")";
    }

    public InGetFncNames(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        module = readDWord();
        int count = (int) readDWord();
        names = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            FunctionName fn = new FunctionName();
            fn.offset = (int) readDWord();
            fn.firstLine = (int) readDWord();
            fn.lastLine = (int) readDWord();
            fn.name = readString();
        }
    }

}
