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
public class InSetBreakpoint extends InDebuggerMessage {

    public static final int ID = 19;

    public List<Integer> files;
    public List<Integer> lines;

    @Override
    public String toString() {
        return super.toString() + "(bp.count=" + files.size() + ")";
    }

    public InSetBreakpoint(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        int count = (int) readDWord();
        files = new ArrayList<>();
        lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (!wideLines) {
                long bp = readDWord();
                files.add(decodeFile(bp));
                lines.add(decodeLine(bp));
            } else {
                files.add((int) readDWord());
                lines.add((int) readDWord());
            }
        }
    }

}
