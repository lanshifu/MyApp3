package com.lanshifu.baselibrary.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lanshifu.baselibrary.log.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lanxiaobin on 2017/10/16.
 */

public class ImsiUtil {

    private static Integer simId_1 = 0;
    private static Integer simId_2 = 1;
    private static String imsi_1 = "";
    private static String imsi_2 = "";
    private static String imei_1 = "";
    private static String imei_2 = "";



    public boolean contentIMSI(Context context) {
        boolean flag = false;
        IMSInfo imsInfo = getIMSInfo(context);
        if (imsInfo != null) {
            if (!TextUtils.isEmpty(imsInfo.getImsi_1())) {
                flag = true;
            }
            if (!TextUtils.isEmpty(imsInfo.getImei_1())) {
                flag = true;
            }
            if (!TextUtils.isEmpty(imsInfo.getImsi_2())) {
                flag = true;
            }
            if (!TextUtils.isEmpty(imsInfo.getImei_2())) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 获取IMSInfo
     *
     * @return
     */
    public static IMSInfo getIMSInfo(Context context) {
        IMSInfo imsInfo = initQualcommDoubleSim(context);
        if (imsInfo != null) {
            return imsInfo;
        } else {
            imsInfo = initMtkDoubleSim(context);
            if (imsInfo != null) {
                return imsInfo;
            } else {
                imsInfo = initMtkSecondDoubleSim(context);
                if (imsInfo != null) {
                    return imsInfo;
                } else {
                    imsInfo = initSpreadDoubleSim(context);
                    if (imsInfo != null) {
                        return imsInfo;
                    } else {
                        imsInfo = getIMSI(context);
                        if (imsInfo != null) {
                            return imsInfo;
                        } else {
                            imsInfo = null;
                            return imsInfo;
                        }
                    }
                }
            }
        }
    }


    /**
     * MTK的芯片的判断
     *
     * @return
     * @param context
     */
    private static  IMSInfo initMtkDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);

            Method m = TelephonyManager.class.getDeclaredMethod(
                    "getSubscriberIdGemini", int.class);
            imsi_1 = (String) m.invoke(tm, simId_1);
            imsi_2 = (String) m.invoke(tm, simId_2);

            Method m1 = TelephonyManager.class.getDeclaredMethod(
                    "getDeviceIdGemini", int.class);
            imei_1 = (String) m1.invoke(tm, simId_1);
            imei_2 = (String) m1.invoke(tm, simId_2);

            imsInfo = new IMSInfo();
            imsInfo.chipName = "MTK芯片";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;
        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * MTK的芯片的判断2
     *
     * @return
     */
    private static IMSInfo initMtkSecondDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);

            Method mx = TelephonyManager.class.getMethod("getDefault",
                    int.class);
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);

            imsi_1 = tm1.getSubscriberId();
            imsi_2 = tm2.getSubscriberId();

            imei_1 = tm1.getDeviceId();
            imei_2 = tm2.getDeviceId();

            imsInfo = new IMSInfo();
            imsInfo.chipName = "MTK芯片";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;

        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * 展讯芯片的判断
     *
     * @return
     * @param context
     */
    private static IMSInfo initSpreadDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            Class<?> c = Class
                    .forName("com.android.internal.telephony.PhoneFactory");
            Method m = c.getMethod("getServiceName", String.class, int.class);
            String spreadTmService = (String) m.invoke(c,
                    Context.TELEPHONY_SERVICE, 1);
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi_1 = tm.getSubscriberId();
            imei_1 = tm.getDeviceId();
            TelephonyManager tm1 = (TelephonyManager) context
                    .getSystemService(spreadTmService);
            imsi_2 = tm1.getSubscriberId();
            imei_2 = tm1.getDeviceId();
            imsInfo = new IMSInfo();
            imsInfo.chipName = "展讯芯片";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;
        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * 高通芯片判断
     *
     * @return
     * @param context
     */
    private static IMSInfo initQualcommDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
            Object obj = context.getSystemService("phone_msim");
            Method md = cx.getMethod("getDeviceId", int.class);
            Method ms = cx.getMethod("getSubscriberId", int.class);
            imei_1 = (String) md.invoke(obj, simId_1);
            imei_2 = (String) md.invoke(obj, simId_2);
            imsi_1 = (String) ms.invoke(obj, simId_1);
            imsi_2 = (String) ms.invoke(obj, simId_2);
            int statephoneType_2 = 0;
            boolean flag = false;
            try {
                Method mx = cx.getMethod("getPreferredDataSubscription", int.class);
                Method is = cx.getMethod("isMultiSimEnabled", int.class);
                statephoneType_2 = (Integer) mx.invoke(obj);
                flag = (Boolean) is.invoke(obj);
            } catch (Exception e) {
                LogHelper.e("initQualcommDoubleSim: "+e);
            }
            imsInfo = new IMSInfo();
            imsInfo.chipName = "高通芯片-getPreferredDataSubscription:" + statephoneType_2 + ",\nflag:" + flag;
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;

        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * 系统的api
     *
     * @return
     */
    private static IMSInfo getIMSI(Context context) {
        IMSInfo imsInfo = null;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi_1 = tm.getSubscriberId();
            imei_1 = tm.getDeviceId();
        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        if (TextUtils.isEmpty(imsi_1) || imsi_1.length() < 10) {
            imsInfo = null;
            return imsInfo;
        } else {
            imsInfo = new IMSInfo();
            imsInfo.chipName = "单卡芯片";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = "没有";
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = "没有";
            return imsInfo;
        }

    }

    public static class IMSInfo {
        String chipName;
        String imsi_1;
        String imei_1;
        String imsi_2;
        String imei_2;

        public String getChipName() {
            return chipName;
        }

        public void setChipName(String chipName) {
            this.chipName = chipName;
        }

        public String getImsi_1() {
            return imsi_1;
        }

        public void setImsi_1(String imsi_1) {
            this.imsi_1 = imsi_1;
        }

        public String getImei_1() {
            return imei_1;
        }

        public void setImei_1(String imei_1) {
            this.imei_1 = imei_1;
        }

        public String getImsi_2() {
            return imsi_2;
        }

        public void setImsi_2(String imsi_2) {
            this.imsi_2 = imsi_2;
        }

        public String getImei_2() {
            return imei_2;
        }

        public void setImei_2(String imei_2) {
            this.imei_2 = imei_2;
        }

        @Override
        public String toString() {
            return "IMSInfo{" +
                    "chipName='" + chipName + '\'' +
                    ", \nimsi_1='" + imsi_1 + '\'' +
                    ", \nimei_1='" + imei_1 + '\'' +
                    ", \nimsi_2='" + imsi_2 + '\'' +
                    ", \nimei_2='" + imei_2 + '\'' +
                    '}';
        }
    }

    public static String getIMSIUnderLollipop(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }


}
