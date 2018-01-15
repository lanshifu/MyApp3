package com.lanshifu.baselibrary.network;

import org.reactivestreams.Publisher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 线程切换简化 使用compose操作符
 * Created by lanxiaobin on 2017/9/6.
 */

public class RxScheduler {

    /**
     * 线程池,默认50个线程,用于大量数据操作, 如异步获取1000 +个联系人名称
     */
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);

    /**
     * Observable 线程切换
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(io.reactivex.Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }


        };
    }

    /**
     * Flowable(不支持背压) 线程切换
     * @param <T>
     * @return
     */
     public static <T> FlowableTransformer<T, T> io_main_flowable() {
            return new FlowableTransformer<T, T>() {
                @Override
                public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                    return upstream
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }

            };
        }


    /**
     * 大量线程操作使用固定大小线程池
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> fixedThreadPool_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(io.reactivex.Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.from(RxScheduler.fixedThreadPool))
                        .observeOn(AndroidSchedulers.mainThread());
            }

        };
    }



}
