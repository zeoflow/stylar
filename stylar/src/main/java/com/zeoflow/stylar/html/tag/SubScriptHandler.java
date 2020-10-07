package com.zeoflow.stylar.html.tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.span.SubScriptSpan;

import java.util.Collection;
import java.util.Collections;

public class SubScriptHandler extends SimpleTagHandler
{
    @Nullable
    @Override
    public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag)
    {
        return new SubScriptSpan();
    }

    @NonNull
    @Override
    public Collection<String> supportedTags()
    {
        return Collections.singleton("sub");
    }
}
