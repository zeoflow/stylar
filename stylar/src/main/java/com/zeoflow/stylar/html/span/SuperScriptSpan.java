package com.zeoflow.stylar.html.span;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.html.HtmlPlugin;

public class SuperScriptSpan extends MetricAffectingSpan {

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint tp) {
        apply(tp);
    }

    private void apply(TextPaint paint) {
        paint.setTextSize(paint.getTextSize() * HtmlPlugin.SCRIPT_DEF_TEXT_SIZE_RATIO);
        paint.baselineShift += (int) (paint.ascent() / 2);
    }
}
