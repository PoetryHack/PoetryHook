package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM9;

public class MixinClassVisitor extends ClassVisitor {
    private final String methodName;
    private final String methodSignature;
    private final MixinMethod mixin;
    private final boolean isPost;

    public MixinClassVisitor(ClassVisitor classVisitor, MixinMethod mixin, String methodName, String methodSignature, boolean isPost) {
        super(ASM9, classVisitor);
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.mixin = mixin;
        this.isPost = isPost;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//        boolean wtf = signature == null; //this code causes issues
        if (name.equals(this.methodName) && (/*wtf || */descriptor.equals(this.methodSignature))) {
            if (!isPost) {
                return new MixinMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions), this.mixin);
            } else {
                return new PostMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
