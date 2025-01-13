/**
 * @author lz520520
 * @date 2024/12/19 15:39
 */

package org.win_manage;


import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.nio.charset.Charset;

public class Kernel32Lib {
    public static IKernel32 INSTANCE = (IKernel32) Native.loadLibrary("kernel32.dll", IKernel32.class, W32APIOptions.UNICODE_OPTIONS);

    public static String readProcessString(WinNT.HANDLE processHandle, Pointer baseAddress, int length) {
        Pointer bufferPointer = new Memory(length);
        byte[] buffer = new byte[length];

        IntByReference bytesRead = new IntByReference();
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(processHandle, baseAddress, bufferPointer, length, bytesRead);
        bufferPointer.read(0, buffer, 0, bytesRead.getValue());
        return success ?new String(buffer, Charset.forName("UTF-16LE")): null;
    }


    static interface IKernel32 extends StdCallLibrary {
        WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD dwFlags, WinDef.DWORD th32ProcessID);
        boolean Process32First(WinNT.HANDLE hsnapshot, Tlhelp32.PROCESSENTRY32 lppe);
        boolean Process32Next(WinNT.HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32 lppe);
        WinNT.HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);


        boolean PeekNamedPipe(WinNT.HANDLE param1HANDLE, byte[] param1ArrayOfbyte, int param1Int, IntByReference param1IntByReference1, IntByReference param1IntByReference2, IntByReference param1IntByReference3);

        boolean CreatePipe(WinNT.HANDLEByReference param1HANDLEByReference1, WinNT.HANDLEByReference param1HANDLEByReference2, WinBase.SECURITY_ATTRIBUTES param1SECURITY_ATTRIBUTES, int param1Int);

        boolean SetHandleInformation(WinNT.HANDLE param1HANDLE, int param1Int1, int param1Int2);

        boolean CreateProcess(String param1String1, String param1String2, WinBase.SECURITY_ATTRIBUTES param1SECURITY_ATTRIBUTES1, WinBase.SECURITY_ATTRIBUTES param1SECURITY_ATTRIBUTES2, boolean param1Boolean, WinDef.DWORD param1DWORD, Pointer param1Pointer, String param1String3, WinBase.STARTUPINFO param1STARTUPINFO, WinBase.PROCESS_INFORMATION param1PROCESS_INFORMATION);

        Pointer VirtualAllocEx(WinNT.HANDLE param1HANDLE, Pointer param1Pointer, BaseTSD.SIZE_T param1SIZE_T, int param1Int1, int param1Int2);

        boolean WriteProcessMemory(WinNT.HANDLE param1HANDLE, Pointer param1Pointer1, Pointer param1Pointer2, int param1Int, IntByReference param1IntByReference);

        WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE param1HANDLE, WinBase.SECURITY_ATTRIBUTES param1SECURITY_ATTRIBUTES, int param1Int1, Pointer param1Pointer1, Pointer param1Pointer2, int param1Int2, WinDef.DWORDByReference param1DWORDByReference);

        boolean CloseHandle(WinNT.HANDLE hObject);

        int GetLastError();

        boolean ReadFile(WinNT.HANDLE param1HANDLE, byte[] param1ArrayOfbyte, int param1Int, IntByReference param1IntByReference, WinBase.OVERLAPPED param1OVERLAPPED);

        boolean TerminateProcess(WinNT.HANDLE param1HANDLE, int param1Int);

        boolean VirtualProtect(Pointer param1Pointer, BaseTSD.SIZE_T param1SIZE_T, WinDef.DWORD param1DWORD, WinDef.DWORDByReference param1DWORDByReference);

        boolean CreateThread(WinBase.SECURITY_ATTRIBUTES param1SECURITY_ATTRIBUTES, int param1Int1, Pointer param1Pointer1, Pointer param1Pointer2, int param1Int2, WinDef.DWORDByReference param1DWORDByReference);
    }
}
