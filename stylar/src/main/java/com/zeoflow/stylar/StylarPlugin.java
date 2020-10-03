package com.zeoflow.stylar;

import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.zeoflow.stylar.core.CorePlugin;
import com.zeoflow.stylar.core.StylarTheme;
import com.zeoflow.stylar.image.AsyncDrawableSpan;
import com.zeoflow.stylar.movement.MovementMethodPlugin;

/**
 * Class represents a plugin (extension) to Markwon to configure how parsing and rendering
 * of markdown is carried on.
 *
 * @see AbstractStylarPlugin
 * @see CorePlugin
 * @see MovementMethodPlugin
 * @since 3.0.0
 */
public interface StylarPlugin
{

    /**
     * @see Registry#require(Class, Action)
     * @since 4.0.0
     */
    interface Action<P extends StylarPlugin> {
        void apply(@NonNull P p);
    }

    /**
     * @see #configure(Registry)
     * @since 4.0.0
     */
    interface Registry {

        @NonNull
        <P extends StylarPlugin> P require(@NonNull Class<P> plugin);

        <P extends StylarPlugin> void require(
                @NonNull Class<P> plugin,
                @NonNull Action<? super P> action);
    }

    /**
     * This method will be called before any other during {@link Stylar} instance construction.
     *
     * @since 4.0.0
     */
    void configure(@NonNull Registry registry);

    /**
     * Method to configure <code>org.commonmark.parser.Parser</code> (for example register custom
     * extension, etc).
     */
    void configureParser(@NonNull Parser.Builder builder);

    /**
     * Modify {@link StylarTheme} that is used for rendering of markdown.
     *
     * @see StylarTheme
     * @see StylarTheme.Builder
     */
    void configureTheme(@NonNull StylarTheme.Builder builder);

    /**
     * Configure {@link StylarConfiguration}
     *
     * @see StylarConfiguration
     * @see StylarConfiguration.Builder
     */
    void configureConfiguration(@NonNull StylarConfiguration.Builder builder);

    /**
     * Configure {@link StylarVisitor} to accept new node types or override already registered nodes.
     *
     * @see StylarVisitor
     * @see StylarVisitor.Builder
     */
    void configureVisitor(@NonNull StylarVisitor.Builder builder);

    /**
     * Configure {@link StylarSpansFactory} to change what spans are used for certain node types.
     *
     * @see StylarSpansFactory
     * @see StylarSpansFactory.Builder
     */
    void configureSpansFactory(@NonNull StylarSpansFactory.Builder builder);

    /**
     * Process input markdown and return new string to be used in parsing stage further.
     * Can be described as <code>pre-processing</code> of markdown String.
     *
     * @param markdown String to process
     * @return processed markdown String
     */
    @NonNull
    String processMarkdown(@NonNull String markdown);

    /**
     * This method will be called <strong>before</strong> rendering will occur thus making possible
     * to <code>post-process</code> parsed node (make changes for example).
     *
     * @param node root parsed org.commonmark.node.Node
     */
    void beforeRender(@NonNull Node node);

    /**
     * This method will be called <strong>after</strong> rendering (but before applying markdown to a
     * TextView, if such action will happen). It can be used to clean some
     * internal state, or trigger certain action. Please note that modifying <code>node</code> won\'t
     * have any effect as it has been already <i>visited</i> at this stage.
     *
     * @param node    root parsed org.commonmark.node.Node
     * @param visitor {@link StylarVisitor} instance used to render markdown
     */
    void afterRender(@NonNull Node node, @NonNull StylarVisitor visitor);

    /**
     * This method will be called <strong>before</strong> calling <code>TextView#setText</code>.
     * <p>
     * It can be useful to prepare a TextView for markdown. For example {@code ru.noties.stylar.image.ImagesPlugin}
     * uses this method to unregister previously registered {@link AsyncDrawableSpan}
     * (if there are such spans in this TextView at this point). Or {@link CorePlugin}
     * which measures ordered list numbers
     *
     * @param textView TextView to which <code>markdown</code> will be applied
     * @param markdown Parsed markdown
     */
    void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown);

    /**
     * This method will be called <strong>after</strong> markdown was applied.
     * <p>
     * It can be useful to trigger certain action on spans/textView. For example {@code ru.noties.stylar.image.ImagesPlugin}
     * uses this method to register {@link AsyncDrawableSpan} and start
     * asynchronously loading images.
     * <p>
     * Unlike {@link #beforeSetText(TextView, Spanned)} this method does not receive parsed markdown
     * as at this point spans must be queried by calling <code>TextView#getText#getSpans</code>.
     *
     * @param textView TextView to which markdown was applied
     */
    void afterSetText(@NonNull TextView textView);
}
