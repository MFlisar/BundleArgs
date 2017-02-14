package com.michaelflisar.bundlebuilder.sample.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.base.BaseDetailActivity;

@BundleBuilder(createListOfArgs = true, useConstructorForMandatoryArgs = true)
public class DetailActivity2 extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg
    String stringArg;
    @Arg @Nullable
    String optionalStringArg;
    @Arg("withCustomArgSetterName") @Nullable
    String mWithCustomArgSetterName;
    @Arg
    Integer integerArg;
    @Arg
    boolean boolArg;

    // --------------
    // Activity
    // --------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DetailActivity2BundleBuilder.inject(getIntent().getExtras(), this);
        setContentView(R.layout.activity_detail);

        addArgsToList();
    }
}