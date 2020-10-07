package com.zeoflow.stylar.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zeoflow.stylar.Stylar;
import com.zeoflow.stylar.StylarReducer;

import org.commonmark.node.Node;

import java.util.List;

/**
 * Adapter to display markdown in a RecyclerView. It is done by extracting root blocks from a
 * parsed markdown document (via {@link StylarReducer} and rendering each block in a standalone RecyclerView entry. Provides
 * ability to customize rendering of blocks. For example display certain blocks in a horizontal
 * scrolling container or display tables in a specific widget designed for it ({@link Builder#include(Class, Entry)}).
 *
 * @see #builder(int, int)
 * @see #builder(Entry)
 * @see #create(int, int)
 * @see #create(Entry)
 * @see #setMarkdown(Stylar, String)
 * @see #setParsedMarkdown(Stylar, Node)
 * @see #setParsedMarkdown(Stylar, List)
 * @since 3.0.0
 */
public abstract class StylarAdapter extends RecyclerView.Adapter<StylarAdapter.Holder>
{

    @NonNull
    public static Builder builderTextViewIsRoot(@LayoutRes int defaultEntryLayoutResId)
    {
        return builder(SimpleEntry.createTextViewIsRoot(defaultEntryLayoutResId));
    }

    /**
     * Factory method to obtain {@link Builder} instance.
     *
     * @see Builder
     */
    @NonNull
    public static Builder builder(
        @LayoutRes int defaultEntryLayoutResId,
        @IdRes int defaultEntryTextViewResId
    )
    {
        return builder(SimpleEntry.create(defaultEntryLayoutResId, defaultEntryTextViewResId));
    }

    @NonNull
    public static Builder builder(@NonNull Entry<? extends Node, ? extends Holder> defaultEntry)
    {
        //noinspection unchecked
        return new StylarAdapterImpl.BuilderImpl((Entry<Node, Holder>) defaultEntry);
    }

    @NonNull
    public static StylarAdapter createTextViewIsRoot(@LayoutRes int defaultEntryLayoutResId)
    {
        return builderTextViewIsRoot(defaultEntryLayoutResId)
            .build();
    }

    /**
     * Factory method to create a {@link StylarAdapter} for evaluation purposes. Resulting
     * adapter will use default layout for all blocks. Default layout has no styling and should
     * be specified explicitly.
     *
     * @see #create(Entry)
     * @see #builder(int, int)
     * @see SimpleEntry
     */
    @NonNull
    public static StylarAdapter create(
        @LayoutRes int defaultEntryLayoutResId,
        @IdRes int defaultEntryTextViewResId
    )
    {
        return builder(defaultEntryLayoutResId, defaultEntryTextViewResId).build();
    }

    /**
     * Factory method to create a {@link StylarAdapter} that uses supplied entry to render all
     * nodes.
     *
     * @param defaultEntry {@link Entry} to be used for node rendering
     * @see #builder(Entry)
     */
    @NonNull
    public static StylarAdapter create(@NonNull Entry<? extends Node, ? extends Holder> defaultEntry)
    {
        return builder(defaultEntry).build();
    }

    public abstract void setMarkdown(@NonNull Stylar stylar, @NonNull String markdown);

    public abstract void setParsedMarkdown(@NonNull Stylar stylar, @NonNull Node document);

    public abstract void setParsedMarkdown(@NonNull Stylar stylar, @NonNull List<Node> nodes);

    public abstract int getNodeViewType(@NonNull Class<? extends Node> node);

    /**
     * Builder to create an instance of {@link StylarAdapter}
     *
     * @see #include(Class, Entry)
     * @see #reducer(StylarReducer)
     * @see #build()
     */
    public interface Builder
    {

        /**
         * Include a custom {@link Entry} rendering for a Node. Please note that `node` argument
         * must be <em>exact</em> type, as internally there is no validation for inheritance. if multiple
         * nodes should be rendered with the same {@link Entry} they must specify so explicitly.
         * By calling this method for each.
         *
         * @param node  type of the node to register
         * @param entry {@link Entry} to be used for `node` rendering
         * @return self
         */
        @NonNull
        <N extends Node> Builder include(
            @NonNull Class<N> node,
            @NonNull Entry<? super N, ? extends Holder> entry);

        /**
         * Specify how root Node will be <em>reduced</em> to a list of nodes. There is a default
         * {@link StylarReducer} that will be used if not provided explicitly (there is no need to
         * register your own unless you require it).
         *
         * @param reducer {@link StylarReducer}
         * @return self
         * @see StylarReducer
         */
        @NonNull
        Builder reducer(@NonNull StylarReducer reducer);

        /**
         * @return {@link StylarAdapter}
         */
        @NonNull
        StylarAdapter build();
    }

    /**
     * @see SimpleEntry
     */
    public static abstract class Entry<N extends Node, H extends Holder>
    {

        @NonNull
        public abstract H createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

        public abstract void bindHolder(@NonNull Stylar stylar, @NonNull H holder, @NonNull N node);

        /**
         * Will be called when new content is available (clear internal cache if any)
         */
        public void clear()
        {

        }

        public long id(@NonNull N node)
        {
            return node.hashCode();
        }

        public void onViewRecycled(@NonNull H holder)
        {

        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class Holder extends RecyclerView.ViewHolder
    {

        public Holder(@NonNull View itemView)
        {
            super(itemView);
        }

        // please note that this method should be called after constructor
        @Nullable
        protected <V extends View> V findView(@IdRes int id)
        {
            return itemView.findViewById(id);
        }

        // please note that this method should be called after constructor
        @NonNull
        protected <V extends View> V requireView(@IdRes int id)
        {
            final V v = itemView.findViewById(id);
            if (v == null)
            {
                final String name;
                if (id == 0
                    || id == View.NO_ID)
                {
                    name = String.valueOf(id);
                } else
                {
                    name = "R.id." + itemView.getResources().getResourceName(id);
                }
                throw new NullPointerException(String.format("No view with id(R.id.%s) is found " +
                    "in layout: %s", name, itemView));
            }
            return v;
        }
    }
}
