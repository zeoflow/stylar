package com.zeoflow.stylar.html.tag;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.node.ListItem;

import java.util.Arrays;
import java.util.Collection;

import com.zeoflow.stylar.StylarConfiguration;
import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.SpannableBuilder;
import com.zeoflow.stylar.core.CoreProps;
import com.zeoflow.stylar.html.HtmlTag;
import com.zeoflow.stylar.html.StylarHtmlRenderer;
import com.zeoflow.stylar.html.TagHandler;

public class ListHandler extends TagHandler {

    @Override
    public void handle(
            @NonNull StylarVisitor visitor,
            @NonNull StylarHtmlRenderer renderer,
            @NonNull HtmlTag tag) {

        if (!tag.isBlock()) {
            return;
        }

        final HtmlTag.Block block = tag.getAsBlock();
        final boolean ol = "ol".equals(block.name());
        final boolean ul = "ul".equals(block.name());

        if (!ol && !ul) {
            return;
        }

        final StylarConfiguration configuration = visitor.configuration();
        final RenderProps renderProps = visitor.renderProps();
        final SpanFactory spanFactory = configuration.spansFactory().get(ListItem.class);

        int number = 1;
        final int bulletLevel = currentBulletListLevel(block);

        for (HtmlTag.Block child : block.children()) {

            visitChildren(visitor, renderer, child);

            if (spanFactory != null && "li".equals(child.name())) {

                // insert list item here
                if (ol) {
                    CoreProps.LIST_ITEM_TYPE.set(renderProps, CoreProps.ListItemType.ORDERED);
                    CoreProps.ORDERED_LIST_ITEM_NUMBER.set(renderProps, number++);
                } else {
                    CoreProps.LIST_ITEM_TYPE.set(renderProps, CoreProps.ListItemType.BULLET);
                    CoreProps.BULLET_LIST_ITEM_LEVEL.set(renderProps, bulletLevel);
                }

                SpannableBuilder.setSpans(
                        visitor.builder(),
                        spanFactory.getSpans(configuration, renderProps),
                        child.start(),
                        child.end());
            }
        }
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Arrays.asList("ol", "ul");
    }

    private static int currentBulletListLevel(@NonNull HtmlTag.Block block) {
        int level = 0;
        while ((block = block.parent()) != null) {
            if ("ul".equals(block.name())
                    || "ol".equals(block.name())) {
                level += 1;
            }
        }
        return level;
    }
}
