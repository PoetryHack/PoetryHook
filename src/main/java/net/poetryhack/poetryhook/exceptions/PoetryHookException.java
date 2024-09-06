/**
 * Created: 11.05.2024
 */

package net.poetryhack.poetryhook.exceptions;

/**
 * @since 1.0.0
 */
public class PoetryHookException extends RuntimeException {
    public PoetryHookException(String message) {
        super(message);
    }

    public PoetryHookException(Throwable cause) {
        super(cause);
    }
}
