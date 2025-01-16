/**
 * @author lz520520
 * @date 2025/1/16 11:02
 */

package org.win_manage.util;

public class MyPair<F, S> {
    private final F first;
    private final S second;

    public MyPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
