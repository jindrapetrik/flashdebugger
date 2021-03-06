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
public class OutSetQuality extends OutDebuggerMessage {

    public static int ID = 4;

    public String quality;

    public static final String QUALITY_LOW = "LOW";
    public static final String QUALITY_MEDIUM = "MEDIUM";
    public static final String QUALITY_HIGH = "HIGH";
    public static final String QUALITY_AUTOLOW = "AUTOLOW";
    public static final String QUALITY_AUTOMEDIUM = "AUTOMEDIUM";
    public static final String QUALITY_AUTOHIGH = "AUTOHIGH";
    public static final String QUALITY_BEST = "BEST";

    @Override
    public String toString() {
        return super.toString() + "(quality=" + quality + ")";
    }

    public OutSetQuality(DebuggerConnection c, String quality) {
        super(c, ID);
        this.quality = quality;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeString(os, quality);
    }

}
