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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class InFrame extends InDebuggerMessage {

    public static final int ID = 31;

    public int depth;
    public List<Variable> registers;
    public List<Variable> variables;
    public long frameId = -1;

    @Override
    public String toString() {
        return super.toString() + "(depth=" + depth + ", registers.count=" + registers.size() + ", variables.count=" + variables.size() + ")";
    }

    public InFrame(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        depth = (int) readDWord();
        registers = new ArrayList<>();

        if (depth > -1) {
            int num = (int) readDWord();
            for (int i = 0; i < num; i++) {
                registers.add(readRegister(c, i + 1));
            }
        }

        int currentArg = -1;
        boolean gettingScopeChain = false;
        if (available() > 0) {
            frameId = readPtr(c);
            readVariable(c);
        }
        variables = new ArrayList<>();
        while (available() > 0) {
            Variable child = readVariable(c);
            if (currentArg == -1 && child.name.equals(ARGUMENTS_MARKER)) {
                currentArg = 0;
                gettingScopeChain = false;
            } else if (child.name.equals(SCOPE_CHAIN_MARKER)) {
                currentArg = -1;
                gettingScopeChain = true;
            } else if (currentArg >= 0) {
                currentArg++;
                if (child.name.equals("undefined")) {
                    child.name = "_arg" + currentArg;
                }
            }

            if (!gettingScopeChain) {
                //addvariablemember...
            }
            variables.add(child);
        }
    }

}
