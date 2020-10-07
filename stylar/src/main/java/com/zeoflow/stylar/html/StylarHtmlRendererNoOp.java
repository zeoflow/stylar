package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.StylarVisitor;

class StylarHtmlRendererNoOp extends StylarHtmlRenderer
{

    @Override
    public void render(@NonNull StylarVisitor visitor, @NonNull StylarHtmlParser parser)
    {
        parser.reset();
    }

    @Nullable
    @Override
    public TagHandler tagHandler(@NonNull String tagName)
    {
        return null;
    }
}
