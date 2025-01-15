/**
* @author lz520520
* @date 2025/1/14 17:38
*/

package org.win_manage.os.linux;



import org.win_manage.my_jna.linux.Libc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {
    static ProcessInfo getProcess(int pid)  {
        try {
            String data = ReadFile(String.format("/proc/%d/stat", pid));
            int binStart = data.indexOf("(") + 1;
            if (binStart  <= 0) {
                return null;
            }

            int binEnd = data.substring(binStart).indexOf(")");
            if (binEnd  <= 0) {
                return null;
            }
            ProcessInfo info = new ProcessInfo();
            info.name =  data.substring(binStart, binStart+ binEnd);

            data = data.substring(binStart + binEnd + 2);
            String[] tmp = data.split(" ");
            if ( tmp.length < 4 ) {
                return info;
            }
            info.pid = pid;
            info.ppid = Integer.parseInt(tmp[1]);
            return info;

        } catch (Exception e) {
            return null;
        }
    }
    public static String getProcessArchitecture(String pid) {
        String exePath = String.format("/proc/%s/exe", pid);
        File file = new File(exePath);

        if (!file.exists() || !file.canRead()) {
            return null;
        }
        RandomAccessFile raf = null;
        try  {
            raf = new RandomAccessFile(file, "r");
            // 跳到 ELF 文件头的机器类型字段 (0x12)
            raf.seek(0x12);

            // 读取 2 字节的机器类型
            byte[] mach = new byte[2];
            int bytesRead = raf.read(mach);

            if (bytesRead < 2) {
                return null;
            }

            // 根据 ELF 标识判断架构
            if ((mach[0] & 0xFF) == 0xB3) {
                return "aarch64"; // ARM 64
            } else if ((mach[0] & 0xFF) == 0x03) {
                return "x86"; // x86
            } else if ((mach[0] & 0xFF) == 0x3E) {
                return "x86_64"; // x86_64
            }
        } catch (Exception ignored) {}finally {
            if (raf != null) {
                try {
                    raf.close();
                }catch (Exception ignored) {}
            }
        }
        return "";
    }

    public static ArrayList<ProcessInfo> getProcesses() throws Exception {
        ArrayList<ProcessInfo> infos = new ArrayList<ProcessInfo>();
        String procDirectoryPath = "/proc";
        File procDirectory = new File(procDirectoryPath);

        Pattern pidPattern = Pattern.compile("^\\d+$");
        Pattern uidPattern = Pattern.compile("\\bUid:\\s(\\d+)");

        Map<String, String> uidToUsernameMap = getUserMapping();


        if (procDirectory.exists() && procDirectory.isDirectory()) {
            File[] files = procDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isDirectory()) {
                        continue;
                    }
                    String pid = file.getName();
                    if (!pidPattern.matcher(pid).matches()) {
                        continue;
                    }
                    try {
                        ProcessInfo info = getProcess(Integer.parseInt(pid));
                        if (info == null) {
                            continue;
                        }
                        try {
                            byte[] buffer = new byte[4096];
                            int result = Libc.INSTANCE.readlink(String.format("/proc/%s/exe", pid), buffer, buffer.length);
                            if (result >= 0) {
                                info.exe = new String(buffer, 0, result);
                            }
                        }catch (Exception ignored) {

                        }
                        info.arch = getProcessArchitecture(pid);
                        try {
                            String status = ReadFile(String.format("/proc/%s/status", pid));
                            Matcher matcher =  uidPattern.matcher(status);
                            if (matcher.find()) {
                                info.owner = uidToUsernameMap.get(matcher.group(1));

                            }
                        }catch (Exception ignored) {
                        }
                        try {
                            info.cmdLine  = ReadFile(String.format("/proc/%s/cmdline", pid)).replaceAll("\\x00", " ");
                        } catch (Exception ignored) {}


                        infos.add(info);

                    }catch (Exception ignored) {

                    }



                }
            }
        }

        return  infos;
    }
    static Map<String, String> getUserMapping() {
        Map<String, String> uidToUsernameMap = new HashMap();

        try  {
            BufferedReader reader = new BufferedReader(new FileReader("/etc/passwd"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(":");
                if (fields.length >= 3) {
                    String username = fields[0];
                    String uid = fields[2];
                    uidToUsernameMap.put(uid, username);
                }
            }
        } catch (IOException e) {
        }

        return uidToUsernameMap;
    }
    static String ReadFile(String filename) throws Exception {

        FileInputStream in=new FileInputStream(filename);
        ByteArrayOutputStream out=new ByteArrayOutputStream(1024);
        byte[] temp=new byte[1024];
        int size=0;
        while((size=in.read(temp))!=-1) {
            out.write(temp,0,size);
        }
        in.close();
        return out.toString();

    }

}
