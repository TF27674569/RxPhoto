package com.rx.photo;

import android.net.Uri;

import com.rx.module.RxFragment;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * description：
 * <p/>
 * Created by TIAN FENG on 2018/1/12.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */
public class RxZoom {
    private RxFragment mRxFragment;
    private Uri mUri;
    private int mScanX = 1;
    private int mScanY = 1;
    private int mOutputX = -1;
    private int mOutputY = -1;


    public RxZoom(RxFragment fragment,Uri uri) {
        mRxFragment = fragment;
        mUri = uri;
    }

    // 宽高比
    public RxZoom scan(int scanX, int scanY) {
        mScanX = scanX;
        mScanY = scanY;
        return this;
    }

    // 宽高输出大小
    public RxZoom output(int outputX, int outputY) {
        mOutputX = outputX;
        mOutputY = outputY;
        return this;
    }

    public Observable<Uri> obsrver() {
        if (mUri == null) {
            throw new NullPointerException("裁剪uri不可为null.");
        }

        PublishSubject<Uri> publishSubject = PublishSubject.create();
        mRxFragment.startZoom(publishSubject,mUri, mScanX, mScanY, mOutputX, mOutputY);
        return publishSubject;
    }

}
