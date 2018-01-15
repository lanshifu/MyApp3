package com.lanshifu.myapp_3.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.didi.virtualapk.PluginManager;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.lanshifu.baselibrary.basemvp.BasePresenter;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.network.RxScheduler;
import com.lanshifu.baselibrary.utils.FileUtil;
import com.lanshifu.baselibrary.utils.StorageUtil;
import com.lanshifu.baselibrary.utils.ToastUtil;
import com.lanshifu.myapp_3.MainApplication;
import com.lanshifu.myapp_3.mvp.MainView;
import com.lanshifu.myapp_3.network.MyObserver;
import com.lanshifu.myapp_3.network.RetrofitHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;

/**
 * Created by lanshifu on 2018/1/14.
 */

public class MainPresenter extends BasePresenter<MainView> {

    private static final String PLUGIN_NAME = "imageplugin.apk";
    private String mTempPicPath;
    private String mPicturePath;
    private HashMap<String, Integer> mAnswers;
    private long starTime = 0;

    private int mCounter = 0;
    public void loadPlugin(Context base) {
        PluginManager pluginManager = PluginManager.getInstance(base);
//        File apk = new File(StorageUtil.getPluginFolder() + "plugin.apk");
        File apk = new File(Environment.getExternalStorageDirectory(), PLUGIN_NAME);
        if (apk.exists()) {
            LogHelper.d("loadPlugin");
            try {
                pluginManager.loadPlugin(apk);
                LogHelper.d("loadPlugin 插件plugin success");
            } catch (Exception e) {
                ToastUtil.showShortToast("plugin插件加载失败" + e.getMessage());
                LogHelper.d("插件load 失败 " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShortToast("插件apk不存在");
        }
    }

    public void checkRootPermission() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                e.onNext(Shell.SU.available());
            }
        }).compose(RxScheduler.<Boolean>io_main())
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void _onNext(Boolean root) {
                        if (root) {
                            mView.hasRootPermission();
                            initBaiduOrc();
                        } else {
                            ToastUtil.showShortToast("手机没有root权限，无法使用部分功能");
                        }
                    }

                    @Override
                    public void _onError(String e) {
                        ToastUtil.showShortToast(e);
                    }
                });
    }

    private void initBaiduOrc() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, MainApplication.getContext(), "6FvRsYfgtqD0qP2oG2Lmz2vk", "urjO0DfBbzVISLP72h0lnpvxeXyKhLpK");
    }


    public void cropBitmap(Bitmap bitmap, String savePath) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        Bitmap bm = Bitmap.createBitmap(bitmap, 70, 285, w - 70, 1000, null, false);
        FileUtil.saveBitmap(bm, savePath);
    }


    public void getScreenAndParseText() {
        starTime = System.currentTimeMillis();
        Observable.create(new ObservableOnSubscribe<CommandResult>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<CommandResult> e) throws Exception {
                String commands = "screencap -p " + mTempPicPath;
                CommandResult commandResult = Shell.SU.run(commands);
                mTempPicPath = StorageUtil.getAppRootDir() + "millionTemp.png";
                mPicturePath = StorageUtil.getAppRootDir() + "million.png";
                //裁剪
                Bitmap bitmap = BitmapFactory.decodeFile(mTempPicPath);
                cropBitmap(bitmap, mPicturePath);
                e.onNext(commandResult);
            }
        })
                .compose(RxScheduler.<CommandResult>io_main())
                .subscribe(new MyObserver<CommandResult>() {
                    @Override
                    public void _onNext(CommandResult s) {
                        LogHelper.d(s.getStdout());
                        long screenTime = System.currentTimeMillis() - starTime;
                        LogHelper.d("截屏加裁剪时间: " + screenTime);
                        picToText();
                    }

                    @Override
                    public void _onError(String e) {
                        LogHelper.e(e);
                    }
                });
    }

    private void picToText() {
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(mPicturePath));

        // 调用通用文字识别服务
        OCR.getInstance().recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                // 调用成功，返回GeneralResult对象
                long picToTextTime = System.currentTimeMillis();
                LogHelper.d("图片转文字总时间: " + (picToTextTime - starTime));
                String[] words = new String[result.getWordList().size()];
                for (int i = 0; i < result.getWordList().size(); i++) {
                    WordSimple word = result.getWordList().get(i);
                    words[i] = word.getWords();
                }

                //  9演员章子怡没有参演过以下哪部电影?《建国大业》《非诚勿扰1》《非常完美》
                // 9演员章子怡没有参演过以下哪部电影?
                String question = "";
                int start = 0;
                for (int i = 0; i < words.length; i++) {
                    question = question + words[i];
                    if (words[i].contains("?")) {
                        start = i + 1;
                        break;
                    }
                }

                int index = question.indexOf(".");
                if (index != -1) {
                    question = question.substring(index);
                }
                LogHelper.d("问题 " + question);
                mAnswers = new HashMap<String, Integer>();
                for (int i = start; i < words.length; i++) {
                    LogHelper.d("选项" + i + 1 + ":" + words[i]);
                    mAnswers.put(words[i], 0);
                }
                baidu(question);
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                LogHelper.e("识别识别 " + error);
            }
        });
    }


    private void baidu(String text) {
        RetrofitHelper.getInstance().getDefaultApi().get(text)
                .compose(RxScheduler.<ResponseBody>io_main())
                .subscribe(new MyObserver<ResponseBody>() {
                    @Override
                    public void _onNext(ResponseBody responseBody) {

                        StringBuilder result = new StringBuilder();
                        LogHelper.d("得出结果总时间 " + (System.currentTimeMillis() - starTime));
                        try {
                            String string = responseBody.string();
                            LogHelper.d("网页结果 " + string);
                            for (Map.Entry<String, Integer> stringIntegerEntry : mAnswers.entrySet()) {
                                String key = stringIntegerEntry.getKey().replace("《","").replace("》","");
                                mCounter = 0;
                                int count = countStr(string, key);
                                result.append(key + " :" + count);
                                result.append("\n");
                                LogHelper.d(key + " :" + count);
                            }

                            mView.heroResult(result.toString(),System.currentTimeMillis() - starTime);

                        } catch (IOException e) {
                            mView.heroError(e.getMessage());
                        }


                    }

                    @Override
                    public void _onError(String e) {
                        ToastUtil.showShortToast(e);
                        mView.heroError(e);
                    }
                });
    }


    /**
     * 判断str1中包含str2的个数
     *
     * @param str1
     * @param str2
     * @return mCounter
     */
    public int countStr(String str1, String str2) {
        if (!str1.contains(str2)) {
            return 0;
        } else {
            mCounter++;
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return mCounter;
        }
    }
}
