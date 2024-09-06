package net.poetryhack.poetryhook;

import net.poetryhack.poetryhook.annotations.Inject;
import net.poetryhack.poetryhook.annotations.Mixin;
import net.poetryhack.poetryhook.annotations.Redirect;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import net.poetryhack.poetryhook.util.MixinMethod;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("unused")
public interface MixinBase extends Opcodes {

    default ArrayList<MixinMethod> mixins() {
        ArrayList<MixinMethod> mixinsToReturn = new ArrayList<>();

        if (!this.getClass().isAnnotationPresent(Mixin.class)) {
            throw new PoetryHookException("Mixin declared without @Mixin annotation!\nClass: " + this.getClass().getName());
        }

        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class)
                    || method.isAnnotationPresent(Redirect.class))
            {
                mixinsToReturn.add(new MixinMethod(method));
            }
        }

        return mixinsToReturn;
    }
}
