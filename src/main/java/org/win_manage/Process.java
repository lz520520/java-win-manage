/**
 * @author lz520520
 * @date 2024/12/19 15:38
 */

package org.win_manage;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.win_manage.my_jna.Kernel32Lib;
import org.win_manage.my_jna.MyWinNT;
import org.win_manage.my_jna.NtDllLib;

import java.util.ArrayList;


public class Process {
    public static ArrayList<ProcessInfo> getProcesses() throws Exception {
        try {
            enableDebugPrivilege();
        }catch (Exception ignored){

        }
        ArrayList<ProcessInfo> infos = new ArrayList<ProcessInfo>();
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        if (snapshot == null) {
            throw new Exception("Failed to create snapshot");
        }
        Tlhelp32.PROCESSENTRY32 processEntry = new Tlhelp32.PROCESSENTRY32();
        if (Kernel32.INSTANCE.Process32First(snapshot, processEntry)) {
            do {
                String processName = Native.toString(processEntry.szExeFile);
                int processId = processEntry.th32ProcessID.intValue();

                // Get process file path
                String processPath = getExe(processId);
                // Get process command line
                String commandLine = getProcessCommandLine(processId);
                String owner = getProcessAccount(processId);

                ProcessInfo info = new ProcessInfo();
                info.pid = processId;
                info.ppid = processEntry.th32ParentProcessID.intValue();
                info.name = processName;
                info.exe = processPath;
                info.cmdLine = commandLine;
                info.owner = owner;
                info.arch = getArch(processId);
                info.sessionID = getSession(processId);
                infos.add(info);



            } while (Kernel32.INSTANCE.Process32Next(snapshot, processEntry));
        }
        Kernel32.INSTANCE.CloseHandle(snapshot);
        return  infos;
    }
    static int getSession(int pid) {
        IntByReference session = new IntByReference(-1);
        Kernel32.INSTANCE.ProcessIdToSessionId(pid, session);
        return session.getValue();
    }
    public static String getExe(int processId) {
        WinNT.HANDLE processHandle = null;
        String exe = "";
        try {
            processHandle = Kernel32.INSTANCE.OpenProcess(
                    WinNT.PROCESS_QUERY_INFORMATION , false, processId);
            if (processHandle == null) {
                throw new Exception("OpenProcess");
            }
            char[] path = new char[WinDef.MAX_PATH];
            Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, path, path.length);
            exe = Native.toString(path);
        }catch (Exception ignored) {

        }finally {
            if (processHandle != null) {
                Kernel32.INSTANCE.CloseHandle(processHandle);
            }
        }


