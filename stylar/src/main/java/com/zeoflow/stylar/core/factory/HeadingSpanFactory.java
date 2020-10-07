package com.zeoflow.stylar.core.factory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.core.CoreProps;
import com.zeoflow.stylar.core.spans.HeadingSpan;

public class HeadingSpanFactory implements SpanFactory
{
    @Nullable
    @Override
    public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps props)
    {
        return new HeadingSpan(
            configuration.theme(),
            CoreProps.HEADING_LEVEL.require(props)
        );
    }
}
