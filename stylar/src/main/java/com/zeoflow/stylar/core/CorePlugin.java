package com.zeoflow.stylar.core;

import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.StylarSpansFactory;
import com.zeoflow.stylar.StylarVisitor;
import com.zeoflow.stylar.core.factory.BlockQuoteSpanFactory;
import com.zeoflow.stylar.core.factory.CodeBlockSpanFactory;
import com.zeoflow.stylar.core.factory.CodeSpanFactory;
import com.zeoflow.stylar.core.factory.EmphasisSpanFactory;
import com.zeoflow.stylar.core.factory.HeadingSpanFactory;
import com.zeoflow.stylar.core.factory.LinkSpanFactory;
import com.zeoflow.stylar.core.factory.ListItemSpanFactory;
import com.zeoflow.stylar.core.factory.StrongEmphasisSpanFactory;
import com.zeoflow.stylar.core.factory.ThematicBreakSpanFactory;
import com.zeoflow.stylar.core.spans.OrderedListItemSpan;
import com.zeoflow.stylar.core.spans.TextViewSpan;
import com.zeoflow.stylar.image.ImageProps;

import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see CoreProps
 * @since 3.0.0
 */
public class CorePlugin extends AbstractStylarPlugin
{

    // @since 4.0.0
    private final List<OnTextAddedListener> onTextAddedListeners = new ArrayList<>(0);
    // @since 4.5.0
    private boolean hasExplicitMovementMethod;

    protected CorePlugin()
    {
    }

    @NonNull
    public static CorePlugin create()
    {
        return new CorePlugin();
    }

    /**
     * @return a set with enabled by default block types
     * @since 4.4.0
     */
    @NonNull
    public static Set<Class<? extends Block>> enabledBlockTypes()
    {
        return new HashSet<>(Arrays.asList(
            BlockQuote.class,
            Heading.class,
            FencedCodeBlock.class,
            HtmlBlock.class,
            ThematicBreak.class,
            ListBlock.class,
            IndentedCodeBlock.class
        ));
    }

