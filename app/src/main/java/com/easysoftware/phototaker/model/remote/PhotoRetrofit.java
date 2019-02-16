package com.easysoftware.phototaker.model.remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoRetrofit {
    private static volatile Retrofit retrofit = null;
    private static Object mutex = new Object();

    private PhotoRetrofit() {}

    public static Retrofit getInstance(String baseUrl) {
        if (retrofit == null) {
            synchronized (mutex) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
