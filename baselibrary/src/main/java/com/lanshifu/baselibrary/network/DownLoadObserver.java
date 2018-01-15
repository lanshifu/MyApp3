package com.lanshifu.baselibrary.network;


import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.baselibrary.network.progress.ProgressListener;
import com.lanshifu.baselibrary.network.progress.ProgressManager;
import com.lanshifu.baselibrary.network.progress.body.ProgressInfo;
import com.lanshifu.baselibrary.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

import static okhttp3.internal.Util.closeQuietly;

/**
 * 封装了文件下载监听
 * Created by lanxiaobin on 2017/11/8.
 */

public abstract class DownLoadObserver<T> implements Observer<T> {

    private String mFilepath; //保存路径

    /**
     * 封装了下载
     *
     * @param url       下载地址
     * @param filePath  保存的地址
     */
    public DownLoadObserver(String url, String filePath) {
       this.mFilepath = filePath;
        LogHelper.d("lxb ->url " +url);
        ProgressManager.getInstance().addDownLoadListener(url, new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                DownLoadObserver.this.onProgress(progressInfo);
            }

            @Override
            public void onError(long id, Exception e) {
                LogHelper.e("lxb ->DownLoadObserver "+e);
                onDownFailed(e.getMessage());
            }
        });
    }


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

        ResponseBody responseBody = null;
        //保存到文件
        if(!(t instanceof ResponseBody)){
            LogHelper.e("lxb -> 传入类型非ResponseBody");
            onDownFailed("传入类型非ResponseBody");
            return;
        }
        responseBody = (ResponseBody) t;
        saveToSD(responseBody);

    }

    @Override
    public void onError(Throwable e) {
        LogHelper.e("lxb ->onError "+e);
        onDownFailed(e.getMessage());

    }

    @Override
    public void onComplete() {

    }

    private void saveToSD(final ResponseBody responseBody) {
        final File file = new File(mFilepath);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean success = writeFile2Disk(responseBody, file);
                long fileSize = FileUtil.getFileSize(file);
                LogHelper.d("lxb ->getFileSize" +fileSize);
                //ResponseBody.string  调用一次就失效了,所以这里通过文件大小大小判断是否下载失败
                //如果接口报错,返回的报错信息小于1k
                if(fileSize < 1024){
                    success = false;
                }
                e.onNext(success);
                e.onComplete();
            }
        })
                .compose(RxScheduler.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            onDownLoadSuccess();
                        } else {
                            onDownFailed("保下载失败,保存sd卡出错或者服务端文件不存在");
                        }
                    }
                });


    }

    public static boolean writeFile2Disk(final ResponseBody response, final File file) {
        OutputStream os = null;
        InputStream is = response.byteStream();
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];

            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
            return true;

        } catch (Exception e) {
            LogHelper.e("lxb ->Exception :" + e);
            return false;
        } finally {
            closeQuietly(os);
            closeQuietly(is);
        }

    }



    protected abstract void onProgress(ProgressInfo progressInfo);

    protected abstract void onDownLoadSuccess();

    protected abstract void onDownFailed(String error);

}
