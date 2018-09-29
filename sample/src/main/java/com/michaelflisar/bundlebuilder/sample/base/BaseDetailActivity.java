package com.michaelflisar.bundlebuilder.sample.base;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michaelflisar.bundlebuilder.sample.R;

/**
 * Created by flisar on 14.02.2017.
 */

public class BaseDetailActivity extends AppCompatActivity
{
    private LinearLayout mLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mLinearLayout = (LinearLayout)findViewById(R.id.llMain);
    }

    protected void addArgsToList()
    {
        Bundle b = getIntent().getExtras();
        for (String key : b.keySet())
            addValue(key, b.get(key));
    }

    protected void addValue(String customLabel, Object value)
    {
        TextView tv = new TextView(this);
        tv.setText(value != null ? ("[" + (customLabel == null ? value.getClass().getSimpleName() : customLabel) + "] " + value.toString()) : (customLabel != null ? "[" + customLabel + "] " : "") + "NULL");
        mLinearLayout.addView(tv);
    }
}
