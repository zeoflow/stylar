package com.zeoflow.gif;

/**
 * Builder for {@link GifDrawable} which can be used to construct new drawables
 * by reusing old ones.
 */
public class GifDrawableBuilder extends  GifDrawableInit<GifDrawableBuilder>{

	@Override
	protected GifDrawableBuilder self() {
		return this;
	}
}
