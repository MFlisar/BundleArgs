package com.michaelflisar.bundlebuilder.sample.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.base.BaseDetailActivity;

@BundleBuilder(useConstructorForMandatoryArgs = false, setterPrefix = "with")
public class DetailActivity3 extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg
    String stringArg;
    @Arg @Nullable
    String nullableStringArg;
    @Arg("customArgSetterName") @Nullable
    String mValue;
    @Arg
    Integer integerArg;
    @Arg
    boolean boolArg;
    @Arg(optional = true)
    Boolean optionalBoolArgs;
    @Arg(optional = true) @Nullable
    Boolean optionalNullableBoolArgs;
    @Arg(optional = true) @Nullable
    String optionalNullableWithDefaultValue = "default value";
    @Arg(optional = true) @Nullable
    String optionalNullableWithOverwrittenDefaultValue = "default value";

    // --------------
    // Activity
    // --------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DetailActivity3BundleBuilder.inject(getIntent().getExtras(), this);

        addArgsToList();
    }
}