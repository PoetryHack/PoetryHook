/**
 * Created: 08.1.2024
 */

package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * @since 1.0.0
 * @author sootysplash
 */
public class MixinClassWriter extends ClassWriter {
    public MixinClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        return super.getCommonSuperClass(type1, type2);
    }
}
