package com.zeoflow.stylar.image;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.zeoflow.stylar.StylarConfiguration;

/**
 * @see ImageSizeResolverDef
 * @see StylarConfiguration.Builder#imageSizeResolver(ImageSizeResolver)
 * @since 1.0.1
 */
public abstract class ImageSizeResolver
{

    /**
     * @since 4.0.0
     */
    @NonNull
    public abstract Rect resolveImageSize(@NonNull AsyncDrawable drawable);
}
