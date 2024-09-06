/**
 * Created: 07.10.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.Matcher;
import net.poetryhack.poetryhook.annotations.Mixin;
import net.poetryhack.poetryhook.annotations.MixinInfo;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @since 1.0.0
 * @author sootysplash, revised by majorsopa
 */
public class MixinMethod {
    protected final boolean returnFromHook;
    protected final Class<?> returnType;
    protected final Matcher matcher;
    protected MixinType type;
    protected final InjectLocation location;
    protected int opcode;
    protected Method match_method;
    protected String fieldName;
    protected String methodName;
    public final MixinInfo annotation;
    public final Method methodToCall;
    public final Class<?> injectTo;
    public boolean loaded = false;

    public MixinMethod(Method method) {
        this.methodToCall = method;
        this.injectTo = this.methodToCall.getDeclaringClass().getAnnotation(Mixin.class).value();
        this.annotation = MixinInfo.get(this.methodToCall);
        this.type = this.annotation.mixinType;
        this.matcher = this.annotation.matcher;
        this.fieldName = this.matcher.field_name();
        this.methodName = this.annotation.value;

        Class<?>[] annotClasses = this.annotation.toHookArgs;
        boolean isAnnotation = annotClasses.length != 0 || this.annotation.forceUseAnnotationArgs;
        Class<?>[] params = this.methodToCall.getParameterTypes();
        if (!isAnnotation && params[0] == this.injectTo) {
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
        if (this.location.equals(InjectLocation.MATCH_METHOD)) {
            try {
                String methodName = this.matcher.method_name();
                this.match_method = this.matcher.method_class().getDeclaredMethod(methodName, this.matcher.method_parameters());
            } catch (NoSuchMethodException e) {
                throw new PoetryHookException(e);// revised by majorsopa
            }
        }
        this.opcode = this.matcher.match_opcode();
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
