# RxPhoto
Rxjava封装6.0权限1跳转系统相机相册


## 引入依赖
### **compile 'com.tianfeng:Rx:1.0.0'** </br></br>
## 需要rxjava2的支持
### **compile 'io.reactivex.rxjava2:rxjava:2.0.1'**
### **compile 'io.reactivex.rxjava2:rxandroid:2.0.1'**</br></br>

## 如果出现异常
![找不到库文件](https://github.com/TF27674569/RxPhoto/blob/master/app/image/%E5%BC%82%E5%B8%B8.png)

在项目根目录的build.gradle中添加</br>
```java
  repositories {
        jcenter()
        ...
        maven {
            url  "https://dl.bintray.com/tf27674569/maven"
        }

    }
```
## 使用
### 单个功能模块
#### 1.权限请求
```java
  Rx.create(this)
                .permission()
                .permission(Manifest.permission.CAMERA)
                .request()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        
                    }
                });
```
#### 2.相机相册裁剪(替换关键字即可)
```java
 Rx.create(this)
                .camera()
                .obsrver()
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {
                        
                    }
                });
```
#### 3.组合使用形成事件流
请求相机权限->跳转相机->跳转裁剪->转bitmap->显示
```java
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
```


