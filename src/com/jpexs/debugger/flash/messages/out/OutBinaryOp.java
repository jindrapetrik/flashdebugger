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

/**
 *
 * @author JPEXS
 */
public class OutBinaryOp extends OutDebuggerMessage {

    public static int ID = 52;

    public static final int OP_IS = 0;
    public static final int OP_INSTANCEOF = 1;
    public static final int OP_IN = 2;
    public static final int OP_AS = 3;

    public int id;
    public int binaryOp;
    public String leftType;
    public String leftValue;
    public String rightType;
    public String rightValue;

    @Override
    public String toString() {
        return super.toString() + "(id=" + id + ", binaryOp=" + binaryOp + ", leftType=" + leftType + ", leftValue=" + leftValue + ", rightType=" + rightType + ", rightValue=" + rightValue + ")";
    }

    public OutBinaryOp(DebuggerConnection c, int id, int binaryOp, String leftType, String leftValue, String rightType, String rightValue) {
        super(c, ID);
        this.id = id;
        this.binaryOp = binaryOp;
        this.leftType = leftType;
        this.leftValue = leftValue;
        this.rightType = rightType;
        this.rightValue = rightValue;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        writeDWord(os, id);
        writeByte(os, binaryOp);
        writeString(os, leftType);
        writeString(os, leftValue);
        writeString(os, rightType);
        writeString(os, rightValue);
    }

}
