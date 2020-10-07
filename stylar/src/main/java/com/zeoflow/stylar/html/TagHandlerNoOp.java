package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarVisitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @since 4.0.0
 */
public class TagHandlerNoOp extends TagHandler
{

    private final Collection<String> tags;

    @SuppressWarnings("WeakerAccess")
    TagHandlerNoOp(Collection<String> tags)
    {
        this.tags = tags;
    }

    @NonNull
    public static TagHandlerNoOp create(@NonNull String tag)
    {
        return new TagHandlerNoOp(Collections.singleton(tag));
    }

    @NonNull
    public static TagHandlerNoOp create(@NonNull String... tags)
    {
        return new TagHandlerNoOp(Arrays.asList(tags));
    }

    @Override
    public void handle(@NonNull StylarVisitor visitor, @NonNull StylarHtmlRenderer renderer, @NonNull HtmlTag tag)
    {
        // no op
    }

    @NonNull
    @Override
    public Collection<String> supportedTags()
    {
        return tags;
    }
}
