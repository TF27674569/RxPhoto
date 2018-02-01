package com.rxphoto;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.rx.Rx;

import java.io.InputStream;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Rx.create(this)
                .permission()
                .permission(Manifest.permission.CAMERA)
                .request()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });

        Rx.create(this)
                .camera()
                .obsrver()
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {

                    }
                });

        Rx.create(this)
                .permission()
                .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request()
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception { // 判断权限是否授予陈宫
                        if (!aBoolean)
                            Toast.makeText(MainActivity.this, "您取消的权限", Toast.LENGTH_SHORT).show();
                        return aBoolean;
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<Uri>>() {//成功后跳转相机
                    @Override
                    public ObservableSource<Uri> apply(Boolean aBoolean) throws Exception {
                        return Rx
                                .create(MainActivity.this)
                                .camera()
                                .obsrver();
                    }
                })
                .flatMap(new Function<Uri, ObservableSource<Uri>>() {// 相机拍时候跳转裁剪
                    @Override
                    public ObservableSource<Uri> apply(Uri uri) throws Exception {
                        return Rx
                                .create(MainActivity.this)
                                .zoom(uri)
                                .obsrver();
                    }
                })
                .map(new Function<Uri, Bitmap>() {// 裁剪后 uri转bitmap
                    @Override
                    public Bitmap apply(Uri uri) throws Exception {
                        InputStream input = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        input.close();
                        return bitmap;
                    }
                })
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        // 最后显示图片
                    }
                });
    }
}
