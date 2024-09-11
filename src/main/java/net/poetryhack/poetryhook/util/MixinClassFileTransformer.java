/**
 * Created: 03.29.2024
 */

package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;

/**
 * @since 1.0.0
 * @author majorsopa, revised by sootysplash
 */
public final class MixinClassFileTransformer implements ClassFileTransformer {
    private final MixinMethod mixin;
    private final String className;
    private final String methodName;
    private final String methodSig;

    public MixinClassFileTransformer(MixinMethod mixin) {
        this.mixin = mixin;
        this.methodName = this.mixin.methodName; // revised by sootysplash
        this.className = mixin.injectTo.getName().replace(".", "/"); // revised by sootysplash
        StringBuilder methodSigSb = new StringBuilder("(");

        // sootysplash start
        Class<?>[] annotation = mixin.annotation.toHookArgs;
        boolean isAnnotation = annotation.length != 0 || mixin.annotation.forceUseAnnotationArgs;
        Class<?>[] params = this.mixin.methodToCall.getParameterTypes();
        if (params.length > 0 && !isAnnotation && params[0] == this.mixin.injectTo) {  // modded by majorsopa to avoid ArrayIndexOutOfBoundsException
            params = Arrays.copyOfRange(params, 1, params.length);
        }
        Class<?>[] clazzes = isAnnotation ? annotation : params;
        // sootysplash end

        for (Class<?> clazz : clazzes) {
            switch (clazz.getName()) {
                case "int":
                    methodSigSb.append("I");
                    break;
                case "long":
                    methodSigSb.append("J");
                    break;
                case "float":
                    methodSigSb.append("F");
                    break;
                case "double":
                    methodSigSb.append("D");
                    break;
                case "byte":
                    methodSigSb.append("B");
                    break;
                case "char":
                    methodSigSb.append("C");
                    break;
                case "short":
                    methodSigSb.append("S");
                    break;
                case "boolean":
                    methodSigSb.append("Z");
                    break;
                default:
                    methodSigSb.append("L");
                    methodSigSb.append(clazz.getName().replace(".", "/"));
                    methodSigSb.append(";");

            }
        }
        methodSigSb.append(")");


        Class<?> retType = mixin.returnType;
        switch (retType.getName()) {
            case "int":
                methodSigSb.append("I");
                break;
            case "long":
                methodSigSb.append("J");
                break;
            case "float":
                methodSigSb.append("F");
                break;
            case "double":
                methodSigSb.append("D");
                break;
            case "byte":
                methodSigSb.append("B");
                break;
            case "char":
                methodSigSb.append("C");
                break;
            case "short":
                methodSigSb.append("S");
                break;
            case "boolean":
                methodSigSb.append("Z");
                break;
            case "void":
                methodSigSb.append("V");
                break;
            default:
                methodSigSb.append("L");
                methodSigSb.append(retType.getName().replace(".", "/"));
                methodSigSb.append(";");

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
            } catch (Exception e) {
                e.printStackTrace(System.err); // revised by sootysplash, simply rethrowing the exception will cause the exception to be lost (and harder to debug)
            }
        }

        return classfileBuffer;
    }
}
