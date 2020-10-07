package com.zeoflow.stylar;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

/**
 * @since 4.3.0
 */
public class BlockHandlerDef implements StylarVisitor.BlockHandler
{
    @Override
    public void blockStart(@NonNull StylarVisitor visitor, @NonNull Node node)
    {
        visitor.ensureNewLine();
    }

    @Override
    public void blockEnd(@NonNull StylarVisitor visitor, @NonNull Node node)
    {
        if (visitor.hasNext(node))
        {
            visitor.ensureNewLine();
            visitor.forceNewLine();
        }
    }
}
