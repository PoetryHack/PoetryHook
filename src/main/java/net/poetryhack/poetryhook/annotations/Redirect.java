/**
 * Created: 07.11.2024
 */

package net.poetryhack.poetryhook.annotations;

import net.poetryhack.poetryhook.exceptions.DefaultExceptionHandler;
import net.poetryhack.poetryhook.exceptions.PoetryExceptionHandler;
import net.poetryhack.poetryhook.util.InjectLocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sootysplash
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Redirect {

    InjectLocation injectLocation();

    String value();

    Class<?>[] toHookArgs() default {};

    boolean forceUseAnnotationArgs() default false;

    Matcher matcher() default @Matcher;

    Class<? extends PoetryExceptionHandler> exceptionHandler() default DefaultExceptionHandler.class;

}
