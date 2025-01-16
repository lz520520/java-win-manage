package org.win_manage;


import org.win_manage.os.linux.Netstat;
import org.win_manage.os.linux.NetstatInfo;
import org.win_manage.os.linux.Process;
import org.win_manage.os.linux.ProcessInfo;

import java.util.ArrayList;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Map<Long, Integer>  pidMap = Netstat.querySocketToPidMap();
        for (Map.Entry<Long, Integer> longIntegerEntry : pidMap.entrySet()) {
            System.out.printf("k: %d; v: %s %n", longIntegerEntry.getKey(), longIntegerEntry.getValue());

        }
        getNetstat();


    }
    static void getProcess() throws Exception {
        ArrayList<ProcessInfo> infos = Process.getProcesses();
        for (ProcessInfo info : infos) {
            System.out.printf("PID: %d; Owner: %s ; Name: %s; Arch: %s;  Path: %s; Command Line: %s %n",
                    info.pid,info.owner, info.name,info.arch,  info.exe, info.cmdLine);
        }
    }
    static void getNetstat() throws Exception {
        ArrayList<NetstatInfo> infos = Netstat.getNetstat();
        for (NetstatInfo info : infos) {
            System.out.printf("PID: %d; name: %s ; local: %s:%d;  remote: %s:%d %n",
                    info.pid,info.name, info.local_address, info.local_port, info.remote_address, info.remote_port);
        }
    }
}