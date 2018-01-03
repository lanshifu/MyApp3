package com.lanshifu.myapp_3.network;

import android.text.TextUtils;

import com.lanshifu.baselibrary.MainApplication;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.utils.SystemUtil;
import com.lanshifu.myapp_3.Config;
import com.lanshifu.myapp_3.network.api.ApiConstant;
import com.lanshifu.myapp_3.network.api.DefaultApi;
import com.lanshifu.myapp_3.network.progress.ProgressManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的单例
 * Created by lanxiaobin on 2017/9/6.
 */

public class RetrofitHelper {


    private static RetrofitHelper mRetrofitHelper = null;

    /**
     * 默认有统一请求头的OkHttpClient
     */
    private static OkHttpClient mDefaultOkHttpClient;

    /**
     * 没有统一请求头的OkHttpClient
     */
    private static OkHttpClient mNoHeaderOkHttpClient;

    private RetrofitHelper() {
    }

    public static RetrofitHelper getInstance() {
        synchronized (RetrofitHelper.class) {
            if (mRetrofitHelper == null) {
                mRetrofitHelper = new RetrofitHelper();
            }
        }
        return mRetrofitHelper;
    }

    static {
        initOkHttp();
    }


    private static void initOkHttp() {
        HttpLoggingInterceptor loggingInterceptor = null;
        if (Config.DEBUG) {
            loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogHelper.d("[okhttp] "+message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        if (mDefaultOkHttpClient == null) {
            //设置Http缓存
            Cache cache = new Cache(new File(MainApplication.getContext()
                    .getCacheDir(), "HttpCache"), 1024 * 1024 * 10);

//            mDefaultOkHttpClient = new OkHttpClient.Builder()
            //ProgressManager 里面提供文件上传下载进度监听
            mDefaultOkHttpClient = ProgressManager.getInstance().with(new OkHttpClient.Builder())
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new headerInterceptor())
                    .cache(cache)
                    .retryOnConnectionFailure(true) //错误重连
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }

        if(mNoHeaderOkHttpClient == null){
            mNoHeaderOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .retryOnConnectionFailure(true) //错误重连
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }

    }


    /**
     * 统一添加请求头
     */
    private static class headerInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder builder = original.newBuilder()
                    .addHeader("system", "0");
            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    private class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (SystemUtil.isNetworkAvailable()) {
                //有网络时只从网络获取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (SystemUtil.isNetworkAvailable()) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }

    }


    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private <T> T createApi(Class<T> clazz, String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mDefaultOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }


    /**
     * 根据传入的baseUrl，和api创建retrofit
     * @param clazz
     * @param baseUrl
     * @param okHttpClient 自定义okHttpClient
     * @param <T>
     * @return
     */
    private <T> T createApi(Class<T> clazz, String baseUrl,OkHttpClient okHttpClient) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }


    /**
     * 默认请求
     *
     * @return
     */
    public DefaultApi getDefaultApi() {
        return createApi(DefaultApi.class, ApiConstant.URL_DEFAULT);
    }


}
