package com.lanshifu.myapp_3.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lanshifu.baselibrary.base.BaseFragment;
import com.lanshifu.myapp_3.MainApplication;
import com.lanshifu.myapp_3.R;
import com.yhao.floatwindow.FloatWindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lanxiaobin on 2018/1/3.
 */

public class HomeFragment extends BaseFragment {
    private static final int MY_PERMISSIONS_REQUEST_SD = 10;
    @Bind(R.id.iv_screen)
    ImageView mIvScreen;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        checkSdPermission();

    }


    private void checkSdPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_SD);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }


    @OnClick(R.id.bt_flow)
    public void onViewClicked() {
        FloatWindow.destroy();
    }
}
