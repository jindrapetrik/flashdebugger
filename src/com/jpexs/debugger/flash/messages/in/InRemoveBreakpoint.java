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
public class InRemoveBreakpoint extends InDebuggerMessage {

    public static final int ID = 22;

    public List<Integer> breakPointFiles;
    public List<Integer> breakPointLines;

    @Override
    public String toString() {
        return super.toString() + "(breakPoints.count=" + breakPointFiles.size() + ")";
    }

    public InRemoveBreakpoint(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        breakPointFiles = new ArrayList<>();
        breakPointLines = new ArrayList<>();
        long count = readDWord();
        for (int i = 0; i < count; i++) {
            if (c.wideLines) {
                breakPointFiles.add((int) readDWord());
                breakPointLines.add((int) readDWord());
            } else {
                breakPointFiles.add((int) readWord());
                breakPointLines.add((int) readWord());
            }
        }
    }

}
