package com.zeoflow.stylar.syntax;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.codestyle.CodeStyle;

public class CodeStyleSyntaxHighlight implements SyntaxHighlight {

    @NonNull
    public static CodeStyleSyntaxHighlight create(
            @NonNull CodeStyle codeStyle,
            @NonNull CodeStyleTheme theme) {
        return new CodeStyleSyntaxHighlight(codeStyle, theme, null);
    }

    @NonNull
    public static CodeStyleSyntaxHighlight create(
            @NonNull CodeStyle codeStyle,
            @NonNull CodeStyleTheme theme,
            @Nullable String fallback) {
        return new CodeStyleSyntaxHighlight(codeStyle, theme, fallback);
    }

    private final CodeStyle codeStyle;
    private final CodeStyleTheme theme;
    private final String fallback;

    protected CodeStyleSyntaxHighlight(
            @NonNull CodeStyle codeStyle,
            @NonNull CodeStyleTheme theme,
            @Nullable String fallback) {
        this.codeStyle = codeStyle;
        this.theme = theme;
        this.fallback = fallback;
    }

    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {

        // @since 4.2.2
        // although not null, but still is empty
        if (code.isEmpty()) {
            return code;
        }

        // if info is null, do not highlight -> LICENCE footer very commonly wrapped inside code
        // block without syntax name specified (so, do not highlight)
        return info == null
                ? highlightNoLanguageInfo(code)
                : highlightWithLanguageInfo(info, code);
    }

    @NonNull
    protected CharSequence highlightNoLanguageInfo(@NonNull String code) {
        return code;
    }

    @NonNull
    protected CharSequence highlightWithLanguageInfo(@NonNull String info, @NonNull String code) {

        final CharSequence out;

        final String language;
        final CodeStyle.Grammar grammar;
        {
            String _language = info;
            CodeStyle.Grammar _grammar = codeStyle.grammar(info);
            if (_grammar == null && !TextUtils.isEmpty(fallback)) {
                _language = fallback;
                assert fallback != null;
                _grammar = codeStyle.grammar(fallback);
            }
            language = _language;
            grammar = _grammar;
        }

        if (grammar != null) {
            out = highlight(language, grammar, code);
        } else {
            out = code;
        }

        return out;
    }

    @NonNull
    protected CharSequence highlight(@NonNull String language, @NonNull CodeStyle.Grammar grammar, @NonNull String code) {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final CodeStyleSyntaxVisitor visitor = new CodeStyleSyntaxVisitor(language, theme, builder);
        visitor.visit(codeStyle.tokenize(code, grammar));
        return builder;
    }

    @NonNull
    protected CodeStyle codestyle() {
        return codeStyle;
    }

    @NonNull
    protected CodeStyleTheme theme() {
        return theme;
    }

    @Nullable
    protected String fallback() {
        return fallback;
    }
}
