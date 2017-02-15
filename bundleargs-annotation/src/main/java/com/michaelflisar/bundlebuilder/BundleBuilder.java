package com.michaelflisar.bundlebuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.CLASS)
public @interface BundleBuilder
{
    boolean createListOfArgs() default false;
    boolean useConstructorForMandatoryArgs() default false;
    String setterPrefix() default "";
}
