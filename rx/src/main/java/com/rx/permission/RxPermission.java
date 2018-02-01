package com.rx.permission;

import android.annotation.TargetApi;
import android.os.Build;

import com.rx.module.RxFragment;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * description：
 * <p/>
 * Created by TIAN FENG on 2018/2/1.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */

public class RxPermission {

    // 回调所需的Fragment
    private RxFragment mFragment;
    // 未申请的权限
    private ArrayList<String> mUnRequestPermissions;


    public RxPermission(RxFragment fragment) {
        this.mFragment = fragment;
    }

    // 请求权限
    public RxPermission permission(String... permissions) {
        // 未申请的权限
        // 未申请的权限
        mUnRequestPermissions = new ArrayList<>();
        // 遍历权限
        for (String requestPermission : permissions) {
            if (isGranted(requestPermission)) {
                continue;
            }
            if (isRevoked(requestPermission)) {
                continue;
            }
            // 添加到未授权的集合
            mUnRequestPermissions.add(requestPermission);
        }
        return this;
    }

    // 请求权限
    public Observable<Boolean> request() {

        // 6.0 以下或者是没有未申请的权限 直接返回true
        if (!isMarshmallow() || mUnRequestPermissions.isEmpty()) {
            return Observable.just(true);
        }

        PublishSubject<Boolean> publishSubject = PublishSubject.create();
        requestPermissionsFromFragment(mUnRequestPermissions.toArray(new String[mUnRequestPermissions.size()]), publishSubject);
        return publishSubject;
    }

    /*******************************************************************************************************************************************/
    // 申请权限
    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions, PublishSubject<Boolean> publishSubject) {
        mFragment.requestPermissions(permissions, publishSubject);
    }

    // 查看权限是否符合policy的规定
    boolean isRevoked(String permission) {
        return isMarshmallow() && mFragment.isRevoked(permission);
    }

    // 是否已经申请过
    boolean isGranted(String permission) {
        return !isMarshmallow() || mFragment.isGranted(permission);
    }

    // 是否是6.0
    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


}
