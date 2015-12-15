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

import com.jpexs.debugger.flash.messages.in.InBreakAtExt;
import com.jpexs.debugger.flash.messages.in.InCallFunction;
import com.jpexs.debugger.flash.messages.in.InConstantPool;
import com.jpexs.debugger.flash.messages.in.InContinue;
import com.jpexs.debugger.flash.messages.in.InFrame;
import com.jpexs.debugger.flash.messages.in.InGetActions;
import com.jpexs.debugger.flash.messages.in.InGetFncNames;
import com.jpexs.debugger.flash.messages.in.InGetVariable;
import com.jpexs.debugger.flash.messages.in.InOption;
import com.jpexs.debugger.flash.messages.in.InRemoveBreakpoint;
import com.jpexs.debugger.flash.messages.in.InSetBreakpoint;
import com.jpexs.debugger.flash.messages.in.InSetVariable;
import com.jpexs.debugger.flash.messages.in.InSetVariable2;
import com.jpexs.debugger.flash.messages.in.InSquelch;
import com.jpexs.debugger.flash.messages.in.InSwfInfo;
import com.jpexs.debugger.flash.messages.in.InWatch2;
import com.jpexs.debugger.flash.messages.out.OutAddWatch2;
import com.jpexs.debugger.flash.messages.out.OutCallFunction;
import com.jpexs.debugger.flash.messages.out.OutConstantPool;
import com.jpexs.debugger.flash.messages.out.OutContinue;
import com.jpexs.debugger.flash.messages.out.OutExit;
import com.jpexs.debugger.flash.messages.out.OutGetActions;
import com.jpexs.debugger.flash.messages.out.OutGetFncNames;
import com.jpexs.debugger.flash.messages.out.OutGetFrame;
import com.jpexs.debugger.flash.messages.out.OutGetOption;
import com.jpexs.debugger.flash.messages.out.OutGetVariable;
import com.jpexs.debugger.flash.messages.out.OutGetVariableWhichInvokesGetter;
import com.jpexs.debugger.flash.messages.out.OutRemoveBreakpoints;
import com.jpexs.debugger.flash.messages.out.OutRemoveWatch2;
import com.jpexs.debugger.flash.messages.out.OutSetActiveIsolate;
import com.jpexs.debugger.flash.messages.out.OutSetBreakpoints;
import com.jpexs.debugger.flash.messages.out.OutSetOption;
import com.jpexs.debugger.flash.messages.out.OutSetSquelch;
import com.jpexs.debugger.flash.messages.out.OutSetVariable;
import com.jpexs.debugger.flash.messages.out.OutStepContinue;
import com.jpexs.debugger.flash.messages.out.OutStepInto;
import com.jpexs.debugger.flash.messages.out.OutStepOut;
import com.jpexs.debugger.flash.messages.out.OutStepOver;
import com.jpexs.debugger.flash.messages.out.OutStopDebug;
import com.jpexs.debugger.flash.messages.out.OutSwfInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JPEXS
 */
public class DebuggerCommands {

    private DebuggerConnection connection;

    public DebuggerConnection getConnection() {
        return connection;
    }

