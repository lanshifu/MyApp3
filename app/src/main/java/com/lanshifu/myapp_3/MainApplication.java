package com.lanshifu.myapp_3;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.didi.virtualapk.PluginManager;
import com.lanshifu.baselibrary.BaseApplication;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.utils.ToastUtil;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

/**
 * Created by lanxiaobin on 2018/1/4.
 */

public class MainApplication extends BaseApplication{

    private static final String TAG = "MainApplication-lxb";

    @Override
    public void onCreate() {
        super.onCreate();


        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.ic_settings_black_24dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShortToast("设置");
            }
        });

        //效果图1
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView)
                .setWidth(Screen.width,0.2f)
                .setHeight(Screen.width,0.2f) //屏幕宽度的 20%
                .setX(Screen.width,0.8f)
                .setY(Screen.height,0.3f)    //屏幕高度的 30%
                .setMoveType(MoveType.slide)
                .setMoveStyle(500,new BounceInterpolator())
                .setDesktopShow(true)
                .build();


        ImageView imageView2 = new ImageView(getApplicationContext());
        imageView2.setImageResource(R.mipmap.ic_launcher_round);

//      效果图2
        FloatWindow
                .with(getApplicationContext())
                .setView(imageView2)
                .setWidth(Screen.width,0.2f)
                .setHeight(Screen.width,0.2f)
                .setX(Screen.width,0.7f)
                .setY(Screen.height,0.4f)
                .setTag("second")
                .setMoveType(MoveType.back)
                .setFilter(true,MainActivity.class)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        long start = System.currentTimeMillis();
        PluginManager.getInstance(base).init();
        Log.d(TAG, "attachBaseContext: use time:" + (System.currentTimeMillis() - start));
    }
}
