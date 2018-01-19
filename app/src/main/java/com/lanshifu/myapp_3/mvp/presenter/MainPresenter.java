package com.lanshifu.myapp_3.mvp.presenter;

import android.content.Context;
import android.os.Environment;

import com.didi.virtualapk.PluginManager;
import com.jaredrummler.android.shell.Shell;
import com.lanshifu.baselibrary.basemvp.BasePresenter;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.network.RxScheduler;
import com.lanshifu.baselibrary.utils.ToastUtil;
import com.lanshifu.myapp_3.model.WeatherInfo;
import com.lanshifu.myapp_3.mvp.view.MainView;
import com.lanshifu.myapp_3.network.MyObserver;
import com.lanshifu.myapp_3.network.RetrofitHelper;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by lanshifu on 2018/1/14.
 */

public class MainPresenter extends BasePresenter<MainView> {

    private static final String PLUGIN_NAME = "imageplugin.apk";
    private String mTempPicPath;
    private String mPicturePath;
    private HashMap<String, Integer> mAnswers;
    private long starTime = 0;

    private int mCounter = 0;
    public void loadPlugin(Context base) {
        PluginManager pluginManager = PluginManager.getInstance(base);
//        File apk = new File(StorageUtil.getPluginFolder() + "plugin.apk");
        File apk = new File(Environment.getExternalStorageDirectory(), PLUGIN_NAME);
        if (apk.exists()) {
            LogHelper.d("loadPlugin");
            try {
                pluginManager.loadPlugin(apk);
                LogHelper.d("loadPlugin 插件plugin success");
            } catch (Exception e) {
                ToastUtil.showShortToast("plugin插件加载失败" + e.getMessage());
                LogHelper.d("插件load 失败 " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast("插件apk不存在");
        }
    }

    public void checkRootPermission() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                e.onNext(Shell.SU.available());
            }
        }).compose(RxScheduler.<Boolean>io_main())
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void _onNext(Boolean root) {
                        if (root) {
                            mView.hasRootPermission();
                        } else {
                            ToastUtil.showShortToast("手机没有root权限，无法使用部分功能");
                        }
                    }

                    @Override
                    public void _onError(String e) {
                        ToastUtil.showShortToast(e);
                    }
                });
    }


    public void getWeather(){
        RetrofitHelper.getInstance().getDefaultApi()
                .getWheather("101280101")
                .compose(RxScheduler.<WeatherInfo>io_main())
                .subscribe(new MyObserver<WeatherInfo>() {
                    @Override
                    public void _onNext(WeatherInfo weatherInfo) {
                        LogHelper.d("lxb ->" +weatherInfo.toString());
                        ToastUtil.showLongToast(weatherInfo.toString());
                    }

                    @Override
                    public void _onError(String e) {
                        LogHelper.e("lxb ->"+e);
                    }
                });

    }
}
