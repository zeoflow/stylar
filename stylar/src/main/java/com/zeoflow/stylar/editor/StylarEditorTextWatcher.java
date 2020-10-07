package com.zeoflow.stylar.editor;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Implementation of TextWatcher that uses {@link StylarEditor#process(Editable)} method
 * to apply markdown highlighting right after text changes.
 *
 * @see StylarEditor#process(Editable)
 * @see StylarEditor#preRender(Editable, StylarEditor.PreRenderResultListener)
 * @see #withProcess(StylarEditor)
 * @see #withPreRender(StylarEditor, ExecutorService, EditText)
 * @since 4.2.0
 */
public abstract class StylarEditorTextWatcher implements TextWatcher
{

    @NonNull
    public static StylarEditorTextWatcher withProcess(@NonNull StylarEditor editor)
    {
        return new WithProcess(editor);
    }

    @NonNull
    public static StylarEditorTextWatcher withPreRender(
        @NonNull StylarEditor editor,
        @NonNull ExecutorService executorService,
        @NonNull EditText editText)
    {
        return new WithPreRender(editor, executorService, editText);
    }

    @Override
    public abstract void afterTextChanged(Editable s);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }


    static class WithProcess extends StylarEditorTextWatcher
    {

        private final StylarEditor editor;

        private boolean selfChange;

        WithProcess(@NonNull StylarEditor editor)
        {
            this.editor = editor;
        }

        @Override
        public void afterTextChanged(Editable s)
        {

            if (selfChange)
            {
                return;
            }

            selfChange = true;
            try
            {
                editor.process(s);
            } finally
            {
                selfChange = false;
            }
        }
    }

    static class WithPreRender extends StylarEditorTextWatcher
    {

        private final StylarEditor editor;
        private final ExecutorService executorService;

        // As we operate on a single thread (main) we are fine with a regular int
        //  for marking current _generation_
        private int generator;

        @Nullable
        private EditText editText;

        private Future<?> future;

        private boolean selfChange;

        WithPreRender(
            @NonNull StylarEditor editor,
            @NonNull ExecutorService executorService,
            @NonNull EditText editText)
        {
            this.editor = editor;
            this.executorService = executorService;
            this.editText = editText;
            this.editText.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
            {
                @Override
                public void onViewAttachedToWindow(View v)
                {

                }

                @Override
                public void onViewDetachedFromWindow(View v)
                {
                    WithPreRender.this.editText = null;
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s)
        {

            if (selfChange)
            {
                return;
            }

            // both will be the same here (generator incremented and key assigned incremented value)
            final int key = ++this.generator;

            if (future != null)
            {
                future.cancel(true);
            }

            // copy current content (it's not good to pass EditText editable to other thread)
            final SpannableStringBuilder builder = new SpannableStringBuilder(s);

            future = executorService.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        editor.preRender(builder, new StylarEditor.PreRenderResultListener()
                        {
                            @Override
                            public void onPreRenderResult(@NonNull final StylarEditor.PreRenderResult result)
                            {
                                final EditText et = editText;
                                if (et != null)
                                {
                                    et.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (key == generator)
                                            {
                                                final EditText et = editText;
                                                if (et != null)
                                                {
                                                    selfChange = true;
                                                    try
                                                    {
                                                        result.dispatchTo(editText.getText());
                                                    } finally
                                                    {
                                                        selfChange = false;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } catch (final Throwable t)
                    {
                        final EditText et = editText;
                        if (et != null)
                        {
                            // propagate exception to main thread
                            et.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    throw new RuntimeException(t);
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
