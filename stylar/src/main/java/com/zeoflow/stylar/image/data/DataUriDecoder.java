package com.zeoflow.stylar.image.data;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class DataUriDecoder
{

    @NonNull
    public static DataUriDecoder create()
    {
        return new Impl();
    }

    @Nullable
    public abstract byte[] decode(@NonNull DataUri dataUri) throws Throwable;

    static class Impl extends DataUriDecoder
    {

        private static final String CHARSET = "UTF-8";

        @Nullable
        @Override
        public byte[] decode(@NonNull DataUri dataUri) throws Throwable
        {

            final String data = dataUri.data();

            if (!TextUtils.isEmpty(data))
            {
                if (dataUri.base64())
                {
                    return Base64.decode(data.getBytes(CHARSET), Base64.DEFAULT);
                } else
                {
                    return data.getBytes(CHARSET);
                }
            } else
            {
                return null;
            }
        }
    }
}
