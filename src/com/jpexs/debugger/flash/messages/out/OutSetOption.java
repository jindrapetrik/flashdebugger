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

/**
 *
 * @author JPEXS
 */
public class OutSetOption extends OutDebuggerMessage {

    public static int ID = 28;

    public String option;
    public String value;

    public static final String OPTION_CONCURRENT_DEBUGGER = "concurrent_debugger";
    public static final String OPTION_WIDE_LINE_DEBUGGER = "wide_line_debugger";
    public static final String OPTION_ASTRACE = "astrace";
    public static final String OPTION_BREAK_ON_FAULT = "break_on_fault";
    public static final String OPTION_CONSOLE_ERRORS = "console_errors";
    public static final String OPTION_DISABLE_SCRIPT_STUCK = "disable_script_stuck";
    public static final String OPTION_DISABLE_SCRIPT_STUCK_DIALOG = "disable_script_stuck_dialog";
    public static final String OPTION_ENUMERATE_OVERRIDE = "enumerate_override";
    public static final String OPTION_GETTER_TIMEOUT = "getter_timeout";
    public static final String OPTION_INVOKE_SETTERS = "invoke_setters";
    public static final String OPTION_NOTIFY_ON_FAILURE = "notify_on_failure";
    public static final String OPTION_SETTER_TIMEOUT = "setter_timeout";
    public static final String OPTION_SCRIPT_TIMEOUT = "script_timeout";
    public static final String OPTION_LOAD_MESSAGES = "swf_load_messages";
    public static final String OPTION_VERBOSE = "verbose";

    @Override
    public String toString() {
        return super.toString() + "(option=" + option + ", value=" + value + ")";
    }

    public OutSetOption(DebuggerConnection c, String option, String value) {
        super(c, ID);
        this.option = option;
        this.value = value;
        //receive InOption
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeString(os, option);
        writeString(os, value);
    }

}
