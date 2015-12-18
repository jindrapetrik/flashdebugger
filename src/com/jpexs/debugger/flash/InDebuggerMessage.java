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

import com.jpexs.debugger.flash.messages.in.InAskBreakpoints;
import com.jpexs.debugger.flash.messages.in.InBinaryOp;
import com.jpexs.debugger.flash.messages.in.InBreakAt;
import com.jpexs.debugger.flash.messages.in.InBreakAtExt;
import com.jpexs.debugger.flash.messages.in.InBreakReason;
import com.jpexs.debugger.flash.messages.in.InCallFunction;
import com.jpexs.debugger.flash.messages.in.InConstantPool;
import com.jpexs.debugger.flash.messages.in.InContinue;
import com.jpexs.debugger.flash.messages.in.InDeleteVariable;
import com.jpexs.debugger.flash.messages.in.InErrorConsole;
import com.jpexs.debugger.flash.messages.in.InErrorException;
import com.jpexs.debugger.flash.messages.in.InErrorExecLimit;
import com.jpexs.debugger.flash.messages.in.InErrorProtoLimit;
import com.jpexs.debugger.flash.messages.in.InErrorScriptStuck;
import com.jpexs.debugger.flash.messages.in.InErrorStackUnderflow;
import com.jpexs.debugger.flash.messages.in.InErrorTarget;
import com.jpexs.debugger.flash.messages.in.InErrorURLOpen;
import com.jpexs.debugger.flash.messages.in.InErrorWith;
import com.jpexs.debugger.flash.messages.in.InErrorZeroDivide;
import com.jpexs.debugger.flash.messages.in.InExit;
import com.jpexs.debugger.flash.messages.in.InFrame;
import com.jpexs.debugger.flash.messages.in.InGetActions;
import com.jpexs.debugger.flash.messages.in.InGetFncNames;
import com.jpexs.debugger.flash.messages.in.InGetSwd;
import com.jpexs.debugger.flash.messages.in.InGetSwf;
import com.jpexs.debugger.flash.messages.in.InGetVariable;
import com.jpexs.debugger.flash.messages.in.InIsolate;
import com.jpexs.debugger.flash.messages.in.InIsolateCreate;
import com.jpexs.debugger.flash.messages.in.InIsolateEnumerate;
import com.jpexs.debugger.flash.messages.in.InIsolateExit;
import com.jpexs.debugger.flash.messages.in.InNewObject;
import com.jpexs.debugger.flash.messages.in.InNotSynced;
import com.jpexs.debugger.flash.messages.in.InNumScript;
import com.jpexs.debugger.flash.messages.in.InOption;
import com.jpexs.debugger.flash.messages.in.InParam;
import com.jpexs.debugger.flash.messages.in.InPassAllExceptionsToDebugger;
import com.jpexs.debugger.flash.messages.in.InPlaceObject;
import com.jpexs.debugger.flash.messages.in.InProcessTag;
import com.jpexs.debugger.flash.messages.in.InRemoveBreakpoint;
import com.jpexs.debugger.flash.messages.in.InRemoveExceptionBreakpoint;
import com.jpexs.debugger.flash.messages.in.InRemoveObject;
import com.jpexs.debugger.flash.messages.in.InRemoveScript;
import com.jpexs.debugger.flash.messages.in.InScript;
import com.jpexs.debugger.flash.messages.in.InSetActiveIsolate;
import com.jpexs.debugger.flash.messages.in.InSetBreakpoint;
import com.jpexs.debugger.flash.messages.in.InSetExceptionBreakpoint;
import com.jpexs.debugger.flash.messages.in.InSetLocalVariables;
import com.jpexs.debugger.flash.messages.in.InSetMenuState;
import com.jpexs.debugger.flash.messages.in.InSetProperty;
import com.jpexs.debugger.flash.messages.in.InSetVariable;
import com.jpexs.debugger.flash.messages.in.InSetVariable2;
import com.jpexs.debugger.flash.messages.in.InSquelch;
import com.jpexs.debugger.flash.messages.in.InSwfInfo;
import com.jpexs.debugger.flash.messages.in.InTrace;
import com.jpexs.debugger.flash.messages.in.InUnknown;
import com.jpexs.debugger.flash.messages.in.InVersion;
import com.jpexs.debugger.flash.messages.in.InWatch;
import com.jpexs.debugger.flash.messages.in.InWatch2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author JPEXS
 */
