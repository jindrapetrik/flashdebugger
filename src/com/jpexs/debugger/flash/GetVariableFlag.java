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
package com.jpexs.debugger.flash;

/**
 *
 * @author JPEXS
 */
public interface GetVariableFlag {

    public static final int INVOKE_GETTER = 0x00000001;

    public static final int ALSO_GET_CHILDREN = 0x00000002;

    public static final int DONT_GET_FUNCTIONS = 0x00000004;

    public static final int GET_CLASS_HIERARCHY = 0x00000008;
}
