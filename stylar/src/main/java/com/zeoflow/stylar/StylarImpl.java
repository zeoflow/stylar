package com.zeoflow.stylar;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.view.StylarView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @since 3.0.0
 */
public class StylarImpl extends Stylar
{

    private final TextView.BufferType bufferType;
    private final Parser parser;
    private final StylarVisitorFactory visitorFactory; // @since 4.1.1
    private final StylarConfiguration configuration;
    private final List<StylarPlugin> plugins;
    private StylarView stylarView;
    private boolean anchoredHeadings;
    private boolean imagePlugins;
    private boolean codeStyle;
    private ClickEvent clickEvent;

    // @since 4.1.0
    @Nullable
    private final TextSetter textSetter;

    // @since 4.4.0
    private final boolean fallbackToRawInputWhenEmpty;

    public StylarImpl(
        @NonNull TextView.BufferType bufferType,
        @Nullable TextSetter textSetter,
        @NonNull Parser parser,
        @NonNull StylarVisitorFactory visitorFactory,
        @NonNull StylarConfiguration configuration,
        @NonNull List<StylarPlugin> plugins,
        boolean fallbackToRawInputWhenEmpty,
        @NonNull StylarView stylarView,
        boolean imagePlugins,
        boolean anchoredHeadings,
        boolean codeStyle,
        ClickEvent clickEvent
    )
    {
        this.bufferType = bufferType;
        this.textSetter = textSetter;
        this.parser = parser;
        this.visitorFactory = visitorFactory;
        this.configuration = configuration;
        this.plugins = plugins;
        this.fallbackToRawInputWhenEmpty = fallbackToRawInputWhenEmpty;
        this.stylarView = stylarView;
        this.imagePlugins = imagePlugins;
        this.anchoredHeadings = anchoredHeadings;
        this.codeStyle = codeStyle;
        this.clickEvent = clickEvent;
    }

    @NonNull
    @Override
    public Node parse(@NonNull String input)
    {

        // make sure that all plugins are called `processMarkdown` before parsing
        for (StylarPlugin plugin : plugins)
        {
            input = plugin.processMarkdown(input);
        }

        return parser.parse(input);
    }

    @NonNull
    @Override
    public Spanned render(@NonNull Node node)
    {

        for (StylarPlugin plugin : plugins)
        {
            plugin.beforeRender(node);
        }

        // @since 4.1.1 obtain visitor via factory
        final StylarVisitor visitor = visitorFactory.create();

        node.accept(visitor);

        for (StylarPlugin plugin : plugins)
        {
            plugin.afterRender(node, visitor);
        }

        //noinspection UnnecessaryLocalVariable
        final Spanned spanned = visitor.builder().spannableStringBuilder();

        // clear render props and builder after rendering
        // @since 4.1.1 as we no longer reuse visitor - there is no need to clean it
        //  we might still do it if we introduce a thread-local storage though
//        visitor.clear();

        return spanned;
    }

    @NonNull
    @Override
    public Spanned toMarkdown(@NonNull String input)
    {
        final Spanned spanned = render(parse(input));

        // @since 4.4.0
        // if spanned is empty, we are configured to use raw input and input is not empty
        if (TextUtils.isEmpty(spanned)
            && fallbackToRawInputWhenEmpty
            && !TextUtils.isEmpty(input))
        {
            // let's use SpannableStringBuilder in order to keep backward-compatibility
            return new SpannableStringBuilder(input);
        }

        return spanned;
    }

    @Override
    public void setMarkdown(@NonNull TextView textView, @NonNull String markdown)
    {
        setParsedMarkdown(textView, toMarkdown(markdown));
    }

    @Override
    public void setMarkdown(@NonNull String markdown)
    {
        setParsedMarkdown(stylarView.getMtvTextView(), toMarkdown(markdown));
    }

    @Override
    public void setParsedMarkdown(@NonNull final TextView textView, @NonNull Spanned markdown)
    {

        for (StylarPlugin plugin : plugins)
        {
            plugin.beforeSetText(textView, markdown);
        }

        // @since 4.1.0
        if (textSetter != null)
        {
            textSetter.setText(textView, markdown, bufferType, () ->
            {
                // on-complete we just must call `afterSetText` on all plugins
                for (StylarPlugin plugin : plugins)
                {
                    plugin.afterSetText(textView);
                }
            });
        } else
        {

            // if no text-setter is specified -> just a regular sync operation
            textView.setText(markdown, bufferType);

            for (StylarPlugin plugin : plugins)
            {
                plugin.afterSetText(textView);
            }
        }
    }

    @Override
    public void withLayout(@NonNull StylarView stylarView)
    {
        this.stylarView = stylarView;
    }

    @Override
    public void withAnchoredHeadings(boolean anchoredHeadings)
    {
        this.anchoredHeadings = anchoredHeadings;
    }

    @Override
    public void withImagePlugins(boolean imagePlugins)
    {
        this.imagePlugins = imagePlugins;
    }

    @Override
    public void withCodeStyle(boolean codeStyle)
    {
        this.codeStyle = codeStyle;
    }

    @Override
    public void setClickEvent(@NonNull ClickEvent clickEvent)
    {
        this.clickEvent = clickEvent;
    }

    @Override
    public boolean hasPlugin(@NonNull Class<? extends StylarPlugin> type)
    {
        return getPlugin(type) != null;
    }

    @Nullable
    @Override
    public <P extends StylarPlugin> P getPlugin(@NonNull Class<P> type)
    {
        StylarPlugin out = null;
        for (StylarPlugin plugin : plugins)
        {
            if (type.isAssignableFrom(plugin.getClass()))
            {
                out = plugin;
            }
        }
        //noinspection unchecked
        return (P) out;
    }

    @NonNull
    @Override
    public <P extends StylarPlugin> P requirePlugin(@NonNull Class<P> type)
    {
        final P plugin = getPlugin(type);
        if (plugin == null)
        {
            throw new IllegalStateException(String.format(Locale.US, "Requested plugin `%s` is not " +
                "registered with this Markwon instance", type.getName()));
        }
        return plugin;
    }

    @NonNull
    @Override
    public List<? extends StylarPlugin> getPlugins()
    {
        return Collections.unmodifiableList(plugins);
    }

    @NonNull
    @Override
    public StylarConfiguration configuration()
    {
        return configuration;
    }
}
