package com.rx.module;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.rx.FileUtil;

import java.util.ArrayList;

import io.reactivex.subjects.Subject;

/**
 * description：
 * <p/>
 * Created by TIAN FENG on 2018/2/1.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */

public class RxFragment extends Fragment {
    // 请求permission的权限
    private static final int PERMISSIONS_CODE = 42;
    private static final int CAMERA_CODE = 46;//相机
    private static final int PICTURE_CODE = 47;//相册
    private static final int ZOOM_CODE = 48;//裁剪

    // Rxjava请求根的集合
    private SparseArray<Subject> mPublishSubjects;

    // uri
    private Uri mTempUri;

    public RxFragment() {
        mPublishSubjects = new SparseArray<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // Camera订阅器
    public void startCamera(Subject<Uri> publishSubjec) {
        if (publishSubjec == null) {
            throw new NullPointerException("Subject is null, please call setCameraPublishSubjec by SystemFragment.");
        }
        mPublishSubjects.put(CAMERA_CODE, publishSubjec);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempUri = Uri.fromFile(FileUtil.getPhotoCacheFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    // Picture订阅器
    public void startPicture(Subject<Uri> publishSubjec) {
        if (publishSubjec == null) {
            throw new NullPointerException("Subject is null, please call setPicturePublishSubjec by SystemFragment.");
        }
        mPublishSubjects.put(PICTURE_CODE, publishSubjec);

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE_CODE);
    }

    // Zoom订阅器
    public void startZoom(Subject<Uri> publishSubjec, Uri uri, int scanX, int scanY, int outputX, int outputY) {

        if (publishSubjec == null) {
            throw new NullPointerException("Subject is null, please call setZoomPublishSubjec by SystemFragment.");
        }
        mPublishSubjects.put(ZOOM_CODE, publishSubjec);

        // 创建一个临时的uri
        mTempUri = Uri.fromFile(FileUtil.getPhotoCacheFile());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //是否裁剪
        intent.putExtra("crop", "true");
        //设置xy的裁剪比例
        intent.putExtra("aspectX", scanX);
        intent.putExtra("aspectY", scanY);

        if (outputX != -1 && outputY != -1) {
            //设置输出的宽高
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
        }
        //是否缩放
        intent.putExtra("scale", false);
        //是否返回图片数据，可以不用，直接用uri就可以了
        intent.putExtra("return-data", false);
        //设置输入图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //是否关闭面部识别
        intent.putExtra("noFaceDetection", true);
        //输入图片的Uri，指定以后，可以在这个uri获得图片
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri);
        //启动
        startActivityForResult(intent, ZOOM_CODE);
    }


    // 请求权限
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(@NonNull String[] permissions, Subject<Boolean> publishSubject) {
        mPublishSubjects.put(PERMISSIONS_CODE, publishSubject);
        requestPermissions(permissions, PERMISSIONS_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 没有对应的key
        if (mPublishSubjects.indexOfKey(requestCode) == -1) return;
        // 回调权限请求
        mPublishSubjects.get(requestCode).onNext(getUnrequestPermission(permissions).isEmpty());
        mPublishSubjects.get(requestCode).onComplete();
    }

    // 是否授权过
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    // 查看权限是否符合policy的规定
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    /**
     * 获取未受理的权限
     */
    public ArrayList<String> getUnrequestPermission(String[] permissions) {
        ArrayList<String> unrequestPermission = new ArrayList<>();
        for (String permission : permissions) {
            if (isGranted(permission)) {
                continue;
            }
            if (isRevoked(permission)) {
                continue;
            }
            unrequestPermission.add(permission);
        }
        return unrequestPermission;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 不是自己的请求码直接返回
        if (mPublishSubjects.indexOfKey(requestCode) == -1) return;

        // 是否成功
        if (resultCode != Activity.RESULT_OK) return;

        // 获取uri
        Uri uri = requestCode == PICTURE_CODE ? data.getData() : mTempUri;
        // 成功可达
        mPublishSubjects.get(requestCode).onNext(uri);
        mPublishSubjects.get(requestCode).onComplete();

        // 如果是相机操作，发广播通知相册更新
        if (requestCode == CAMERA_CODE) {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }

    }
}
