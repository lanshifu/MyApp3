package com.lanshifu.myapp_3.network.api;

import com.lanshifu.myapp_3.network.HttpResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 默认的请求方法写在 DefaultApi里，如果baseurl不同则新建一个Api
 * Created by lanxiaobin on 2017/9/6.
 */

public interface DefaultApi {

//    //登录接口
//    @FormUrlEncoded
//    @POST("/cmcm/login")
//    Observable<HttpResult> login(
//            @Field("random") String random,
//            @Field("imsi") String imsi,
//            @Field("phone") String phone);
//
//    //用户信息更新接口（包括生日设置）
//    @FormUrlEncoded
//    @POST("/cmcm/user/update")
//    Observable<HttpResult> updateUserInfo(
//            @Field("name") String name,
//            @Field("birthday") String birthday,
//            @Field("phone") String phone,
//            @Field("birthdaySign") String birthdaySign //0 1
//    );
//
//
//  //上传用户日志
//    @POST("/cmcm/system/log/upload")
//    Observable<HttpResult> uploadCrashLog(
//            @Header("phone") String phone,
//            @Body RequestBody Body
//    );


    @Headers({
            "Host: www.baidu.com",
            "User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) Gecko/20100101 Firefox/57.0"
    })
    @GET("http://www.baidu.com/s")
    Observable<ResponseBody> get(
            @Query("wd") String wd);


}
