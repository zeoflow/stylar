package com.zeoflow.stylar.ext.tasklist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.zeoflow.stylar.AbstractStylarPlugin;
import com.zeoflow.stylar.StylarSpansFactory;
import com.zeoflow.stylar.StylarVisitor;

import org.commonmark.parser.Parser;

/**
 * @since 3.0.0
 */
public class TaskListPlugin extends AbstractStylarPlugin
{

    /**
     * Supplied Drawable must be stateful ({@link Drawable#isStateful()} returns true). If a task
     * is marked as done, then this drawable will be updated with an {@code int[] { android.R.attr.state_checked }}
     * as the state, otherwise an empty array will be used. This library provides a ready to be
     * used Drawable: {@link TaskListDrawable}
     *
     * @see TaskListDrawable
     */
    @NonNull
    public static TaskListPlugin create(@NonNull Drawable drawable) {
        return new TaskListPlugin(drawable);
    }

    @NonNull
    public static TaskListPlugin create(@NonNull Context context) {

        // by default we will be using link color for the checkbox color
        // & window background as a checkMark color
        final int linkColor = resolve(context, android.R.attr.textColorLink);
        final int backgroundColor = resolve(context, android.R.attr.colorBackground);

        return new TaskListPlugin(new TaskListDrawable(linkColor, linkColor, backgroundColor));
    }

    @NonNull
    public static TaskListPlugin create(
            @ColorInt int checkedFillColor,
            @ColorInt int normalOutlineColor,
            @ColorInt int checkMarkColor) {
        return new TaskListPlugin(new TaskListDrawable(
                checkedFillColor,
                normalOutlineColor,
                checkMarkColor));
    }

    private final Drawable drawable;

    private TaskListPlugin(@NonNull Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.postProcessor(new TaskListPostProcessor());
    }

    @Override
    public void configureSpansFactory(@NonNull StylarSpansFactory.Builder builder) {
        builder.setFactory(TaskListItem.class, new TaskListSpanFactory(drawable));
    }

    @Override
    public void configureVisitor(@NonNull StylarVisitor.Builder builder) {
        builder
                .on(TaskListItem.class, new StylarVisitor.NodeVisitor<TaskListItem>() {
                    @Override
                    public void visit(@NonNull StylarVisitor visitor, @NonNull TaskListItem taskListItem) {

                        final int length = visitor.length();

                        visitor.visitChildren(taskListItem);

                        TaskListProps.DONE.set(visitor.renderProps(), taskListItem.isDone());

                        visitor.setSpansForNode(taskListItem, length);

                        if (visitor.hasNext(taskListItem)) {
                            visitor.ensureNewLine();
                        }
                    }
                });
    }

    private static int resolve(@NonNull Context context, @AttrRes int attr) {
        final TypedValue typedValue = new TypedValue();
        final int[] attrs = new int[]{attr};
        final TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, attrs);
        try {
            return typedArray.getColor(0, 0);
        } finally {
            typedArray.recycle();
        }
    }
}
