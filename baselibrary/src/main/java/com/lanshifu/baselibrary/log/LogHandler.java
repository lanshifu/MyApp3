package com.lanshifu.baselibrary.log;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import com.lanshifu.baselibrary.BaseApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class LogHandler extends Thread {
    private boolean running = true;

    private static LinkedBlockingQueue<LogObj> queue = null;

    private static File file = null;

    private FileWriter filerWriter = null;

    private BufferedWriter bufWriter = null;

    private static Context mContext;

    public LogHandler(Context context) {
        init(context);
    }

    private void init(Context context) {
        queue = new LinkedBlockingQueue<LogObj>(2000);
        mContext = context;
    }

    public boolean getRunning() {
        return running;
    }

    public void setRunning(boolean runFlag) {
        this.running = runFlag;
    }

    public synchronized static boolean addQueue(LogObj obj) {
        boolean flag = false;
        try {
            flag = queue.offer(obj);

            if (!flag) {
                // Log.i("add queue fail",
                // "add queue fail, may be the message queue is full");
            } else {
                // Log.i("add queue success", "current queue size = " +
                // queue.size());
            }
        } catch (Exception e) {
            Log.i("saveInfo exception", "saveInfo exception");
        }
        return flag;
    }

    protected synchronized LogObj popup() throws InterruptedException {
        return queue.take();
    }

    protected synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    protected synchronized int getSize() {
        return queue.size();
    }

    public void run() {
        while (this.getRunning()) {
            try {
                if (this.isEmpty()) {
                    if (this.bufWriter != null) {
                        // this.bufWriter.flush();
                        this.bufWriter.close();
                        this.bufWriter = null;
                    }
                    if (this.filerWriter != null) {
                        // this.filerWriter.flush();
                        this.filerWriter.close();
                        this.filerWriter = null;
                    }
                }

                try {
                    LogObj obj = this.popup();
                    if (obj != null) {
                        File filePath = new File(LogHelper.MYLOG_PATH_SDCARD_DIR);
                        if (!filePath.exists()) {
                            Log.d("excomm_log", LogHelper.MYLOG_PATH_SDCARD_DIR
                                    + " is not exists");
                            if (!filePath.mkdirs()) {
                                Log.d("excomm_log", LogHelper.MYLOG_PATH_SDCARD_DIR
                                        + " mkdirs failed");
                                return;
                            }
                        }
                        Date nowtime = new Date();
                        String needWriteFiel = LogHelper.logfile.format(nowtime);
                        String needWriteMessage = LogHelper.myLogSdf.format(nowtime) + " "
                                + obj.getTag() + " " + obj.getText();
                        if (file == null || file.getAbsolutePath().indexOf(needWriteFiel) < 0) {
                            file = new File(LogHelper.MYLOG_PATH_SDCARD_DIR, obj.getFilename()
                                    + "_" + needWriteFiel + ".log");
                            PackageManager packageManager = BaseApplication.getContext()
                                    .getPackageManager();
                            PackageInfo info = packageManager.getPackageInfo(BaseApplication
                                    .getContext().getPackageName(), 0);
                            needWriteMessage = "excomm_log  " + info.versionName + "\r\n"
                                    + needWriteMessage;
                        }
                        file = LogHelper.backupLogFile(file, obj.getFilename());

                        if (this.filerWriter == null) {
                            this.filerWriter = new FileWriter(file, true);
                        }
                        if (this.bufWriter == null) {
                            this.bufWriter = new BufferedWriter(filerWriter);
                        }

                        if (this.bufWriter != null) {
                            LogHelper.writeLogtoFile(needWriteMessage, this.bufWriter);
                        }
                        continue;
                    }
                } catch (Exception ex) {
                    Log.e("excomm_log", "write log exception:" + ex.getMessage(), ex);
                }
            } catch (Exception e2) {
                Log.e("excomm_log", "log handler exception:" + e2.getMessage(), e2);

            }
        }

    }
}
