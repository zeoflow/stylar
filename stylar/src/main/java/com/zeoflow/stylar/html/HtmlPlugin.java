package com.zeoflow.stylar.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.StylarVisitor;
import com.zeoflow.stylar.html.tag.BlockquoteHandler;
import com.zeoflow.stylar.html.tag.EmphasisHandler;
import com.zeoflow.stylar.html.tag.HeadingHandler;
import com.zeoflow.stylar.html.tag.ImageHandler;
import com.zeoflow.stylar.html.tag.LinkHandler;
import com.zeoflow.stylar.html.tag.ListHandler;
import com.zeoflow.stylar.html.tag.StrikeHandler;
import com.zeoflow.stylar.html.tag.StrongEmphasisHandler;
import com.zeoflow.stylar.html.tag.SubScriptHandler;
import com.zeoflow.stylar.html.tag.SuperScriptHandler;
import com.zeoflow.stylar.html.tag.UnderlineHandler;

import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;

/**
 * @since 3.0.0
 */
public class HtmlPlugin extends AbstractStylarPlugin
{

    public static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;
    private final StylarHtmlRendererImpl.Builder builder;
    private StylarHtmlParser htmlParser;
    private StylarHtmlRenderer htmlRenderer;
    // @since 4.4.0
    private HtmlEmptyTagReplacement emptyTagReplacement = new HtmlEmptyTagReplacement();

    @SuppressWarnings("WeakerAccess")
    HtmlPlugin()
    {
        this.builder = new StylarHtmlRendererImpl.Builder();
    }

    @NonNull
    public static HtmlPlugin create()
    {
        return new HtmlPlugin();
    }

    /**
     * @since 4.0.0
     */
    @NonNull
    public static HtmlPlugin create(@NonNull HtmlConfigure configure)
    {
        final HtmlPlugin plugin = create();
        configure.configureHtml(plugin);
        return plugin;
    }

    /**
     * @param allowNonClosedTags whether or not non-closed tags should be closed
     *                           at the document end. By default `false`
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin allowNonClosedTags(boolean allowNonClosedTags)
    {
        builder.allowNonClosedTags(allowNonClosedTags);
        return this;
    }

    /**
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin addHandler(@NonNull TagHandler tagHandler)
    {
        builder.addHandler(tagHandler);
        return this;
    }

    /**
     * @since 4.0.0
     */
    @Nullable
    public TagHandler getHandler(@NonNull String tagName)
    {
        return builder.getHandler(tagName);
    }

    /**
     * Indicate if HtmlPlugin should register default HTML tag handlers. Pass `true` to <strong>not</strong>
     * include default handlers. By default default handlers are included. You can use
     * {@link TagHandlerNoOp} to no-op certain default tags.
     *
     * @see TagHandlerNoOp
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin excludeDefaults(boolean excludeDefaults)
    {
        builder.excludeDefaults(excludeDefaults);
        return this;
    }

    /**
     * @param emptyTagReplacement {@link HtmlEmptyTagReplacement}
     * @since 4.4.0
     */
    @NonNull
    public HtmlPlugin emptyTagReplacement(@NonNull HtmlEmptyTagReplacement emptyTagReplacement)
    {
        this.emptyTagReplacement = emptyTagReplacement;
        return this;
    }

    @Override
    public void configureConfiguration(@NonNull StylarConfiguration.Builder configurationBuilder)
    {

        // @since 4.0.0 we init internal html-renderer here (marks the end of configuration)

        final StylarHtmlRendererImpl.Builder builder = this.builder;

        if (!builder.excludeDefaults())
        {
            // please note that it's better to not checkState for
            // this method call (minor optimization), final `build` method call
            // will check for the state and throw an exception if applicable
            builder.addDefaultTagHandler(ImageHandler.create());
            builder.addDefaultTagHandler(new LinkHandler());
            builder.addDefaultTagHandler(new BlockquoteHandler());
            builder.addDefaultTagHandler(new SubScriptHandler());
            builder.addDefaultTagHandler(new SuperScriptHandler());
            builder.addDefaultTagHandler(new StrongEmphasisHandler());
            builder.addDefaultTagHandler(new StrikeHandler());
            builder.addDefaultTagHandler(new UnderlineHandler());
            builder.addDefaultTagHandler(new ListHandler());
            builder.addDefaultTagHandler(new EmphasisHandler());
            builder.addDefaultTagHandler(new HeadingHandler());
        }

        htmlParser = StylarHtmlParserImpl.create(emptyTagReplacement);
        htmlRenderer = builder.build();
    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull StylarVisitor visitor)
    {
        final StylarHtmlRenderer htmlRenderer = this.htmlRenderer;
        if (htmlRenderer != null)
        {
            htmlRenderer.render(visitor, htmlParser);
        } else
        {
            throw new IllegalStateException("Unexpected state, html-renderer is not defined");
        }
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder)
    {
        builder
            .on(HtmlBlock.class, new StylarVisitor.NodeVisitor<HtmlBlock>()
            {
                @Override
                public void visit(@NonNull StylarVisitor visitor, @NonNull HtmlBlock htmlBlock)
                {
                    visitHtml(visitor, htmlBlock.getLiteral());
                }
            })
            .on(HtmlInline.class, new StylarVisitor.NodeVisitor<HtmlInline>()
            {
                @Override
                public void visit(@NonNull StylarVisitor visitor, @NonNull HtmlInline htmlInline)
                {
                    visitHtml(visitor, htmlInline.getLiteral());
                }
            });
    }

    private void visitHtml(@NonNull StylarVisitor visitor, @Nullable String html)
    {
        if (html != null)
        {
            htmlParser.processFragment(visitor.builder(), html);
        }
    }

    /**
     * @see #create(HtmlConfigure)
     * @since 4.0.0
     */
    public interface HtmlConfigure
    {
        void configureHtml(@NonNull HtmlPlugin plugin);
    }
}
