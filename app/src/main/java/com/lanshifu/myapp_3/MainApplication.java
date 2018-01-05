package com.lanshifu.myapp_3;

import android.content.Context;
import android.util.Log;

import com.didi.virtualapk.PluginManager;
import com.lanshifu.baselibrary.BaseApplication;
import com.lanshifu.baselibrary.log.LogHelper;

/**
 * Created by lanxiaobin on 2018/1/4.
 */

public class MainApplication extends BaseApplication{

    private static final String TAG = "MainApplication-lxb";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        long start = System.currentTimeMillis();
        PluginManager.getInstance(base).init();
        Log.d(TAG, "attachBaseContext: use time:" + (System.currentTimeMillis() - start));
    }
}
