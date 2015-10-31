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
package com.jpexs.debugger.flash;

/**
 *
 * @author JPEXS
 */
public class Variable {

    public String name;
    public int vType;
    public Object value = null;
    public String typeName = "";
    public String className = "";
    public boolean isPrimitive = false;

    @Override
    public String toString() {
        return "var(name=" + name + ", type=" + typeNameFor(vType) + ")";
    }

    public Variable(String name, int vType, Object value, String typeName, String className, boolean isPrimitive) {
        this.vType = vType;
        this.value = value;
        this.typeName = typeName;
        this.className = className;
        this.isPrimitive = isPrimitive;
        this.name = name;
    }

    public static String typeNameFor(int type) {
        String s = "string";
        switch (type) {
            case VariableType.NUMBER:
                s = "number";
                break;

            case VariableType.BOOLEAN:
                s = "boolean";
                break;

            case VariableType.STRING:
                s = "string";
                break;

            case VariableType.OBJECT:
                s = "object";
                break;

            case VariableType.FUNCTION:
                s = "function";
                break;

            case VariableType.MOVIECLIP:
                s = "movieclip";
                break;

            case VariableType.NULL:
                s = "null";
                break;

            case VariableType.UNDEFINED:
            case VariableType.UNKNOWN:
            default:
                s = "undefined";
                break;
        }
        return s;
    }
}
