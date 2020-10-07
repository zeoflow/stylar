package com.zeoflow.stylar.utils;

import android.content.Context;

import androidx.annotation.NonNull;

public class Dip
{

    private final float density;

    @SuppressWarnings("WeakerAccess")
    public Dip(float density)
    {
        this.density = density;
    }

    @NonNull
    public static Dip create(@NonNull Context context)
    {
        return new Dip(context.getResources().getDisplayMetrics().density);
    }

    @NonNull
    public static Dip create(float density)
    {
        return new Dip(density);
    }

    public int toPx(int dp)
    {
        return (int) (dp * density + .5F);
    }
}
