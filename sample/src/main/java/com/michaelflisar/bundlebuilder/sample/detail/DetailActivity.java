package com.michaelflisar.bundlebuilder.sample.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.base.BaseDetailActivity;

@BundleBuilder(createListOfArgs = true, useConstructorForMandatoryArgs = false)
public class DetailActivity extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg
    String stringArg;
    @Arg @Nullable
    String nullableRequiredStringArg;
    @Arg("withCustomArgSetterName") @Nullable
    String mWithCustomArgSetterName;

    @Arg
    Integer integerArg;
    @Arg
    int intArg;
    @Arg
    boolean boolArg;
    @Arg(optional = true)
    Boolean optionalBoolArgs;
    @Arg(optional = true) @Nullable
    Boolean optionalNullableBoolArgs;


    // --------------
    // Activity
    // --------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DetailActivityBundleBuilder.inject(getIntent().getExtras(), this);

        addArgsToList();
    }
}