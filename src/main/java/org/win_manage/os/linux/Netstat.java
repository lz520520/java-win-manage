/**
 * @author lz520520
 * @date 2025/1/14 10:31
 */

package org.win_manage.os.linux;


import org.win_manage.my_jna.linux.Libc;
import org.win_manage.util.InternetProtocolStats;
import org.win_manage.util.MyPair;
import org.win_manage.util.ParseUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Netstat {
    public static final Pattern DIGITS = Pattern.compile("\\d+");

    private static final Pattern SOCKET = Pattern.compile("socket:\\[(\\d+)\\]");


    public List<InternetProtocolStats.IPConnection> getConnections() {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        Map<Long, Integer> pidMap = querySocketToPidMap();
        conns.addAll(queryConnections("tcp", 4, pidMap));
        conns.addAll(queryConnections("tcp", 6, pidMap));
        conns.addAll(queryConnections("udp", 4, pidMap));
        conns.addAll(queryConnections("udp", 6, pidMap));
        return conns;
    }
    public static File[] getPidFiles() {
        return listNumericFiles("/proc");
    }
    private static File[] listNumericFiles(String path) {
        File directory = new File(path);
        File[] numericFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return DIGITS.matcher(file.getName()).matches();
            }
        });
        return numericFiles == null ? new File[0] : numericFiles;
    }
    public static File[] getFileDescriptorFiles(int pid) {
        return listNumericFiles(String.format(Locale.ROOT, ProcPath.PID_FD, pid));
    }
    public static Map<Long, Integer> querySocketToPidMap() {
        Map<Long, Integer> pidMap = new HashMap();
        for (File f : getPidFiles()) {
            int pid = ParseUtil.parseIntOrDefault(f.getName(), -1);
            File[] fds = getFileDescriptorFiles(pid);
            for (File fd : fds) {
                String symLink = readlink(fd.getAbsolutePath());
                if (symLink != null) {
                    Matcher m = SOCKET.matcher(symLink);
                    if (m.matches()) {
                        pidMap.put(ParseUtil.parseLongOrDefault(m.group(1), -1L), pid);
                    }
                }
            }
        }
        return pidMap;
    }
    static String readlink(String path) {
        byte[] buffer = new byte[4096];
        int result = Libc.INSTANCE.readlink(path, buffer, buffer.length);
        if (result >= 0) {
            return  new String(buffer, 0, result);
        }
        return null;
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
    static List<String> readFileLines(String filename){
        List<String> lines = new ArrayList<String>();
        try {
            String data = ReadFile(filename);
            String[]  tmp = data.split("\n");
            for (String s : tmp) {
                s = s.trim();
                if (s.isEmpty()) {
                    continue;
                }
                lines.add(s);
            }
        }catch (Exception ignored) {
            
        }
        return lines;
    }
    static String getName(int pid)  {
        try {
            String data = ReadFile(String.format("/proc/%d/stat", pid));
            int binStart = data.indexOf("(") + 1;
            if (binStart  <= 0) {
                return "";
            }

            int binEnd = data.substring(binStart).indexOf(")");
            if (binEnd  <= 0) {
                return "";
            }
            return data.substring(binStart, binStart+ binEnd);
        } catch (Exception e) {
            return "";
        }
    }

    private static List<InternetProtocolStats.IPConnection> queryConnections(String protocol, int ipver, Map<Long, Integer> pidMap) {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        for (String s : readFileLines(ProcPath.NET + "/" + protocol + (ipver == 6 ? "6" : ""))) {
            if (s.indexOf(':') >= 0) {
                String[] split = ParseUtil.whitespaces.split(s.trim());
                if (split.length > 9) {
                    MyPair<byte[], Integer> lAddr = parseIpAddr(split[1]);
                    MyPair<byte[], Integer> fAddr = parseIpAddr(split[2]);
                    InternetProtocolStats.TcpState state = stateLookup(ParseUtil.hexStringToInt(split[3], 0));
                    MyPair<Integer, Integer> txQrxQ = parseHexColonHex(split[4]);
                    long inode = ParseUtil.parseLongOrDefault(split[9], 0);
                    int pid = -1;
                    try {
                        pid = pidMap.get(inode);
                    }catch (Exception ignored) {

                    }
//                    lAddr.getFirst();
//                    lAddr.getSecond();
//                    fAddr.getFirst();
//                    fAddr.getSecond();
//                    txQrxQ.getFirst();
//                    txQrxQ.getSecond();

                    conns.add(new InternetProtocolStats.IPConnection(protocol + ipver, lAddr.getFirst(), lAddr.getSecond(), fAddr.getFirst(), fAddr.getSecond(),
                            state, txQrxQ.getFirst(), txQrxQ.getSecond(), pid, getName(pid)));
                }
            }
        }
        return conns;
    }

    private static MyPair<byte[], Integer> parseIpAddr(String s) {
        int colon = s.indexOf(':');
        if (colon > 0 && colon < s.length()) {
            byte[] first = ParseUtil.hexStringToByteArray(s.substring(0, colon));
            // Bytes are in __be32 endianness. we must invert each set of 4 bytes
            for (int i = 0; i + 3 < first.length; i += 4) {
                byte tmp = first[i];
                first[i] = first[i + 3];
                first[i + 3] = tmp;
                tmp = first[i + 1];
                first[i + 1] = first[i + 2];
                first[i + 2] = tmp;
            }
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new MyPair(first, second);
        }
        return new MyPair(new byte[0], 0);
    }

    private static MyPair<Integer, Integer> parseHexColonHex(String s) {
        int colon = s.indexOf(':');
        if (colon > 0 && colon < s.length()) {
            int first = ParseUtil.hexStringToInt(s.substring(0, colon), 0);
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new MyPair(first, second);
        }
        return new MyPair(0, 0);
    }

    private static InternetProtocolStats.TcpState stateLookup(int state) {
        switch (state) {
            case 0x01:
                return InternetProtocolStats.TcpState.ESTABLISHED;
            case 0x02:
                return InternetProtocolStats.TcpState.SYN_SENT;
            case 0x03:
                return InternetProtocolStats.TcpState.SYN_RECV;
            case 0x04:
                return InternetProtocolStats.TcpState.FIN_WAIT_1;
            case 0x05:
                return InternetProtocolStats.TcpState.FIN_WAIT_2;
            case 0x06:
                return InternetProtocolStats.TcpState.TIME_WAIT;
            case 0x07:
                return InternetProtocolStats.TcpState.CLOSED;
            case 0x08:
                return InternetProtocolStats.TcpState.CLOSE_WAIT;
            case 0x09:
                return InternetProtocolStats.TcpState.LAST_ACK;
            case 0x0A:
                return InternetProtocolStats.TcpState.LISTEN;
            case 0x0B:
                return InternetProtocolStats.TcpState.CLOSING;
            case 0x00:
            default:
                return InternetProtocolStats.TcpState.UNKNOWN;
        }
    }
    public static ArrayList<NetstatInfo> getNetstat() {
        Netstat netstat = new Netstat();
        List<InternetProtocolStats.IPConnection> conns = netstat.getConnections();
        ArrayList<NetstatInfo> infos = new ArrayList<NetstatInfo>();
        for (InternetProtocolStats.IPConnection conn : conns) {
            NetstatInfo info = new NetstatInfo();
            info.pid = conn.getOwningProcessId();
            info.name = conn.getName();
            try {
                info.local_address = InetAddress.getByAddress(conn.getLocalAddress()).getHostAddress();
            }catch (Exception ignored) {

            }
            info.local_port = conn.getLocalPort();
            try {
                info.remote_address = InetAddress.getByAddress(conn.getForeignAddress()).getHostAddress();
            }catch (Exception ignored) {
            }
            info.remote_port = conn.getForeignPort();
            info.protocol = conn.getType();
            info.status = conn.getState().toString();
            infos.add(info);
        }
        return infos;
    }
}
