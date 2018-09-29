package com.michaelflisar.bundlebuilder.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.michaelflisar.bundlebuilder.Arg;
import com.michaelflisar.bundlebuilder.BundleBuilder;

/**
 * Created by Michael on 15.02.2017.
 */

@BundleBuilder
public class Test
{
    @Arg
    Long id;
    @Arg
    String value;
    @Arg @Nullable
    String optionalValue;

    public Test(Bundle args)
    {
        TestBundleBuilder.inject(args, this);
    }
}
