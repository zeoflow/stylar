package com.zeoflow.stylar.editor;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.Stylar;
import com.zeoflow.stylar.editor.handler.EmphasisEditHandler;
import com.zeoflow.stylar.editor.handler.StrongEmphasisEditHandler;

/**
 * @see EditHandler
 * @see EmphasisEditHandler
 * @see StrongEmphasisEditHandler
 * @since 4.2.0
 */
public abstract class AbstractEditHandler<T> implements EditHandler<T>
{
    @Override
    public void init(@NonNull Stylar stylar)
    {

    }
}
