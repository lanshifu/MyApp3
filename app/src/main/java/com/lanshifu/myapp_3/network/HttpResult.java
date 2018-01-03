package com.lanshifu.myapp_3.network;

/**
 * 返回码 统一处理
 * Created by lanxiaobin on 2017/9/6.
 */

public class HttpResult<T> {

    private int code;
    private String message;
    private long timestamp;
    private boolean success;
    private T data;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        if (message == null) {
            return "message is null";
        }
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return code == 0;
    }


    public T getData() {
        return data;
    }


}