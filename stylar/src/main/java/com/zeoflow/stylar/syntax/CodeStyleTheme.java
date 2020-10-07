package com.zeoflow.stylar.syntax;

import android.text.SpannableStringBuilder;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.zeoflow.stylar.codestyle.CodeStyle;

public interface CodeStyleTheme
{

    @ColorInt
    int background();

    @ColorInt
    int textColor();

    void apply(
        @NonNull String language,
        @NonNull CodeStyle.Syntax syntax,
        @NonNull SpannableStringBuilder builder,
        int start,
        int end
    );
}
