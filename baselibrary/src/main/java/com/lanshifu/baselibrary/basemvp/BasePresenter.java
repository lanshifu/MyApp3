package com.lanshifu.baselibrary.basemvp;

import android.content.Context;

import com.lanshifu.baselibrary.baserxjava.RxManager;

/**
 * Created by 蓝师傅 on 2016/12/28.
 */

public class BasePresenter<V> {

    public Context mContext;
    public V mView;
    public RxManager mRxManager = new RxManager();


    public void setView(V mView){
        this.mView = mView;
        onCreate();
    }


    public void onCreate() {
        //可以做一下初始化操作，例如rxbus添加观察者

    }

    public void onDestory(){
        //做一些释放资源，回收操作,在activity/fragment ondestory中调用
        mRxManager.clear();
    }



}
