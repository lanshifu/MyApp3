package com.lanshifu.myapp_3.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.myapp_3.MainApplication;
import com.lanshifu.myapp_3.R;
import com.lanshifu.myapp_3.server.MiniService;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.Bind;

/**
 * Created by lanxiaobin on 2018/1/17.
 */

public class WebServerActivity extends BaseActivity {

    @Bind(R.id.tv_ip)
    TextView mTvIp;

    private static final int PORT = 12345;
    private Intent mServiceIntent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_server;
    }

    @Override
    protected void initView() {

        setTitleText("mini服务器");
        mTvIp.setText(getHostIP());
        startMiniServer();
    }

    private void startMiniServer() {
        mServiceIntent = new Intent(this, MiniService.class);
        startService(mServiceIntent);
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            LogHelper.e(e.getMessage());
            return "检查wifi是否连接:" + e.getMessage();
        }
        return hostIp + ":" + PORT;

    }


}
