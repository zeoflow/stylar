package com.zeoflow.stylar.simple.ext;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;

/**
 * @since 4.0.0
 */
public class SimpleExtPlugin extends AbstractStylarPlugin
{

    private final SimpleExtBuilder builder = new SimpleExtBuilder();

    @SuppressWarnings("WeakerAccess")
    SimpleExtPlugin()
    {
    }

    @NonNull
    public static SimpleExtPlugin create()
    {
        return new SimpleExtPlugin();
    }

    @NonNull
    public static SimpleExtPlugin create(@NonNull SimpleExtConfigure configure)
    {
        final SimpleExtPlugin plugin = new SimpleExtPlugin();
        configure.configure(plugin);
        return plugin;
    }

    @NonNull
    public SimpleExtPlugin addExtension(
        int length,
        char character,
        @NonNull SpanFactory spanFactory)
    {
        builder.addExtension(length, character, spanFactory);
        return this;
    }

    @NonNull
    public SimpleExtPlugin addExtension(
        int length,
        char openingCharacter,
        char closingCharacter,
        @NonNull SpanFactory spanFactory)
    {
        builder.addExtension(length, openingCharacter, closingCharacter, spanFactory);
        return this;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder)
    {
        for (DelimiterProcessor processor : this.builder.build())
        {
            builder.customDelimiterProcessor(processor);
        }
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(SimpleExtNode.class, new StylarVisitor.NodeVisitor<SimpleExtNode>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull SimpleExtNode simpleExtNode)
            {

                final int length = visitor.length();

                visitor.visitChildren(simpleExtNode);

                SpannableBuilder.setSpans(
                    visitor.builder(),
                    simpleExtNode.spanFactory().getSpans(visitor.configuration(), visitor.renderProps()),
                    length,
                    visitor.length()
                );
            }
        });
    }

    public interface SimpleExtConfigure
    {
        void configure(@NonNull SimpleExtPlugin plugin);
    }
}
