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
public class InGetVariable extends InDebuggerMessage {

    public static final int ID = 30;

    public List<Variable> childs;
    public List<Long> childsIds;

    public Variable parent;
    public long parentId;

    @Override
    public String toString() {
        return super.toString() + "(parentId=" + parentId + ", parent=" + parent + ", childs.length=" + childs.size() + ")";
    }

    public InGetVariable(DebuggerConnection c, byte[] data) {
        super(c, ID, data);
        childs = new ArrayList<>();
        childsIds = new ArrayList<>();
        boolean hasParent = false;

        while (available() > 0) {
            long id = readPtr(c);
            if (!hasParent) {
                parentId = id;
                parent = readVariable(c);
                hasParent = true;
            } else {
                Variable child = readVariable(c);
                /*if (showMember(child)) {

                 } else {
                 if (isTraits(child)) {
                 //if name is MyClass$something, then it belongs to MyClass - static
                 }
                 }*/
                childs.add(child);
                childsIds.add(id);
            }
        }
    }

}
