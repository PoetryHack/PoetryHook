/**
 * Created: 09.12.2024
 */

package net.poetryhack.poetryhook.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author majorsopa
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ObjectWrapper {
    /**
     * The binary class name of the class to wrap, e.g. java.lang.String
     */
    String value();
}