package com.michaelflisar.bundlebuilder;

public @interface BundleBuilder
{
    boolean createListOfArgs() default false;
    boolean useConstructorForMandatoryArgs() default false;
}
