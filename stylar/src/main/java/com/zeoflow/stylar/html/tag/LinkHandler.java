package com.zeoflow.stylar.html.tag;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.core.CoreProps;
import com.zeoflow.stylar.html.HtmlTag;

import org.commonmark.node.Link;

import java.util.Collection;
import java.util.Collections;

public class LinkHandler extends SimpleTagHandler
{
    @Nullable
    @Override
    public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag)
    {
        final String destination = tag.attributes().get("href");
        if (!TextUtils.isEmpty(destination))
        {
            final SpanFactory spanFactory = configuration.spansFactory().get(Link.class);
            if (spanFactory != null)
            {

                CoreProps.LINK_DESTINATION.set(
                    renderProps,
                    destination
                );

                return spanFactory.getSpans(configuration, renderProps);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public Collection<String> supportedTags()
    {
        return Collections.singleton("a");
    }
}
