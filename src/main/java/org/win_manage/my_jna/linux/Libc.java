/**
 * @author lz520520
 * @date 2024/12/19 15:39
 */

package org.win_manage.my_jna.linux;


import com.sun.jna.*;


public class Libc {
    public static CLibrary INSTANCE = Native.loadLibrary(Platform.C_LIBRARY_NAME, CLibrary.class);

    public static interface CLibrary extends Library {
        int readlink(String path, byte[] buf, int bufsize);
    }
}
