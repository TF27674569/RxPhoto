package com.rx;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;

import com.rx.module.RxFragment;
import com.rx.permission.RxPermission;
import com.rx.photo.RxCamera;
import com.rx.photo.RxPicture;
import com.rx.photo.RxZoom;


/**
 * description：
 * <p/>
 * Created by TIAN FENG on 2018/1/11.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */
public class Rx {

    private RxFragment mRxFragment;
    private String TAG = "com.rx.Rx";

    private Rx(Activity activity) {
        mRxFragment = getRxFragment(activity);
    }

    private RxFragment getRxFragment(Activity activity) {
        // 根据tag找Fragment
        RxFragment systemFragment = findRxFragment(activity);

        // 判断是否有此Activity
        boolean isNewInstance = systemFragment == null;
        // 如果没有
        if (isNewInstance) {
            // 创建一个
            systemFragment = new RxFragment();
            // 拿FragmentManager
            FragmentManager fragmentManager = activity.getFragmentManager();
            // 通过事物添加
            fragmentManager
                    .beginTransaction()
                    .add(systemFragment, TAG)
                    .commitAllowingStateLoss();// 防止手机横竖屏切换时崩溃
            fragmentManager.executePendingTransactions();// 强制创建没有调用的Fragment
        }
        return systemFragment;
    }

    // 根据tag找Fragment
    private RxFragment findRxFragment(Activity activity) {
        return (RxFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    /**************************************************************************************************************************/


    // 创建
    public static Rx create(Activity activity) {
        return new Rx(activity);
    }

    // 权限
    public RxPermission permission() {
        return new RxPermission(mRxFragment);
    }

    // 相机
    public RxCamera camera() {
        return new RxCamera(mRxFragment);
    }

    // 相册
    public RxPicture picture() {
        return new RxPicture(mRxFragment);
    }

    // 裁剪
    public RxZoom zoom(Uri uri) {
        return new RxZoom(mRxFragment,uri);
    }


}
