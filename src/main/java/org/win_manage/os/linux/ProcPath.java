/**
 * @author lz520520
 * @date 2025/1/16 10:55
 */

package org.win_manage.os.linux;

public class ProcPath {
    public static final String PROC = "/proc";

    public static final String ASOUND = PROC + "/asound/";
    public static final String AUXV = PROC + "/self/auxv";
    public static final String CPUINFO = PROC + "/cpuinfo";
    public static final String DISKSTATS = PROC + "/diskstats";
    public static final String MEMINFO = PROC + "/meminfo";
    public static final String MODEL = PROC + "/device-tree/model";
    public static final String MOUNTS = PROC + "/mounts";
    public static final String NET = PROC + "/net";
    public static final String PID_CMDLINE = PROC + "/%d/cmdline";
    public static final String PID_CWD = PROC + "/%d/cwd";
    public static final String PID_EXE = PROC + "/%d/exe";
    public static final String PID_ENVIRON = PROC + "/%d/environ";
    public static final String PID_FD = PROC + "/%d/fd";
    public static final String PID_IO = PROC + "/%d/io";
    public static final String PID_STAT = PROC + "/%d/stat";
    public static final String PID_STATM = PROC + "/%d/statm";
    public static final String PID_STATUS = PROC + "/%d/status";
    public static final String SELF_STAT = PROC + "/self/stat";
    public static final String STAT = PROC + "/stat";
    public static final String SYS_FS_FILE_NR = PROC + "/sys/fs/file-nr";
    public static final String SYS_FS_FILE_MAX = PROC + "/sys/fs/file-max";
    public static final String TASK_PATH = PROC + "/%d/task";
    public static final String TASK_COMM = TASK_PATH + "/%d/comm";
    public static final String TASK_STATUS = TASK_PATH + "/%d/status";
    public static final String TASK_STAT = TASK_PATH + "/%d/stat";
    public static final String THREAD_SELF = PROC + "/thread-self";
    public static final String UPTIME = PROC + "/uptime";
    public static final String VERSION = PROC + "/version";
    public static final String VMSTAT = PROC + "/vmstat";

    private ProcPath() {
    }


}
