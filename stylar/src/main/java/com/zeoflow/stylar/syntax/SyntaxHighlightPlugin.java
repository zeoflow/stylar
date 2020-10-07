package com.zeoflow.stylar.syntax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.core.StylarTheme;

public class SyntaxHighlightPlugin extends AbstractStylarPlugin
{

    private final CodeStyle codeStyle;
    private final CodeStyleTheme theme;
    private final String fallbackLanguage;
    public SyntaxHighlightPlugin(
        @NonNull CodeStyle codeStyle,
        @NonNull CodeStyleTheme theme,
        @Nullable String fallbackLanguage)
    {
        this.codeStyle = codeStyle;
        this.theme = theme;
        this.fallbackLanguage = fallbackLanguage;
    }

    @NonNull
    public static SyntaxHighlightPlugin create(
        @NonNull CodeStyle codeStyle,
        @NonNull CodeStyleTheme theme)
    {
        return create(codeStyle, theme, null);
    }

    @NonNull
    public static SyntaxHighlightPlugin create(
        @NonNull CodeStyle codeStyle,
        @NonNull CodeStyleTheme theme,
        @Nullable String fallbackLanguage)
    {
        return new SyntaxHighlightPlugin(codeStyle, theme, fallbackLanguage);
    }

    @Override
    public void configureTheme(@NonNull StylarTheme.Builder builder)
    {
        builder
            .codeTextColor(theme.textColor())
            .codeBackgroundColor(theme.background());
    }

    @Override
    public void configureConfiguration(@NonNull StylarConfiguration.Builder builder)
    {
        builder.syntaxHighlight(CodeStyleSyntaxHighlight.create(codeStyle, theme, fallbackLanguage));
    }
}
