package com.lanshifu.myapp_3.activity;

import android.view.View;

import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.myapp_3.R;

import butterknife.OnClick;

/**
 * Created by lanxiaobin on 2018/2/27.
 */

public class WifiP2PActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifip2p;
    }

    @Override
    protected void initView() {
        setTitleText("wifi点对点传输");

    }


    @OnClick({R.id.btn_send_file, R.id.btn_receive_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_file:
                startActivity(SendFileActivity.class);

                break;
            case R.id.btn_receive_file:
                startActivity(ReceiveFileActivity.class);
                break;
        }
    }
}
