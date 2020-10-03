package com.zeoflow.stylar;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * @see LinkResolverDef
 * @see StylarConfiguration.Builder#linkResolver(LinkResolver)
 * @since 4.0.0
 */
public interface LinkResolver {
    void resolve(@NonNull View view, @NonNull String link);
}
