package com.jparkkennaby.store.annoations;

import java.lang.annotation.*;

import com.jparkkennaby.store.entities.Role;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IsAuthorized {
    Role[] value() default {}; // No role restriction by default
}
