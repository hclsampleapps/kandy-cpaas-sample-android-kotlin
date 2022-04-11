package com.hcl.kandy.cpaas.utils;

import com.hcl.kandy.cpaas.BuildConfig;

import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitUtils {
    public static HttpLoggingInterceptor getLoggingLevel() {
        if (BuildConfig.DEBUG) {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
    }

}
