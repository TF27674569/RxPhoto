package com.rx;

import android.os.Environment;

import java.io.File;

/**
 * description：
 * <p>
 * Created by TIAN FENG on 2018/1/12.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */
public class FileUtil {

    // 获取拍照临时的缓存文件
    public static File getPhotoCacheFile() {
        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return new File(fileDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
    }
}
