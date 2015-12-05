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

/**
 *
 * @author JPEXS
 */
public class InSetProperty extends InDebuggerMessage {

    public static final int ID = 1;

    public long objId;
    public int item;
    public String value;

    public static final String[] PROPERTY_NAMES = new String[]{
        "_x", "_y", "_xscale", "_yscale", "_currentframe", "_totalframes", "_alpha", "_visible",
        "_width", "_height", "_rotation", "_target", "_framesloaded", "_name", "_droptarget",
        "_url", "_highquality", "_focusrect", "_soundbuftime", "_quality", "_xmouse", "_ymouse"
    };

    @Override
    public String toString() {
        return super.toString() + "(objId=" + objId + ", item=" + item + ", value=" + value + ")";
    }

    public InSetProperty(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        objId = readPtr(c);
        item = readWord();
        value = readString();
    }

}
