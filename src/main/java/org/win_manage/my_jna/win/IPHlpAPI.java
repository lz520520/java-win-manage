/* Copyright (c) 2018,2020 Daniel Widdis, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package org.win_manage.my_jna.win;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.util.List;

/**
 * Windows IP Helper API
 *
 * @see <A HREF=
 *      "https://msdn.microsoft.com/en-us/library/windows/desktop/aa373083(v=vs.85).aspx">IP
 *      Helper Reference</A>
 */
public interface IPHlpAPI extends Library {
    public static IPHlpAPI INSTANCE = Native.loadLibrary("iphlpapi.dll", IPHlpAPI.class, W32APIOptions.DEFAULT_OPTIONS);

    int IF_MAX_STRING_SIZE = 256;
    int IF_MAX_PHYS_ADDRESS_LENGTH = 32;
    int MAX_INTERFACE_NAME_LEN = 256;
    int MAXLEN_IFDESCR = 256;
    int MAXLEN_PHYSADDR = 8;
    int MAX_HOSTNAME_LEN = 128;
    int MAX_DOMAIN_NAME_LEN = 128;
    int MAX_SCOPE_ID_LEN = 256;

    // Source: Winsock2.h
    int AF_UNSPEC = 0; // The address family is unspecified.
    int AF_INET = 2; // The Internet Protocol version 4 (IPv4) address family.
    int AF_IPX = 6; // The IPX/SPX address family.
    int AF_NETBIOS = 17; // The NetBIOS address family.
    int AF_INET6 = 23; // The Internet Protocol version 6 (IPv6) address family.
    int AF_IRDA = 26; // The Infrared Data Association (IrDA) address family.
    int AF_BTH = 32; // The Bluetooth address family.

    interface TCP_TABLE_CLASS {
        int TCP_TABLE_BASIC_LISTENER = 0;
        int TCP_TABLE_BASIC_CONNECTIONS = 1;
        int TCP_TABLE_BASIC_ALL = 2;
        int TCP_TABLE_OWNER_PID_LISTENER = 3;
        int TCP_TABLE_OWNER_PID_CONNECTIONS = 4;
        int TCP_TABLE_OWNER_PID_ALL = 5;
        int TCP_TABLE_OWNER_MODULE_LISTENER = 6;
        int TCP_TABLE_OWNER_MODULE_CONNECTIONS = 7;
        int TCP_TABLE_OWNER_MODULE_ALL = 8;
    }

    /**
     * Defines the set of values used to indicate the type of table returned by
     * calls to {@link #GetExtendedUdpTable}.
     */
    interface UDP_TABLE_CLASS {
        int UDP_TABLE_BASIC = 0;
        int UDP_TABLE_OWNER_PID = 1;
        int UDP_TABLE_OWNER_MODULE = 2;
    }

    /**
     * Enumerates different possible TCP states.
     */
    interface MIB_TCP_STATE {
        int MIB_TCP_STATE_CLOSED = 1;
        int MIB_TCP_STATE_LISTEN = 2;
        int MIB_TCP_STATE_SYN_SENT = 3;
        int MIB_TCP_STATE_SYN_RCVD = 4;
        int MIB_TCP_STATE_ESTAB = 5;
        int MIB_TCP_STATE_FIN_WAIT1 = 6;
        int MIB_TCP_STATE_FIN_WAIT2 = 7;
        int MIB_TCP_STATE_CLOSE_WAIT = 8;
        int MIB_TCP_STATE_CLOSING = 9;
        int MIB_TCP_STATE_LAST_ACK = 10;
        int MIB_TCP_STATE_TIME_WAIT = 11;
        int MIB_TCP_STATE_DELETE_TCB = 12;
    }

    /**
     * The MIB_IFROW structure stores information about a particular interface.
     *
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/previous-versions/windows/desktop/api/ifmib/ns-ifmib-_mib_ifrow">MIB_IFROW</A>
     */
    class MIB_IFROW extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("wszName", "dwIndex", "dwType", "dwMtu", "dwSpeed", "dwPhysAddrLen", "bPhysAddr", "dwAdminStatus",
                "dwOperStatus", "dwLastChange", "dwInOctets", "dwInUcastPkts", "dwInNUcastPkts", "dwInDiscards",
                "dwInErrors", "dwInUnknownProtos", "dwOutOctets", "dwOutUcastPkts", "dwOutNUcastPkts", "dwOutDiscards",
                "dwOutErrors", "dwOutQLen", "dwDescrLen", "bDescr");

