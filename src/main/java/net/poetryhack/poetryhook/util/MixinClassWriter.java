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
//        try {
            return super.getCommonSuperClass(type1, type2);
        /*} catch (Exception e) {
            System.err.println("Exception when getting super common class:" + type1 + "|" + type2);
            try {
                Class<?> c = Class.forName("org.spongepowered.asm.mixin.transformer.ClassInfo");
                Method m1 = c.getMethod("getCommonSuperClass", String.class, String.class);
                m1.setAccessible(true);
                Object o = m1.invoke(null, type1, type2);
                Method m2 = c.getMethod("getName");
                m2.setAccessible(true);
                return (String) m2.invoke(o);
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            throw new PoetryHookException(e);
        }*/
    }

}
