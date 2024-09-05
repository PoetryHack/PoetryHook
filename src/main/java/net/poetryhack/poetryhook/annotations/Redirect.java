package net.poetryhack.poetryhook.annotations;

import net.poetryhack.poetryhook.util.InjectLocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Redirect {

    InjectLocation injectLocation();

    String value();

    Class<?>[] toHookArgs() default {};

    boolean forceUseAnnotationArgs() default false;

    Matcher matcher() default @Matcher;

}
