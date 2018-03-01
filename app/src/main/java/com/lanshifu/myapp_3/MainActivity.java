package com.lanshifu.myapp_3;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.didi.virtualapk.PluginManager;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.myapp_3.mvp.presenter.MainPresenter;
import com.lanshifu.myapp_3.mvp.view.MainView;
import com.lanshifu.myapp_3.network.MyObserver;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.Screen;

import java.lang.reflect.Method;


    public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    private static final String PLUGIN_PACKAGE = "com.lanshifu.imageplugin";
    private static final String PLUGIN_MAIN = "com.lanshifu.imageplugin.MainActivity_plugin";

    private static final int CODE_IMAGE = 10;
    private TextView mTv_result;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mPresenter.setView(this);
    }

    @Override
    protected void initView() {
        setTitleText("主页");
        hideBackIcon();
        mPresenter.loadPlugin(this);
        mPresenter.checkRootPermission();
//        mPresenter.getWeather();
        checkPermission();
        showShortToast(getCount() +"");

        Glide.get(this).setMemoryCategory(MemoryCategory.LOW);

    }

    private int getCount(){
        return 1;
    }

    private void checkPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void _onNext(Boolean aBoolean) {
                        showShortToast("权限"+aBoolean);

                    }

                    @Override
                    public void _onError(String e) {
                        showShortToast("权限失败"+e);
                    }
                });
    }

    @Override
    protected int getTBMenusId() {
        return R.menu.menu_main;
    }

    boolean mIsEdit = true;

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            showShortToast("点击了关于");
            hideBackIcon();
            return true;
        } else if (item.getItemId() == R.id.action_setting) {
            mIsEdit = !mIsEdit;
            showShortToast("点击了设置");
            mIsEdit = !mIsEdit;
            invalidateOptionsMenu();
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
            showErrorToast("plugin [" + PLUGIN_PACKAGE + "] not loaded");
            return;
        }
        // test Activity and Service
        Intent intent = new Intent();
        intent.putExtra("data", "主页过来的，啦啦啦");
        intent.setClassName(PLUGIN_PACKAGE, PLUGIN_MAIN);
        startActivityForResult(intent, CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CODE_IMAGE) {
            String extra = data.getStringExtra("data");
            if (!TextUtils.isEmpty(extra)) {
                showShortToast("返回信息 " + extra);
            }
        }
    }


    // 让菜单同时显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item_setting = menu.findItem(R.id.action_setting);
        if (item_setting != null) {
            View actionView = item_setting.getActionView();
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showShortToast("点击了设置");
                    mIsEdit = !mIsEdit;
                    invalidateOptionsMenu();

                }
            });
            TextView tv_name = actionView.findViewById(R.id.tv_name);
            ImageView iv_icon = actionView.findViewById(R.id.iv_icon);
            tv_name.setText(mIsEdit ? "设置" : "完成");
            iv_icon.setImageResource(mIsEdit ? R.drawable.icon_success : R.drawable.ic_error);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    private void openFlow() {
        View view = View.inflate(this, R.layout.layout_flowview, null);
        mTv_result = view.findViewById(R.id.tv_result);
        view.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_result.setText("点击了");
            }
        });

        FloatWindow
                .with(MainApplication.getContext())
                .setView(view)
                .setX(100)                       //100px
                .setY(Screen.height, 0.3f)        //屏幕高度的 30%
                .setDesktopShow(true)
                .build();
    }


    @Override
    public void showProgressDialog(String text) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void hasRootPermission() {
        openFlow();
    }

    @Override
    public void heroResult(String result, long time) {
        mTv_result.setText(result);
        mTv_result.append("\n耗时：" + time);
    }

    @Override
    public void heroError(String text) {
        mTv_result.setText("出错了：" + text);
    }
}
