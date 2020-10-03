package com.zeoflow.stylar.editor;

import android.text.Editable;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.Stylar;
import com.zeoflow.stylar.editor.handler.EmphasisEditHandler;
import com.zeoflow.stylar.editor.handler.StrongEmphasisEditHandler;

/**
 * @see EmphasisEditHandler
 * @see StrongEmphasisEditHandler
 * @since 4.2.0
 */
public interface EditHandler<T> {

    void init(@NonNull Stylar stylar);

    void configurePersistedSpans(@NonNull PersistedSpans.Builder builder);

    // span is present only in off-screen rendered markdown, it must be processed and
    //  a NEW one must be added to editable (via edit-persist-spans)
    //
    // NB, editable.setSpan must obtain span from `spans` and must be configured beforehand
    // multiple spans are OK as long as they are configured

    /**
     * @param persistedSpans
     * @param editable
     * @param input
     * @param span
     * @param spanStart
     * @param spanTextLength
     * @see StylarEditorUtils
     */
    void handleMarkdownSpan(
            @NonNull PersistedSpans persistedSpans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull T span,
            int spanStart,
            int spanTextLength);

    @NonNull
    Class<T> markdownSpanType();
}
