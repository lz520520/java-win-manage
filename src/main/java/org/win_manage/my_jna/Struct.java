/*
 * Copyright 2022 The OSHI Project Contributors
 * SPDX-License-Identifier: MIT
 */
package org.win_manage.my_jna;

import org.win_manage.my_jna.IPHlpAPI.MIB_IFROW;
import org.win_manage.my_jna.IPHlpAPI.MIB_IF_ROW2;
import org.win_manage.my_jna.IPHlpAPI.MIB_TCPSTATS;
import org.win_manage.my_jna.IPHlpAPI.MIB_UDPSTATS;
import com.sun.jna.platform.win32.Pdh.PDH_RAW_COUNTER;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;


/**
 * Wrapper classes for JNA clases which extend {@link com.sun.jna.Structure} intended for use in try-with-resources
 * blocks.
 */
public interface Struct {


    /*
     * Windows
     */

    class CloseableMibIfRow extends MIB_IFROW implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableMibIfRow2 extends MIB_IF_ROW2 implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableMibTcpStats extends MIB_TCPSTATS implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableMibUdpStats extends MIB_UDPSTATS implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseablePdhRawCounter extends PDH_RAW_COUNTER implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseablePerformanceInformation extends PERFORMANCE_INFORMATION implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableSpDeviceInterfaceData extends SP_DEVICE_INTERFACE_DATA implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableSpDevinfoData extends SP_DEVINFO_DATA implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }

    class CloseableSystemInfo extends SYSTEM_INFO implements MyAutoCloseable {
        @Override
        public void close() {
            Util.freeMemory(getPointer());
        }
    }
}