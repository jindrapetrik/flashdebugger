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
public abstract class DebuggerMessage {

    public abstract int getType();

    public abstract byte[] getData();

    public static final String ARGUMENTS_MARKER = "$arguments";
    public static final String SCOPE_CHAIN_MARKER = "$scopechain";

    public static final int kNumberType = 0;
    public static final int kBooleanType = 1;
    public static final int kStringType = 2;
    public static final int kObjectType = 3;
    public static final int kMovieClipType = 4;
    public static final int kNullType = 5;
    public static final int kUndefinedType = 6;
    public static final int kReferenceType = 7;
    public static final int kArrayType = 8;
    public static final int kObjectEndType = 9;
    public static final int kStrictArrayType = 10;
    public static final int kDateType = 11;
    public static final int kLongStringType = 12;
    public static final int kUnsupportedType = 13;
    public static final int kRecordSetType = 14;
    public static final int kXMLType = 15;
    public static final int kTypedObjectType = 16;
    public static final int kAvmPlusObjectType = 17;
    public static final int kNamespaceType = 18;
    public static final int kTraitsType = 19;	// Special - variable is class name or similar

    /**
     * These values are obtained directly from the Player. See ScriptObject in
     * splay.h.
     */
    public static final int kNormalObjectType = 0;
    public static final int kXMLSocketObjectType = 1;
    public static final int kTextFieldObjectType = 2;
    public static final int kButtonObjectType = 3;
    public static final int kNumberObjectType = 4;
    public static final int kBooleanObjectType = 5;
    public static final int kNativeStringObject = 6;
    public static final int kNativeArrayObject = 7;
    public static final int kDateObjectType = 8;
    public static final int kSoundObjectType = 9;
    public static final int kNativeXMLDoc = 10;
    public static final int kNativeXMLNode = 11;
    public static final int kNativeCameraObject = 12;
    public static final int kNativeMicrophoneObject = 13;
    public static final int kNativeCommunicationObject = 14;
    public static final int kNetConnectionObjectType = 15;
    public static final int kNetStreamObjectType = 16;
    public static final int kVideoObjectType = 17;
    public static final int kTextFormatObjectType = 18;
    public static final int kSharedObjectType = 19;
    public static final int kSharedObjectDataType = 20;
    public static final int kPrintJobObjectType = 21;
    public static final int kMovieClipLoaderObjectType = 22;
    public static final int kStyleSheetObjectType = 23;
    public static final int kFapPacketDummyObject = 24;
    public static final int kLoadVarsObject = 25;
    public static final int kTextSnapshotType = 26;

    public static String classNameFor(long clsType, boolean isMc) {
        String clsName;
        switch ((int) clsType) {
            case kNormalObjectType:
                clsName = (isMc) ? "MovieClip" : "Object";  //$NON-NLS-2$
                break;
            case kXMLSocketObjectType:
                clsName = "XMLSocket";
                break;
            case kTextFieldObjectType:
                clsName = "TextField";
                break;
            case kButtonObjectType:
                clsName = "Button";
                break;
            case kNumberObjectType:
                clsName = "Number";
                break;
            case kBooleanObjectType:
                clsName = "Boolean";
                break;
            case kNativeStringObject:
                clsName = "String";
                break;
            case kNativeArrayObject:
                clsName = "Array";
                break;
            case kDateObjectType:
                clsName = "Date";
                break;
            case kSoundObjectType:
                clsName = "Sound";
                break;
            case kNativeXMLDoc:
                clsName = "XML";
                break;
            case kNativeXMLNode:
                clsName = "XMLNode";
                break;
            case kNativeCameraObject:
                clsName = "Camera";
                break;
            case kNativeMicrophoneObject:
                clsName = "Microphone";
                break;
            case kNativeCommunicationObject:
                clsName = "Communication";
                break;
            case kNetConnectionObjectType:
                clsName = "Connection";
                break;
            case kNetStreamObjectType:
                clsName = "Stream";
                break;
            case kVideoObjectType:
                clsName = "Video";
                break;
            case kTextFormatObjectType:
                clsName = "TextFormat";
                break;
            case kSharedObjectType:
                clsName = "SharedObject";
                break;
            case kSharedObjectDataType:
                clsName = "SharedObjectData";
                break;
            case kPrintJobObjectType:
                clsName = "PrintJob";
                break;
            case kMovieClipLoaderObjectType:
                clsName = "MovieClipLoader";
                break;
            case kStyleSheetObjectType:
                clsName = "StyleSheet";
                break;
            case kFapPacketDummyObject:
                clsName = "FapPacket";
                break;
            case kLoadVarsObject:
                clsName = "LoadVars";
                break;
            case kTextSnapshotType:
                clsName = "TextSnapshot";
                break;
            default:
                clsName = "unknown" + "<" + clsType + ">";
                break;
        }
        return clsName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
