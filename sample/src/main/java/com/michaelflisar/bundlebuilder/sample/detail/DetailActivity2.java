package com.michaelflisar.bundlebuilder.sample.detail;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.base.BaseDetailActivity;

import java.util.ArrayList;

@BundleBuilder(useConstructorForMandatoryArgs = true)
public class DetailActivity2 extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg
    String stringArg;
    @Arg @Nullable
    String nullableStringArg;
    @Arg(name = "withCustomArgSetterName") @Nullable
    String mWithCustomArgSetterName;
    @Arg
    Integer integerArg;
    @Arg
    boolean boolArg;
    @Arg(optional = true)
    Boolean optionalBoolArgs;
    @Arg(optional = true) @Nullable
    Boolean optionalNullableBoolArgs;
    @Arg(optional = true) @Nullable
    ArrayList<String> stringArrayListArgs;

    // --------------
    // Activity
    // --------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DetailActivity2BundleBuilder.inject(getIntent().getExtras(), this);

        addArgsToList();
    }
}