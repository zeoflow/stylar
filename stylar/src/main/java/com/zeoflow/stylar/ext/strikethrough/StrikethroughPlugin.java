package com.zeoflow.stylar.ext.strikethrough;

import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.StylarSpansFactory;
import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;

import java.util.Collections;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;

/**
 * Plugin to add strikethrough markdown feature. This plugin will extend commonmark-java.Parser
 * with strikethrough extension, add SpanFactory and register commonmark-java.Strikethrough node
 * visitor
 *
 * @see #create()
 * @since 3.0.0
 */
public class StrikethroughPlugin extends AbstractStylarPlugin
{

    @NonNull
    public static StrikethroughPlugin create() {
        return new StrikethroughPlugin();
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.extensions(Collections.singleton(StrikethroughExtension.create()));
    }

    @Override
    public void configureSpansFactory(@NonNull StylarSpansFactory.Builder builder) {
        builder.setFactory(Strikethrough.class, new SpanFactory() {
            @Override
            public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps props) {
                return new StrikethroughSpan();
            }
        });
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder) {
        builder.on(Strikethrough.class, new StylarVisitor.NodeVisitor<Strikethrough>() {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Strikethrough strikethrough) {
                final int length = visitor.length();
                visitor.visitChildren(strikethrough);
                visitor.setSpansForNodeOptional(strikethrough, length);
            }
        });
    }
}
