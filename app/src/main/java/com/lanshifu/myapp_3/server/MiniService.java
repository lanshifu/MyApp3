package com.lanshifu.myapp_3.server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.lanshifu.myapp_3.MainApplication;
import com.lanshifu.myapp_3.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by lanshifu on 2018/1/17.
 */

public class MiniService extends Service {

    private static final int PORT = 12345;
    private AsyncHttpServer server = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void initServer() {
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

        server.get("/jquery-1.7.2.min.js", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    String fullPath = request.getPath();
                    fullPath = fullPath.replace("%20", " ");
                    String resourceName = fullPath;
                    if (resourceName.startsWith("/")) {
                        resourceName = resourceName.substring(1);
                    }
                    if (resourceName.indexOf("?") > 0) {
                        resourceName = resourceName.substring(0, resourceName.indexOf("?"));
                    }
                    response.setContentType("application/javascript");
                    BufferedInputStream bInputStream = new BufferedInputStream(getAssets().open(resourceName));
                    response.sendStream(bInputStream, bInputStream.available());
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(404).end();
                    return;
                }
            }
        });

        server.get("/files/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String path = request.getPath().replace("/files/", "");
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        response.sendStream(fis, fis.available());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                response.code(404).send("Not found!");
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
                        if (file.exists() && file.isFile() && (file.getName().endsWith(".mp4")
                                ||file.getName().endsWith(".png"))) {
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

    private void  openFlowWindow() {
        FloatWindow.destroy("miniserver");
        View view = View.inflate(this , R.layout.layout_window_mini_server,null);
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindow.destroy("miniserver");
                stopService(new Intent(MiniService.this,MiniService.class));
            }

        });
        FloatWindow
                .with(MainApplication.getContext())
                .setView(view)
                .setX(100)                       //100px
                .setY(Screen.height, 0.3f)        //屏幕高度的 30%
                .setDesktopShow(true)
                .setMoveType(MoveType.slide)
                .setTag("miniserver")
                .build();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initServer();
        openFlowWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMiniServer();
    }

    private void stopMiniServer() {
        if (server != null) {
            server.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }

    }
}
