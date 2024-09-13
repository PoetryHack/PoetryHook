/**
 * Created: 11.05.2024
 */

package net.poetryhack.poetryhook.exceptions;

/**
 * @author majorsopa
 * @since 1.0.0
 */
public final class PoetryHookException extends RuntimeException {
    public PoetryHookException(String message) {
        super(message);
    }

    public PoetryHookException(Throwable cause) {
        super(cause);
    }
}
