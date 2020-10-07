package com.zeoflow.stylar.simple.ext;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.SpanFactory;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Visitor;

// @since 4.0.0
class SimpleExtNode extends CustomNode
{

    private final SpanFactory spanFactory;

    SimpleExtNode(@NonNull SpanFactory spanFactory)
    {
        this.spanFactory = spanFactory;
    }

    @Override
    public void accept(Visitor visitor)
    {
        visitor.visit(this);
    }

    @NonNull
    SpanFactory spanFactory()
    {
        return spanFactory;
    }
}
