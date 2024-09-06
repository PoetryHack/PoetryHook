/**
 * Created: 03.29.2024
 */

package net.poetryhack.poetryhook;

import net.poetryhack.poetryhook.annotations.Inject;
import net.poetryhack.poetryhook.annotations.Mixin;
import net.poetryhack.poetryhook.annotations.Redirect;
import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import net.poetryhack.poetryhook.util.MixinMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Interface to implement when creating a new mixin class
 *
 * @author majorsopa, fully rewritten by sootysplash
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface MixinBase {

    /**
     * @return ArrayList of {@link MixinMethod} contained by this mixin
     * @since 1.0.0
     * @author sootysplash
     */
    default ArrayList<MixinMethod> mixins() {
        ArrayList<MixinMethod> mixinsToReturn = new ArrayList<>();

        if (!this.getClass().isAnnotationPresent(Mixin.class)) {
            throw new PoetryHookException("Mixin declared without @Mixin annotation!\nClass: " + this.getClass().getName());// revised by major sopa
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
