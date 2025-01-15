package org.win_manage;


import org.win_manage.os.linux.Process;
import org.win_manage.os.linux.ProcessInfo;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {

        ArrayList<ProcessInfo> infos = Process.getProcesses();
        for (ProcessInfo info : infos) {
            System.out.printf("PID: %d; Owner: %s ; Name: %s; Arch: %s;  Path: %s; Command Line: %s %n",
                    info.pid,info.owner, info.name,info.arch,  info.exe, info.cmdLine);
        }

    }

}