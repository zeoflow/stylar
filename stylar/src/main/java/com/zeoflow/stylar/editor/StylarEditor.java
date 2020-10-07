package com.zeoflow.stylar.editor;

import android.text.Editable;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.Stylar;

import java.util.HashMap;
import java.util.Map;

/**
 * @see #builder(Stylar)
 * @see #create(Stylar)
 * @see #process(Editable)
 * @see #preRender(Editable, PreRenderResultListener)
 * @since 4.2.0
 */
public abstract class StylarEditor
{

    /**
     * Creates default instance of {@link StylarEditor}. By default it will handle only
     * punctuation spans (highlight markdown punctuation and nothing more).
     *
     * @see #builder(Stylar)
     */
    @NonNull
    public static StylarEditor create(@NonNull Stylar stylar)
    {
        return builder(stylar).build();
    }

    /**
     * @see #create(Stylar)
     * @see Builder
     */
    @NonNull
    public static Builder builder(@NonNull Stylar stylar)
    {
        return new Builder(stylar);
    }

    /**
     * Synchronous method that processes supplied Editable in-place. If you wish to move this job
     * to another thread consider using {@link #preRender(Editable, PreRenderResultListener)}
     *
     * @param editable to process
     * @see #preRender(Editable, PreRenderResultListener)
     */
    public abstract void process(@NonNull Editable editable);

    /**
     * Pre-render highlight result. Can be useful to create highlight information on a different
     * thread.
     * <p>
     * Please note that currently only `setSpan` and `removeSpan` actions will be recorded (and thus dispatched).
     * Make sure you use only these methods in your {@link EditHandler}, or implement the required
     * functionality some other way.
     *
     * @param editable          to process and pre-render
     * @param preRenderListener listener to be notified when pre-render result will be ready
     * @see #process(Editable)
     */
    public abstract void preRender(@NonNull Editable editable, @NonNull PreRenderResultListener preRenderListener);

    /**
     * @see #preRender(Editable, PreRenderResultListener)
     */
    public interface PreRenderResult
    {

        /**
         * @return Editable instance for which result was calculated. This must not be
         * actual Editable of EditText
         */
        @NonNull
        Editable resultEditable();

        /**
         * Dispatch pre-rendering result to EditText
         *
         * @param editable to dispatch result to
         */
        void dispatchTo(@NonNull Editable editable);
    }

    /**
     * @see #preRender(Editable, PreRenderResultListener)
     */
    public interface PreRenderResultListener
    {
        void onPreRenderResult(@NonNull PreRenderResult result);
    }


    interface SpansHandler
    {
        void handle(
            @NonNull PersistedSpans spans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull Object span,
            int spanStart,
            int spanTextLength);
    }

    public static class Builder
    {

        private final Stylar stylar;
        private final PersistedSpans.Provider persistedSpansProvider = PersistedSpans.provider();
        private final Map<Class<?>, EditHandler> editHandlers = new HashMap<>(0);

        private Class<?> punctuationSpanType;

        Builder(@NonNull Stylar stylar)
        {
            this.stylar = stylar;
        }

        @NonNull
        public <T> Builder useEditHandler(@NonNull EditHandler<T> handler)
        {
            this.editHandlers.put(handler.markdownSpanType(), handler);
            return this;
        }


        /**
         * Specify which punctuation span will be used.
         *
         * @param type    of the span
         * @param factory to create a new instance of the span
         */
        @NonNull
        public <T> Builder punctuationSpan(@NonNull Class<T> type, @NonNull PersistedSpans.SpanFactory<T> factory)
        {
            this.punctuationSpanType = type;
            this.persistedSpansProvider.persistSpan(type, factory);
            return this;
        }

        @NonNull
        public StylarEditor build()
        {

            Class<?> punctuationSpanType = this.punctuationSpanType;
            if (punctuationSpanType == null)
            {
                punctuationSpan(PunctuationSpan.class, new PersistedSpans.SpanFactory<PunctuationSpan>()
                {
                    @NonNull
                    @Override
                    public PunctuationSpan create()
                    {
                        return new PunctuationSpan();
                    }
                });
                punctuationSpanType = this.punctuationSpanType;
            }

            for (EditHandler handler : editHandlers.values())
            {
                handler.init(stylar);
                handler.configurePersistedSpans(persistedSpansProvider);
            }

            final SpansHandler spansHandler = editHandlers.size() == 0
                ? null
                : new SpansHandlerImpl(editHandlers);

            return new StylarEditorImpl(
                stylar,
                persistedSpansProvider,
                punctuationSpanType,
                spansHandler);
        }
    }

    static class SpansHandlerImpl implements SpansHandler
    {

        private final Map<Class<?>, EditHandler> spanHandlers;

        SpansHandlerImpl(@NonNull Map<Class<?>, EditHandler> spanHandlers)
        {
            this.spanHandlers = spanHandlers;
        }

        @Override
        public void handle(
            @NonNull PersistedSpans spans,
            @NonNull Editable editable,
            @NonNull String input,
            @NonNull Object span,
            int spanStart,
            int spanTextLength)
        {
            final EditHandler handler = spanHandlers.get(span.getClass());
            if (handler != null)
            {
                //noinspection unchecked
                handler.handleMarkdownSpan(spans, editable, input, span, spanStart, spanTextLength);
            }
        }
    }
}