    public DebuggerCommands(DebuggerConnection connection) {
        this.connection = connection;
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void stepInto() throws IOException {
        connection.sendMessage(new OutStepInto(connection), InContinue.class);
    }

    public void stepOut() throws IOException {
        connection.sendMessage(new OutStepOut(connection), InContinue.class);

    }

    public void stepOver() throws IOException {
        connection.sendMessage(new OutStepOver(connection), InContinue.class);
    }

    public void stepContinue() throws IOException {
        connection.sendMessage(new OutStepContinue(connection), InContinue.class);

    }

    public List<InGetFncNames.FunctionName> getFunctionNames(int file, int line) throws IOException {
        if (connection.playerVersion >= 9) {
            InGetFncNames ifn = connection.sendMessage(new OutGetFncNames(connection, file, line), InGetFncNames.class);
            return ifn.names; //how about module?
        }
        return null;
    }

    public boolean removeBreakPoint(int file, int line) throws IOException {
        connection.writeMessage(new OutRemoveBreakpoints(connection, file, line));
        return true;//?
    }

    public byte[] getActions(int module, int offset, int length) throws IOException {
        InGetActions iga = connection.sendMessage(new OutGetActions(connection, module, offset, length), InGetActions.class);
        return iga.actionData; //TODO: offset?
    }

    public List<InSwfInfo.SwfInfo> getSwfInfo(int module) throws IOException {
        InSwfInfo isi = connection.sendMessage(new OutSwfInfo(connection, module), InSwfInfo.class);
        return isi.swfInfos;

    }

    public void exit(boolean requestTerminate) throws IOException {
        connection.writeMessage(new OutExit(connection, requestTerminate));

    }

    public void sendContinue() throws IOException {
        connection.sendMessageIgnoreResult(new OutContinue(connection), InContinue.class);

    }

    public InSetBreakpoint addBreakPoints(List<Integer> files, List<Integer> lines) throws IOException {
        return connection.sendMessage(new OutSetBreakpoints(connection, files, lines), InSetBreakpoint.class);
    }

    public boolean addBreakPoint(int file, int line) throws IOException {
        List<Integer> files = new ArrayList<>();
        List<Integer> lines = new ArrayList<>();
        files.add(file);
        lines.add(line);

        InSetBreakpoint bp = addBreakPoints(files, lines);
        for (int i = 0; i < bp.lines.size(); i++) {
            if (bp.files.get(i) == file && bp.lines.get(i) == line) {
                return true;
            }
        }
        return false;
    }

    public InConstantPool getConstantPool(int poolId) throws IOException {
        return connection.sendMessage(new OutConstantPool(connection, poolId), InConstantPool.class);
    }

    public InFrame getFrame(int depth) throws IOException {
        if (!connection.squelchEnabled) {
            return null;
        }
        return connection.sendMessage(new OutGetFrame(connection, depth), InFrame.class);
    }

    public InCallFunction callFunction(boolean isConstructor, String funcName, String thisType, String thisValue, List<String> argTypes, List<String> argValues) throws IOException {
        return connection.sendMessage(new OutCallFunction(connection, isConstructor, funcName, thisType, thisValue, argTypes, argValues), InCallFunction.class);
    }

    public InGetVariable getVariable(long id, String name, boolean fireGetter, boolean getChildrenToo) throws IOException {
        int flags = GetVariableFlag.DONT_GET_FUNCTIONS;
        if (fireGetter) {
            flags |= GetVariableFlag.INVOKE_GETTER;
        }
        if (getChildrenToo) {
            flags |= GetVariableFlag.ALSO_GET_CHILDREN | GetVariableFlag.GET_CLASS_HIERARCHY;
        }
        return connection.sendMessage(
                fireGetter ? new OutGetVariableWhichInvokesGetter(connection, id, name, flags) : new OutGetVariable(connection, id, name, flags), InGetVariable.class);

    }

    public void setVariable(long id, String name, int type, String value) throws IOException {
        squelch(false);
        OutSetVariable osv = new OutSetVariable(connection, id, name, Variable.typeNameFor(type), value);
        if (type == VariableType.STRING) {
            InSetVariable isv = connection.sendMessage(osv, InSetVariable.class);
        } else {
            InSetVariable2 isv = connection.sendMessage(osv, InSetVariable2.class);
        }
        //InGetVariable igv = getVariable(id, name, false, false);
        //TODO: handle two results?

        squelch(true);

    }

    public boolean squelch(boolean on) throws IOException {
        InSquelch isq = connection.sendMessage(new OutSetSquelch(connection, on), InSquelch.class);
        return isq.state;

    }

    public static class Watch {

        public String varName;
        public long varId;
        public int tag;
        public int flags;

        public Watch(String varName, long varId, int tag, int flags) {
            this.varName = varName;
            this.varId = varId;
            this.tag = tag;
            this.flags = flags;
        }
    }

    public Watch addWatch(long varId, String memberName, int flags, int tag) throws IOException {
        InWatch2 iw2 = connection.sendMessage(new OutAddWatch2(connection, varId, memberName, flags, tag), InWatch2.class);
        if (iw2.success > 0) {
            return new Watch(memberName, varId, tag, iw2.flags);
        }
        return null;
    }

    public boolean removeWatch(long varId, String memberName) throws IOException {
        InWatch2 iw2 = connection.sendMessage(new OutRemoveWatch2(connection, varId, memberName), InWatch2.class);
        return iw2.success == 1;
    }

    public void setActiveIsolate(long isolate) throws IOException {
        connection.writeMessage(new OutSetActiveIsolate(connection, isolate));
    }

    public void setOption(String name, String value) throws IOException {

        InOption io = connection.sendMessage(new OutSetOption(connection, name, value), InOption.class);
    }

    public String getOption(String name, String defaultValue) throws IOException {
        InOption io = connection.sendMessage(new OutGetOption(connection, name), InOption.class);
        String v = io.v;

        if (v.isEmpty()) {
            return defaultValue;
        }
        return v;
    }

    public void debuggerSetWideLine() throws IOException {
        setOption("wide_line_debugger", "on");
    }

    public boolean playerIsWideLine() throws IOException {
        return getOption("wide_line_player", "off").equals("on");
    }

    public boolean playerCanCallFunctions() throws IOException {
        return getOption("can_call_functions", "off").equals("on");
    }

    public boolean playerConcurrency() throws IOException {
        return getOption("concurrent_player", "off").equals("on");
    }

    public boolean playerCanBreakOnAllExceptions() throws IOException {
        return getOption("can_break_on_all_exceptions", "off").equals("on");
    }

    public boolean playerCanTerminate() throws IOException {
        return getOption("can_terminate", "off").equals("on");
    }

    public boolean playerSupportsWatchpoints() throws IOException {
        return getOption("can_set_watchpoints", "off").equals("on");
    }

    public void stopWarning() throws IOException {
        setOption("disable_script_stuck_dialog", "on"); //AS2
        setOption("disable_script_stuck", "on"); //hack for AS3
    }

    public void setOption(String name, boolean val) throws IOException {
        setOption(name, val ? "on" : "off");
    }

    public void setStopOnFault() throws IOException {
        setOption("break_on_fault", true);
    }

    public void setEnumerateOverride() throws IOException {
        setOption("enumerate_override", true);
    }

    public void setNotifyFailure() throws IOException {
        setOption("notify_on_failure", true);
    }

    public void setInvokeSetters() throws IOException {
        setOption("invoke_setters", true);
    }

    public void setSwfLoadNotify() throws IOException {
        setOption("swf_load_messages", true);
    }

    public void consoleErrorsAsTrace(boolean on) throws IOException {
        setOption("console_errors", on);
    }

    public void setGetterTimeout(int timeout) throws IOException {
        setOption("getter_timeout", "" + timeout);
    }

    public void setSetterTimeout(int timeout) throws IOException {
        setOption("setter_timeout", "" + timeout);
    }

    public boolean suspend() throws IOException {
        int tries = 30;
        while (tries > 0) {
            InBreakAtExt iba = connection.sendMessageWithTimeout(new OutStopDebug(connection), InBreakAtExt.class);
            if (iba != null) {
                return true;
            }
            tries--;
        }
        return false;
    }

}
