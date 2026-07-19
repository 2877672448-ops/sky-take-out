package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    OperationType value() default OperationType.INSERT;

}
