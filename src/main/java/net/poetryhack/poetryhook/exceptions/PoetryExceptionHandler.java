package net.poetryhack.poetryhook.exceptions;

import net.poetryhack.poetryhook.util.MixinMethod;

import java.lang.reflect.Method;

/**
 * @author sootysplash
 * @since 1.0.0
 */
public interface PoetryExceptionHandler {

    Class<?> handleStringClassNotFound(String className, ClassNotFoundException exc, MixinMethod from);

    Class<?> handleObjectWrapperNotFound(String className, ClassNotFoundException exc, MixinMethod from);

    Class<?> handleReturnTypeNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from);

    Method handleMatchMethodNotFound(Class<?> declaringClass, String methodName, Class<?>[] methodArgs, NoSuchMethodException exc, MixinMethod from);

    void handleMixinFailedInject(MixinMethod failure);

}
