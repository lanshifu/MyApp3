package com.lanshifu.baselibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageUtil {
	
	public static Context mContext;
	
	public static void init(Context context){
		mContext = context;
	}

	public static String getAppRootDir() {
		String rootPath = "";
		if (checkSDCard()) {
			//外部存储可用
			rootPath = mContext.getExternalFilesDir(null).getPath() +"/";
		}else {
			//外部存储不可用
			rootPath = mContext.getCacheDir().getPath() +"/";
		}
		return rootPath;
	}



	public static String getUpdateDir() {
		String path = getAppRootDir() + "update/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	private static String getTakePhotoDir() {
		String path = getAppRootDir() + "camera/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static File getCrashLogDirFile() {
		String path = getAppRootDir() + "crash/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static String getLogPath(String name) {
		return getLogFolder() + name + ".txt";
	}

	public static String getLogFolder() {
		String path = getAppRootDir() + "dev/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getPluginFolder() {
		String path = getAppRootDir() + "plugin/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getTakePhotoPath() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		String name = dateFormat.format(date) + ".jpg";

		return getTakePhotoDir() + name;
	}

	public static String getTakePhotoNamePath(String number, String time) {
		String dir = Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera/";
		return dir + number + "-" + time + ".jpg";
	}

	public static boolean checkSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable();
	}

}
