package com.jparkkennaby.store.annoations;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD }) // Allow on class and methods
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaxTableSizeCheck {
    Class<?> entity(); // The entity type to count

    // Optional key if the entity class name doesn't match config
    // String name() default "";
}
