package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarVisitor;

import java.util.Collection;

public abstract class TagHandler {

    public abstract void handle(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlRenderer renderer,
            @NonNull HtmlTag tag
    );

    /**
     * @since 4.0.0
     */
    @NonNull
    public abstract Collection<String> supportedTags();


    protected static void visitChildren(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlRenderer renderer,
            @NonNull HtmlTag.Block block) {

        TagHandler handler;

        for (HtmlTag.Block child : block.children()) {

            if (!child.isClosed()) {
                continue;
            }

            handler = renderer.tagHandler(child.name());
            if (handler != null) {
                handler.handle(visitor, renderer, child);
            } else {
                visitChildren(visitor, renderer, child);
            }
        }
    }
}
