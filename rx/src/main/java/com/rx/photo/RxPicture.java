package com.rx.photo;

import android.net.Uri;

import com.rx.module.RxFragment;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * description：
 * <p>
 * Created by TIAN FENG on 2018/1/12.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */
public class RxPicture {
    private RxFragment mRxFragment;


    public RxPicture(RxFragment fragment) {
        mRxFragment = fragment;
    }

    public Observable<Uri> obsrver() {
        PublishSubject<Uri> publishSubject = PublishSubject.create();
        mRxFragment.startPicture(publishSubject);
        return publishSubject;
    }
}
