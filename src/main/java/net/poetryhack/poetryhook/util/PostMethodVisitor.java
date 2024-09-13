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
 * @author sootysplash
 * @since 1.0.0
 */
public final class PostMethodVisitor extends MethodVisitor {
    private final HashMap<String, Integer> label2int = new HashMap<>();
    private final StringBuffer sb = new StringBuffer();
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
        sb.append("FRAME: ").append(type).append(" | ").append(numLocal).append(" | ").append(Arrays.toString(local)).append(" | ").append(numStack).append(" | ").append(Arrays.toString(stack)).append("\n");
        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitInvokeDynamicInsn(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        sb.append("DYNAMIC: ").append(name).append(" | ").append(descriptor).append(" | ").append(bootstrapMethodHandle).append(" | ").append(Arrays.toString(bootstrapMethodArguments)).append("\n");
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isInterface) {
        sb.append("METHOD: ").append(getOpcodeName(opcode)).append(" | ").append(owner).append(" | ").append(name).append(" ").append(descriptor).append("\n");
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(final int opcode) {
        sb.append("INSN: ").append(getOpcodeName(opcode)).append("\n");
        super.visitInsn(opcode);
    }

    @Override
    public void visitLdcInsn(final Object value) {
        sb.append("LDC: ").append(value).append("\n");
        super.visitLdcInsn(value);
    }

    @Override
    public void visitVarInsn(final int opcode, final int varIndex) {
        sb.append("VAR: ").append(getOpcodeName(opcode)).append(" index: ").append(varIndex).append("\n");
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        sb.append("TYPE: ").append(getOpcodeName(opcode)).append(" type: ").append(type).append("\n");
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(
            final int opcode, final String owner, final String name, final String descriptor) {
        sb.append("FIELD: ").append(getOpcodeName(opcode)).append(" ").append(owner).append(" ").append(name).append(" ").append(descriptor).append("\n");
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        sb.append("JUMP: ").append(getOpcodeName(opcode)).append(" TO: ").append(label2int.get(label.toString())).append("\n");
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(final Label label) {
        String name = label.toString();
        if (!label2int.containsKey(name)) {
            label2int.put(name, min++);
        }
        sb.append("LABEL: ").append(label2int.get(name)).append("\n");
        super.visitLabel(label);
    }

    @Override
    public void visitIincInsn(final int varIndex, final int increment) {
        sb.append("IInc: ").append(varIndex).append(" | ").append(increment).append("\n");
        super.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        sb.append("IntInsn: ").append(getOpcodeName(opcode)).append(" | ").append(operand).append("\n");
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitLocalVariable(
            final String name,
            final String descriptor,
            final String signature,
            final Label start,
            final Label end,
            final int index) {
        sb.append("LOCAL: ").append(name).append(" | ").append(descriptor).append(" | ").append(signature).append(" | ").append(label2int.get(start.toString())).append(" | ").append(label2int.get(end.toString())).append(" | ").append(index).append("\n");
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        sb.append("MAXS: ").append(maxStack).append(" | ").append(maxLocals).append("\n");
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        System.out.println(sb + "\n");
        super.visitEnd();
    }

    private String getOpcodeName(int opcode) {
        String opName = String.valueOf(opcode);
        for (Field f : Opcodes.class.getFields()) {
            try {
                f.setAccessible(true);
                if (((int) f.get(null)) == opcode
                        && f.getType() == int.class
                        && f.getName().charAt(0) != 'V'
                        && !f.getName().startsWith("H_")
                        && !f.getName().startsWith("F_")
                        && !f.getName().startsWith("ACC_")
                        && !f.getName().startsWith("T_")) {
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
