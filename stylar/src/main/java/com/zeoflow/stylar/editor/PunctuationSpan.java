package com.zeoflow.stylar.editor;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

import com.zeoflow.stylar.utils.ColorUtils;

class PunctuationSpan extends CharacterStyle
{

    private static final int DEF_PUNCTUATION_ALPHA = 75;

    @Override
    public void updateDrawState(TextPaint tp)
    {
        final int color = ColorUtils.applyAlpha(tp.getColor(), DEF_PUNCTUATION_ALPHA);
        tp.setColor(color);
    }
}
