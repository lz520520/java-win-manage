/*
 * Copyright 2022 The OSHI Project Contributors
 * SPDX-License-Identifier: MIT
 */
package org.win_manage.my_jna.win;

import com.sun.jna.NativeLong;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;


/**
 * Wrapper classes for JNA clases which extend {@link com.sun.jna.ptr.ByReference} intended for use in
 * try-with-resources blocks.
 */
public interface ByRef {

    class CloseableIntByReference extends IntByReference implements MyAutoCloseable {
        public CloseableIntByReference() {
            super();
        }

        public CloseableIntByReference(int value) {
            super(value);
        }

        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableLongByReference extends LongByReference implements MyAutoCloseable {
        public CloseableLongByReference() {
            super();
        }

        public CloseableLongByReference(long value) {
            super(value);
        }

        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableNativeLongByReference extends NativeLongByReference implements MyAutoCloseable {
        public CloseableNativeLongByReference() {
            super();
        }

        public CloseableNativeLongByReference(NativeLong nativeLong) {
            super(nativeLong);
        }

        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseablePointerByReference extends PointerByReference implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableLONGLONGByReference extends LONGLONGByReference implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableULONGptrByReference extends ULONG_PTRByReference implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableHANDLEByReference extends HANDLEByReference implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

//    class CloseableSizeTByReference extends size_t.ByReference implements MyAutoCloseable {
//        public CloseableSizeTByReference() {
//            super();
//        }
//
//        public CloseableSizeTByReference(long value) {
//            super(value);
//        }
//
//        @Override
//        public void close() {
//            Util.freeMemory(getPointer());
//        }
//    }

    class CloseablePROCESSENTRY32ByReference extends PROCESSENTRY32.ByReference implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }
}