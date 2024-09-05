package net.poetryhack.poetryhook.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class MixinClassWriter extends ClassWriter {
    public MixinClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
//        try {
            return super.getCommonSuperClass(type1, type2);
        /*} catch (Exception e) {
//            System.out.println("Exception when getting super common class:" + type1 + "|" + type2);
            try {
                Class<?> c = Class.forName("org.spongepowered.asm.mixin.transformer.ClassInfo");
                Method m1 = c.getMethod("getCommonSuperClass", String.class, String.class);
                m1.setAccessible(true);
                Object o = m1.invoke(null, type1, type2);
                Method m2 = c.getMethod("getName");
                m2.setAccessible(true);
                String str = (String) m2.invoke(o);
                System.out.println("Sponge mixin superclass:" + str);
            } catch (Exception ex) {
                e.addSuppressed(e);
            }
            throw new RuntimeException(e);
        }*/
    }

}