public class InDebuggerMessage extends DebuggerMessage {

    /**
     * Enums originally extracted from shared_tcserver/tcparser.h; these
     * correspond to Flash player values that are currently in playerdebugger.h,
     * class DebugAtomType.
     */
    public int type;
    public byte[] data;

    public static final int DEFAULT_ISOLATE_ID = 1;

    protected int targetIsolate;

    private ByteArrayInputStream is;

    protected boolean wideLines = false;

    protected DebuggerConnection connection;

    protected InDebuggerMessage(DebuggerConnection c, int type, byte[] data) {
        this.data = data;
        is = new ByteArrayInputStream(data);
        connection = c;
        targetIsolate = c.activeIsolateId;
        wideLines = c.wideLines;
    }

    public static final InDebuggerMessage getInstance(DebuggerConnection c, int type, byte[] data) {
        switch (type) {
            case InUnknown.ID:
                return new InUnknown(c, data);
            case InSetMenuState.ID:
                return new InSetMenuState(c, data);
            case InSetProperty.ID:
                return new InSetProperty(c, data);
            case InExit.ID:
                return new InExit(c, data);
            case InNewObject.ID:
                return new InNewObject(c, data);
            case InRemoveObject.ID:
                return new InRemoveObject(c, data);
            case InTrace.ID:
                return new InTrace(c, data);
            case InErrorTarget.ID:
                return new InErrorTarget(c, data);
            case InErrorExecLimit.ID:
                return new InErrorExecLimit(c, data);
            case InErrorWith.ID:
                return new InErrorWith(c, data);
            case InErrorProtoLimit.ID:
                return new InErrorProtoLimit(c, data);
            case InSetVariable.ID:
                return new InSetVariable(c, data);
            case InDeleteVariable.ID:
                return new InDeleteVariable(c, data);
            case InParam.ID:
                return new InParam(c, data);
            case InPlaceObject.ID:
                return new InPlaceObject(c, data);
            case InScript.ID:
                return new InScript(c, data);
            case InAskBreakpoints.ID:
                return new InAskBreakpoints(c, data);
            case InBreakAt.ID:
                return new InBreakAt(c, data);
            case InContinue.ID:
                return new InContinue(c, data);
            case InSetLocalVariables.ID:
                return new InSetLocalVariables(c, data);
            case InSetBreakpoint.ID:
                return new InSetBreakpoint(c, data);
            case InNumScript.ID:
                return new InNumScript(c, data);
            case InRemoveScript.ID:
                return new InRemoveScript(c, data);
            case InRemoveBreakpoint.ID:
                return new InRemoveBreakpoint(c, data);
            case InNotSynced.ID:
                return new InNotSynced(c, data);
            case InErrorURLOpen.ID:
                return new InErrorURLOpen(c, data);
            case InProcessTag.ID:
                return new InProcessTag(c, data);
            case InVersion.ID:
                return new InVersion(c, data);
            case InBreakAtExt.ID:
                return new InBreakAtExt(c, data);
            case InSetVariable2.ID:
                return new InSetVariable2(c, data);
            case InSquelch.ID:
                return new InSquelch(c, data);
            case InGetVariable.ID:
                return new InGetVariable(c, data);
            case InFrame.ID:
                return new InFrame(c, data);
            case InOption.ID:
                return new InOption(c, data);
            case InWatch.ID:
                return new InWatch(c, data);
            case InGetSwf.ID:
                return new InGetSwf(c, data);
            case InGetSwd.ID:
                return new InGetSwd(c, data);
            case InErrorException.ID:
                return new InErrorException(c, data);
            case InErrorStackUnderflow.ID:
                return new InErrorStackUnderflow(c, data);
            case InErrorZeroDivide.ID:
                return new InErrorZeroDivide(c, data);
            case InErrorScriptStuck.ID:
                return new InErrorScriptStuck(c, data);
            case InBreakReason.ID:
                return new InBreakReason(c, data);
            case InGetActions.ID:
                return new InGetActions(c, data);
            case InSwfInfo.ID:
                return new InSwfInfo(c, data);
            case InConstantPool.ID:
                return new InConstantPool(c, data);
            case InErrorConsole.ID:
                return new InErrorConsole(c, data);
            case InGetFncNames.ID:
                return new InGetFncNames(c, data);
            case InCallFunction.ID:
                return new InCallFunction(c, data);
            case InWatch2.ID:
                return new InWatch2(c, data);
            case InPassAllExceptionsToDebugger.ID:
                return new InPassAllExceptionsToDebugger(c, data);
            case InBinaryOp.ID:
                return new InBinaryOp(c, data);
            case InIsolateCreate.ID:
                return new InIsolateCreate(c, data);
            case InIsolateExit.ID:
                return new InIsolateExit(c, data);
            case InIsolateEnumerate.ID:
                return new InIsolateEnumerate(c, data);
            case InSetActiveIsolate.ID:
                return new InSetActiveIsolate(c, data);
            case InIsolate.ID:
                return new InIsolate(c, data);
            case InSetExceptionBreakpoint.ID:
                return new InSetExceptionBreakpoint(c, data);
            case InRemoveExceptionBreakpoint.ID:
                return new InRemoveExceptionBreakpoint(c, data);
            default:
                return new InUnknown(c, data);
        }
    }

