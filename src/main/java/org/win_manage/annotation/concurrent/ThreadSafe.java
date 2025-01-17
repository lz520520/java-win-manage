/**
 * @author lz520520
 * @date 2025/1/14 11:32
 */

package org.win_manage.annotation.concurrent;

import java.lang.annotation.Documented;

/**
 * The presence of this annotation indicates that the author believes the class to be thread-safe. As such, there should
 * be no sequence of accessing the public methods or fields that could put an instance of this class into an invalid
 * state, irrespective of any rearrangement of those operations by the Java Runtime and without introducing any
 * requirements for synchronization or coordination by the caller/accessor.
 * <p>
 * This annotation is intended for internal use in OSHI as a temporary workaround until it is available in
 * {@code jakarta.annotations}.
 */
@Documented
public @interface ThreadSafe {
}