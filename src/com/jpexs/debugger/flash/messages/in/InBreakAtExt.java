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
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class InBreakAtExt extends InDebuggerMessage {

    public static final int ID = 27;

    public int file;
    public int line;

    public List<Integer> files;
    public List<Integer> lines;
    public List<Long> ids;
    public List<String> stacks;

    @Override
    public String toString() {
        return super.toString() + "(file=" + file + ", line=" + line + ", items.count=" + files.size() + ")";
    }

    public InBreakAtExt(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        if (!wideLines) {
            file = readWord();
            line = readWord();
        } else {
            file = (int) readDWord();
            line = (int) readDWord();
        }
        long num = readDWord();
        files = new ArrayList<>();
        lines = new ArrayList<>();
        ids = new ArrayList<>();
        stacks = new ArrayList<>();
        for (long i = 0; i < num; i++) {
            int file_i;
            int line_i;
            if (!wideLines) {
                file_i = readWord();
                line_i = readWord();
            } else {
                file_i = (int) readDWord();
                line_i = (int) readDWord();
            }
            long id = readPtr(c);
            String stack = readString();
            files.add(file_i);
            lines.add(line_i);
            ids.add(id);
            stacks.add(stack);
        }
    }

}
