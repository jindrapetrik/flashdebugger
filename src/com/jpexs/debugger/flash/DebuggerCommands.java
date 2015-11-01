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

import com.jpexs.debugger.flash.messages.in.InCallFunction;
import com.jpexs.debugger.flash.messages.in.InContinue;
import com.jpexs.debugger.flash.messages.in.InFrame;
import com.jpexs.debugger.flash.messages.in.InGetActions;
import com.jpexs.debugger.flash.messages.in.InGetFncNames;
import com.jpexs.debugger.flash.messages.in.InGetVariable;
import com.jpexs.debugger.flash.messages.in.InOption;
import com.jpexs.debugger.flash.messages.in.InSetBreakpoint;
import com.jpexs.debugger.flash.messages.in.InSetVariable;
import com.jpexs.debugger.flash.messages.in.InSetVariable2;
import com.jpexs.debugger.flash.messages.in.InSquelch;
import com.jpexs.debugger.flash.messages.in.InSwfInfo;
import com.jpexs.debugger.flash.messages.in.InWatch2;
import com.jpexs.debugger.flash.messages.out.OutAddWatch2;
import com.jpexs.debugger.flash.messages.out.OutCallFunction;
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

    public DebuggerCommands(DebuggerConnection connection) {
        this.connection = connection;
    }

    public void stepInto() {
        try {
            connection.sendMessage(new OutStepInto(connection), InContinue.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stepOut() {
        try {
            connection.sendMessage(new OutStepOut(connection), InContinue.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stepOver() {
        try {
            connection.sendMessage(new OutStepOver(connection), InContinue.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stepContinue() {
        try {
            connection.sendMessage(new OutStepContinue(connection), InContinue.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<InGetFncNames.FunctionName> getFunctionNames(int file, int line) {
        if (connection.playerVersion >= 9) {
            try {
                InGetFncNames ifn = connection.sendMessage(new OutGetFncNames(connection, file, line), InGetFncNames.class);
                return ifn.names; //how about module?
            } catch (IOException ex) {
                Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public void removeBreakPoint(int file, int line) {
        try {
            connection.writeMessage(new OutRemoveBreakpoints(connection, file, line));
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] getActions(int module, int offset, int length) {
        try {
            InGetActions iga = connection.sendMessage(new OutGetActions(connection, module, offset, length), InGetActions.class);
            return iga.actionData; //TODO: offset?
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<InSwfInfo.SwfInfo> getSwfInfo(int module) {
        try {
            InSwfInfo isi = connection.sendMessage(new OutSwfInfo(connection, module), InSwfInfo.class);
            return isi.swfInfos;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void exit(boolean requestTerminate) {
        try {
            connection.writeMessage(new OutExit(connection, requestTerminate));
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendContinue() {
        try {
            connection.sendMessageIgnoreResult(new OutContinue(connection), InContinue.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean addBreakPoint(int file, int line) {
        try {
            List<Integer> files = new ArrayList<>();
            files.add(file);
            List<Integer> lines = new ArrayList<>();
            lines.add(line);
            InSetBreakpoint bp = connection.sendMessage(new OutSetBreakpoints(connection, files, lines), InSetBreakpoint.class);
            for (int i = 0; i < bp.lines.size(); i++) {
                if (bp.files.get(i) == file && bp.lines.get(i) == line) {
                    return true;
                }
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public InFrame getFrame(int depth) {
        if (!connection.squelchEnabled) {
            return null;
        }
        try {
            return connection.sendMessage(new OutGetFrame(connection, depth), InFrame.class);

        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public InCallFunction callFunction(boolean isConstructor, String funcName, String thisType, String thisValue, List<String> argTypes, List<String> argValues) {
        try {
            return connection.sendMessage(new OutCallFunction(connection, isConstructor, funcName, thisType, thisValue, argTypes, argValues), InCallFunction.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public InGetVariable getVariable(long id, String name, boolean fireGetter, boolean getChildrenToo) {
        int flags = GetVariableFlag.DONT_GET_FUNCTIONS;
        if (fireGetter) {
            flags |= GetVariableFlag.INVOKE_GETTER;
        }
        if (getChildrenToo) {
            flags |= GetVariableFlag.ALSO_GET_CHILDREN | GetVariableFlag.GET_CLASS_HIERARCHY;
        }
        try {
            return connection.sendMessage(
                    fireGetter ? new OutGetVariableWhichInvokesGetter(connection, id, name, flags) : new OutGetVariable(connection, id, name, flags), InGetVariable.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setVariable(long id, String name, int type, String value) {
        squelch(false);
        OutSetVariable osv = new OutSetVariable(connection, id, name, Variable.typeNameFor(type), value);
        try {
            if (type == VariableType.STRING) {
                InSetVariable isv = connection.sendMessage(osv, InSetVariable.class);
            } else {
                InSetVariable2 isv = connection.sendMessage(osv, InSetVariable2.class);
            }
            //TODO: handle two results?
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        squelch(true);

    }

    public boolean squelch(boolean on) {
        try {
            InSquelch isq = connection.sendMessage(new OutSetSquelch(connection, on), InSquelch.class);
            return isq.state;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;//??
    }

    public boolean addWatch(long varId, String varName, int flags, int tag) {
        try {
            InWatch2 iw2 = connection.sendMessage(new OutAddWatch2(connection, varId, varName, flags, tag), InWatch2.class);
            return iw2.success == 1;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeWatch(long varId, String memberName) {
        try {
            InWatch2 iw2 = connection.sendMessage(new OutRemoveWatch2(connection, varId, memberName), InWatch2.class);
            return iw2.success == 1;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void setActiveIsolate(long isolate) {
        try {
            connection.writeMessage(new OutSetActiveIsolate(connection, isolate));
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setOption(String name, String value) {
        try {
            InOption io = connection.sendMessage(new OutSetOption(connection, name, value), InOption.class);
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getOption(String name, String defaultValue) {
        String v = defaultValue;
        try {
            InOption io = connection.sendMessage(new OutGetOption(connection, name), InOption.class);
            v = io.v;
        } catch (IOException ex) {
            Logger.getLogger(DebuggerCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        return v;
    }

    public void debuggerSetWideLine() {
        setOption("wide_line_debugger", "on");
    }

    public boolean playerIsWideLine() {
        return getOption("wide_line_player", "off").equals("on");
    }

    public boolean playerCanCallFunctions() {
        return getOption("can_call_functions", "off").equals("on");
    }

    public boolean playerConcurrency() {
        return getOption("concurrent_player", "off").equals("on");
    }

    public boolean playerCanBreakOnAllExceptions() {
        return getOption("can_break_on_all_exceptions", "off").equals("on");
    }

    public boolean playerCanTerminate() {
        return getOption("can_terminate", "off").equals("on");
    }

    public boolean playerSupportsWatchpoints() {
        return getOption("can_set_watchpoints", "off").equals("on");
    }

    public void stopWarning() {
        setOption("disable_script_stuck_dialog", "on"); //AS2
        setOption("disable_script_stuck", "on"); //hack for AS3
    }

    public void setOption(String name, boolean val) {
        setOption(name, val ? "on" : "off");
    }

    public void setStopOnFault() {
        setOption("break_on_fault", true);
    }

    public void setEnumerateOverride() {
        setOption("enumerate_override", true);
    }

    public void setNotifyFailure() {
        setOption("notify_on_failure", true);
    }

    public void setInvokeSetters() {
        setOption("invoke_setters", true);
    }

    public void setSwfLoadNotify() {
        setOption("swf_load_messages", true);
    }

    public void consoleErrorsAsTrace(boolean on) {
        setOption("console_errors", on);
    }

    public void setGetterTimeout(int timeout) {
        setOption("getter_timeout", "" + timeout);
    }

    public void setSetterTimeout(int timeout) {
        setOption("setter_timeout", "" + timeout);
    }

}
