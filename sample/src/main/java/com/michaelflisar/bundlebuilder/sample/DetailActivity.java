package com.michaelflisar.bundlebuilder.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;

import java.util.ArrayList;
import java.util.List;

@BundleBuilder(createListOfArgs = true, useConstructorForMandatoryArgs = false)
public class DetailActivity extends BaseDetailActivity
{
    // --------------
    // Arguments
    // --------------

    @Arg String stringArg;
    @Arg @Nullable String optionalStringArg;
    @Arg("withCustomArgSetterName") @Nullable String mWithCustomArgSetterName;
    @Arg Integer integerArg;

    // --------------
    // Activity
    // --------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DetailActivityBundleBuilder.inject(getIntent().getExtras(), this);
        setContentView(R.layout.activity_detail);

        addArgsToList();
    }
}