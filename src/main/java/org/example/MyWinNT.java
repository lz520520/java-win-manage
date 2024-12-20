/**
 * @author lz520520
 * @date 2024/12/19 16:53
 */

package org.example;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

import java.util.ArrayList;
import java.util.List;


public interface MyWinNT extends com.sun.jna.platform.win32.WinNT {


   public static  class PROCESS_BASIC_INFORMATION extends Structure {

        public static final List<String> FIELDS = createFieldsOrder("ExitStatus", "PebBaseAddress", "AffinityMask","BasePriority", "UniqueProcessId", "InheritedFromUniqueProcessId");

       public Pointer ExitStatus;               // NTSTATUS类型
       public Pointer PebBaseAddress;                 // PPEB类型
       public Pointer AffinityMask;                   // ULONG_PTR类型
       public int BasePriority;                       // KPRIORITY类型
       public Pointer UniqueProcessId;                // ULONG_PTR类型
       public Pointer InheritedFromUniqueProcessId;   // ULONG_PTR类型

       @Override
       protected List<String> getFieldOrder() {
           return FIELDS;
       }
       public PROCESS_BASIC_INFORMATION() {
       }
       public PROCESS_BASIC_INFORMATION(Pointer memory) {
           super(memory);
           this.read();
       }
       public static class ByReference extends PROCESS_BASIC_INFORMATION implements Structure.ByReference {
           public ByReference() {
           }


           public ByReference(Pointer memory) {
               super(memory);
           }
       }
   }

    class RTL_USER_PROCESS_PARAMETERS extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Reserved1","Reserved2","ImagePathName", "CommandLine");

        public byte[] Reserved1 = new byte[16]; // 占位
        public Pointer[] Reserved2 = new Pointer[10]; // 占位
        public UNICODE_STRING ImagePathName; // 可执行文件路径
        public UNICODE_STRING CommandLine;   // 命令行


        public RTL_USER_PROCESS_PARAMETERS(Pointer memory) {
            super(memory);
            read();
        }
        public RTL_USER_PROCESS_PARAMETERS() {
        }
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class UNICODE_STRING extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Length", "MaximumLength", "Buffer");

        public short Length;
        public short MaximumLength;
        public Pointer Buffer;
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    class PEB extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Reserved1", "BeingDebugged", "Reserved2","Reserved3", "Ldr", "ProcessParameters");

        public byte[] Reserved1 = new byte[2];   // 保持字节对齐
        public byte BeingDebugged;              // 占用 1 字节
        public byte Reserved2;                  // 占用 1 字节
        public Pointer[] Reserved3 = new Pointer[2];
        public Pointer Ldr;                     // PPEB_LDR_DATA (未使用)
        public Pointer ProcessParameters;       // PRTL_USER_PROCESS_PARAMETERS
        // 后续字段省略，因为只需要访问 ProcessParameters

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public PEB() {
        }
        public PEB(Pointer memory) {
            super(memory);
            this.read();
        }
        public static class ByReference extends PEB implements Structure.ByReference {
            public ByReference() {
            }


            public ByReference(Pointer memory) {
                super(memory);
            }
        }
    }

//    public static class LUID extends Structure {
//        public static final List<String> FIELDS = createFieldsOrder("LowPart", "HighPart");
//
//        public int LowPart;
//        public int HighPart;
//
//        @Override
//        protected List<String> getFieldOrder() {
//            return FIELDS;
//        }
//    }
}