        public char[] wszName = new char[MAX_INTERFACE_NAME_LEN];
        public int dwIndex;
        public int dwType;
        public int dwMtu;
        public int dwSpeed;
        public int dwPhysAddrLen;
        public byte[] bPhysAddr = new byte[MAXLEN_PHYSADDR];
        public int dwAdminStatus;
        public int dwOperStatus;
        public int dwLastChange;
        public int dwInOctets;
        public int dwInUcastPkts;
        public int dwInNUcastPkts;
        public int dwInDiscards;
        public int dwInErrors;
        public int dwInUnknownProtos;
        public int dwOutOctets;
        public int dwOutUcastPkts;
        public int dwOutNUcastPkts;
        public int dwOutDiscards;
        public int dwOutErrors;
        public int dwOutQLen;
        public int dwDescrLen;
        public byte[] bDescr = new byte[MAXLEN_IFDESCR];
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The MIB_IF_ROW2 structure stores information about a particular interface.
     *
     * @see <A HREF=
     *      "https://msdn.microsoft.com/library/windows/hardware/ff559214">MIB_IF_ROW2</A>
     */
    class MIB_IF_ROW2 extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("InterfaceLuid", "InterfaceIndex", "InterfaceGuid", "Alias", "Description", "PhysicalAddressLength",
                "PhysicalAddress", "PermanentPhysicalAddress", "Mtu", "Type", "TunnelType", "MediaType",
                "PhysicalMediumType", "AccessType", "DirectionType", "InterfaceAndOperStatusFlags", "OperStatus",
                "AdminStatus", "MediaConnectState", "NetworkGuid", "ConnectionType", "TransmitLinkSpeed",
                "ReceiveLinkSpeed", "InOctets", "InUcastPkts", "InNUcastPkts", "InDiscards", "InErrors", "InUnknownProtos",
                "InUcastOctets", "InMulticastOctets", "InBroadcastOctets", "OutOctets", "OutUcastPkts", "OutNUcastPkts",
                "OutDiscards", "OutErrors", "OutUcastOctets", "OutMulticastOctets", "OutBroadcastOctets", "OutQLen" );

        public long InterfaceLuid; // 64-bit union NET_LUID
        public int InterfaceIndex;
        public GUID InterfaceGuid;
        public char[] Alias = new char[IF_MAX_STRING_SIZE + 1];
        public char[] Description = new char[IF_MAX_STRING_SIZE + 1];
        public int PhysicalAddressLength;
        public byte[] PhysicalAddress = new byte[IF_MAX_PHYS_ADDRESS_LENGTH];
        public byte[] PermanentPhysicalAddress = new byte[IF_MAX_PHYS_ADDRESS_LENGTH];
        public int Mtu;
        public int Type;
        // enums
        public int TunnelType;
        public int MediaType;
        public int PhysicalMediumType;
        public int AccessType;
        public int DirectionType;
        // 8-bit structure
        public byte InterfaceAndOperStatusFlags;
        // enums
        public int OperStatus;
        public int AdminStatus;
        public int MediaConnectState;
        public GUID NetworkGuid;
        public int ConnectionType;
        public long TransmitLinkSpeed;
        public long ReceiveLinkSpeed;
        public long InOctets;
        public long InUcastPkts;
        public long InNUcastPkts;
        public long InDiscards;
        public long InErrors;
        public long InUnknownProtos;
        public long InUcastOctets;
        public long InMulticastOctets;
        public long InBroadcastOctets;
        public long OutOctets;
        public long OutUcastPkts;
        public long OutNUcastPkts;
        public long OutDiscards;
        public long OutErrors;
        public long OutUcastOctets;
        public long OutMulticastOctets;
        public long OutBroadcastOctets;
        public long OutQLen;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The IP_ADDRESS_STRING structure stores an IPv4 address in dotted decimal
     * notation. The IP_ADDRESS_STRING structure definition is also the type
     * definition for the IP_MASK_STRING structure.
     *
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/iptypes/ns-iptypes-ip_address_string">IP_ADDRESS_STRING</A>
     */
    class IP_ADDRESS_STRING extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("String");

        // Null terminated string
        // up to 3 chars (decimal 0-255) and dot
        // ending with null
        public byte[] String = new byte[16];
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The IP_ADDR_STRING structure represents a node in a linked-list of IPv4
     * addresses.
     *
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/iptypes/ns-iptypes-_ip_addr_string">IP_ADDR_STRING</A>
     */
    class IP_ADDR_STRING extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Next", "IpAddress", "IpMask", "Context");

