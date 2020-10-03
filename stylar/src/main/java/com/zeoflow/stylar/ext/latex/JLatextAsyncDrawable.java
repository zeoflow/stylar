package com.zeoflow.stylar.ext.latex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.image.AsyncDrawable;
import com.zeoflow.stylar.image.AsyncDrawableLoader;
import com.zeoflow.stylar.image.ImageSize;
import com.zeoflow.stylar.image.ImageSizeResolver;

/**
 * @since 4.3.0
 */
class JLatextAsyncDrawable extends AsyncDrawable {

    private final boolean isBlock;

    JLatextAsyncDrawable(
            @NonNull String destination,
            @NonNull AsyncDrawableLoader loader,
            @NonNull ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize,
            boolean isBlock
    ) {
        super(destination, loader, imageSizeResolver, imageSize);
        this.isBlock = isBlock;
    }

    public boolean isBlock() {
        return isBlock;
    }
}
