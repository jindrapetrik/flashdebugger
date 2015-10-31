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

import com.jpexs.debugger.flash.Variable;
import com.jpexs.debugger.flash.DebuggerConnection;
import com.jpexs.debugger.flash.InDebuggerMessage;

/**
 *
 * @author JPEXS
 */
public class InErrorException extends InDebuggerMessage {

    public static final int ID = 36;

    public String exceptionMessage;
    public boolean willExceptionBeCaught;
    public long offset;
    public Variable thrownVar;

    @Override
    public String toString() {
        return super.toString() + "(msg=" + exceptionMessage + ", willBeCaught=" + willExceptionBeCaught + ", offset=" + offset + ", thrownVar=" + thrownVar + ")";
    }

    public InErrorException(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        offset = readDWord();
        willExceptionBeCaught = false;

        if (available() > 0) { //FP9+
            exceptionMessage = readString();
            if (available() > 0) {
                if (readByte() != 0) {
                    willExceptionBeCaught = (readByte() != 0 ? true
                            : false);
                    readPtr(c);
                    thrownVar = readVariable(c);
                }
            }
        } else {
            exceptionMessage = "";
        }
    }

}
