/**
 * Created: 03.29.2024
 */

package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM9;

/**
 * @author majorsopa, revised by sootysplash
 * @since 1.0.0
 */
public final class MixinClassVisitor extends ClassVisitor {
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
        if (name.equals(this.methodName) && descriptor.equals(this.methodSignature)) {
            // sootysplash start
            if (isPost) {
                return new PostMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));
            }
            // sootysplash end
            return new MixinMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions), this.mixin);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
