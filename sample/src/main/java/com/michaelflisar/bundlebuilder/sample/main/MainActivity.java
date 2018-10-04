package com.michaelflisar.bundlebuilder.sample.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.Test;
import com.michaelflisar.bundlebuilder.sample.TestBundleBuilder;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivity2BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivity3BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivityBundleBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btTry1 = (Button) findViewById(R.id.btTry1);
        Button btTry2 = (Button) findViewById(R.id.btTry2);
        Button btTry3 = (Button) findViewById(R.id.btTry3);
        Button btTry4 = (Button) findViewById(R.id.btTry4);
        btTry1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DetailActivityBundleBuilder builder = new DetailActivityBundleBuilder()
                        .stringArg("String Test Argument")
                        .integerArg(100)
                        .intArg(100)
                        .boolArg(true)
                        //.optionalStringArg("Optional Arg")
                        .withCustomArgSetterName("Custom Setter Name Argument");
                // Variant 1: Intent + pass use the intent
//                Intent i = builder.buildIntent(MainActivity.this);
//                startActivity(i);

                // Variante 2: use builders startActivity function
                builder.startActivity(MainActivity.this);

                // Variant 3: create bundle and use this if you need a bundle
//                Bundle b = builder.build();

                // Variant 4: create a class, therefore the annotated class MUST have a constructor with one argument which is of type Bundle!!!
                 Test t = new TestBundleBuilder()
                         .id(1L)
                         .optionalValue("test")
                         .value("test2")
                         .create();


            }
        });
        btTry2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new DetailActivity2BundleBuilder("String Test Argument", null, "custom name args", 100, true)
//                        .optionalBoolArgs(true)
//                        .optionalNullableBoolArgs(null)
                        .buildIntent(MainActivity.this);
                startActivity(i);
            }
        });
        btTry3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DetailActivity3BundleBuilder()
                        .withBoolArg(true)
                        .withIntegerArg(1)
                        .withNullableStringArg(null)
                        .withOptionalBoolArgs(null)
                        .withStringArg("test")
                        .withCustomArgSetterName("custom setter name value")
//                        .withOptionalNullableWithDefaultValue("overwritten default value")
                        .withOptionalNullableWithOverwrittenDefaultValue("overwritten default value")
                        .startActivity(MainActivity.this);
            }
        });
        btTry4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    // we missed setting some mandatoray fields => exception will be thrown!
                    Intent i = new DetailActivityBundleBuilder()
                            .buildIntent(MainActivity.this);
                    startActivity(i);
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
