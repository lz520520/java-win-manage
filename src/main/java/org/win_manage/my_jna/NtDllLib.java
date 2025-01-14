/**
 * @author lz520520
 * @date 2024/12/19 16:48
 */

package org.win_manage.my_jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import org.win_manage.my_jna.MyWinNT;

public class NtDllLib {
    public static INtDll INSTANCE = Native.loadLibrary("ntdll.dll", INtDll.class, W32APIOptions.DEFAULT_OPTIONS);
    public static interface INtDll extends StdCallLibrary {
        int NtQueryInformationProcess(WinNT.HANDLE processHandle,
                                      int processInformationClass,
                                      MyWinNT.PROCESS_BASIC_INFORMATION processInformation,
                                      int processInformationLength,
                                      IntByReference returnLength);
    }
}
