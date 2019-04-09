package com.example.administrator.myapplication.c.chapter01;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.c.chapter01.utils.MyUtils;
import com.example.administrator.myapplication.c.chapter01.utils.VersionUpdateUtils;

public class SplashActivity extends Activity {

    /** 应用版本号 */
    private TextView mVersionTV;
    /** 本地版本号 */
    private String mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置没有标题栏 在加载布局之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mVersion = MyUtils.getVersion(getApplicationContext());
        initView();
        final VersionUpdateUtils updateUtils = new VersionUpdateUtils(mVersion,
                SplashActivity.this);
        new Thread() {
            public void run() {
                // 获取服务器版本号
                updateUtils.getCloudVersion();
            };
        }.start();
    }

    /** 初始化控件 */
    private void initView() {
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mVersionTV.setText("版本号 " + mVersion);
    }
}
