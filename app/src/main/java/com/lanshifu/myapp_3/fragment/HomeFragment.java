package com.lanshifu.myapp_3.fragment;

import android.view.View;
import android.widget.ImageView;

import com.lanshifu.baselibrary.base.BaseFragment;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.activity.WebServerActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by lanxiaobin on 2018/1/3.
 */

public class HomeFragment extends BaseFragment {
    private static final int MY_PERMISSIONS_REQUEST_SD = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

    }


    @OnClick({R.id.bt_server, R.id.bt_box})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_server:
                startActivity(WebServerActivity.class);

                break;
            case R.id.bt_box:
                break;
        }
    }
}
