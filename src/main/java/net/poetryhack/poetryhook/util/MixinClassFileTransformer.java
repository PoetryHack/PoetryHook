package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;

public class MixinClassFileTransformer implements ClassFileTransformer {
    private final MixinMethod mixin;
    private final String className;
    private final String methodName;
    private final String methodSig;

    public MixinClassFileTransformer(MixinMethod mixin) {
        this.mixin = mixin;
        this.methodName = this.mixin.methodName;
        this.className = mixin.injectTo.getName().replace(".", "/");
        // repeated code
        StringBuilder methodSigSb = new StringBuilder("(");

        Class<?>[] annotation = mixin.annotation.toHookArgs;
        boolean isAnnotation = annotation.length != 0 || mixin.annotation.forceUseAnnotationArgs;
        Class<?>[] params = this.mixin.methodToCall.getParameterTypes();
        if (!isAnnotation && params[0] == this.mixin.injectTo) {
            params = Arrays.copyOfRange(params, 1, params.length);
        }
        Class<?>[] clazzes = isAnnotation ? annotation : params;

        for (Class<?> clazz : clazzes) {
            switch (clazz.getName()) {
                case "int" -> methodSigSb.append("I");
                case "long" -> methodSigSb.append("J");
                case "float" -> methodSigSb.append("F");
                case "double" -> methodSigSb.append("D");
                case "byte" -> methodSigSb.append("B");
                case "char" -> methodSigSb.append("C");
                case "short" -> methodSigSb.append("S");
                case "boolean" -> methodSigSb.append("Z");
                case "" -> {}
                default -> {
                    methodSigSb.append("L").append(clazz.getName().replace(".", "/"));
//                    if (clazz.getTypeParameters().length > 0) methodSigSb.append("<*>");
                    methodSigSb.append(";");
                }
            }
        }
        methodSigSb.append(")");

        {
            Class<?> retType = mixin.returnType;
            switch (retType.getName()) {
                case "int" -> methodSigSb.append("I");
                case "long" -> methodSigSb.append("J");
                case "float" -> methodSigSb.append("F");
                case "double" -> methodSigSb.append("D");
                case "byte" -> methodSigSb.append("B");
                case "char" -> methodSigSb.append("C");
                case "short" -> methodSigSb.append("S");
                case "boolean" -> methodSigSb.append("Z");
                case "void" -> methodSigSb.append("V");
                default -> methodSigSb.append("L").append(retType.getName().replace(".", "/")).append(";");
            }
        }
        this.methodSig = methodSigSb.toString();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, java.security.ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.equals(this.className)) {
            try {
                ClassReader classReader = new ClassReader(classfileBuffer);
                final MixinClassWriter classWriter = new MixinClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
                classReader.accept(new MixinClassVisitor(classWriter, this.mixin, this.methodName, this.methodSig, false), 0);
                classWriter.visitEnd();

                byte[] data = classWriter.toByteArray();
                boolean post = this.mixin.annotation.isPost;
                if (post) {
                    ClassReader postReader = new ClassReader(Arrays.copyOf(data, data.length));
                    MixinClassWriter postWriter = new MixinClassWriter(postReader, 0);
                    postReader.accept(new MixinClassVisitor(postWriter, this.mixin, this.methodName, this.methodSig, true), 0);
                    postWriter.visitEnd();
                }

                return data;
            } catch (Exception e) {//else this silently throws an exception
//                e.printStackTrace();
            }
        }

        return classfileBuffer;
    }
}
