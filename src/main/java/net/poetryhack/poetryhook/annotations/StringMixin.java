/**
 * Created: 09.06.2024
 */

package net.poetryhack.poetryhook.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @since 1.0.0
 * @author majorsopa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StringMixin {
    /**
     * The binary class name of the class to inject into, e.g. java.lang.String
     */
    String value();
}
