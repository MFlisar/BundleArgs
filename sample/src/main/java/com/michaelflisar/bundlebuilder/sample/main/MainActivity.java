package com.michaelflisar.bundlebuilder.sample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivity2BundleBuilder;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivityBundleBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btTry1 = (Button) findViewById(R.id.btTry1);
        Button btTry2 = (Button) findViewById(R.id.btTry2);
        Button btTry3 = (Button) findViewById(R.id.btTry3);
        btTry1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new DetailActivityBundleBuilder()
                        .stringArg("String Test Argument")
                        .integerArg(100)
                        .intArg(100)
                        .boolArg(true)
                        //.optionalStringArg("Optional Arg")
                        .withCustomArgSetterName("Custom Setter Name Argument")
                        .buildIntent(MainActivity.this);
                startActivity(i);
            }
        });
        btTry2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new DetailActivity2BundleBuilder("String Test Argument", 100, true)
                        //.optionalStringArg("Optional Arg")
                        .withCustomArgSetterName("Custom Setter Name Argument")
                        .buildIntent(MainActivity.this);
                startActivity(i);
            }
        });
        btTry3.setOnClickListener(new View.OnClickListener()
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
