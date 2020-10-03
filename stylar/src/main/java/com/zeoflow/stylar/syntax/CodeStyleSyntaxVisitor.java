package com.zeoflow.stylar.syntax;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.codestyle.AbsVisitor;
import com.zeoflow.stylar.codestyle.CodeStyle;

class CodeStyleSyntaxVisitor extends AbsVisitor {

    private final String language;
    private final CodeStyleTheme theme;
    private final SpannableStringBuilder builder;

    CodeStyleSyntaxVisitor(
            @NonNull String language,
            @NonNull CodeStyleTheme theme,
            @NonNull SpannableStringBuilder builder) {
        this.language = language;
        this.theme = theme;
        this.builder = builder;
    }

    @Override
    protected void visitText(@NonNull CodeStyle.Text text) {
        builder.append(text.literal());
    }

    @Override
    protected void visitSyntax(@NonNull CodeStyle.Syntax syntax) {

        final int start = builder.length();
        visit(syntax.children());
        final int end = builder.length();

        if (end != start) {
            theme.apply(language, syntax, builder, start, end);
        }
    }
}
