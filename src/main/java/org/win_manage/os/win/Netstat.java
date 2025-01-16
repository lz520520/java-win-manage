/**
 * @author lz520520
 * @date 2025/1/14 10:31
 */

package org.win_manage.os.win;


import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import org.win_manage.my_jna.win.*;
import org.win_manage.util.InternetProtocolStats;
import org.win_manage.util.ParseUtil;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.win_manage.my_jna.win.IPHlpAPI.AF_INET;
import static org.win_manage.my_jna.win.IPHlpAPI.AF_INET6;
import static org.win_manage.my_jna.win.IPHlpAPI.UDP_TABLE_CLASS.UDP_TABLE_OWNER_PID;
import static org.win_manage.my_jna.win.MyWinNT.TCP_TABLE_OWNER_PID_ALL;


public class Netstat {
    private static final IPHlpAPI IPHLP = IPHlpAPI.INSTANCE;

    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();

    public InternetProtocolStats.TcpStats getTCPv4Stats() {
        Struct.CloseableMibTcpStats stats = null;
        try {
            stats = new Struct.CloseableMibTcpStats();
            IPHLP.GetTcpStatisticsEx(stats, AF_INET);
            InternetProtocolStats.TcpStats tcpStats =  new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails,
                    stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs,
                    stats.dwOutRsts);
            return tcpStats;
        }finally {
            if (stats != null) {
                stats.close();
            }

        }

    }

    public InternetProtocolStats.TcpStats getTCPv6Stats() {
        Struct.CloseableMibTcpStats stats = null;
        try {
            stats = new Struct.CloseableMibTcpStats();
            IPHLP.GetTcpStatisticsEx(stats, AF_INET6);
            return new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails,
                    stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs,
                    stats.dwOutRsts);
        }finally {
            if (stats != null) {
                stats.close();
            }

        }
    }

    public InternetProtocolStats.UdpStats getUDPv4Stats() {
        Struct.CloseableMibUdpStats stats = null;
        try {
            stats = new Struct.CloseableMibUdpStats();
            IPHLP.GetUdpStatisticsEx(stats, AF_INET);
            return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);

        }finally {
            if (stats != null) {
                stats.close();
            }

        }
    }

    public InternetProtocolStats.UdpStats getUDPv6Stats() {
        Struct.CloseableMibUdpStats stats = null;
        try {
            stats = new Struct.CloseableMibUdpStats();
            IPHLP.GetUdpStatisticsEx(stats, AF_INET6);
            return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);

        }finally {
            if (stats != null) {
                stats.close();
            }

        }
    }

    public List<InternetProtocolStats.IPConnection> getConnections() {
        if (IS_VISTA_OR_GREATER) {
            List<InternetProtocolStats.IPConnection> conns = new ArrayList();
            conns.addAll(queryTCPv4Connections());
            conns.addAll(queryTCPv6Connections());
            conns.addAll(queryUDPv4Connections());
            conns.addAll(queryUDPv6Connections());
            return conns;
        }
        return Collections.emptyList();
    }

    private static List<InternetProtocolStats.IPConnection> queryTCPv4Connections() {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        ByRef.CloseableIntByReference sizePtr = null;
        try {
            sizePtr = new ByRef.CloseableIntByReference();
            int ret = IPHLP.GetExtendedTcpTable(null, sizePtr, false, AF_INET, TCP_TABLE_OWNER_PID_ALL, 0);
            // Get buffer and populate table
            int size = sizePtr.getValue();
            Memory buf = new Memory(size);
            do {
                ret = IPHLP.GetExtendedTcpTable(buf, sizePtr, false, AF_INET, TCP_TABLE_OWNER_PID_ALL, 0);
                if (ret == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    size = sizePtr.getValue();
                    buf.clear();
                    buf = new Memory(size);
                }
            } while (ret == WinError.ERROR_INSUFFICIENT_BUFFER);
            IPHlpAPI.MIB_TCPTABLE_OWNER_PID tcpTable = new IPHlpAPI.MIB_TCPTABLE_OWNER_PID(buf);
            for (int i = 0; i < tcpTable.dwNumEntries; i++) {
                IPHlpAPI.MIB_TCPROW_OWNER_PID row = tcpTable.table[i];
                conns.add(new InternetProtocolStats.IPConnection("tcp4", ParseUtil.parseIntToIP(row.dwLocalAddr),
                        ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), ParseUtil.parseIntToIP(row.dwRemoteAddr),
                        ParseUtil.bigEndian16ToLittleEndian(row.dwRemotePort), stateLookup(row.dwState), 0, 0,
                        row.dwOwningPid, getExe(row.dwOwningPid)));
            }
            buf.clear();
        }finally {
            if (sizePtr != null) {
                sizePtr.close();
            }
        }
        // Get size needed
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryTCPv6Connections() {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        ByRef.CloseableIntByReference sizePtr = null;
        try {
            sizePtr  = new ByRef.CloseableIntByReference();
            int ret = IPHLP.GetExtendedTcpTable(null, sizePtr, false, AF_INET6, TCP_TABLE_OWNER_PID_ALL, 0);
            // Get buffer and populate table
            int size = sizePtr.getValue();
            Memory buf = new Memory(size);
            do {
                ret = IPHLP.GetExtendedTcpTable(buf, sizePtr, false, AF_INET6, TCP_TABLE_OWNER_PID_ALL, 0);
                if (ret == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    size = sizePtr.getValue();
                    // TODO: close
                    buf.clear();
                    buf = new Memory(size);
                }
            } while (ret == WinError.ERROR_INSUFFICIENT_BUFFER);
            IPHlpAPI.MIB_TCP6TABLE_OWNER_PID tcpTable = new IPHlpAPI.MIB_TCP6TABLE_OWNER_PID(buf);
            for (int i = 0; i < tcpTable.dwNumEntries; i++) {
                IPHlpAPI.MIB_TCP6ROW_OWNER_PID row = tcpTable.table[i];
                conns.add(new InternetProtocolStats.IPConnection("tcp6", row.LocalAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort),
                        row.RemoteAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwRemotePort), stateLookup(row.State),
                        0, 0, row.dwOwningPid,getExe(row.dwOwningPid)));
            }
            // TODO
            buf.clear();
        }finally {
            if (sizePtr != null) {
                sizePtr.close();
            }
        }

        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryUDPv4Connections() {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        ByRef.CloseableIntByReference sizePtr = null;
        try {
            sizePtr = new ByRef.CloseableIntByReference();
            int ret = IPHLP.GetExtendedUdpTable(null, sizePtr, false, AF_INET, UDP_TABLE_OWNER_PID, 0);
            // Get buffer and populate table
            int size = sizePtr.getValue();
            Memory buf = new Memory(size);
            do {
                ret = IPHLP.GetExtendedUdpTable(buf, sizePtr, false, AF_INET, UDP_TABLE_OWNER_PID, 0);
                if (ret == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    size = sizePtr.getValue();
                    buf.clear();
                    buf = new Memory(size);
                }
            } while (ret == WinError.ERROR_INSUFFICIENT_BUFFER);
            IPHlpAPI.MIB_UDPTABLE_OWNER_PID udpTable = new IPHlpAPI.MIB_UDPTABLE_OWNER_PID(buf);
            for (int i = 0; i < udpTable.dwNumEntries; i++) {
                IPHlpAPI.MIB_UDPROW_OWNER_PID row = udpTable.table[i];
                conns.add(new InternetProtocolStats.IPConnection("udp4", ParseUtil.parseIntToIP(row.dwLocalAddr),
                        ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0,
                        row.dwOwningPid,getExe(row.dwOwningPid)));
            }
            buf.clear();
        }finally {
            if (sizePtr != null) {
                sizePtr.close();
            }
        }
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryUDPv6Connections() {
        List<InternetProtocolStats.IPConnection> conns = new ArrayList();
        ByRef.CloseableIntByReference sizePtr = null;
        try {
            sizePtr = new ByRef.CloseableIntByReference();
            int ret = IPHLP.GetExtendedUdpTable(null, sizePtr, false, AF_INET6, UDP_TABLE_OWNER_PID, 0);
            // Get buffer and populate table
            int size = sizePtr.getValue();
            Memory buf = new Memory(size);
            do {
                ret = IPHLP.GetExtendedUdpTable(buf, sizePtr, false, AF_INET6, UDP_TABLE_OWNER_PID, 0);
                if (ret == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    size = sizePtr.getValue();
                    buf.clear();
                    buf = new Memory(size);
                }
            } while (ret == WinError.ERROR_INSUFFICIENT_BUFFER);
            IPHlpAPI.MIB_UDP6TABLE_OWNER_PID udpTable = new IPHlpAPI.MIB_UDP6TABLE_OWNER_PID(buf);
            for (int i = 0; i < udpTable.dwNumEntries; i++) {
                IPHlpAPI.MIB_UDP6ROW_OWNER_PID row = udpTable.table[i];
                conns.add(
                        new InternetProtocolStats.IPConnection("udp6", row.ucLocalAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort),
                                new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0, row.dwOwningPid,getExe(row.dwOwningPid)));
            }
        }finally {
            if (sizePtr != null) {
                sizePtr.close();
            }
        }


        return conns;
    }

    private static InternetProtocolStats.TcpState stateLookup(int state) {
        switch (state) {
            case 1:
            case 12:
                return InternetProtocolStats.TcpState.CLOSED;
            case 2:
                return InternetProtocolStats.TcpState.LISTEN;
            case 3:
                return InternetProtocolStats.TcpState.SYN_SENT;
            case 4:
                return InternetProtocolStats.TcpState.SYN_RECV;
            case 5:
                return InternetProtocolStats.TcpState.ESTABLISHED;
            case 6:
                return InternetProtocolStats.TcpState.FIN_WAIT_1;
            case 7:
                return InternetProtocolStats.TcpState.FIN_WAIT_2;
            case 8:
                return InternetProtocolStats.TcpState.CLOSE_WAIT;
            case 9:
                return InternetProtocolStats.TcpState.CLOSING;
            case 10:
                return InternetProtocolStats.TcpState.LAST_ACK;
            case 11:
                return InternetProtocolStats.TcpState.TIME_WAIT;
            default:
                return InternetProtocolStats.TcpState.UNKNOWN;
        }
    }
    public static ArrayList<NetstatInfo> getNetstat(){
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
    public static String getExe(int processId) {
        if (processId <= 0) {
            return "";
        }
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
            exe = new File(exe).getName();
        }catch (Exception ignored) {

        }finally {
            if (processHandle != null) {
                Kernel32.INSTANCE.CloseHandle(processHandle);
            }
        }


        return exe;
    }
    public static void main(String[] args) throws Exception {
        ArrayList infos = getNetstat();
        for (Object info : infos) {
            System.out.println(info);
        }

    }
}
