package com.lanshifu.myapp_3.fragment;

import android.view.View;

import com.lanshifu.baselibrary.base.BaseFragment;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.activity.ArticleListActivity;
import com.lanshifu.myapp_3.activity.WebServerActivity;
import com.lanshifu.myapp_3.activity.WifiP2PActivity;

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


    @OnClick({R.id.bt_server, R.id.bt_book, R.id.bt_wifip2p})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_server:
                startActivity(WebServerActivity.class);

                break;
            case R.id.bt_book:
                startActivity(ArticleListActivity.class);
                break;

            case R.id.bt_wifip2p:
                startActivity(WifiP2PActivity.class);
                break;
        }
    }
}
