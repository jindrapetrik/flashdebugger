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
public class OutSetProperty extends OutDebuggerMessage {

    public static int ID = 12;

    public long objId;
    public int propertyId;
    public String type;
    public String value;

    public static final String[] PROPERTY_NAMES = new String[]{
        "_x", "_y", "_xscale", "_yscale", "_currentframe", "_totalframes", "_alpha", "_visible",
        "_width", "_height", "_rotation", "_target", "_framesloaded", "_name", "_droptarget",
        "_url", "_highquality", "_focusrect", "_soundbuftime", "_quality", "_xmouse", "_ymouse"
    };

    @Override
    public String toString() {
        return super.toString() + "(objId=" + objId + ", propertyId=" + propertyId + ", type=" + type + ", value=" + value + ")";
    }

    public OutSetProperty(DebuggerConnection c, long objId, int propertyId, String type, String value) {
        super(c, ID);
        this.objId = objId;
        this.propertyId = propertyId;
        this.type = type;
        this.value = value;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writePtr(os, objId);
        writeWord(os, propertyId);
        writeString(os, type);
        writeString(os, value);
    }

}
