package com.zeoflow.stylar;

import androidx.annotation.NonNull;

import org.commonmark.node.SoftLineBreak;

/**
 * @since 4.3.0
 */
public class SoftBreakAddsNewLinePlugin extends AbstractStylarPlugin
{

    @NonNull
    public static SoftBreakAddsNewLinePlugin create()
    {
        return new SoftBreakAddsNewLinePlugin();
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(SoftLineBreak.class, new StylarVisitor.NodeVisitor<SoftLineBreak>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull SoftLineBreak softLineBreak)
            {
                visitor.ensureNewLine();
            }
        });
    }
}
