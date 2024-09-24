/**
 * Created: 07.10.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.*;
import net.poetryhack.poetryhook.exceptions.DefaultExceptionHandler;
import net.poetryhack.poetryhook.exceptions.PoetryExceptionHandler;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author sootysplash, revised by majorsopa
 * @since 1.0.0
 */
public class MixinMethod {
    public final MixinInfo annotation;
    public final PoetryExceptionHandler exceptionHandler;
    public final Method methodToCall;
    public final Class<?> injectTo;
    protected final boolean returnFromHook;
    protected final Class<?> returnType;
    protected final Matcher matcher;
    protected final InjectLocation location;
    public boolean loaded = false;
    protected MixinType type;
    protected int opcode;
    protected Method match_method;
    protected String fieldName;
    protected String methodName;

    public MixinMethod(Method method) {
        this.methodToCall = method;

        this.annotation = MixinInfo.get(this.methodToCall);

        PoetryExceptionHandler handler;
        try {
            handler = this.annotation.exceptionHandler.getDeclaredConstructor().newInstance();
        } catch (Exception m) {
            handler = new DefaultExceptionHandler();
        }
        this.exceptionHandler = handler;

        Class<?> declaringClass = this.methodToCall.getDeclaringClass();
        // Annotations are checked by MixinBase.mixins()
        Class<?> targetClass;
        if (declaringClass.isAnnotationPresent(StringMixin.class)) {
            String className = declaringClass.getAnnotation(StringMixin.class).value();
            try {
                targetClass = MixinMethod.class.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                targetClass = handler.handleStringClassNotFound(className, e, this);
            }
        } else {
            targetClass = declaringClass.getAnnotation(Mixin.class).value();
        }
        this.injectTo = targetClass;
        this.type = this.annotation.mixinType;
        this.matcher = this.annotation.matcher;
        this.fieldName = this.matcher.field_name();
        this.methodName = this.annotation.value;

        Class<?>[] annotClasses = this.annotation.toHookArgs;
        boolean isAnnotation = annotClasses.length != 0 || this.annotation.forceUseAnnotationArgs;
        Class<?>[] params;
        {
            Parameter[] paramsArray = this.methodToCall.getParameters();
            ArrayList<Class<?>> paramsArrayList = new ArrayList<>();
            for (Parameter param : paramsArray) {
                Class<?> classToAdd;

                if (param.isAnnotationPresent(ObjectWrapper.class)) {
                    String paramName = param.getAnnotation(ObjectWrapper.class).value();
                    try {
                        classToAdd = MixinMethod.class.getClassLoader().loadClass(paramName);  // todo make it so this isn't hardcoded to this classloader
                    } catch (ClassNotFoundException e) {
                        classToAdd = handler.handleObjectWrapperNotFound(paramName, e, this);
                    }
                } else {
                    classToAdd = param.getType();
                }

                paramsArrayList.add(classToAdd);
            }
            // oh my bad code
            params = new Class<?>[paramsArrayList.size()];
            for (int i = 0; i < paramsArrayList.size(); i++) {
                params[i] = paramsArrayList.get(i);
            }
        }
        if (!isAnnotation && params.length > 0 && params[0] == this.injectTo) {
            params = Arrays.copyOfRange(params, 1, params.length);
        }
        Class<?>[] clazzes = isAnnotation ? annotClasses : params;

        Class<?> returnClass;
        try {
            returnClass = this.injectTo.getDeclaredMethod(this.methodName, clazzes).getReturnType();
        } catch (NoSuchMethodException e) {
            returnClass = handler.handleReturnTypeNotFound(this.injectTo, this.methodName, clazzes, e, this);
        }
        this.returnType = returnClass;

        this.returnFromHook = this.annotation.returnFromHook;
        this.location = this.annotation.location;
        this.opcode = this.matcher.match_opcode();
        if (!this.location.equals(InjectLocation.MATCH_METHOD)) {
            return;
        }
        Method matchM;
        String methodName = this.matcher.method_name();
        try {
            matchM = this.matcher.method_class().getDeclaredMethod(methodName, this.matcher.method_parameters());
        } catch (NoSuchMethodException e) {
            matchM = handler.handleMatchMethodNotFound(this.matcher.method_class(), methodName, this.matcher.method_parameters(), e, this);
        }
        this.match_method = matchM;
    }

    protected boolean match_opcode(int opcode) {
        return this.opcode == -1 || this.opcode == opcode;
    }

    protected boolean isInject() {
        return type == MixinType.Inject;
    }

    protected boolean isRedirect() {
        return type == MixinType.Redirect;
    }
}
