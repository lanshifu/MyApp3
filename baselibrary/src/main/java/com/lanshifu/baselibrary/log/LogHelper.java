
package com.lanshifu.baselibrary.log;

import android.content.Context;
import android.util.Log;

import com.lanshifu.baselibrary.utils.StorageUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class LogHelper implements ILogger {
    public static boolean mIsDebugMode = true;
    private static Boolean MYLOG_WRITE_TO_FILE = true;

    private static String TAG = "lxb";
    private static int logLevel = Log.VERBOSE;
    private static String MYLOGFILEName = "lxb";

    private static final String CLASS_METHOD_LINE_FORMAT = "[%d] [%s.%s:%d] %s";

    public static String MYLOG_PATH_SDCARD_DIR = StorageUtil.getLogFolder() + "logs";

    public static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat myLogSdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private static SimpleDateFormat bakLogSdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    private static int logFileBackupNumber = 1;
    private static int logFileBackupSize = 50;


    public static void trace(String str) {
        log(str, Log.DEBUG);
    }

    public synchronized static void writeLogtoFile(String needWriteMessage,
            BufferedWriter bufWriter) {
        try {
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
        } catch (IOException e) {
            log("", Log.ERROR, e);
        }
    }

    public static File backupLogFile(File file, String fileName) {
        try {
            if (!file.exists()) {
                return file;
            }
            int maxSize = logFileBackupSize * 1024 * 1024;

            if (file.length() >= maxSize) {
                Date now = new Date();
                boolean sign = file.renameTo(new File(MYLOG_PATH_SDCARD_DIR, fileName + "_"
                        + bakLogSdf.format(now) + ".bak"));
                if (sign) {
                    file = new File(MYLOG_PATH_SDCARD_DIR, fileName + "_" + logfile.format(now)
                            + ".log");
                    deleteBackupFile(MYLOG_PATH_SDCARD_DIR, fileName + "_" + logfile.format(now));
                }
            }
        } catch (Exception e) {
            LogHelper.e("backupLogFile method exception");
        }
        return file;
    }

    public static void deleteBackupFile(String filepath, String deleteTag) {
        File file = new File(filepath);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            List<Integer> timeList = new ArrayList<Integer>();
            for (int i = 0; i < allFiles.length; i++) {
                File dataFile = allFiles[i];
                if (dataFile.isFile() && dataFile.getName().startsWith(deleteTag)
                        && dataFile.getName().endsWith(".bak")) {
                    String time = dataFile.getName().substring(
                            dataFile.getName().indexOf(deleteTag) + deleteTag.length() + 1,
                            dataFile.getName().indexOf(".bak"));
                    timeList.add(Integer.parseInt(time));
                }
            }
            if (timeList.size() > logFileBackupNumber) {
                Collections.sort(timeList);

                for (int i = 0; i < timeList.size() - logFileBackupNumber; i++) {
                    File delFile = new File(filepath + "/" + deleteTag + "-" + timeList.get(i)
                            + ".bak");
                    deleteDir(delFile);
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static void log(String str, int level) {
        log(str, level, null);
    }

    private static void log(String str, int level, Throwable throwable,
            StackTraceElement traceElement) {
        int threadId = Thread.currentThread().hashCode();
        String className = traceElement.getClassName();
        String simpleClzName = className.substring(className.lastIndexOf(".") + 1,
                className.length());
        String logText = String.format(CLASS_METHOD_LINE_FORMAT, threadId, simpleClzName,
                traceElement.getMethodName(), traceElement.getLineNumber(), str);
        // ERROR, WARN, INFO, LOAD_LOCAL_DEBUG, VERBOSE
        String logTag = "";
        if (level == Log.VERBOSE) {
            Log.v(TAG, logText);
            logTag = "[V]";
        } else if (level == Log.DEBUG) {
            Log.d(TAG, logText);
            logTag = "[D]";
        } else if (level == Log.INFO) {
            Log.i(TAG, logText);
            logTag = "[I]";
        } else if (level == Log.WARN) {
            Log.w(TAG, logText);
            logTag = "[V]";
        } else if (level == Log.ERROR) {
            if (throwable != null) {
                Log.e(TAG, logText, throwable);
            } else {
                Log.e(TAG, logText);
            }
            logTag = "[E]";
        }
        try {
            if (MYLOG_WRITE_TO_FILE
                    && android.os.Environment.getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED)) {
                if (throwable != null) {
                    str += "\n" + Log.getStackTraceString(throwable);
                    logText = String.format(CLASS_METHOD_LINE_FORMAT, threadId, simpleClzName,
                            traceElement.getMethodName(), traceElement.getLineNumber(), str);
                }
                LogObj obj = new LogObj();
                obj.setFilename(MYLOGFILEName);
                obj.setTag(logTag);
                obj.setText(logText);
                LogHandler.addQueue(obj);
            }
        } catch (Exception e) {
            LogHelper.e("throw exception + " + e.getMessage(), e);
        }
    }

    /**
     * Log.
     *
     * @param str the str
     * @param level the level
     * @param throwable the throwable
     */
    private static void log(String str, int level, Throwable throwable) {
        if (mIsDebugMode) {
            if (logLevel <= level) {
                // Get the method name from the stackTrace.
                StackTraceElement[] array = Thread.currentThread().getStackTrace();
                StackTraceElement traceElement = (array != null && array.length > 5 ? array[5]
                        : array[array.length - 1]);
                log(str, level, throwable, traceElement);
            }
        }
    }

    /**
     * VERBOSE.
     *
     * @param str the str
     */
    public static void v(String str) {
        log(str, Log.VERBOSE);
    }

    /**
     * LOAD_LOCAL_DEBUG.
     *
     * @param str the str
     */
    public static void d(String str) {
        log(str, Log.DEBUG);
    }

    /**
     * WARN.
     *
     * @param str the str
     */
    public static void w(String str) {
        log(str, Log.WARN);
    }

    /**
     * INFO.
     *
     * @param str the str
     */
    public static void i(String str) {
        log(str, Log.INFO);
    }

    /**
     * ERROR.
     *
     * @param str the str
     */
    public static void e(String str) {
        log(str, Log.ERROR);
    }

    /**
     * ERROR.
     *
     * @param str the str
     * @param throwable the throwable
     */
    public static void e(String str, Throwable throwable) {
        log(str, Log.ERROR, throwable);
    }

    @Override
    public void debug(String arg0) {
        d(arg0);
    }

    @Override
    public void error(String arg0, Throwable arg1) {
        e(arg0, arg1);
    }

    @Override
    public void error(String arg0) {
        e(arg0);
    }

    @Override
    public void info(String arg0) {
        i(arg0);
    }

    @Override
    public void warn(String arg0) {
        w(arg0);
    }
}
