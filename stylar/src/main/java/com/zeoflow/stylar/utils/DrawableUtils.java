package com.zeoflow.stylar.utils;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * @deprecated Please use {@link com.zeoflow.stylar.image.DrawableUtils}
 */
@Deprecated
public abstract class DrawableUtils
{

    private DrawableUtils()
    {
    }

    public static void intrinsicBounds(@NonNull Drawable drawable)
    {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
}
