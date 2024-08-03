/*
 * Copyright (C) 2015 JPEXS, All rights reserved.
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

public interface VariableFlags {

    //Protect from enumeration
    public static final int DONT_ENUMERATE = 0x00000001;
    
    //Protect from deletion
    public static final int DONT_DELETE = 0x00000002;

    //Protect from assigning a value. Does not imply isconst
    public static final int READ_ONLY = 0x00000004;
    
    //Only visible by VM initialized for version 6 or higher 
    public static final int ONLY_SWF6_UP = 0x00000080;
    
    //Ignore in SWF6-initialized VM
    public static final int IGNORE_SWF6 = 0x00000100;
    
    //Only visible by VM initialized for version 7 or higher 
    public static final int ONLY_SWF7_UP = 0x00000400;
    
    //Only visible by VM initialized for version 8 or higher 
    public static final int ONLY_SWF8_UP = 0x00001000;
    
    //Only visible by VM initialized for version 9 or higher 
    public static final int ONLY_SWF9_UP = 0x00002000;   

    public static final int IS_LOCAL = 0x00000020;

    /**
     * argument to a function
     */
    public static final int IS_ARGUMENT = 0x00010000;

    /**
     * dynamic property - only AS3
     */
    public static final int IS_DYNAMIC = 0x00020000;

    public static final int IS_EXCEPTION = 0x00040000;

    public static final int HAS_GETTER = 0x00080000;

    public static final int HAS_SETTER = 0x00100000;

    public static final int IS_STATIC = 0x00200000;

    public static final int IS_CONST = 0x00400000;

    public static final int PUBLIC_SCOPE = 0x00000000;

    public static final int PRIVATE_SCOPE = 0x00800000;

    public static final int PROTECTED_SCOPE = 0x01000000;

    public static final int INTERNAL_SCOPE = 0x01800000;

    public static final int NAMESPACE_SCOPE = 0x02000000;

    public static final int SCOPE_MASK = PUBLIC_SCOPE | PRIVATE_SCOPE | PROTECTED_SCOPE | INTERNAL_SCOPE | NAMESPACE_SCOPE;

    //Is the value class?
    public static final int IS_CLASS = 0x04000000;
}
