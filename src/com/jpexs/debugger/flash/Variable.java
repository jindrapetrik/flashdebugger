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
    public int flags;

    public boolean hasFlag(int flag) {
        return (flags & flag) == flag;
    }

    public String getModifiers() {
        return getScopeModifier()
                + (hasFlag(VariableFlags.IS_DYNAMIC) ? " dynamic" : "")
                + (hasFlag(VariableFlags.IS_STATIC) ? " static" : "");
    }

    public String getDeclaration() {
        return getModifiers() + (hasFlag(VariableFlags.IS_CONST) ? " const " : " var ") + name + ((!hasFlag(VariableFlags.IS_CONST) && hasFlag(VariableFlags.READ_ONLY)) ? " [readonly]" : "");
    }

    public String getScopeModifier() {
        if (hasFlag(VariableFlags.IS_LOCAL)) {
            return "";
        }
        int scope = flags & VariableFlags.SCOPE_MASK;
        switch (scope) {
            case VariableFlags.PUBLIC_SCOPE:
                return "public";
            case VariableFlags.PROTECTED_SCOPE:
                return "protected";
            case VariableFlags.PRIVATE_SCOPE:
                return "private";
            case VariableFlags.NAMESPACE_SCOPE:
                return "<ns>";
            case VariableFlags.INTERNAL_SCOPE:
                return "";
            default:
                return "?";
        }
    }

    @Override
    public String toString() {
        return "var(name=" + name + ", type=" + getTypeAsStr() + "), value=" + getValueAsStr() + ", flags=" + flags;
    }

    public String getTypeAsStr() {
        return typeNameFor(vType);
    }

    public String getValueAsStr() {
        switch (vType) {
            case VariableType.OBJECT:
            case VariableType.MOVIECLIP:
            case VariableType.FUNCTION:
                return getTypeAsStr() + "(" + value + ")";
            default:
                return "" + value;
        }
    }

    public Variable(String name, int vType, Object value, String typeName, String className, boolean isPrimitive, int flags) {
        this.vType = vType;
        this.value = value;
        this.typeName = typeName;
        this.className = className;
        this.isPrimitive = isPrimitive;
        this.name = name;
        this.flags = flags;
    }

    public static String typeNameFor(int type) {
        switch (type) {
            case VariableType.NUMBER:
                return "number";

            case VariableType.BOOLEAN:
                return "boolean";

            case VariableType.STRING:
                return "String";

            case VariableType.OBJECT:
                return "Object";

            case VariableType.FUNCTION:
                return "Function";

            case VariableType.MOVIECLIP:
                return "MovieClip";

            case VariableType.NULL:
                return "null";

            case VariableType.UNDEFINED:
            case VariableType.UNKNOWN:
            default:
                return "undefined";
        }
    }
}
