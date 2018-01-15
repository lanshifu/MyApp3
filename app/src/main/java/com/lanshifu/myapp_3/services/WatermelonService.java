package com.lanshifu.myapp_3.services;

import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.lanshifu.baselibrary.baserxjava.RxManager;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.utils.ToastUtil;
import com.lanshifu.myapp_3.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

/**
 * Created by lanxiaobin on 2018/1/11.
 */

public class WatermelonService extends BaseAccessibilityService {

    RxManager mRxManager = new RxManager();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        String pkgName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        LogHelper.d("lxb ->pkgName " +pkgName);
        LogHelper.d("lxb ->className " +className);



    }


    @Override
    public void onInterrupt() {
        LogHelper.d("onInterrupt");
        ToastUtil.showShortToast("去广告功能已关闭");
        onDestory();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogHelper.d("onUnbind");
        ToastUtil.showShortToast("去广告功能已关闭");
        onDestory();
        return super.onUnbind(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogHelper.d("onServiceConnected");
        bind();


    }

    private void showFunctionList() {
        final View view = View.inflate(this, R.layout.layout_function_list,null);
        ImageView iv_array = view.findViewById(R.id.iv_array);
        iv_array.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //效果图1
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(Screen.width,0.5f)
                .setHeight(Screen.width,0.5f)
                .setX(Screen.width,0.8f)
                .setY(Screen.height,0.3f)    //屏幕高度的 30%
                .setMoveType(MoveType.slide)
                .setMoveStyle(500,new BounceInterpolator())
                .setDesktopShow(true)
                .setTag("functionlist")
                .build();

    }

    private void bind() {
        ToastUtil.showShortToast("辅助功能已启动,赶紧体验吧");
        mRxManager = new RxManager();
//        showFunctionList();
    }

    private void onDestory(){
        mRxManager.clear();
        FloatWindow.destroy("functionlist");
    }
}
