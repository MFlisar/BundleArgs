package com.michaelflisar.bundlebuilder.sample.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michaelflisar.bundlebuilder.sample.R;
import com.michaelflisar.bundlebuilder.sample.detail.DetailActivityBundleBuilder;

import java.util.List;

/**
 * Created by flisar on 14.02.2017.
 */

public class BaseDetailActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    protected void addArgsToList()
    {
        LinearLayout ll = (LinearLayout)findViewById(R.id.llMain);
        List<Object> allArgs = DetailActivityBundleBuilder.getArguments(getIntent().getExtras());
        for (int i = 0; i < allArgs.size(); i++)
        {
            TextView tv = new TextView(this);
            tv.setText(allArgs.get(i) != null ? ("[" + allArgs.get(i).getClass().getSimpleName() + "] " + allArgs.get(i).toString()) : "NULL");
            ll.addView(tv);
        }
    }
}
