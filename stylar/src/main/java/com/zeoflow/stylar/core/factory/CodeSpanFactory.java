package com.zeoflow.stylar.core.factory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.core.spans.CodeSpan;

public class CodeSpanFactory implements SpanFactory
{
    @Nullable
    @Override
    public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps props)
    {
        return new CodeSpan(configuration.theme());
    }
}
