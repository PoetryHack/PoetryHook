/**
 * Created: 03.29.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.ShouldReturn;
import net.poetryhack.poetryhook.annotations.ToReturn;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @since 1.0.0
 */
public class MixinMethodVisitor extends MethodVisitor implements Opcodes {
    private final MixinMethod mixin;
    private static short index = 256;
    public static final HashMap<Class<?>, Class<?>> primToWrapper = new HashMap<>();
    public static final HashMap<Class<?>, Method> wrapperPrimMethod = new HashMap<>();

    static {
        primToWrapper.put(boolean.class, Boolean.class);
        primToWrapper.put(int.class, Integer.class);
        primToWrapper.put(short.class, Short.class);
        primToWrapper.put(byte.class, Byte.class);
        primToWrapper.put(long.class, Long.class);
        primToWrapper.put(float.class, Float.class);
        primToWrapper.put(double.class, Double.class);
        primToWrapper.put(char.class, Character.class);

        try {
            wrapperPrimMethod.put(boolean.class, Boolean.class.getDeclaredMethod("booleanValue"));
            wrapperPrimMethod.put(int.class, Integer.class.getDeclaredMethod("intValue"));
            wrapperPrimMethod.put(short.class, Short.class.getDeclaredMethod("shortValue"));
            wrapperPrimMethod.put(byte.class, Byte.class.getDeclaredMethod("byteValue"));
            wrapperPrimMethod.put(long.class, Long.class.getDeclaredMethod("longValue"));
            wrapperPrimMethod.put(float.class, Float.class.getDeclaredMethod("floatValue"));
            wrapperPrimMethod.put(double.class, Double.class.getDeclaredMethod("doubleValue"));
            wrapperPrimMethod.put(char.class, Character.class.getDeclaredMethod("charValue"));
        } catch (NoSuchMethodException e) {// shouldn't happen
            throw new PoetryHookException(e);
        }
    }

    public MixinMethodVisitor(MethodVisitor methodVisitor, MixinMethod methodToCall) {
        super(ASM9, methodVisitor);
        this.mixin = methodToCall;
    }

