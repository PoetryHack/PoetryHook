package net.poetryhack.poetryhook.exceptions;

import net.poetryhack.poetryhook.util.MixinMethod;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author sootysplash
 * @since 1.0.0
 */
public class DefaultExceptionHandler implements PoetryExceptionHandler {

    public DefaultExceptionHandler() {}

    @Override
    public Class<?> handleStringClassNotFound(String className, ClassNotFoundException exc, MixinMethod from) {
        throw new PoetryHookException(String.format("Class: (%s) was not found on runtime for mixin: %s", className, from.getDebugString()), exc);
    }

    @Override
    public Class<?> handleObjectWrapperNotFound(String className, ClassNotFoundException exc, MixinMethod from) {
        throw new PoetryHookException(String.format("Object Class: (%s) was not found on runtime for mixin: %s", className, from.getDebugString()) , exc);
    }

    @Override
    public Class<?> handleReturnTypeNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from) {
        throw new PoetryHookException(String.format("Return Type Method for mixin: %s not found, declaring class %s, method name: %s, method args: %s", from.getDebugString(), declaringClass.getName(), methodName, Arrays.toString(methodArgs)), exc);
    }

    @Override
    public Method handleMatchMethodNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from) {
        throw new PoetryHookException(String.format("Match Method for mixin: %s not found, declaring class %s, method name: %s, method args: %s", from.getDebugString(), declaringClass.getName(), methodName, Arrays.toString(methodArgs)), exc);
    }

    @Override
    public void handleMixinFailedInject(MixinMethod failure) {
        throw new PoetryHookException(String.format("Failed to inject Mixin: %s", failure.getDebugString()));
    }

}
