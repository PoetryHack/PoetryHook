/**
 * Created: 07.11.2024
 */

package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.ASM9;

/**
 * @since 1.0.0
 * @author sootysplash
 */
public final class PostMethodVisitor extends MethodVisitor {
    private final HashMap<String, Integer> label2int = new HashMap<>();
    private int min = 0;
    private final StringBuffer sb = new StringBuffer();
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
        sb.append("FRAME: " + type + " | " + numLocal + " | " + Arrays.toString(local) + " | " + numStack + " | " + Arrays.toString(stack) + "\n");
        super.visitFrame(type, numLocal, local, numStack, stack);
    }
    @Override
    public void visitInvokeDynamicInsn(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        sb.append("DYNAMIC: " + name + " | " + descriptor + " | " + bootstrapMethodHandle + " | " + Arrays.toString(bootstrapMethodArguments) + "\n");
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }
    @Override
    public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isInterface) {
        sb.append("METHOD: " + getOpcodeName(opcode) + " | " + owner + " | " + name + " " + descriptor + "\n");
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
    @Override
    public void visitInsn(final int opcode) {
        sb.append("INSN: " + getOpcodeName(opcode) + "\n");
        super.visitInsn(opcode);
    }
    @Override
    public void visitVarInsn(final int opcode, final int varIndex) {
        sb.append("VAR: " + getOpcodeName(opcode) + " index: " + varIndex + "\n");
        super.visitVarInsn(opcode, varIndex);
    }
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        sb.append("TYPE: " + getOpcodeName(opcode) + " type: " + type + "\n");
        super.visitTypeInsn(opcode, type);
    }
    @Override
    public void visitFieldInsn(
            final int opcode, final String owner, final String name, final String descriptor){
        sb.append("FIELD: " + getOpcodeName(opcode) + " " + owner + " " + name + " " + descriptor + "\n");
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        sb.append("JUMP: " + getOpcodeName(opcode) + " TO: " + label2int.get(label.toString()) + "\n");
        super.visitJumpInsn(opcode, label);
    }
    @Override
    public void visitLabel(final Label label) {
        String name = label.toString();
        if (!label2int.containsKey(name)) {
            label2int.put(name, min++);
        }
        sb.append("LABEL: " + label2int.get(name) + "\n");
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
        sb.append("LOCAL: " + name + " | " + descriptor + " | " + signature + " | " + label2int.get(start.toString()) + " | " + label2int.get(end.toString()) + " | " + index + "\n");
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        sb.append("MAXS: " + maxStack + " | " + maxLocals + "\n");
        super.visitMaxs(maxStack, maxLocals);
    }
    @Override
    public void visitEnd() {
        System.out.println(sb +"\n");
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
                throw new PoetryHookException(e); //revised by majorsopa
            }
        }
        return opName;
    }
}
