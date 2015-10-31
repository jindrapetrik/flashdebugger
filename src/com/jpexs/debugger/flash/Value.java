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

public interface Value {

    public static final Object UNDEFINED = new Object() {
        @Override
        public String toString() {
            return "undefined";
        }
    };

    public static final long UNKNOWN_ID = -1;

    public static final long GLOBAL_ID = -2;

    public static final long THIS_ID = -3;

    public static final long ROOT_ID = -4;

    public static final long BASE_ID = -100;

    public static final long LEVEL_ID = -300;

    public static final String TRAITS_TYPE_NAME = "traits";
}
