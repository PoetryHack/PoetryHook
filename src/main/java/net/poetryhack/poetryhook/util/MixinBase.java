package net.poetryhack.poetryhook.util;

import net.poetryhack.poetryhook.annotations.Inject;
import net.poetryhack.poetryhook.annotations.Mixin;
import net.poetryhack.poetryhook.annotations.Redirect;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.ArrayList;

public interface MixinBase extends Opcodes {

    default ArrayList<MixinMethod> mixins() {
        ArrayList<MixinMethod> m = new ArrayList<>();
        if (!this.getClass().isAnnotationPresent(Mixin.class)) {
            throw new IllegalStateException("Mixin declared without @Mixin annotation!\nClass: " + this.getClass().getName());
        }
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class)
                    || method.isAnnotationPresent(Redirect.class)) {
                m.add(new MixinMethod(method));
            }
        }
        return m;
    }
}
