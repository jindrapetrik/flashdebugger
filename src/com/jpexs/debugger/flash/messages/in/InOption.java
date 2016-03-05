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
public class InOption extends InDebuggerMessage {

    public static final int ID = 32;

    public String s;
    public String v;

    public static String OPTION_MOVIE = "movie";
    public static String OPTION_PASSWORD = "password";
    public static String OPTION_CONCURRENT_PLAYER = "concurrent_player";
    public static String OPTION_WIDE_LINE_PLAYER = "wide_line_player";
    public static String OPTION_CAN_TERMINATE = "can_terminate";
    public static String OPTION_CAN_BREAK_ON_ALL_EXCEPTIONS = "can_break_on_all_exceptions";
    public static String OPTION_CAN_SET_WATCHPOINTS = "can_set_watchpoints";
    public static String OPTION_CAN_CALL_FUNCTIONS = "can_call_functions";
    public static String OPTION_TRACE = "trace";

    @Override
    public String toString() {
        return super.toString() + "(name=" + s + ", value=" + v + ")";
    }

    public InOption(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        s = readString();
        v = readString();
    }

    @Override
    public void exec() {
        connection.options.put(s, v);
    }

}
