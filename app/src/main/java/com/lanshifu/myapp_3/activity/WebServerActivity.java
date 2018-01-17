package com.lanshifu.myapp_3.activity;

import android.os.Environment;
import android.widget.TextView;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.myapp_3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by lanxiaobin on 2018/1/17.
 */

public class WebServerActivity extends BaseActivity {

    @Bind(R.id.tv_ip)
    TextView mTvIp;

    private static final int PORT = 12345;
    private AsyncHttpServer server = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_server;
    }

    @Override
    protected void initView() {

        mTvIp.setText(getHostIP());
    }


    @OnClick(R.id.btn_start_server)
    public void onViewClicked() {

        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    response.send(getIndexContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(500).end();
                }
            }
        });

        server.get("/files", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONArray array = new JSONArray();
                File dir = new File(Environment.getExternalStorageDirectory().getPath());
                String[] fileNames = dir.list();
                if (fileNames != null) {
                    for (String fileName : fileNames) {
                        File file = new File(dir, fileName);
                        if (file.exists() && file.isFile() && file.getName().endsWith(".mp4")) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", fileName);
                                jsonObject.put("path", file.getAbsolutePath());
                                array.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                response.send(array.toString());
            }
        });

        server.listen(mAsyncServer, PORT);
    }

    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open("index.html"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
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
