package com.zeoflow.stylar;

import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import com.zeoflow.stylar.core.StylarTheme;

/**
 * Class that extends {@link StylarPlugin} with all methods implemented (empty body)
 * for easier plugin implementation. Only required methods can be overriden
 *
 * @see StylarPlugin
 * @since 3.0.0
 */
public abstract class AbstractStylarPlugin implements StylarPlugin
{

    @Override
    public void configure(@NonNull Registry registry) {

    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {

    }

    @Override
    public void configureTheme(@NonNull StylarTheme.Builder builder) {

    }

    @Override
    public void configureConfiguration(@NonNull StylarConfiguration.Builder builder) {

    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder) {

    }

    @Override
    public void configureSpansFactory(@NonNull StylarSpansFactory.Builder builder) {

    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        return markdown;
    }

    @Override
    public void beforeRender(@NonNull Node node) {

    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull StylarVisitor visitor) {

    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {

    }

    @Override
    public void afterSetText(@NonNull TextView textView) {

    }
}
