/**
 * Created: 07.11.2024
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
@Target(ElementType.LOCAL_VARIABLE)
public @interface Matcher {
    int match_opcode() default -1;

    boolean match_after() default false;

    //MATCH_METHOD
    Class<?> method_class() default void.class;

    String method_name() default "";

    Class<?>[] method_parameters() default {};

    //MATCH_FIELD
    Class<?> field_parent() default void.class;

    Class<?> field_class() default void.class;

    String field_name() default "";

    //MATCH_TYPE
    Class<?> type_class() default void.class;
}
