package com.ikasan.component.factory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IkasanComponent {
    String prefix() default "";

    String factoryPrefix() default "";

    String suffix() default "";
}
