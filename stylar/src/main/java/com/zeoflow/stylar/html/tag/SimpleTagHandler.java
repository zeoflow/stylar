package com.zeoflow.stylar.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.StylarVisitor;

import java.util.Collection;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.StylarHtmlRenderer;
import com.zeoflow.stylar.html.TagHandler;

public abstract class SimpleTagHandler extends TagHandler {

    @Nullable
    public abstract Object getSpans(
            @NonNull StylarConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag);

    @NonNull
    @Override
    public abstract Collection<String> supportedTags();


    @Override
    public void handle(@NonNull StylarVisitor visitor, @NonNull StylarHtmlRenderer renderer, @NonNull HtmlTag tag) {
        // @since 4.5.0 check if tag is block one and visit children
        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        final Object spans = getSpans(visitor.configuration(), visitor.renderProps(), tag);
        if (spans != null) {
            SpannableBuilder.setSpans(visitor.builder(), spans, tag.start(), tag.end());
        }
    }
}
