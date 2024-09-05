package net.poetryhack.poetryhook.util;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.ASM9;

public class PostMethodVisitor extends MethodVisitor {
    private final HashMap<String, Integer> label2int = new HashMap<>();
    private int min = 0;
    public PostMethodVisitor(MethodVisitor methodVisitor) {
        super(ASM9, methodVisitor);
    }
    @Override
    public void visitFrame(
            final int type,
            final int numLocal,
            final Object[] local,
            final int numStack,
            final Object[] stack) {
        super.visitFrame(type, numLocal, local, numStack, stack);
    }
    @Override
    public void visitInvokeDynamicInsn(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }
    @Override
    public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
    @Override
    public void visitInsn(final int opcode) {
        super.visitInsn(opcode);
    }
    @Override
    public void visitVarInsn(final int opcode, final int varIndex) {
        super.visitVarInsn(opcode, varIndex);
    }
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        super.visitTypeInsn(opcode, type);
    }
    @Override
    public void visitFieldInsn(
            final int opcode, final String owner, final String name, final String descriptor){
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {

        super.visitJumpInsn(opcode, label);
    }
    @Override
    public void visitLabel(final Label label) {
        String name = label.toString();
        if (!label2int.containsKey(name)) {
            label2int.put(name, min++);
        }
        super.visitLabel(label);
    }
    @Override
    public void visitLocalVariable(
            final String name,
            final String descriptor,
            final String signature,
            final Label start,
            final Label end,
            final int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }
    @Override
    public void visitEnd() {
        super.visitEnd();
    }
    private String getOpcodeName(int opcode) {
        String opName = String.valueOf(opcode);
        for (Field f : Opcodes.class.getFields()) {
            try {
                f.setAccessible(true);
                if (((int) f.get(null)) == opcode && f.getName().charAt(0) != 'V') {
                    opName = f.getName();
                    break;
                }
            } catch (Exception e) {
//                e.printStackTrace(System.out);
            }
        }
        return opName;
    }
}
