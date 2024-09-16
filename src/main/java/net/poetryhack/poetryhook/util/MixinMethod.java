/**
 * Created: 07.10.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.*;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;

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
    public final Method  methodToCall;
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

        Class<?> declaringClass = this.methodToCall.getDeclaringClass();
        // Annotations are checked by MixinBase.mixins()
        if (declaringClass.isAnnotationPresent(StringMixin.class)) {
            try {
                injectTo = MixinMethod.class.getClassLoader().loadClass(declaringClass.getAnnotation(StringMixin.class).value());
            } catch (ClassNotFoundException e) {
                throw new PoetryHookException(e);
            }
        } else {
            injectTo = declaringClass.getAnnotation(Mixin.class).value();
        }

        this.annotation = MixinInfo.get(this.methodToCall);
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
                    try {
                        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                        classToAdd = contextClassLoader.loadClass(param.getAnnotation(ObjectWrapper.class).value());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
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

        try {
            this.returnType = this.injectTo.getDeclaredMethod(this.methodName, clazzes).getReturnType();
        } catch (NoSuchMethodException e) {
            throw new PoetryHookException(e);// revised by majorsopa
        }

        this.returnFromHook = this.annotation.returnFromHook;
        this.location = this.annotation.location;
        this.opcode = this.matcher.match_opcode();
        if (!this.location.equals(InjectLocation.MATCH_METHOD)) {
            return;
        }
        try {
            String methodName = this.matcher.method_name();
            this.match_method = this.matcher.method_class().getDeclaredMethod(methodName, this.matcher.method_parameters());
        } catch (NoSuchMethodException e) {
            throw new PoetryHookException(e);// revised by majorsopa
        }
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