    public void reset() {
        is = new ByteArrayInputStream(data);
    }

    public InDebuggerMessage(DebuggerConnection c, int type) {
        this(c, type, new byte[0]);
    }

    public long readDWord() {
        int b1 = is.read();
        int b2 = is.read();
        int b3 = is.read();
        int b4 = is.read();
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + b1;
    }

    public long readLong() {
        long dw1 = readDWord();
        long dw2 = readDWord();
        return (dw2 << 32) + dw1;
    }

    public int readByte() {
        return is.read();
    }

    public int readWord() {
        int b1 = is.read();
        int b2 = is.read();
        return (b2 << 8) + b1;
    }

    public byte[] readBytes(int cnt) {
        byte[] ret = new byte[cnt];
        try {
            is.read(ret);
        } catch (IOException ex) {
            //ignore?
        }
        return ret;
    }

    public long readPtr(DebuggerConnection c) {
        if (c.sizeOfPtr == 4) {
            return readDWord();
        } else if (c.sizeOfPtr == 8) {
            return readLong();
        } else {
            return 0; //should not happen
        }
    }

    public String readString() {
        int c;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((c = is.read()) > 0) {
            baos.write(c);
        }
        if (c == -1) {
            throw new ArrayIndexOutOfBoundsException("Unterminated string");
        }
        try {
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return new String(baos.toByteArray());
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public int available() {
        return is.available();
    }

    public void exec() {

    }

    public Variable readVariable(DebuggerConnection c) {
        return readVariable(c, readString());
    }

    public Variable readRegister(DebuggerConnection c, int number) {
        int oType = readWord();
        return readAtom(c, "$" + number, oType, 0);
    }

    public Variable readVariable(DebuggerConnection c, String name) {
        int oType = readWord();
        int flags = (int) readDWord();
        return readAtom(c, name, oType, flags);
    }

    public Variable readAtom(DebuggerConnection c, String name, int oType, int flags) {
        int vType = VariableType.UNKNOWN;
        Object value = null;
        String typeName = "";
        String className = "";
        boolean isPrimitive = false;

        switch (oType) {
            case kNumberType: {
                String s = readString();
                double dval = Double.NaN;
                try {
                    dval = Double.parseDouble(s);
                } catch (NumberFormatException nfe) {
                }

                value = (Double) dval;
                isPrimitive = true;
                vType = VariableType.NUMBER;
                break;
            }

            case kBooleanType: {
                int bval = readByte();
                value = (Boolean) (bval == 0);
                isPrimitive = true;
                vType = VariableType.BOOLEAN;

                break;
            }

            case kStringType: {
                String s = readString();

                value = s;
                isPrimitive = true;
                vType = VariableType.STRING;

                break;
            }

            case kObjectType:
            case kNamespaceType: {
                long oid = readPtr(c);
                long cType = (oid == -1) ? 0 : readDWord();
                int isFnc = (oid == -1) ? 0 : readWord();
                int rsvd = (oid == -1) ? 0 : readWord();
                typeName = (oid == -1) ? "" : readString();
                /* anirudhs: Date fix for expression evaluation */
 /* Player 10.2 onwards, the typename for Date comes
                 * as <dateformat>@oid where example of date format is:
                 * <Tue Feb 7 15:41:16 GMT+0530 2012>
                 * We have to fix the typename to how it originally
                 * appeared prior to this bug which is Date@oid.
                 * Note that even player 9 did not send oType as 11,
                 * instead oType was Object where as typeName was Date.
                 * What the customer sees is expression evaluation will
                 * always try to interpret date as a number. (ECMA.defaultValue
                 * has a check for preferredType of Date to be String)
                 */
                if (typeName.startsWith("<")) {
                    int atIndex = typeName.indexOf('@');
                    String dateVal = typeName;
                    if (atIndex > -1) {
                        dateVal = typeName.substring(0, atIndex);
                    }
                    SimpleDateFormat dFormat = new SimpleDateFormat("<EEE MMM d HH:mm:ss 'GMT'z yyyy>");
                    try {
                        Date dateObj = dFormat.parse(dateVal);
                        if (dateObj != null && dateObj.getTime() != 0) {
                            oType = kDateType;
                            typeName = "Date" + typeName.substring(atIndex);
                        }
                    } catch (ParseException e) {
                        //ignore
                    }
                }

                className = classNameFor(cType, false);
                value = new Long(oid);
                vType = (isFnc == 0) ? VariableType.OBJECT : VariableType.FUNCTION;
                break;
            }

            case kMovieClipType: {
                long oid = readPtr(c);
                long cType = (oid == -1) ? 0 : readDWord();
                long rsvd = (oid == -1) ? 0 : readDWord();
                typeName = (oid == -1) ? "" : readString();
                className = classNameFor(cType, true);

                value = new Long(oid);
                vType = VariableType.MOVIECLIP;
                break;
            }

            case kNullType: {
                value = null;
                isPrimitive = true;
                vType = VariableType.NULL;
                break;
            }

            case kUndefinedType: {
                value = Value.UNDEFINED;
                isPrimitive = true;
                vType = VariableType.UNDEFINED;
                break;
            }

            case kTraitsType: {
                // This one is special: When passed to the debugger, it indicates
                // that the "variable" is not a variable at all, but rather is a
                // class name. For example, if class Y extends class X, then
                // we will send a kDTypeTraits for class Y; then we'll send all the
                // members of class Y; then we'll send a kDTypeTraits for class X;
                // and then we'll send all the members of class X. This is only
                // used by the AVM+ debugger.
                vType = VariableType.UNKNOWN;
                typeName = Value.TRAITS_TYPE_NAME;
                break;
            }

            case kReferenceType:
            case kArrayType:
            case kObjectEndType:
            case kStrictArrayType:
            case kDateType:
            case kLongStringType:
            case kUnsupportedType:
            case kRecordSetType:
            case kXMLType:
            case kTypedObjectType:
            case kAvmPlusObjectType:
            default: {
                // System.out.println("<unknown>");
                break;
            }
        }

        return new Variable(name, vType, value, typeName, className, isPrimitive, flags);
    }

    public int getTargetIsolate() {
        return targetIsolate;
    }

    public static final int decodeFile(long id) {
        return (int) (id & 0xffff);
    }

    public static final int decodeLine(long id) {
        return (int) (id >> 16 & 0xffff);
    }

    protected boolean isTraits(Variable variable) {
        if (variable.vType == VariableType.UNKNOWN
                && Value.TRAITS_TYPE_NAME.equals(variable.typeName)) {
            return true;
        }
        return false;
    }

    protected boolean showMember(Variable child) {
        if (isTraits(child)) {
            return false;
        }
        return true;
    }

}