    @Override
    public void visitCode() {
        if (mixin.location.equals(InjectLocation.HEAD) && mixin.isInject()) {
            this.callMethod(this.mixin);
        }
        super.visitCode();
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
        if (mixin.matcher.match_after() && mixin.isInject()) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
        if (mixin.location.equals(InjectLocation.MATCH_METHOD)
                && mixin.match_opcode(opcode)
                && owner.equals(mixin.match_method.getDeclaringClass().getName().replace(".", "/"))
                && name.equals(mixin.match_method.getName())
                && descriptor.equals(Type.getMethodDescriptor(mixin.match_method))) {
            callMethod(mixin);
        } else if (mixin.isRedirect()) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
        if (!mixin.matcher.match_after() && mixin.isInject()) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        if (mixin.matcher.match_after() && mixin.isInject()) {
            super.visitTypeInsn(opcode, type);
        }
        if (mixin.location.equals(InjectLocation.MATCH_TYPE)
                && mixin.match_opcode(opcode)
                && type.equals(mixin.matcher.type_class().getName().replace(".", "/"))) {
            callMethod(mixin);
        } else if (mixin.isRedirect()) {
            super.visitTypeInsn(opcode, type);
        }
        if (!mixin.matcher.match_after() && mixin.isInject()) {
            super.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        if (mixin.matcher.match_after() && mixin.isInject()) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
        if (mixin.location.equals(InjectLocation.MATCH_FIELD)
                && mixin.match_opcode(opcode)
                && (owner.equals(mixin.matcher.field_parent().getName().replace('.', '/')) || mixin.matcher.field_parent().equals(void.class))
                && name.equals(mixin.fieldName)
                && (descriptor.equals(Type.getDescriptor(mixin.matcher.field_class())) || mixin.matcher.field_class().equals(void.class))) {
            callMethod(mixin);
        } else if (mixin.isRedirect()) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
        if (!mixin.matcher.match_after() && mixin.isInject()) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (mixin.location.equals(InjectLocation.TAIL) && this.isAReturn(opcode) && mixin.isInject()) {
            this.callMethod(this.mixin);
        }
        super.visitInsn(opcode);
    }

    private void callMethod(MixinMethod mixin) {

        Method methodToCall = mixin.methodToCall;

        StringBuilder sb = new StringBuilder();
        if (!mixin.isRedirect()) {
            sb.append("(");
            {
                int methodI = 0;
                for (Class<?> clazz : methodToCall.getParameterTypes()) {
                    switch (clazz.getName()) {
                        case "int" -> {
                            sb.append("I");
                            super.visitVarInsn(ILOAD, methodI);
                        }
                        case "long" -> {
                            sb.append("J");
                            super.visitVarInsn(LLOAD, methodI);
                            methodI++;
                        }
                        case "float" -> {
                            sb.append("F");
                            super.visitVarInsn(FLOAD, methodI);
                        }
                        case "double" -> {
                            sb.append("D");
                            super.visitVarInsn(DLOAD, methodI);
                        }
                        case "byte" -> {
                            sb.append("B");
                            super.visitVarInsn(ILOAD, methodI);
                        }
                        case "char" -> {
                            sb.append("C");
                            super.visitVarInsn(ILOAD, methodI);
                        }
                        case "short" -> {
                            sb.append("S");
                            super.visitVarInsn(ILOAD, methodI);
                        }
                        case "boolean" -> {
                            sb.append("Z");
                            super.visitVarInsn(ILOAD, methodI);
                        }
                        case "" -> {}
                        default -> {
                            sb.append("L").append(clazz.getName().replace(".", "/")).append(";");
                            // investigate if variadic needed here
                            super.visitVarInsn(ALOAD, methodI);
                        }
                    }
                    methodI++;
                }
            }
            sb.append(")");
            switch (methodToCall.getReturnType().getName()) {
                case "int" -> sb.append("I");
                case "long" -> sb.append("J");
                case "float" -> sb.append("F");
                case "double" -> sb.append("D");
                case "byte" -> sb.append("B");
                case "char" -> sb.append("C");
                case "short" -> sb.append("S");
                case "boolean" -> sb.append("Z");
                case "void" -> sb.append("V");
                default -> sb.append("L").append(methodToCall.getReturnType().getName().replace(".", "/")).append(";");
            }
        } else {
            sb.append(Type.getMethodDescriptor(methodToCall));
        }

        mixin.loaded = true;

        if (!mixin.isRedirect()) {
            Label label1 = new Label();
            this.visitLabel(label1);
        }

        super.visitMethodInsn(
                INVOKESTATIC,
                methodToCall.getDeclaringClass().getName().replace('.', '/'),
                methodToCall.getName(),
                sb.toString(),
                false
        );

        if (mixin.returnFromHook) {
            if (!mixin.methodToCall.getReturnType().equals(MixinReturnObject.class)) {
                return;
            }

            Method shouldReturn = null, toReturn = null;
            for (Method me : MixinReturnObject.class.getDeclaredMethods()) {
                if (me.isAnnotationPresent(ShouldReturn.class)) {
                    shouldReturn = me;
                }
                if (me.isAnnotationPresent(ToReturn.class)) {
                    toReturn = me;
                }
            }
            boolean pop = shouldReturn == null;
            if (toReturn == null) {
                pop = true;
            }

            if (pop) {
                super.visitInsn(POP);
                return;
            }

            String owner = MixinReturnObject.class.getName().replace('.', '/');

            int index = MixinMethodVisitor.index--;

            super.visitVarInsn(
                    ASTORE,
                    index
            );

            Label label2 = new Label();
            this.visitLabel(label2);
            super.visitVarInsn(
                    ALOAD,
                    index
            );

            super.visitMethodInsn(
                    INVOKEVIRTUAL,
                    owner,
                    shouldReturn.getName(),
                    Type.getMethodDescriptor(shouldReturn),
                    false
            );

            Label label3 = new Label();
            super.visitJumpInsn(
                    IFEQ,
                    label3
            );

            if (!mixin.returnType.equals(void.class)) {

                Label label4 = new Label();
                this.visitLabel(label4);
                super.visitVarInsn(
                        ALOAD,
                        index
                );

                super.visitMethodInsn(
                        INVOKEVIRTUAL,
                        owner,
                        toReturn.getName(),
                        Type.getMethodDescriptor(toReturn),
                        false
                );

                Class<?> type = primToWrapper.containsKey(mixin.returnType) ? primToWrapper.get(mixin.returnType) : mixin.returnType;
                super.visitTypeInsn(
                        CHECKCAST,
                        type.getName().replace('.', '/')
                );

                if (wrapperPrimMethod.containsKey(mixin.returnType)) {
                    Method primitiveType = wrapperPrimMethod.get(mixin.returnType);

                    super.visitMethodInsn(
                            INVOKEVIRTUAL,
                            primitiveType.getDeclaringClass().getName().replace('.', '/'),
                            primitiveType.getName(),
                            Type.getMethodDescriptor(primitiveType),
                            false
                    );
                }

            } else {
                Label label4 = new Label();
                this.visitLabel(label4);
            }

            switch (mixin.returnType.getName()) {
                case "int", "byte", "char", "short", "boolean" -> super.visitInsn(IRETURN);
                case "long" -> super.visitInsn(LRETURN);
                case "float" -> super.visitInsn(FRETURN);
                case "double" -> super.visitInsn(DRETURN);
                case "void" -> super.visitInsn(RETURN);
                default -> super.visitInsn(ARETURN);
            }

            this.visitLabel(label3);
        }
    }

    private boolean isAReturn(int opcode) {
        switch (opcode) {
            case IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}