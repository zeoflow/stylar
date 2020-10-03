package com.zeoflow.stylar.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.ClickEvent;
import com.zeoflow.stylar.Stylar;
import com.zeoflow.stylar.core.StylarTheme;
import com.zeoflow.stylar.utils.AssetsHelper;
import com.zeoflow.stylar.view.StylarView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //log the exception
        // set markdown

        String accClosed = "### Your account has been closed.\n\nIt looks like the **`Terms of Service`** may have been violated.\n\nTo have our support team look into this, please [**`contact us`**]($contact_us).";
        StylarView stylarView = findViewById(R.id.zStylarView);
        final Stylar stylar = Stylar.builder(this)
            .withLayoutElement(stylarView)
            .withAnchoredHeadings(true)
            .withImagePlugins(true)
            .withCodeStyle(false)
            .setClickEvent(link -> Toast.makeText(MainActivity.this, link, Toast.LENGTH_SHORT).show())
            .usePlugin(new AbstractStylarPlugin() {
                @Override
                public void configureTheme(@NonNull StylarTheme.Builder builder) {
                    builder
                        .codeTextColor(Color.parseColor("#CE570CC1"))
                        .codeBackgroundColor(Color.parseColor("#EDEDED"));
                }
            })
            .build();
        stylar.setMarkdown(accClosed);

//        stylar.setMarkdown(AssetsHelper.readFile(getAssets(), "content.txt"));

    }

}
