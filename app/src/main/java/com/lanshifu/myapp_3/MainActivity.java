package com.lanshifu.myapp_3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;

import com.didi.virtualapk.PluginManager;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.myapp_3.network.MyObserver;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;


public class MainActivity extends BaseActivity {

    private static final String PLUGIN_PACKAGE = "com.lanshifu.imageplugin";
    private static final String PLUGIN_MAIN = "com.lanshifu.imageplugin.MainActivity_plugin";
    private static final String PLUGIN_NAME = "imageplugin.apk";
    private static final int CODE_IMAGE = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setTitleText("主页");
        hideBackIcon();

        loadPlugin(this);

        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void _onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void _onError(String e) {

                    }
                });
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
        } else if (item.getItemId() == R.id.plugin_image) {
            showShortToast("点击了插件");
            openPluginApk();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void openPluginApk() {
        if (PluginManager.getInstance(this).getLoadedPlugin(PLUGIN_PACKAGE) == null) {
            showErrorToast("plugin ["+ PLUGIN_PACKAGE +"] not loaded");
            return;
        }

        // test Activity and Service
        Intent intent = new Intent();
        intent.putExtra("data","主页过来的，啦啦啦");
        intent.setClassName(PLUGIN_PACKAGE, PLUGIN_MAIN);
        startActivityForResult(intent,CODE_IMAGE);
    }


    private void loadPlugin(Context base) {
        PluginManager pluginManager = PluginManager.getInstance(base);
//        File apk = new File(StorageUtil.getPluginFolder() + "plugin.apk");
        File apk = new File(Environment.getExternalStorageDirectory(), PLUGIN_NAME);
        if (apk.exists()) {
            LogHelper.d("loadPlugin");
            try {
                pluginManager.loadPlugin(apk);
                LogHelper.d("loadPlugin 插件plugin success");
            } catch (Exception e) {
                showErrorToast("plugin插件加载失败" + e.getMessage());
                LogHelper.d("插件load 失败 " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showErrorToast("插件apk不存在");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        if (requestCode == CODE_IMAGE){
            String extra = data.getStringExtra("data");
            if (!TextUtils.isEmpty(extra)){
                showShortToast("返回信息 "+ extra);
            }
        }
    }
}
