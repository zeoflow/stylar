package com.zeoflow.stylar.html.tag;

import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.StylarVisitor;

import java.util.Arrays;
import java.util.Collection;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.StylarHtmlRenderer;
import com.zeoflow.stylar.html.TagHandler;

public class StrikeHandler extends TagHandler {

    // flag to detect if commonmark-java-strikethrough is in classpath, so we use SpanFactory
    // to obtain strikethrough span
    private static final boolean HAS_MARKDOWN_IMPLEMENTATION;

    static {
        boolean hasMarkdownImplementation;
        try {
            // @since 4.3.1 we class Class.forName instead of trying
            //  to access the class by full qualified name (which caused issues with DexGuard)
            Class.forName("org.commonmark.ext.gfm.strikethrough.Strikethrough");
            hasMarkdownImplementation = true;
        } catch (Throwable t) {
            hasMarkdownImplementation = false;
        }
        HAS_MARKDOWN_IMPLEMENTATION = hasMarkdownImplementation;
    }

    @Override
    public void handle(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        if (tag.isBlock()) {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
                visitor.builder(),
                HAS_MARKDOWN_IMPLEMENTATION ? getMarkdownSpans(visitor) : new StrikethroughSpan(),
                tag.start(),
                tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("s", "del");
    }

    @Nullable
    private static Object getMarkdownSpans(@NonNull StylarVisitor visitor) {
        final StylarConfiguration configuration = visitor.configuration();
        final SpanFactory spanFactory = configuration.spansFactory()
                .get(org.commonmark.ext.gfm.strikethrough.Strikethrough.class);
        if (spanFactory == null) {
            return null;
        }
        return spanFactory.getSpans(configuration, visitor.renderProps());
    }
}
