package com.lanshifu.myapp_3.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.broadcast.DirectBroadcastReceiver;
import com.lanshifu.myapp_3.callback.DirectActionListener;
import com.lanshifu.myapp_3.model.FileTransfer;
import com.lanshifu.myapp_3.services.WifiServerService;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lanxiaobin on 2018/2/27.
 */

public class ReceiveFileActivity extends BaseActivity implements DirectActionListener {
    @Bind(R.id.tv_log)
    TextView mTvLog;


    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private BroadcastReceiver broadcastReceiver;

    private WifiServerService wifiServerService;

    private ProgressDialog progressDialog;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiServerService.MyBinder binder = (WifiServerService.MyBinder) service;
            wifiServerService = binder.getService();
            wifiServerService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            wifiServerService = null;
            bindService();
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_receiver_file;
    }

    @Override
    protected void initView() {

        setTitleText("接收文件");
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), this);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, this);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        bindService();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在接收文件");
        progressDialog.setMax(100);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGroup();
    }


    private WifiServerService.OnProgressChangListener progressChangListener = new WifiServerService.OnProgressChangListener() {
        @Override
        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("文件名： " + new File(fileTransfer.getFilePath()).getName());
                    progressDialog.setProgress(progress);
                    progressDialog.show();
                }
            });
        }

        @Override
        public void onTransferFinished(final File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    if (file != null && file.exists()) {
                        openFile(file.getPath());
                    }
                }
            });
        }
    };

    private void bindService() {
        Intent intent = new Intent(ReceiveFileActivity.this, WifiServerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void openFile(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        try {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext.substring(1));
            mime = TextUtils.isEmpty(mime) ? "" : mime;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), mime);
            startActivity(intent);
        } catch (Exception e) {
            showShortToast("文件打开异常：" + e.getMessage());
        }
    }


    private void createGroup() {
        startProgressDialog("正在创建群组");
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                stopProgressDialog();
                showShortToast("群组创建成功");
            }

            @Override
            public void onFailure(int reason) {
                stopProgressDialog();
                showShortToast("创建群组失败 " + reason);
            }
        });
    }


    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                showShortToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                showShortToast("onFailure");
            }
        });
    }

    @Override
    public void wifiP2pEnabled(boolean enabled) {
        showLog("lxb ->wifiP2pEnabled " + enabled);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        showLog("lxb ->wifiP2pInfo  " + wifiP2pInfo.toString());
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            if (wifiServerService != null) {
                startService(new Intent(this, WifiServerService.class));
            }
        }
    }

    @Override
    public void onDisconnection() {
        showLog("lxb ->onDisconnection   ");
    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
        showLog("lxb ->onSelfDeviceAvailable   " + wifiP2pDevice.toString());
    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        showLog("lxb ->onPeersAvailable   " + wifiP2pDeviceList.size());
    }

    @Override
    public void onChannelDisconnected() {
        showLog("lxb ->onChannelDisconnected   ");
    }


    private void showLog(String text) {
        mTvLog.append(text + "\r");
        LogHelper.d(text);
    }

    @OnClick(R.id.btn_createGroup)
    public void onViewClicked() {
        createGroup();
    }
}
