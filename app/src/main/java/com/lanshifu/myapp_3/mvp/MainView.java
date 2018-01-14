package com.lanshifu.myapp_3.mvp;

import com.lanshifu.baselibrary.basemvp.BaseView;

/**
 * Created by lanshifu on 2018/1/14.
 */

public interface MainView extends BaseView {

    void hasRootPermission();

    void heroResult(String result,long time);

    void heroError(String text);
}
