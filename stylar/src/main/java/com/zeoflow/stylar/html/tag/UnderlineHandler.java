package com.zeoflow.stylar.html.tag;

import android.text.style.UnderlineSpan;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.StylarVisitor;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.StylarHtmlRenderer;
import com.zeoflow.stylar.html.TagHandler;

import java.util.Arrays;
import java.util.Collection;

public class UnderlineHandler extends TagHandler
{

    @Override
    public void handle(
        @NonNull StylarVisitor visitor,
        @NonNull StylarHtmlRenderer renderer,
        @NonNull HtmlTag tag)
    {

        // as parser doesn't treat U tag as an inline one,
        // thus doesn't allow children, we must visit them first

        if (tag.isBlock())
        {
            visitChildren(visitor, renderer, tag.getAsBlock());
        }

        SpannableBuilder.setSpans(
            visitor.builder(),
            new UnderlineSpan(),
            tag.start(),
            tag.end()
        );
    }

    @NonNull
    @Override
    public Collection<String> supportedTags()
    {
        return Arrays.asList("u", "ins");
    }
}
