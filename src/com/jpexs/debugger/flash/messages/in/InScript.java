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

/**
 *
 * @author JPEXS
 */
public class InScript extends InDebuggerMessage {

    public static final int ID = 14;

    public int module;
    public int bitmap;
    public String name;
    public String text;
    public int swfIndex = -1;

    @Override
    public String toString() {
        return super.toString() + "(module=" + module + ", bitmap=" + bitmap + ", name=" + name + ", text=" + text + ", swfIndex=" + swfIndex + ")";
    }

    public InScript(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        module = (int) readDWord();
        bitmap = (int) readDWord();
        name = readString(); //format: "basepath;package;filename"
        text = readString();
        if (available() >= 4) {
            swfIndex = (int) readDWord();
        }
    }

}
