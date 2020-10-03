package com.zeoflow.stylar;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.core.StylarTheme;
import com.zeoflow.stylar.image.ImagesPlugin;
import com.zeoflow.stylar.image.glide.GlideImagesPlugin;
import com.zeoflow.stylar.plugins.AnchorHeadingPlugin;
import com.zeoflow.stylar.plugins.CodeStylePlugin;
import com.zeoflow.stylar.plugins.GifGlideStorePlugin;
import com.zeoflow.stylar.syntax.CodeStyleThemeDefault;
import com.zeoflow.stylar.syntax.SyntaxHighlightPlugin;
import com.zeoflow.stylar.view.StylarView;

import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @since 3.0.0
 */
class StylarBuilderImpl implements Stylar.Builder
{

    private final Context context;

    private final List<StylarPlugin> plugins = new ArrayList<>(3);

    private TextView.BufferType bufferType = TextView.BufferType.SPANNABLE;

    private Stylar.TextSetter textSetter;

    private StylarView stylarView;

    private boolean anchoredHeadings = false;

    private boolean imagePlugins = false;

    private boolean codeStyle = false;

    private ClickEvent clickEvent;

    // @since 4.4.0
    private boolean fallbackToRawInputWhenEmpty = true;

    StylarBuilderImpl(@NonNull Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public Stylar.Builder bufferType(@NonNull TextView.BufferType bufferType)
    {
        this.bufferType = bufferType;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder textSetter(@NonNull Stylar.TextSetter textSetter)
    {
        this.textSetter = textSetter;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder usePlugin(@NonNull StylarPlugin plugin)
    {
        plugins.add(plugin);
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder usePlugins(@NonNull Iterable<? extends StylarPlugin> plugins)
    {

        final Iterator<? extends StylarPlugin> iterator = plugins.iterator();

        StylarPlugin plugin;

        while (iterator.hasNext())
        {
            plugin = iterator.next();
            if (plugin == null)
            {
                throw new NullPointerException();
            }
            this.plugins.add(plugin);
        }

        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder withAnchoredHeadings(boolean anchoredHeadings)
    {
        this.anchoredHeadings = anchoredHeadings;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder withImagePlugins(boolean imagePlugins)
    {
        this.imagePlugins = imagePlugins;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder withCodeStyle(boolean codeStyle)
    {
        this.codeStyle = codeStyle;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder setClickEvent(@NonNull ClickEvent clickEvent)
    {
        this.clickEvent = clickEvent;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder fallbackToRawInputWhenEmpty(boolean fallbackToRawInputWhenEmpty)
    {
        this.fallbackToRawInputWhenEmpty = fallbackToRawInputWhenEmpty;
        return this;
    }

    @NonNull
    @Override
    public Stylar.Builder withLayoutElement(@NonNull StylarView stylarView)
    {
        this.stylarView = stylarView;
        return this;
    }

    @NonNull
    @Override
    public Stylar build()
    {

        if (plugins.isEmpty())
        {
            throw new IllegalStateException("No plugins were added to this builder. Use #usePlugin " +
                "method to add them");
        }

        // please note that this method must not modify supplied collection
        // if nothing should be done -> the same collection can be returned
        final List<StylarPlugin> plugins = preparePlugins(this.plugins);

        final Parser.Builder parserBuilder = new Parser.Builder();
        final StylarTheme.Builder themeBuilder = StylarTheme.builderWithDefaults(context);
        final StylarConfiguration.Builder configurationBuilder = new StylarConfiguration.Builder();
        final StylarVisitor.Builder visitorBuilder = new StylarVisitorImpl.BuilderImpl();
        final StylarSpansFactory.Builder spanFactoryBuilder = new StylarSpansFactoryImpl.BuilderImpl();

        if (imagePlugins)
        {
            plugins.add(ImagesPlugin.create());
            plugins.add(GlideImagesPlugin.create(context));
            plugins.add(GlideImagesPlugin.create(new GifGlideStorePlugin(Glide.with(context))));
        }
        if (codeStyle)
        {
            plugins.add(SyntaxHighlightPlugin.create(new CodeStyle(new CodeStylePlugin()), CodeStyleThemeDefault.create()));
        }
        if (anchoredHeadings)
        {
            if (stylarView != null)
            {
                plugins.add(new AnchorHeadingPlugin((view, top) -> stylarView.getMtvScrollView().smoothScrollTo(0, top)).withClickEvent(clickEvent));
            }
        }

        for (StylarPlugin plugin : plugins)
        {
            plugin.configureParser(parserBuilder);
            plugin.configureTheme(themeBuilder);
            plugin.configureConfiguration(configurationBuilder);
            plugin.configureVisitor(visitorBuilder);
            plugin.configureSpansFactory(spanFactoryBuilder);
        }

        final StylarConfiguration configuration = configurationBuilder.build(
            themeBuilder.build(),
            spanFactoryBuilder.build());

        // @since 4.1.1
        // @since 4.1.2 - do not reuse render-props (each render call should have own render-props)
        final StylarVisitorFactory visitorFactory = StylarVisitorFactory.create(
            visitorBuilder,
            configuration);

        return new StylarImpl(
            bufferType,
            textSetter,
            parserBuilder.build(),
            visitorFactory,
            configuration,
            Collections.unmodifiableList(plugins),
            fallbackToRawInputWhenEmpty,
            stylarView,
            imagePlugins,
            anchoredHeadings,
            codeStyle,
            clickEvent
        );
    }

    @NonNull
    private static List<StylarPlugin> preparePlugins(@NonNull List<StylarPlugin> plugins)
    {
        return new RegistryImpl(plugins).process();
    }
}
