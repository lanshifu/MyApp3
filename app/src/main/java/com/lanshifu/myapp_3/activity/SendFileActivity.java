package com.lanshifu.myapp_3.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.utils.FileUtil;
import com.lanshifu.baselibrary.widget.LoadingDialog;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.broadcast.DirectBroadcastReceiver;
import com.lanshifu.myapp_3.callback.DirectActionListener;
import com.lanshifu.myapp_3.model.FileTransfer;
import com.lanshifu.myapp_3.task.WifiClientTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lanxiaobin on 2018/2/27.
 */

public class SendFileActivity extends BaseActivity implements DirectActionListener {

    public static final String TAG = "SendFileActivity";

    private WifiP2pManager mWifiP2pManager;

    private boolean mWifiP2pEnabled = false;

    private WifiP2pManager.Channel mChannel;

    private TextView tv_myDeviceName;

    private TextView tv_myDeviceAddress;

    private TextView tv_myDeviceStatus;

    private TextView tv_status;

    private TextView tv_fileList;

    private List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();


    private Button btn_disconnect;

    private Button btn_chooseFile;


    private BroadcastReceiver broadcastReceiver;

    private WifiP2pDevice mWifiP2pDevice;
    private BaseQuickAdapter<WifiP2pDevice, BaseViewHolder> mDeviceAdapter;

    private WifiP2pInfo wifiP2pInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_file;
    }

    @Override
    protected void initView() {

        setTitleText("发送文件");
        tv_myDeviceName = (TextView) findViewById(R.id.tv_myDeviceName);
        tv_myDeviceAddress = (TextView) findViewById(R.id.tv_myDeviceAddress);
        tv_myDeviceStatus = (TextView) findViewById(R.id.tv_myDeviceStatus);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_fileList = (TextView) findViewById(R.id.tv_fileList);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_chooseFile = (Button) findViewById(R.id.btn_chooseFile);
        btn_disconnect.setOnClickListener(clickListener);
        btn_chooseFile.setOnClickListener(clickListener);
        RecyclerView rv_deviceList = (RecyclerView) findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();

        mDeviceAdapter = new BaseQuickAdapter<WifiP2pDevice, BaseViewHolder>(
                R.layout.item_device, new ArrayList<WifiP2pDevice>()) {
                    @Override
                    protected void convert(BaseViewHolder helper, WifiP2pDevice item) {
                        helper.setText(R.id.tv_deviceName,item.deviceName);
                        helper.setText(R.id.tv_deviceAddress,item.deviceAddress);
                        helper.setText(R.id.tv_deviceDetails,getDeviceStatus(item.status));

                    }
                };

        mDeviceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mWifiP2pDevice = mDeviceAdapter.getData().get(position);
                connect();
            }
        });

        rv_deviceList.setAdapter(mDeviceAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));


        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), this);
        broadcastReceiver = new DirectBroadcastReceiver(mWifiP2pManager, mChannel, this);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDirectEnable: {
                if (mWifiP2pManager != null && mChannel != null) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                } else {
                    showShortToast("当前设备不支持Wifi Direct");
                }
                return true;
            }
            case R.id.menuDirectDiscover: {
                if (!mWifiP2pEnabled) {
                    showShortToast("需要先打开Wifi");
                    return true;
                }
                showShortToast("正在搜索附近设备");
                wifiP2pDeviceList.clear();
                mDeviceAdapter.replaceData(wifiP2pDeviceList);
                //搜寻附近带有 Wi-Fi P2P 的设备
                mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        showShortToast("Success");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        showShortToast("Failure " +reasonCode);
                    }
                });
                return true;
            }
            default:
                return true;
        }
    }


    private void connect() {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            showShortToast("正在连接 " + mWifiP2pDevice.deviceName);
            mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    showShortToast("连接成功");
                    Log.e(TAG, "connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showLongToast("连接失败 " + reason);
                }
            });
        }
    }

    private void disconnect() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "disconnect onSuccess");
                tv_status.setText(null);
                btn_disconnect.setEnabled(false);
                btn_chooseFile.setEnabled(false);
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_disconnect: {
                    disconnect();
                    break;
                }
                case R.id.btn_chooseFile: {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                    break;
                }
            }
        }
    };


    @Override
    public void wifiP2pEnabled(boolean enabled) {
        mWifiP2pEnabled = enabled;
        LogHelper.d("lxb ->wifiP2pEnabled " +enabled);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        wifiP2pDeviceList.clear();
        mDeviceAdapter.replaceData(wifiP2pDeviceList);
        btn_disconnect.setEnabled(true);
        btn_chooseFile.setEnabled(true);
        LogHelper.d("lxb ->wifiP2pInfo  " +wifiP2pInfo.toString());
        StringBuilder stringBuilder = new StringBuilder();
        if (mWifiP2pDevice != null) {
            stringBuilder.append("连接的设备名：");
            stringBuilder.append(mWifiP2pDevice.deviceName);
            stringBuilder.append("\n");
            stringBuilder.append("连接的设备的地址：");
            stringBuilder.append(mWifiP2pDevice.deviceAddress);
        }
        stringBuilder.append("\n");
        stringBuilder.append("是否群主：");
        stringBuilder.append(wifiP2pInfo.isGroupOwner ? "是群主" : "非群主");
        stringBuilder.append("\n");
        stringBuilder.append("群主IP地址：");
        stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
        tv_status.setText(stringBuilder);
        if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
            this.wifiP2pInfo = wifiP2pInfo;
        }

    }

    @Override
    public void onDisconnection() {
        LogHelper.d("lxb ->onDisconnection");
        btn_disconnect.setEnabled(false);
        btn_chooseFile.setEnabled(false);
        showShortToast("已断开连接");
        wifiP2pDeviceList.clear();
        mDeviceAdapter.replaceData(wifiP2pDeviceList);
        tv_status.setText(null);
        this.wifiP2pInfo = null;
    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
        LogHelper.d("lxb ->wifiP2pDevice " +wifiP2pDevice.toString());
        tv_myDeviceName.setText(wifiP2pDevice.deviceName);
        tv_myDeviceAddress.setText(wifiP2pDevice.deviceAddress);
        tv_myDeviceStatus.setText(getDeviceStatus(wifiP2pDevice.status));
    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        LogHelper.d("lxb ->onPeersAvailable :" + wifiP2pDeviceList.size());
        this.wifiP2pDeviceList.clear();
        this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
        mDeviceAdapter.replaceData(wifiP2pDeviceList);
    }

    @Override
    public void onChannelDisconnected() {
        LogHelper.d("lxb ->onChannelDisconnected");
    }

    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "可用的";
            case WifiP2pDevice.INVITED:
                return "邀请中";
            case WifiP2pDevice.CONNECTED:
                return "已连接";
            case WifiP2pDevice.FAILED:
                return "失败的";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用的";
            default:
                return "未知";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = FileUtil.getPath(this, uri);
//                    String path = getPath(this, uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists() && wifiP2pInfo != null) {
                            FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
                            Log.e(TAG, "待发送的文件：" + fileTransfer);
                            new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                        }
                    }else {
                        showShortToast("path == null");
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
