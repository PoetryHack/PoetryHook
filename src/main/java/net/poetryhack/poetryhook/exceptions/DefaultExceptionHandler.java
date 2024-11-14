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
    public Class<?> handleStringClassNotFound(String[] classNames, ClassNotFoundException exc, boolean hasNext, MixinMethod from) {
        if (!hasNext) {
            throw new PoetryHookException(String.format("Classes: (%s) was not found on runtime for mixin: %s", Arrays.toString(classNames), from.getDebugString()), exc);
        } else {
            return null;
        }
    }

    @Override
    public Class<?> handleObjectWrapperNotFound(String[] classNames, ClassNotFoundException exc, boolean hasNext, MixinMethod from) {
        if (!hasNext) {
            throw new PoetryHookException(String.format("Object Classes: (%s) was not found on runtime for mixin: %s", Arrays.toString(classNames), from.getDebugString()), exc);
        } else {
            return null;
        }
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
    public void handleMixinFailedInject(MixinMethod failure, boolean hasNext) {
        System.err.println(String.format("Failed to inject Mixin: %s", failure.getDebugString()));
        if (!hasNext) {
            throw new PoetryHookException("Failed to load some mixins");
        }
    }

}
