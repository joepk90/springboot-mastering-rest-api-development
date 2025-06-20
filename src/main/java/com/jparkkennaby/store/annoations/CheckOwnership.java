package com.jparkkennaby.store.annoations;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckOwnership {
    Class<?> entity(); // e.g., Product.class, Order.class
}
