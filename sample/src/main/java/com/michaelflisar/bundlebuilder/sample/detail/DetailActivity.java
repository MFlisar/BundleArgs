package com.michaelflisar.bundlebuilder.sample.detail;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.base.BaseDetailActivity;

@BundleBuilder(useConstructorForMandatoryArgs = false, generatePersist = true)
public class DetailActivity extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg
    String stringArg;
    @Arg @Nullable
    String nullableRequiredStringArg;
    @Arg(name = "withCustomArgSetterName") @Nullable
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
        // inject with default values
        DetailActivityBundleBuilder.inject(getIntent().getExtras(), this);
        // or optionally, if you have saved all fields in onSaveInstanceState:
        // DetailActivityBundleBuilder.inject(savedInstanceState != null ? savedInstanceState : getIntent().getExtras(), this);

        addArgsToList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // optional: this will save all fields into the state
        // DetailActivityBundleBuilder.persist(outState, this);
    }
}