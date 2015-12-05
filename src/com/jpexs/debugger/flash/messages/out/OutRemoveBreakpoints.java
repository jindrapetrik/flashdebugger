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
package com.jpexs.debugger.flash.messages.out;

import com.jpexs.debugger.flash.DebuggerConnection;
import com.jpexs.debugger.flash.OutDebuggerMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class OutRemoveBreakpoints extends OutDebuggerMessage {

    public static int ID = 18;

    public List<Integer> files;
    public List<Integer> lines;

    @Override
    public String toString() {
        return super.toString() + "(bp.count=" + files.size() + ")";
    }

    public OutRemoveBreakpoints(DebuggerConnection c, int file, int line) {
        super(c, ID);
        this.files = new ArrayList<>();
        this.lines = new ArrayList<>();

        lines.add(line);
        files.add(file);
    }

    public OutRemoveBreakpoints(DebuggerConnection c, List<Integer> files, List<Integer> lines) {
        super(c, ID);
        this.files = files;
        this.lines = lines;
        //receive InSetBreakpoint
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeDWord(os, files.size());
        for (int i = 0; i < files.size(); i++) {
            if (!connection.wideLines) {
                writeWord(os, files.get(i));
                writeWord(os, lines.get(i));
            } else {
                writeDWord(os, files.get(i));
                writeDWord(os, lines.get(i));
            }
        }
    }

}
