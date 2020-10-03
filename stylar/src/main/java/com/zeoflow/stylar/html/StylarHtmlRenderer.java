package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.StylarVisitor;

/**
 * @since 2.0.0
 */
public abstract class StylarHtmlRenderer
{

    public abstract void render(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlParser parser
    );

    @Nullable
    public abstract TagHandler tagHandler(@NonNull String tagName);
}
