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
package com.jpexs.debugger.flash.messages.out;

import com.jpexs.debugger.flash.DebuggerConnection;
import com.jpexs.debugger.flash.OutDebuggerMessage;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author JPEXS
 */
public class OutGetActions extends OutDebuggerMessage {

    public static int ID = 36;

    public int module;
    public int offset;
    public int length;

    @Override
    public String toString() {
        return super.toString() + "(module=" + module + ", offset=" + offset + ", length=" + length + ")";
    }

    public OutGetActions(DebuggerConnection c, int module, int offset, int length) {
        super(c, ID);
        this.module = module;
        this.offset = offset;
        this.length = length;
        //receive InGetActions
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeWord(os, module);
        writeWord(os, 0); //reserved
        writeDWord(os, offset);
        writeDWord(os, length);
    }

}
