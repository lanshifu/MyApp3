package com.lanshifu.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.lanshifu.baselibrary.BaseApplication;
import com.lanshifu.baselibrary.R;
import com.lanshifu.baselibrary.widget.TopToast;

/**
 * Created by lanxiaobin on 2017/9/7.
 */

public class ToastUtil {

    private static Toast mToast;

    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }


    public static void showShortToast(int resId) {
        showShortToast(mContext.getResources().getText(resId));
    }

    public static void showShortToast(CharSequence hint) {
        showToast(mContext, hint, Toast.LENGTH_SHORT);

    }

    public static void showLongToast(CharSequence hint) {
        showToast(mContext, hint, Toast.LENGTH_LONG);

    }

    private static void showToast(Context context, CharSequence hint, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, hint, duration);
        } else {
            mToast.setText(hint);
            mToast.setDuration(duration);
        }
        mToast.show();
    }


    public static void showSuccessToast(Activity activity, int content) {
        showSuccessToast(activity, "", activity.getString(content));
    }

    public static void showSuccessToast(Activity activity, int title, int content) {
        showSuccessToast(activity, activity.getString(title), activity.getString(content));
    }

    public static void showSuccessToast(Activity activity, String title, String content) {
        TopToast.with(activity)
                .setTitle(title, R.color.black)
                .setMessage(content,R.color.black)
                .setBackgroundColor(R.color.white)
                .setIcon(R.drawable.icon_success)
                .setDuration(2000)
                .show();
    }


    public static void showErrorToast(Activity activity, int content) {
        showErrorToast(activity, "", activity.getString(content));
    }

    public static void showErrorToast(Activity activity, String content) {
        showErrorToast(activity, "", content);
    }

    public static void showErrorToast(Activity activity, int title, int content) {
        showErrorToast(activity, activity.getString(title), activity.getString(content));
    }

    public static void showErrorToast(Activity activity, String title, String content) {
        TopToast.with(activity)
                .setTitle(title, R.color.black)
                .setMessage(content, TextUtils.isEmpty(title) ? R.color.black : R.color.gray)
                .setIcon(R.drawable.icon_warn)
                .setBackgroundColor(R.color.white)
                .setDuration(3000)
                .show();

    }
}
