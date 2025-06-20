package com.jparkkennaby.store.annoations;

import java.lang.annotation.*;

import com.jparkkennaby.store.entities.Role;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateJwt {
    Role[] value() default {}; // No role restriction by default
}