        return exe;
    }

    private static String getProcessCommandLine(int processId) {
        WinNT.HANDLE processHandle = null;
        WinNT.HANDLE snapshot = null;
        String commandLine = "";
        try {
            snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD(processId));
            Tlhelp32.MODULEENTRY32W module = new Tlhelp32.MODULEENTRY32W();

            if (!Kernel32.INSTANCE.Module32FirstW(snapshot, module)){
                throw new Exception("Module32FirstW");
            }
            processHandle = Kernel32.INSTANCE.OpenProcess(
                    WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, processId);

            if (processHandle == null) {
                commandLine = module.szExePath();
                throw new Exception("OpenProcess");
            }
            MyWinNT.PEB peb = getPEB(processHandle);
            if (peb == null) {
                commandLine = module.szExePath();
                throw new Exception("getPEB");
            }
            commandLine = getCommandLineFromPEB(peb, processHandle);
        }catch (Exception ignored) {

        }finally {
            if (processHandle != null) {
                Kernel32.INSTANCE.CloseHandle(processHandle);
            }
            if (snapshot != null) {
                Kernel32.INSTANCE.CloseHandle(snapshot);
            }
        }


        return commandLine;
    }

    public static MyWinNT.PEB getPEB(WinNT.HANDLE processHandle) {
        MyWinNT.PROCESS_BASIC_INFORMATION pbi = new MyWinNT.PROCESS_BASIC_INFORMATION();
        IntByReference returnLength = new IntByReference();
        int status = NtDllLib.INSTANCE.NtQueryInformationProcess(processHandle,
                0, pbi, pbi.size(), returnLength);

        if (status != 0) {
            return null;
        }

        // 创建缓冲区存储远程 PEB 数据
        Memory pebBuffer = new Memory(new MyWinNT.PEB().size());
        IntByReference bytesRead = new IntByReference();

        // 读取远程进程的 PEB 数据
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(
                processHandle,
                pbi.PebBaseAddress,
                pebBuffer,
                (int) pebBuffer.size(),
                bytesRead
        );
        if (!success) {
            return null;
        }
        MyWinNT.PEB peb = new MyWinNT.PEB(pebBuffer);
        peb.read();

        return peb;
    }

    public static String getCommandLineFromPEB(MyWinNT.PEB peb, WinNT.HANDLE processHandle) {
        if (peb == null || peb.ProcessParameters == null) {
            return null;
        }

        // 创建缓冲区存储远程 PEB 数据
        Memory buffer = new Memory(new MyWinNT.RTL_USER_PROCESS_PARAMETERS().size());
        IntByReference bytesRead = new IntByReference();

        // 读取远程进程的 PEB 数据
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(
                processHandle,
                peb.ProcessParameters,
                buffer,
                (int) buffer.size(),
                bytesRead
        );
        if (!success) {
            return null;
        }
        MyWinNT.RTL_USER_PROCESS_PARAMETERS parameters = new MyWinNT.RTL_USER_PROCESS_PARAMETERS(buffer);
        parameters.read();

        return Kernel32Lib.readProcessString(processHandle, parameters.CommandLine.Buffer, parameters.CommandLine.Length);
    }
    public static String getProcessAccount(int pid) {
        String account = "";
        WinNT.HANDLE hToken = null;
        WinNT.HANDLE hProcess = null;
        try {
            // Open the process
            hProcess = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION /* PROCESS_QUERY_INFORMATION */, false, pid);
            // Get the process token
            WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
            if (!Advapi32.INSTANCE.OpenProcessToken(hProcess, WinNT.TOKEN_QUERY, phToken)) {
                throw new Exception("OpenProcessToken");
            }
            hToken =  phToken.getValue();
            // Get the token user information
            WinNT.TOKEN_USER tokenUser = new WinNT.TOKEN_USER(255);
            IntByReference dwSize = new IntByReference(0);
            if (!Advapi32.INSTANCE.GetTokenInformation(hToken, WinNT.TOKEN_INFORMATION_CLASS.TokenUser /* TokenUser */, tokenUser, 255, dwSize)) {
                throw new Exception("GetTokenInformation");
            }

            // Get the SID from the token
            WinNT.PSID  pSid = tokenUser.User.Sid;

            // Get the account name from the SID
            char[] lpName = new char[WinDef.MAX_PATH];
            char[] lpDomain = new char[WinDef.MAX_PATH];
            IntByReference cchName = new IntByReference(WinDef.MAX_PATH);
            IntByReference cchDomain = new IntByReference(WinDef.MAX_PATH);
            PointerByReference peUse = new PointerByReference();

            if (!Advapi32.INSTANCE.LookupAccountSid(null, pSid, lpName, cchName, lpDomain, cchDomain, peUse)) {
                throw new Exception("LookupAccountSid");
            }

            // Convert the result to string
            String accountName = Native.toString(lpName).trim();
            String domain = Native.toString(lpDomain).trim();
            account = String.format("%s\\%s",domain, accountName);
        }catch (Exception ignored) {

        }finally {
            if (hProcess != null){
                Kernel32.INSTANCE.CloseHandle(hProcess);
            }
            if (hToken != null){
                Kernel32.INSTANCE.CloseHandle(hToken);
            }
        }

        return account;
    }

    public static String getArch(int pid) {
        String arch = "";
        WinNT.HANDLE hProcess = null;
        try {
            hProcess = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION /* PROCESS_QUERY_INFORMATION */, false, pid);
            if (hProcess == null) {
                hProcess = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_LIMITED_INFORMATION /* PROCESS_QUERY_INFORMATION */, false, pid);
            }
            IntByReference isWow64 = new IntByReference();

            if (Kernel32.INSTANCE.IsWow64Process(hProcess, isWow64)) {
                if (isWow64.getValue() != 0) {
                    arch = "x86";
                } else {
                    arch = "x86_64";
                }
            }

        }catch (Exception ignored) {

        }finally {
            if (hProcess != null){
                Kernel32.INSTANCE.CloseHandle(hProcess);
            }
        }
        return arch;
    }
    public static boolean enableDebugPrivilege() {
        boolean status = false;
        WinNT.HANDLEByReference phToken = null;
        try {
            // Get the process token
            phToken = new WinNT.HANDLEByReference();
            if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), WinNT.TOKEN_QUERY | WinNT.TOKEN_ADJUST_PRIVILEGES, phToken)) {
                throw new Exception("Failed to open process token.");
            }

            // Prepare the LUID for SE_DEBUG_NAME
            WinNT.LUID luid = new WinNT.LUID();
            if (!Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege", luid)) {
                throw new Exception("Failed to look up privilege value.");
            }

            // Set up the privilege structure
            WinNT.TOKEN_PRIVILEGES privileges = new WinNT.TOKEN_PRIVILEGES(1);
            privileges.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED));

            // Enable the SE_DEBUG_NAME privilege
            status = Advapi32.INSTANCE.AdjustTokenPrivileges(phToken.getValue(), false, privileges, 0, null, null);
        } catch (Exception ignored) {

        }finally {
            if (phToken != null) {
                Kernel32.INSTANCE.CloseHandle(phToken.getValue());
            }
        }

        return status;
    }

}

