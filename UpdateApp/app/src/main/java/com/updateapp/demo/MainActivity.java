package com.updateapp.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zjn.updateapputils.util.CheckVersionRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> permissions = new ArrayList<>();
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            //只有获取到存储权限才可以更新软件
            updateVersion();
        }
        if (permissions.size() != 0) {
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]),
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateVersion();
                } else {
                    Toast.makeText(MainActivity.this, "拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * SD卡根目录
     */
    public static final String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 更新文件
     */
    public final String DIR_UPDATE_APK = ROOTPATH + "/" + MainActivity.this.getPackageName() + "/update_file";

    private void updateVersion() {
        CheckVersionRunnable runnable = CheckVersionRunnable.from(MainActivity.this)
                .setApkPath(DIR_UPDATE_APK)//文件存储路径
                .setDownLoadUrl("http://shouji.360tpcdn.com/190527/7c9d4c6905305189cfa22b0293432454/com.baidu.searchbox_48498048.apk")//下载路径
                .setServerUpLoadLocalVersion("" + (getVersionCode(MainActivity.this) + 1))
                .setServerVersion("" + (getVersionCode(MainActivity.this) + 2))
                .setUpdateMsg("更新内容")
                .isUseCostomDialog(true)
                .setNotifyTitle(getResources().getString(R.string.app_name))
                .setVersionShow("版本说明");
        //启动通知，去下载
        ThreadPoolUtils.newInstance().execute(runnable);
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


}
