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
public class InBreakReason extends InDebuggerMessage {

    public static final int ID = 40;

    public static int REASON_UKNOWN = 0;
    public static int REASON_BREAKPOINT = 1;
    public static int REASON_WATCH = 2;
    public static int REASON_FAULT = 3;
    public static int REASON_STOP_REQUEST = 4;
    public static int REASON_STEP = 5;
    public static int REASON_HALT = 6;
    public static int REASON_SCRIPT_LOADED = 7;

    public int reason;
    public int swfIndex;
    public long offset;
    public long prevOffset;
    public long nextOffset;

    @Override
    public String toString() {
        return super.toString() + "(reason=" + reason + ", swfIndex=" + swfIndex + ", offset=" + offset + ", prev=" + prevOffset + ", next=" + nextOffset + ")";
    }

    public InBreakReason(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        reason = readWord();
        swfIndex = readWord();
        offset = readDWord();
        prevOffset = readDWord();
        nextOffset = readDWord();
    }

}
