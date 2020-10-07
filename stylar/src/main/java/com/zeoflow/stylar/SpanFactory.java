package com.zeoflow.stylar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @since 3.0.0
 */
public interface SpanFactory
{

    @Nullable
    Object getSpans(
        @NonNull StylarConfiguration configuration,
        @NonNull RenderProps props);
}
