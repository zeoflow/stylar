package com.zeoflow.stylar.html.tag;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.node.BlockQuote;

import java.util.Collection;
import java.util.Collections;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.StylarHtmlRenderer;
import com.zeoflow.stylar.html.TagHandler;

public class BlockquoteHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        final StylarConfiguration configuration = visitor.configuration();
        final SpanFactory factory = configuration.spansFactory().get(BlockQuote.class);
        if (factory != null) {
            SpannableBuilder.setSpans(
                    visitor.builder(),
                    factory.getSpans(configuration, visitor.renderProps()),
                    tag.start(),
                    tag.end()
            );
        }
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("blockquote");
    }
}
