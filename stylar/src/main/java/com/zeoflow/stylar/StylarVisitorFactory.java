package com.zeoflow.stylar;

import androidx.annotation.NonNull;

/**
 * @since 4.1.1
 */
public abstract class StylarVisitorFactory
{

    @NonNull
    public static StylarVisitorFactory create(
        @NonNull final StylarVisitorImpl.Builder builder,
        @NonNull final StylarConfiguration configuration)
    {
        return new StylarVisitorFactory()
        {
            @NonNull
            @Override
            StylarVisitor create()
            {
                return builder.build(configuration, new RenderPropsImpl());
            }
        };
    }

    @NonNull
    abstract StylarVisitor create();
}
