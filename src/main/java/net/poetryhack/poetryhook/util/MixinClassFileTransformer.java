/**
 * Created: 03.29.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.ObjectWrapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author majorsopa, revised by sootysplash
 * @since 1.0.0
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
        Class<?>[] params;
        // todo fix the fact this is duplicated in MixinMethod.java
        {
            Parameter[] paramsArray = this.mixin.methodToCall.getParameters();
            ArrayList<Class<?>> paramsArrayList = new ArrayList<>();
            for (Parameter param : paramsArray) {
                Class<?> classToAdd;

                if (param.isAnnotationPresent(ObjectWrapper.class)) {
                    try {
                        classToAdd = MixinMethod.class.getClassLoader().loadClass(param.getAnnotation(ObjectWrapper.class).value());  // todo make it so this isn't hardcoded to this classloader
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
                    ClassReader postReader = new ClassReader(data);
                    MixinClassWriter postWriter = new MixinClassWriter(postReader, ClassWriter.COMPUTE_FRAMES);
                    postReader.accept(new MixinClassVisitor(postWriter, this.mixin, this.methodName, this.methodSig, true), 0);
                    postWriter.visitEnd();
                    data = postWriter.toByteArray();
                }

                return data;
            } catch (Exception e) {
                System.err.println("err transforming class " + className + ": " + e.getMessage());
                e.printStackTrace(System.err);
                throw new PoetryHookException("failed to transform class " + className, e);
            }
        }

        return classfileBuffer;
    }
}
