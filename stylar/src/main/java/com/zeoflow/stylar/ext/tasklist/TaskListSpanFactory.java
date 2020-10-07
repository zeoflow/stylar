package com.zeoflow.stylar.ext.tasklist;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.RenderProps;
import com.zeoflow.stylar.SpanFactory;
import com.zeoflow.stylar.StylarConfiguration;

public class TaskListSpanFactory implements SpanFactory
{

    private final Drawable drawable;

    public TaskListSpanFactory(@NonNull Drawable drawable)
    {
        this.drawable = drawable;
    }

    @Nullable
    @Override
    public Object getSpans(@NonNull StylarConfiguration configuration, @NonNull RenderProps props)
    {
        return new TaskListSpan(
            configuration.theme(),
            drawable,
            TaskListProps.DONE.get(props, false)
        );
    }
}
