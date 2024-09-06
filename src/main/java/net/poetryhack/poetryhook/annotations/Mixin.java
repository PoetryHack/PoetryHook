/**
 * Created: 05.29.2024
 */

package net.poetryhack.poetryhook.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @since 1.0.0
 * @author sootysplash
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mixin {
    Class<?> value();
}
