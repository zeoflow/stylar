package com.zeoflow.stylar.core;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.node.Node;

/**
 * A {@link StylarVisitor.NodeVisitor} that ensures that a markdown
 * block starts with a new line, all children are visited and if further content available
 * ensures a new line after self. Does not render any spans
 *
 * @since 3.0.0
 */
public class SimpleBlockNodeVisitor implements StylarVisitor.NodeVisitor<Node>
{
    @Override
    public void visit(@NonNull StylarVisitor visitor, @NonNull Node node)
    {

        visitor.blockStart(node);

        // @since 3.0.1 we keep track of start in order to apply spans (optionally)
        final int length = visitor.length();

        visitor.visitChildren(node);

        // @since 3.0.1 we apply optional spans
        visitor.setSpansForNodeOptional(node, length);

        visitor.blockEnd(node);
    }
}
