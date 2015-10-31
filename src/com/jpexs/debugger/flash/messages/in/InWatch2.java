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
public class InWatch2 extends InDebuggerMessage {

    public static final int ID = 55;

    public int success;
    public int oldFlags;
    public int oldTag;
    public int flags;
    public long objId;
    public String name;

    @Override
    public String toString() {
        return super.toString() + "(success=" + success + ", oldFlags=" + oldFlags + ", oldTag=" + oldTag + ", flags=" + flags + ", objId=" + objId + ", name=" + name + ")";
    }

    public InWatch2(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        success = readWord();
        oldFlags = readWord();
        oldTag = readWord();
        flags = readWord();
        objId = readPtr(c);
        name = readString();
    }

}
