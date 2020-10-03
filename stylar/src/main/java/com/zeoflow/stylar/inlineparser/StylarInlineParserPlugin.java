package com.zeoflow.stylar.inlineparser;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.AbstractStylarPlugin;

import org.commonmark.parser.Parser;

/**
 * @since 4.3.0
 */
public class StylarInlineParserPlugin extends AbstractStylarPlugin
{

    public interface BuilderConfigure<B extends StylarInlineParser.FactoryBuilder> {
        void configureBuilder(@NonNull B factoryBuilder);
    }

    @NonNull
    public static StylarInlineParserPlugin create() {
        return create(StylarInlineParser.factoryBuilder());
    }

    @NonNull
    public static StylarInlineParserPlugin create(@NonNull BuilderConfigure<StylarInlineParser.FactoryBuilder> configure) {
        final StylarInlineParser.FactoryBuilder factoryBuilder = StylarInlineParser.factoryBuilder();
        configure.configureBuilder(factoryBuilder);
        return new StylarInlineParserPlugin(factoryBuilder);
    }

    @NonNull
    public static StylarInlineParserPlugin create(@NonNull StylarInlineParser.FactoryBuilder factoryBuilder) {
        return new StylarInlineParserPlugin(factoryBuilder);
    }

    @NonNull
    public static <B extends StylarInlineParser.FactoryBuilder> StylarInlineParserPlugin create(
            @NonNull B factoryBuilder,
            @NonNull BuilderConfigure<B> configure) {
        configure.configureBuilder(factoryBuilder);
        return new StylarInlineParserPlugin(factoryBuilder);
    }

    private final StylarInlineParser.FactoryBuilder factoryBuilder;

    @SuppressWarnings("WeakerAccess")
    StylarInlineParserPlugin(@NonNull StylarInlineParser.FactoryBuilder factoryBuilder) {
        this.factoryBuilder = factoryBuilder;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.inlineParserFactory(factoryBuilder.build());
    }

    @NonNull
    public StylarInlineParser.FactoryBuilder factoryBuilder() {
        return factoryBuilder;
    }
}
