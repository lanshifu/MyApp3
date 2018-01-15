package com.lanshifu.baselibrary.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的单例
 * Created by lanxiaobin on 2017/9/6.
 */

public class BaseRetrofitHelper {

    /**
     * 根据传入的baseUrl，和api创建retrofit
     *
     * @param clazz
     * @param baseUrl
     * @param okHttpClient 自定义okHttpClient
     * @param <T>
     * @return
     */
    protected <T> T createApi(Class<T> clazz, String baseUrl, OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }


}
