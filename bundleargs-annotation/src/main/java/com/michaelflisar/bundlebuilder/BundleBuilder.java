package com.michaelflisar.bundlebuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.CLASS)
public @interface BundleBuilder
{
    boolean useConstructorForMandatoryArgs() default false;
    String setterPrefix() default "";
    boolean alwaysAddIntentBuilder() default false;
}