    private static void strongEmphasis(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(StrongEmphasis.class, new StylarVisitor.NodeVisitor<StrongEmphasis>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull StrongEmphasis strongEmphasis)
            {
                final int length = visitor.length();
                visitor.visitChildren(strongEmphasis);
                visitor.setSpansForNodeOptional(strongEmphasis, length);
            }
        });
    }

    private static void emphasis(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Emphasis.class, new StylarVisitor.NodeVisitor<Emphasis>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Emphasis emphasis)
            {
                final int length = visitor.length();
                visitor.visitChildren(emphasis);
                visitor.setSpansForNodeOptional(emphasis, length);
            }
        });
    }

    private static void blockQuote(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(BlockQuote.class, new StylarVisitor.NodeVisitor<BlockQuote>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull BlockQuote blockQuote)
            {

                visitor.blockStart(blockQuote);

                final int length = visitor.length();

                visitor.visitChildren(blockQuote);
                visitor.setSpansForNodeOptional(blockQuote, length);

                visitor.blockEnd(blockQuote);
            }
        });
    }

    private static void code(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Code.class, new StylarVisitor.NodeVisitor<Code>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Code code)
            {

                final int length = visitor.length();

                // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
                // unfortunately we cannot use this for multiline code as we cannot control where a new line break will be inserted
                visitor.builder()
                    .append('\u00a0')
                    .append(code.getLiteral())
                    .append('\u00a0');

                visitor.setSpansForNodeOptional(code, length);
            }
        });
    }

    private static void fencedCodeBlock(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(FencedCodeBlock.class, new StylarVisitor.NodeVisitor<FencedCodeBlock>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull FencedCodeBlock fencedCodeBlock)
            {
                visitCodeBlock(visitor, fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral(), fencedCodeBlock);
            }
        });
    }

    private static void indentedCodeBlock(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(IndentedCodeBlock.class, new StylarVisitor.NodeVisitor<IndentedCodeBlock>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull IndentedCodeBlock indentedCodeBlock)
            {
                visitCodeBlock(visitor, null, indentedCodeBlock.getLiteral(), indentedCodeBlock);
            }
        });
    }

    // @since 4.0.0
    // his method is moved from ImagesPlugin. Alternative implementations must set SpanFactory
    // for Image node in order for this visitor to function
    private static void image(StylarVisitor.Builder builder)
    {
        builder.on(Image.class, new StylarVisitor.NodeVisitor<Image>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Image image)
            {

                // if there is no image spanFactory, ignore
                final SpanFactory spanFactory = visitor.configuration().spansFactory().get(Image.class);
                if (spanFactory == null)
                {
                    visitor.visitChildren(image);
                    return;
                }

                final int length = visitor.length();

                visitor.visitChildren(image);

                // we must check if anything _was_ added, as we need at least one char to render
                if (length == visitor.length())
                {
                    visitor.builder().append('\uFFFC');
                }

                final StylarConfiguration configuration = visitor.configuration();

                final Node parent = image.getParent();
                final boolean link = parent instanceof Link;

                final String destination = configuration
                    .imageDestinationProcessor()
                    .process(image.getDestination());

                final RenderProps props = visitor.renderProps();

                // apply image properties
                // Please note that we explicitly set IMAGE_SIZE to null as we do not clear
                // properties after we applied span (we could though)
                ImageProps.DESTINATION.set(props, destination);
                ImageProps.REPLACEMENT_TEXT_IS_LINK.set(props, link);
                ImageProps.IMAGE_SIZE.set(props, null);

                visitor.setSpans(length, spanFactory.getSpans(configuration, props));
            }
        });
    }

    @VisibleForTesting
    static void visitCodeBlock(
        @NonNull StylarVisitor visitor,
        @Nullable String info,
        @NonNull String code,
        @NonNull Node node)
    {

        visitor.blockStart(node);

        final int length = visitor.length();

        visitor.builder()
            .append('\u00a0').append('\n')
            .append(visitor.configuration().syntaxHighlight().highlight(info, code));

        visitor.ensureNewLine();

        visitor.builder().append('\u00a0');

        // @since 4.1.1
        CoreProps.CODE_BLOCK_INFO.set(visitor.renderProps(), info);

        visitor.setSpansForNodeOptional(node, length);

        visitor.blockEnd(node);
    }

    private static void bulletList(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(BulletList.class, new SimpleBlockNodeVisitor());
    }

    private static void orderedList(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(OrderedList.class, new SimpleBlockNodeVisitor());
    }

    private static void listItem(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(ListItem.class, new StylarVisitor.NodeVisitor<ListItem>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull ListItem listItem)
            {

                final int length = visitor.length();

                // it's important to visit children before applying render props (
                // we can have nested children, who are list items also, thus they will
                // override out props (if we set them before visiting children)
                visitor.visitChildren(listItem);

                final Node parent = listItem.getParent();
                if (parent instanceof OrderedList)
                {

                    final int start = ((OrderedList) parent).getStartNumber();

                    CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.ORDERED);
                    CoreProps.ORDERED_LIST_ITEM_NUMBER.set(visitor.renderProps(), start);

                    // after we have visited the children increment start number
                    final OrderedList orderedList = (OrderedList) parent;
                    orderedList.setStartNumber(orderedList.getStartNumber() + 1);

                } else
                {
                    CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.BULLET);
                    CoreProps.BULLET_LIST_ITEM_LEVEL.set(visitor.renderProps(), listLevel(listItem));
                }

                visitor.setSpansForNodeOptional(listItem, length);

                if (visitor.hasNext(listItem))
                {
                    visitor.ensureNewLine();
                }
            }
        });
    }

    private static int listLevel(@NonNull Node node)
    {
        int level = 0;
        Node parent = node.getParent();
        while (parent != null)
        {
            if (parent instanceof ListItem)
            {
                level += 1;
            }
            parent = parent.getParent();
        }
        return level;
    }

    private static void thematicBreak(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(ThematicBreak.class, new StylarVisitor.NodeVisitor<ThematicBreak>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull ThematicBreak thematicBreak)
            {

                visitor.blockStart(thematicBreak);

                final int length = visitor.length();

                // without space it won't render
                visitor.builder().append('\u00a0');

                visitor.setSpansForNodeOptional(thematicBreak, length);

                visitor.blockEnd(thematicBreak);
            }
        });
    }

    private static void heading(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Heading.class, new StylarVisitor.NodeVisitor<Heading>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Heading heading)
            {

                visitor.blockStart(heading);

                final int length = visitor.length();
                visitor.visitChildren(heading);

                CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

                visitor.setSpansForNodeOptional(heading, length);

                visitor.blockEnd(heading);
            }
        });
    }

    private static void softLineBreak(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(SoftLineBreak.class, new StylarVisitor.NodeVisitor<SoftLineBreak>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull SoftLineBreak softLineBreak)
            {
                visitor.builder().append(' ');
            }
        });
    }

    private static void hardLineBreak(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(HardLineBreak.class, new StylarVisitor.NodeVisitor<HardLineBreak>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull HardLineBreak hardLineBreak)
            {
                visitor.ensureNewLine();
            }
        });
    }

    private static void paragraph(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Paragraph.class, new StylarVisitor.NodeVisitor<Paragraph>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Paragraph paragraph)
            {

                final boolean inTightList = isInTightList(paragraph);

                if (!inTightList)
                {
                    visitor.blockStart(paragraph);
                }

                final int length = visitor.length();
                visitor.visitChildren(paragraph);

                CoreProps.PARAGRAPH_IS_IN_TIGHT_LIST.set(visitor.renderProps(), inTightList);

                // @since 1.1.1 apply paragraph span
                visitor.setSpansForNodeOptional(paragraph, length);

                if (!inTightList)
                {
                    visitor.blockEnd(paragraph);
                }
            }
        });
    }

    private static boolean isInTightList(@NonNull Paragraph paragraph)
    {
        final Node parent = paragraph.getParent();
        if (parent != null)
        {
            final Node gramps = parent.getParent();
            if (gramps instanceof ListBlock)
            {
                ListBlock list = (ListBlock) gramps;
                return list.isTight();
            }
        }
        return false;
    }

    private static void link(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Link.class, new StylarVisitor.NodeVisitor<Link>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Link link)
            {

                final int length = visitor.length();
                visitor.visitChildren(link);

                final String destination = link.getDestination();

                CoreProps.LINK_DESTINATION.set(visitor.renderProps(), destination);

                visitor.setSpansForNodeOptional(link, length);
            }
        });
    }

    /**
     * @since 4.5.0
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public CorePlugin hasExplicitMovementMethod(boolean hasExplicitMovementMethod)
    {
        this.hasExplicitMovementMethod = hasExplicitMovementMethod;
        return this;
    }

    /**
     * Can be useful to post-process text added. For example for auto-linking capabilities.
     *
     * @see OnTextAddedListener
     * @since 4.0.0
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public CorePlugin addOnTextAddedListener(@NonNull OnTextAddedListener onTextAddedListener)
    {
        onTextAddedListeners.add(onTextAddedListener);
        return this;
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder)
    {
        text(builder);
        strongEmphasis(builder);
        emphasis(builder);
        blockQuote(builder);
        code(builder);
        fencedCodeBlock(builder);
        indentedCodeBlock(builder);
        image(builder);
        bulletList(builder);
        orderedList(builder);
        listItem(builder);
        thematicBreak(builder);
        heading(builder);
        softLineBreak(builder);
        hardLineBreak(builder);
        paragraph(builder);
        link(builder);
    }

    @Override
    public void configureSpansFactory(@NonNull StylarSpansFactory.Builder builder)
    {

        // reuse this one for both code-blocks (indent & fenced)
        final CodeBlockSpanFactory codeBlockSpanFactory = new CodeBlockSpanFactory();

        builder
            .setFactory(StrongEmphasis.class, new StrongEmphasisSpanFactory())
            .setFactory(Emphasis.class, new EmphasisSpanFactory())
            .setFactory(BlockQuote.class, new BlockQuoteSpanFactory())
            .setFactory(Code.class, new CodeSpanFactory())
            .setFactory(FencedCodeBlock.class, codeBlockSpanFactory)
            .setFactory(IndentedCodeBlock.class, codeBlockSpanFactory)
            .setFactory(ListItem.class, new ListItemSpanFactory())
            .setFactory(Heading.class, new HeadingSpanFactory())
            .setFactory(Link.class, new LinkSpanFactory())
            .setFactory(ThematicBreak.class, new ThematicBreakSpanFactory());
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown)
    {
        OrderedListItemSpan.measure(textView, markdown);

        // @since 4.4.0
        // we do not break API compatibility, instead we introduce the `instance of` check
        if (markdown instanceof Spannable)
        {
            final Spannable spannable = (Spannable) markdown;
            TextViewSpan.applyTo(spannable, textView);
        }
    }

    @Override
    public void afterSetText(@NonNull TextView textView)
    {
        // let's ensure that there is a movement method applied
        // we do it `afterSetText` so any user-defined movement method won't be
        // replaced (it should be done in `beforeSetText` or manually on a TextView)
        // @since 4.5.0 we additionally check if we should apply _implicit_ movement method
        if (!hasExplicitMovementMethod && textView.getMovementMethod() == null)
        {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void text(@NonNull StylarVisitor.Builder builder)
    {
        builder.on(Text.class, new StylarVisitor.NodeVisitor<Text>()
        {
            @Override
            public void visit(@NonNull StylarVisitor visitor, @NonNull Text text)
            {

                final String literal = text.getLiteral();

                visitor.builder().append(literal);

                // @since 4.0.0
                if (!onTextAddedListeners.isEmpty())
                {
                    // calculate the start position
                    final int length = visitor.length() - literal.length();
                    for (OnTextAddedListener onTextAddedListener : onTextAddedListeners)
                    {
                        onTextAddedListener.onTextAdded(visitor, literal, length);
                    }
                }
            }
        });
    }

    /**
     * @see #addOnTextAddedListener(OnTextAddedListener)
     * @since 4.0.0
     */
    public interface OnTextAddedListener
    {

        /**
         * Will be called when new text is added to resulting {@link SpannableBuilder}.
         * Please note that only text represented by {@link Text} node will trigger this callback
         * (text inside code and code-blocks won\'t trigger it).
         * <p>
         * Please note that if you wish to add spans you must use {@code start} parameter
         * in order to place spans correctly ({@code start} represents the index at which {@code text}
         * was added). So, to set a span for the whole length of the text added one should use:
         * <p>
         * {@code
         * visitor.builder().setSpan(new MySpan(), start, start + text.length(), 0);
         * }
         *
         * @param visitor {@link StylarVisitor}
         * @param text    literal that had been added
         * @param start   index in {@code visitor} as which text had been added
         * @see #addOnTextAddedListener(OnTextAddedListener)
         */
        void onTextAdded(@NonNull StylarVisitor visitor, @NonNull String text, int start);
    }
}
