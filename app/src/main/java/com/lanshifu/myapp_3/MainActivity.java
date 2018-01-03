package com.lanshifu.myapp_3;

import android.view.MenuItem;

import com.lanshifu.baselibrary.base.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setTitleText("主页");
        hideBackIcon();
    }

    @Override
    protected int getTBMenusId() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            showShortToast("点击了关于");
            hideBackIcon();
            return true;
        } else if (item.getItemId() == R.id.action_setting) {
            showShortToast("点击了设置");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
