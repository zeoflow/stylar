package com.zeoflow.stylar;

import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.text.PrecomputedTextCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Please note this class requires `androidx.core:core` artifact being explicitly added to your dependencies.
 * This is intended to be used in a RecyclerView.
 *
 * @see Stylar.TextSetter
 * @since 4.3.1
 */
public class PrecomputedFutureTextSetterCompat implements Stylar.TextSetter
{

    @Nullable
    private final Executor executor;

    @SuppressWarnings("WeakerAccess")
    PrecomputedFutureTextSetterCompat(@Nullable Executor executor)
    {
        this.executor = executor;
    }

    /**
     * @param executor for background execution of text pre-computation,
     *                 if not provided the standard, single threaded one will be used.
     */
    @NonNull
    public static PrecomputedFutureTextSetterCompat create(@Nullable Executor executor)
    {
        return new PrecomputedFutureTextSetterCompat(executor);
    }

    @NonNull
    public static PrecomputedFutureTextSetterCompat create()
    {
        return new PrecomputedFutureTextSetterCompat(null);
    }

    @Override
    public void setText(
        @NonNull TextView textView,
        @NonNull Spanned markdown,
        @NonNull TextView.BufferType bufferType,
        @NonNull Runnable onComplete)
    {
        if (textView instanceof AppCompatTextView)
        {
            final AppCompatTextView appCompatTextView = (AppCompatTextView) textView;
            final Future<PrecomputedTextCompat> future = PrecomputedTextCompat.getTextFuture(
                markdown,
                appCompatTextView.getTextMetricsParamsCompat(),
                executor);
            appCompatTextView.setTextFuture(future);
            // `setTextFuture` is actually a synchronous call, so we should call onComplete now
            onComplete.run();
        } else
        {
            throw new IllegalStateException("TextView provided is not an instance of AppCompatTextView, " +
                "cannot call setTextFuture(), textView: " + textView);
        }
    }
}
