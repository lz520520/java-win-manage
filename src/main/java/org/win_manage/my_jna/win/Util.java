/**
 * @author lz520520
 * @date 2025/1/14 14:55
 */

package org.win_manage.my_jna.win;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class Util {

    private Util() {
    }

    /**
     * Sleeps for the specified number of milliseconds.
     *
     * @param ms How long to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) { // NOSONAR squid:S2142
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Tests if a String matches another String with a wildcard pattern.
     *
     * @param text    The String to test
     * @param pattern The String containing a wildcard pattern where ? represents a single character and * represents
     *                any number of characters. If the first character of the pattern is a carat (^) the test is
     *                performed against the remaining characters and the result of the test is the opposite.
     * @return True if the String matches or if the first character is ^ and the remainder of the String does not match.
     */
    public static boolean wildcardMatch(String text, String pattern) {
        if (pattern.length() > 0 && pattern.charAt(0) == '^') {
            return !wildcardMatch(text, pattern.substring(1));
        }
        return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
    }

    /**
     * Tests if a String is either null or empty.
     *
     * @param s The string to test
     * @return True if the String is either null or empty.
     */
    public static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Tests if a String is either null or empty or the unknown constant.
     *
     * @param s The string to test
     * @return True if the String is either null or empty or the unknown constant.
     */
    public static boolean isBlankOrUnknown(String s) {
        return isBlank(s) || "unknown".equals(s);
    }

    /**
     * If the given Pointer is of class Memory, executes the close method on it to free its native allocation
     *
     * @param p A pointer
     */
    public static void freeMemory(Pointer p) {
        if (p instanceof Memory) {
            // TODO: close()
            ((Memory) p).clear();
        }
    }

    /**
     * Tests if session of a user logged in a device is valid or not.
     *
     * @param user      The user logged in
     * @param device    The device used by user
     * @param loginTime The login time of the user
     * @return True if Session is valid or False if the user of device is empty or the login time is lesser than zero or
     *         greater than current time.
     */
    public static boolean isSessionValid(String user, String device, Long loginTime) {
        return !(user.isEmpty() || device.isEmpty() || loginTime < 0 || loginTime > System.currentTimeMillis());
    }
}
