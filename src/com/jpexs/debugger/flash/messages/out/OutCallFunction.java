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
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class OutCallFunction extends OutDebuggerMessage {

    public static int ID = 48;

    public boolean isConstructor;
    public String funcName;
    public String thisType;
    public String thisValue;
    public List<String> argTypes;
    public List<String> argValues;

    @Override
    public String toString() {
        return super.toString() + "(isConstructor=" + isConstructor + ", funcName=" + funcName + ", thisType=" + thisType + ", thisValue=" + thisValue + ", arg.count=" + argTypes.size() + ")";
    }

    public OutCallFunction(DebuggerConnection c, boolean isConstructor, String funcName, String thisType, String thisValue, List<String> argTypes, List<String> argValues) {
        super(c, ID);
        this.isConstructor = isConstructor;
        this.funcName = funcName;
        this.thisType = thisType;
        this.thisValue = thisValue;
        this.argTypes = argTypes;
        this.argValues = argValues;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeDWord(os, isConstructor ? 1 : 0);
        writeDWord(os, 0); //TODO: active frame number
        writeString(os, thisType);
        writeString(os, thisValue);
        writeString(os, funcName);
        writeDWord(os, argTypes.size());
        for (int i = 0; i < argTypes.size(); i++) {
            writeString(os, argTypes.get(i));
            writeString(os, argValues.get(i));
        }

    }

}
