package com.zeoflow.stylar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.R;

public class StylarView extends FrameLayout
{

    private Context zContext;
    private AttributeSet zAttrs;
    private TextView mtvTextView;
    private ScrollView mtvScrollView;

    public StylarView(@NonNull Context context)
    {
        super(context);
    }

    public StylarView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        zContext = context;
        zAttrs = attrs;
        init();
    }

    public StylarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        zContext = context;
        zAttrs = attrs;
        init();
    }

    public StylarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        zContext = context;
        zAttrs = attrs;
        init();
    }

    private void init()
    {

        LayoutInflater
            .from(zContext)
            .inflate(R.layout.stylar_text_view, this, true);

        mtvTextView = findViewById(R.id.mtvTextView);
        mtvScrollView = findViewById(R.id.mtvScrollView);

    }

    /**
     * @return String
     */
    public String getText()
    {
        return String.valueOf(this.mtvTextView.getText());
    }

    public static String ALIGNMENT_LEFT = "left";
    public static String ALIGNMENT_CENTER = "center";
    public static String ALIGNMENT_RIGHT = "right";

    public void setTextAlignment(String alignment)
    {
        if (alignment.equals(ALIGNMENT_LEFT)) {
            this.mtvTextView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
        } else if (alignment.equals(ALIGNMENT_CENTER)) {
            this.mtvTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } else if (alignment.equals(ALIGNMENT_RIGHT)) {
            this.mtvTextView.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }

    public TextView getMtvTextView()
    {
        return this.mtvTextView;
    }

    public ScrollView getMtvScrollView()
    {
        return this.mtvScrollView;
    }

}
