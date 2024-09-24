package net.poetryhack.poetryhook.exceptions;

import net.poetryhack.poetryhook.util.MixinMethod;

import java.lang.reflect.Method;

/**
 * @author sootysplash
 * @since 1.0.0
 */
public class DefaultExceptionHandler implements PoetryExceptionHandler {

    public DefaultExceptionHandler() {}

    @Override
    public Class<?> handleStringClassNotFound(String className, ClassNotFoundException exc, MixinMethod from) {
        throw new PoetryHookException(exc);
    }

    @Override
    public Class<?> handleObjectWrapperNotFound(String className, ClassNotFoundException exc, MixinMethod from) {
        throw new PoetryHookException(exc);
    }

    @Override
    public Class<?> handleReturnTypeNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from) {
        throw new PoetryHookException(exc);
    }

    @Override
    public Method handleMatchMethodNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from) {
        throw new PoetryHookException(exc);
    }

    @Override
    public void handleMixinFailedInject(MixinMethod failure) {
        throw new PoetryHookException("Failed to inject Mixin: " + failure.methodToCall.getDeclaringClass().getName() + " / " + failure.methodToCall.getName());
    }

}
