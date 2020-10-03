package com.zeoflow.stylar;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.core.StylarTheme;
import com.zeoflow.stylar.image.AsyncDrawableLoader;
import com.zeoflow.stylar.image.ImageSizeResolver;
import com.zeoflow.stylar.image.ImageSizeResolverDef;
import com.zeoflow.stylar.image.destination.ImageDestinationProcessor;
import com.zeoflow.stylar.syntax.SyntaxHighlight;
import com.zeoflow.stylar.syntax.SyntaxHighlightNoOp;

/**
 * since 3.0.0 renamed `SpannableConfiguration` -&gt; `MarkwonConfiguration`
 */
public class StylarConfiguration
{

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    private final StylarTheme theme;
    private final AsyncDrawableLoader asyncDrawableLoader;
    private final SyntaxHighlight syntaxHighlight;
    private final LinkResolver linkResolver;
    // @since 4.4.0
    private final ImageDestinationProcessor imageDestinationProcessor;
    private final ImageSizeResolver imageSizeResolver;

    // @since 3.0.0
    private final StylarSpansFactory spansFactory;

    private StylarConfiguration(@NonNull Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.imageDestinationProcessor = builder.imageDestinationProcessor;
        this.imageSizeResolver = builder.imageSizeResolver;
        this.spansFactory = builder.spansFactory;
    }

    @NonNull
    public StylarTheme theme() {
        return theme;
    }

    @NonNull
    public AsyncDrawableLoader asyncDrawableLoader() {
        return asyncDrawableLoader;
    }

    @NonNull
    public SyntaxHighlight syntaxHighlight() {
        return syntaxHighlight;
    }

    @NonNull
    public LinkResolver linkResolver() {
        return linkResolver;
    }

    /**
     * @since 4.4.0
     */
    @NonNull
    public ImageDestinationProcessor imageDestinationProcessor() {
        return imageDestinationProcessor;
    }

    @NonNull
    public ImageSizeResolver imageSizeResolver() {
        return imageSizeResolver;
    }

    /**
     * @since 3.0.0
     */
    @NonNull
    public StylarSpansFactory spansFactory() {
        return spansFactory;
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder {

        private StylarTheme theme;
        private AsyncDrawableLoader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkResolver linkResolver;
        // @since 4.4.0
        private ImageDestinationProcessor imageDestinationProcessor;
        private ImageSizeResolver imageSizeResolver;
        private StylarSpansFactory spansFactory;

        public Builder() {
        }

        /**
         * @since 4.0.0
         */
        @NonNull
        public Builder asyncDrawableLoader(@NonNull AsyncDrawableLoader asyncDrawableLoader) {
            this.asyncDrawableLoader = asyncDrawableLoader;
            return this;
        }

        @NonNull
        public Builder syntaxHighlight(@NonNull SyntaxHighlight syntaxHighlight) {
            this.syntaxHighlight = syntaxHighlight;
            return this;
        }

        @NonNull
        public Builder linkResolver(@NonNull LinkResolver linkResolver) {
            this.linkResolver = linkResolver;
            return this;
        }

        /**
         * @since 4.4.0
         */
        @NonNull
        public Builder imageDestinationProcessor(@NonNull ImageDestinationProcessor imageDestinationProcessor) {
            this.imageDestinationProcessor = imageDestinationProcessor;
            return this;
        }

        /**
         * @since 1.0.1
         */
        @NonNull
        public Builder imageSizeResolver(@NonNull ImageSizeResolver imageSizeResolver) {
            this.imageSizeResolver = imageSizeResolver;
            return this;
        }

        @NonNull
        public StylarConfiguration build(
                @NonNull StylarTheme theme,
                @NonNull StylarSpansFactory spansFactory) {

            this.theme = theme;
            this.spansFactory = spansFactory;

            // @since 4.0.0
            if (asyncDrawableLoader == null) {
                asyncDrawableLoader = AsyncDrawableLoader.noOp();
            }

            if (syntaxHighlight == null) {
                syntaxHighlight = new SyntaxHighlightNoOp();
            }

            if (linkResolver == null) {
                linkResolver = new LinkResolverDef();
            }

            // @since 4.4.0
            if (imageDestinationProcessor == null) {
                imageDestinationProcessor = ImageDestinationProcessor.noOp();
            }

            if (imageSizeResolver == null) {
                imageSizeResolver = new ImageSizeResolverDef();
            }

            return new StylarConfiguration(this);
        }
    }

}
