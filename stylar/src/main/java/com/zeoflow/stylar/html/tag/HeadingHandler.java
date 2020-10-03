package com.zeoflow.stylar.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Heading;

import java.util.Arrays;
import java.util.Collection;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.core.CoreProps;
import com.zeoflow.stylar.html.HtmlTag;

public class HeadingHandler extends SimpleTagHandler {

    @Nullable
    @Override
    public Object getSpans(
            @NonNull StylarConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {

        final SpanFactory factory = configuration.spansFactory().get(Heading.class);
        if (factory == null) {
            return null;
        }

        int level;
        try {
            level = Integer.parseInt(tag.name().substring(1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            level = 0;
        }

        if (level < 1 || level > 6) {
            return null;
        }

        CoreProps.HEADING_LEVEL.set(renderProps, level);

        return factory.getSpans(configuration, renderProps);
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6");
    }
}
