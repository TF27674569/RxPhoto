package com.rxphoto;

import android.Manifest;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.rx.Rx;

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
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request()
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        if (!aBoolean)
                            Toast.makeText(MainActivity.this, "您取消的权限", Toast.LENGTH_SHORT).show();
                        return aBoolean;
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<Uri>>() {
                    @Override
                    public ObservableSource<Uri> apply(Boolean aBoolean) throws Exception {
                        return Rx
                                .create(MainActivity.this)
                                .camera()
                                .obsrver();
                    }
                })
                .flatMap(new Function<Uri, ObservableSource<Uri>>() {
                    @Override
                    public ObservableSource<Uri> apply(Uri uri) throws Exception {
                        return Rx
                                .create(MainActivity.this)
                                .zoom(uri)
                                .obsrver();
                    }
                })
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {

                    }
                });
    }
}
