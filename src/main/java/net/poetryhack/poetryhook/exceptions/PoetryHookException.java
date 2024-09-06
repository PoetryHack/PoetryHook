package net.poetryhack.poetryhook.exceptions;

public class PoetryHookException extends RuntimeException {
    public PoetryHookException(String message) {
        super(message);
    }

    public PoetryHookException(Throwable cause) {
        super(cause);
    }
}