        public IP_ADDR_STRING.ByReference Next;
        public IP_ADDRESS_STRING IpAddress;
        public IP_ADDRESS_STRING IpMask;
        public int Context;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public static class ByReference extends IP_ADDR_STRING implements Structure.ByReference {
        }
    }

    /**
     * The FIXED_INFO structure contains information that is the same across all the
     * interfaces on a computer.
     *
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/iptypes/ns-iptypes-fixed_info_w2ksp1">FIXED_INFO</A>
     */

    class FIXED_INFO extends Structure {
        public static final List<String> FIELDS = createFieldsOrder( "HostName", "DomainName", "CurrentDnsServer", "DnsServerList", "NodeType", "ScopeId", "EnableRouting",
                "EnableProxy", "EnableDns");

        public byte[] HostName = new byte[MAX_HOSTNAME_LEN + 4];
        public byte[] DomainName = new byte[MAX_DOMAIN_NAME_LEN + 4];
        public IP_ADDR_STRING.ByReference CurrentDnsServer;
        public IP_ADDR_STRING DnsServerList;
        public int NodeType;
        public byte[] ScopeId = new byte[MAX_SCOPE_ID_LEN + 4];
        public int EnableRouting;
        public int EnableProxy;
        public int EnableDns;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public FIXED_INFO(Pointer p) {
            super(p);
            read();
        }

        public FIXED_INFO() {
            super();
        }
    }

    /**
     * The MIB_TCPSTATS structure contains statistics for the TCP protocol running
     * on the local computer.
     * <p>
     * In the Windows SDK, the version of the structure for use on Windows Vista and
     * later is defined as {@code MIB_TCPSTATS_LH}. In the Windows SDK, the version
     * of this structure to be used on earlier systems including Windows 2000 and
     * later is defined as {@code MIB_TCPSTATS_W2K}.
     */

    class MIB_TCPSTATS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwRtoAlgorithm", "dwRtoMin", "dwRtoMax", "dwMaxConn", "dwActiveOpens", "dwPassiveOpens",
                "dwAttemptFails", "dwEstabResets", "dwCurrEstab", "dwInSegs", "dwOutSegs", "dwRetransSegs", "dwInErrs",
                "dwOutRsts", "dwNumConns");
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwRtoAlgorithm; // Union for _W2K version, doesn't change mapping
        public int dwRtoMin;
        public int dwRtoMax;
        public int dwMaxConn;
        public int dwActiveOpens;
        public int dwPassiveOpens;
        public int dwAttemptFails;
        public int dwEstabResets;
        public int dwCurrEstab;
        public int dwInSegs;
        public int dwOutSegs;
        public int dwRetransSegs;
        public int dwInErrs;
        public int dwOutRsts;
        public int dwNumConns;
    }

    /**
     * The MIB_UDPSTATS structure contains statistics for the User Datagram Protocol
     * (UDP) running on the local computer.
     */
    class MIB_UDPSTATS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwInDatagrams", "dwNoPorts", "dwInErrors", "dwOutDatagrams", "dwNumAddrs");
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwInDatagrams;
        public int dwNoPorts;
        public int dwInErrors;
        public int dwOutDatagrams;
        public int dwNumAddrs;
    }

    class MIB_TCPROW_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwState", "dwLocalAddr", "dwLocalPort", "dwRemoteAddr", "dwRemotePort", "dwOwningPid" );
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwState;
        public int dwLocalAddr;
        public int dwLocalPort;
        public int dwRemoteAddr;
        public int dwRemotePort;
        public int dwOwningPid;
    }

    /**
     * Contains a table of IPv4 TCP connections on the local computer.
     */
    class MIB_TCPTABLE_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwNumEntries", "table" );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwNumEntries;
        public MIB_TCPROW_OWNER_PID[] table = new MIB_TCPROW_OWNER_PID[1];

        public MIB_TCPTABLE_OWNER_PID(Pointer buf) {
            super(buf);
            read();
        }

        @Override
        public void read() {
            // First element contains array size
            this.dwNumEntries = getPointer().getInt(0);
            if (this.dwNumEntries > 0) {
                table = (MIB_TCPROW_OWNER_PID[]) new MIB_TCPROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                table = new MIB_TCPROW_OWNER_PID[0];
            }
        }
    }
    /**
     * Contains information that describes an IPv6 TCP connection.
     */
    class MIB_TCP6ROW_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("LocalAddr", "dwLocalScopeId", "dwLocalPort", "RemoteAddr", "dwRemoteScopeId", "dwRemotePort",
                "State", "dwOwningPid" );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public byte[] LocalAddr = new byte[16];
        public int dwLocalScopeId;
        public int dwLocalPort;
        public byte[] RemoteAddr = new byte[16];
        public int dwRemoteScopeId;
        public int dwRemotePort;
        public int State;
        public int dwOwningPid;
    }

    /**
     * Contains a table of IPv6 TCP connections on the local computer.
     */
    class MIB_TCP6TABLE_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwNumEntries", "table" );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwNumEntries;
        public MIB_TCP6ROW_OWNER_PID[] table = new MIB_TCP6ROW_OWNER_PID[1];

        public MIB_TCP6TABLE_OWNER_PID(Pointer buf) {
            super(buf);
            read();
        }

        @Override
        public void read() {
            // First element contains array size
            this.dwNumEntries = getPointer().getInt(0);
            if (this.dwNumEntries > 0) {
                table = (MIB_TCP6ROW_OWNER_PID[]) new MIB_TCP6ROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                table = new MIB_TCP6ROW_OWNER_PID[0];
            }
        }
    }

    /**
     * Contains information that describes an IPv6 UDP connection.
     */
    class MIB_UDPROW_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder( "dwLocalAddr", "dwLocalPort", "dwOwningPid"  );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwLocalAddr;
        public int dwLocalPort;
        public int dwOwningPid;
    }

    /**
     * Contains a table of IPv6 UDP connections on the local computer.
     */
    class MIB_UDPTABLE_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwNumEntries", "table"  );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public int dwNumEntries;
        public MIB_UDPROW_OWNER_PID[] table = new MIB_UDPROW_OWNER_PID[1];

        public MIB_UDPTABLE_OWNER_PID(Pointer buf) {
            super(buf);
            read();
        }

        @Override
        public void read() {
            // First element contains array size
            this.dwNumEntries = getPointer().getInt(0);
            if (this.dwNumEntries > 0) {
                table = (MIB_UDPROW_OWNER_PID[]) new MIB_UDPROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                table = new MIB_UDPROW_OWNER_PID[0];
            }
        }
    }

    /**
     * Contains information that describes an IPv6 UDP connection.
     */
    class MIB_UDP6ROW_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("ucLocalAddr", "dwLocalScopeId", "dwLocalPort", "dwOwningPid"  );

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public byte[] ucLocalAddr = new byte[16];
        public int dwLocalScopeId;
        public int dwLocalPort;
        public int dwOwningPid;
    }

    /**
     * Contains a table of IPv6 UDP connections on the local computer.
     */
    class MIB_UDP6TABLE_OWNER_PID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dwNumEntries", "table"  );
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
        public int dwNumEntries;
        public MIB_UDP6ROW_OWNER_PID[] table = new MIB_UDP6ROW_OWNER_PID[1];

        public MIB_UDP6TABLE_OWNER_PID(Pointer buf) {
            super(buf);
            read();
        }

        @Override
        public void read() {
            // First element contains array size
            this.dwNumEntries = getPointer().getInt(0);
            if (this.dwNumEntries > 0) {
                table = (MIB_UDP6ROW_OWNER_PID[]) new MIB_UDP6ROW_OWNER_PID().toArray(this.dwNumEntries);
                super.read();
            } else {
                table = new MIB_UDP6ROW_OWNER_PID[0];
            }
        }
    }
    /**
     * The GetIfEntry function retrieves information for the specified interface on
     * the local computer.
     * <p>
     * The {@code dwIndex} member in the {@link MIB_IFROW} structure pointed to by
     * the pIfRow parameter must be initialized to a valid network interface index
     * retrieved by a previous call to the GetIfTable, GetIfTable2, or GetIfTable2Ex
     * function. The GetIfEntry function will fail if the dwIndex member of the
     * {@link MIB_IFROW} pointed to by the pIfRow parameter does not match an
     * existing interface index on the local computer.
     *
     * @param pIfRow
     *            A pointer to a MIB_IFROW structure that, on successful return,
     *            receives information for an interface on the local computer. On
     *            input, set the dwIndex member of {@link MIB_IFROW} to the index of
     *            the interface for which to retrieve information.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetIfEntry(MIB_IFROW pIfRow);

    /**
     * The GetIfEntry2 function retrieves information for the specified interface on
     * the local computer.
     * <p>
     * On input, at least one of the following members in the {@link MIB_IF_ROW2}
     * structure passed in the Row parameter must be initialized: InterfaceLuid or
     * InterfaceIndex. The fields are used in the order listed above. So if the
     * InterfaceLuid is specified, then this member is used to determine the
     * interface. If no value was set for the InterfaceLuid member (the value of
     * this member was set to zero), then the InterfaceIndex member is next used to
     * determine the interface. On output, the remaining fields of the
     * {@link MIB_IF_ROW2} structure pointed to by the Row parameter are filled in.
     *
     * @param pIfRow2
     *            A pointer to a {@link MIB_IF_ROW2} structure that, on successful
     *            return, receives information for an interface on the local
     *            computer. On input, the InterfaceLuid or the InterfaceIndex member
     *            of the {@link MIB_IF_ROW2} must be set to the interface for which
     *            to retrieve information.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetIfEntry2(MIB_IF_ROW2 pIfRow2);

    /**
     * The GetNetworkParams function retrieves network parameters for the local
     * computer.
     *
     * @param pFixedInfo
     *            A pointer to a buffer that contains a {@link FIXED_INFO} structure
     *            that receives the network parameters for the local computer, if
     *            the function was successful. This buffer must be allocated by the
     *            caller prior to calling the GetNetworkParams function.
     * @param pOutBufLen
     *            A pointer to a ULONG variable that specifies the size of the
     *            {@link FIXED_INFO} structure. If this size is insufficient to hold
     *            the information, GetNetworkParams fills in this variable with the
     *            required size, and returns an error code of
     *            .
     * @return If the function succeeds, the return value is
     *         .
     */
    int GetNetworkParams(Pointer pFixedInfo, IntByReference pOutBufLen);

    /**
     * The GetTcpStatistics function retrieves the TCP statistics for the local
     * computer.
     *
     * @param Statistics
     *            A {@link MIB_TCPSTATS} structure that receives the TCP statistics
     *            for the local computer.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetTcpStatistics(MIB_TCPSTATS Statistics);

    /**
     * The GetTcpStatisticsEx function retrieves the Transmission Control Protocol
     * (TCP) statistics for the current computer. The GetTcpStatisticsEx function
     * differs from the {@link #GetTcpStatistics} function in that
     * GetTcpStatisticsEx also supports the Internet Protocol version 6 (IPv6)
     * protocol family.
     *
     * @param Statistics
     *            A {@link MIB_TCPSTATS} structure that receives the TCP statistics
     *            for the local computer.
     * @param Family
     *            The protocol family for which to retrieve statistics. This
     *            parameter must be {@link #AF_INET} or {@link #AF_INET6}.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetTcpStatisticsEx(MIB_TCPSTATS Statistics, int Family);

    /**
     * The GetUdpStatistics function retrieves the User Datagram Protocol (UDP)
     * statistics for the local computer.
     *
     * @param Statistics
     *            A {@link MIB_UDPSTATS} structure that receives the UDP statistics
     *            for the local computer.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetUdpStatistics(MIB_UDPSTATS Statistics);

    /**
     * The GetUdpStatisticsEx function retrieves the User Datagram Protocol (UDP)
     * statistics for the current computer. The GetUdpStatisticsEx function differs
     * from the {@link #GetUdpStatistics} function in that GetUdpStatisticsEx also
     * supports the Internet Protocol version 6 (IPv6) protocol family.
     *
     * @param Statistics
     *            A {@link MIB_UDPSTATS} structure that receives the UDP statistics
     *            for the local computer.
     * @param Family
     *            The protocol family for which to retrieve statistics. This
     *            parameter must be {@link #AF_INET} or {@link #AF_INET6}.
     * @return If the function succeeds, the return value is
     *         {@link WinError#NO_ERROR}.
     */
    int GetUdpStatisticsEx(MIB_UDPSTATS Statistics, int Family);
    int GetExtendedTcpTable(Pointer pTcpTable, IntByReference pdwSize, boolean bOrder, int ulAf, int TableClass,
                            int Reserved);
    int GetExtendedUdpTable(Pointer pUdpTable, IntByReference pdwSize, boolean bOrder, int ulAf, int TableClass,
                            int Reserved);
}
