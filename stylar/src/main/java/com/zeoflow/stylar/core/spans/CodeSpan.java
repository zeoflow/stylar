package com.zeoflow.stylar.core.spans;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.core.StylarTheme;

/**
 * @since 3.0.0 split inline and block spans
 */
public class CodeSpan extends MetricAffectingSpan
{

    private final StylarTheme theme;

    public CodeSpan(@NonNull StylarTheme theme)
    {
        this.theme = theme;
    }

    @Override
    public void updateMeasureState(TextPaint p)
    {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint ds)
    {
        apply(ds);
        ds.bgColor = theme.getCodeBackgroundColor(ds);
    }

    private void apply(TextPaint p)
    {
        theme.applyCodeTextStyle(p);
    }
}
