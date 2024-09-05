package net.poetryhack.poetryhook.annotations;

import net.poetryhack.poetryhook.util.InjectLocation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Inject {

    InjectLocation injectLocation() default InjectLocation.HEAD;

    String value();

    Class<?>[] toHookArgs() default {};

    boolean forceUseAnnotationArgs() default false;

    boolean returnFromHook() default false;

    Matcher matcher() default @Matcher;

}
