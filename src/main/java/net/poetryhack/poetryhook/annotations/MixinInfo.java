/**
 * Created: 07.11.2024
 */

package net.poetryhack.poetryhook.annotations;

import net.poetryhack.poetryhook.exceptions.PoetryHookException;
import net.poetryhack.poetryhook.util.InjectLocation;
import net.poetryhack.poetryhook.util.MixinType;

import java.lang.reflect.Method;

/**
 * @author sootysplash
 * @since 1.0.0
 */
public final class MixinInfo {
    public final InjectLocation location;
    public final String value;
    public final Class<?>[] toHookArgs;
    public final boolean returnFromHook;
    public final boolean forceUseAnnotationArgs;
    public final Matcher matcher;
    public final MixinType mixinType;
    public boolean isPost;
    private MixinInfo(InjectLocation location,
                      String value,
                      Class<?>[] toHookArgs,
                      boolean returnFromHook,
                      boolean forceUseAnnotationArgs,
                      Matcher matcher,
                      MixinType mixinType) {
        this.location = location;
        this.value = value;
        this.toHookArgs = toHookArgs;
        this.returnFromHook = returnFromHook;
        this.forceUseAnnotationArgs = forceUseAnnotationArgs;
        this.matcher = matcher;
        this.mixinType = mixinType;
    }

    public static MixinInfo fromInject(Inject inject) {
        return new MixinInfo(inject.injectLocation(),
                inject.value(),
                inject.toHookArgs(),
                inject.returnFromHook(),
                inject.forceUseAnnotationArgs(),
                inject.matcher(),
                MixinType.Inject);
    }

    public static MixinInfo fromRedirect(Redirect redirect) {
        return new MixinInfo(redirect.injectLocation(),
                redirect.value(),
                redirect.toHookArgs(),
                false,
                redirect.forceUseAnnotationArgs(),
                redirect.matcher(),
                MixinType.Redirect);
    }

    public static MixinInfo get(Method method) {
        if (method.isAnnotationPresent(Inject.class)) {
            return fromInject(method.getAnnotation(Inject.class)).setPostIfPresent(method);
        } else if (method.isAnnotationPresent(Redirect.class)) {
            return fromRedirect(method.getAnnotation(Redirect.class)).setPostIfPresent(method);
        } else {
            throw new PoetryHookException("Method passed without @Inject or @Redirect!\n" +
                    "Method: " + method.getName() + " Declaring Class: " + method.getDeclaringClass().getName());
        }
    }

    private MixinInfo setPostIfPresent(Method method) {
        if (method.isAnnotationPresent(CheckPost.class)) {
            this.isPost = true;
        }
        return this;
    }
}
