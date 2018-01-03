package com.lanshifu.myapp_3.network;

/**
 * 服务端状态码不对时抛出此异常，MyObserver的onError中处理
 * Created by lanxiaobin on 2017/9/8.
 */

public class ServerException extends Exception {

    //token 过期
    public static final int CODE_TOKEN_OUT_OF_DATE = 1199;
    //token 错误(不存在)
    public static final int CODE_TOKEN_ERROR = 1198;

    public int code;
    private String message;

    public ServerException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
