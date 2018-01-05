package com.lanshifu.myapp_3.utils;


import com.lanshifu.baselibrary.utils.StringUtil;

/**
 * Author:  [xWX371834\许纯震].
 * Date:    2016/9/9.
 * Description:
 *      保存数组进SharePreference中，用#来分隔，所以，你懂的
 */
public class ArraySPUtil {

    public void cleanSPArray(String key) {
        SPUtil.getInstance().remove(key);
    }

    public static class SINGLETON{
        public static ArraySPUtil INSTANCE= new ArraySPUtil();
    }

    public static ArraySPUtil getInstance(){
        return SINGLETON.INSTANCE;
    }

    public void putSPArray(String key, String[] strings) {
        StringBuilder builder = new StringBuilder();
        int j = 0;
        for (int i = 0; i < strings.length; i++) {
            if (StringUtil.isEmpty(strings[i])) {
                j = i + 1;
                continue;
            }
            if (i != j) {
                builder.append("#");
            }
            builder.append(strings[i]);
        }
        SPUtil.getInstance().putString(key, builder.toString());
    }

    public String[] getSPArray(String key) {
        String string = SPUtil.getInstance().getString(key);
        return StringUtil.isEmpty(string) ? new String[0] : string.split("#");
    }

    public boolean removeSPArrayItem(String key, int position) {
        String[] spArray = getSPArray(key);
        if (position > -1 && position < spArray.length) {
            spArray[position] = "";
            putSPArray(key, spArray);
            return true;
        } else {
            return false;
        }
    }

    public boolean putSPArrayItem(String key, String item) {
        String string = SPUtil.getInstance().getString(key);
        if (string.contains(item)) {
            return false;
        }
        if (StringUtil.isEmpty(string)) {
            SPUtil.getInstance().putString(key, item);
        } else {
            SPUtil.getInstance().putString(key, string + "#" + item);
        }
        return true;
    }
}
