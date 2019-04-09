package com.example.administrator.myapplication.c.chapter01.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.c.chapter01.HomeActivity;
import com.example.administrator.myapplication.c.chapter01.entity.VersionEntity;
import com.lidroid.xutils.http.ResponseInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2019/3/5.
 */

public class VersionUpdateUtils {
    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;
    /**用于更新UI
     * Handler是Android SDK中处理异步类消息的核心类
     * 作用是让子线程通过与UI通信来更新UI界面
     * */
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context,"IO异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context,"JSON解析异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                /** 更新的提醒对话框*/
                case MESSAGE_SHOEW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context,HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;
            }
        };
    };
    /** 本地的版本号*/
    private String mVersion;
    private Activity context;
    //原是ProgressDialog已改成ProgressBar；
    private ProgressDialog mProgressDialog;
    private VersionEntity versionEntity;
    public VersionUpdateUtils(String Version,Activity activity){
        mVersion = Version;
        context  = activity;
    }

    /**获取服务器版本号 */
    public void getCloudVersion(){
        try {
            HttpClient client = new DefaultHttpClient();
            //连接超时
            HttpConnectionParams.setConnectionTimeout(client.getParams(),5000);
            //请求超时
            HttpConnectionParams.setSoTimeout(client.getParams(),5000);
            //安装包下载地址
            HttpGet httpGet = new HttpGet("http://172.26.41.176/updateinfo.html");
            HttpResponse execute = client.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200){
                //请求和响应都成功了
                HttpEntity entity = execute.getEntity();
                String result = EntityUtils.toString(entity,"gbk");
                //创建jsonObject对象
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versioncode = code;
                String des  = jsonObject.getString("des");
                versionEntity.description  = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versioncode)){
                    //版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                }
            }
        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_EEOR);
            e.printStackTrace();
        }catch (IOException e){
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        }catch (JSONException e){
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }
    }
    /**
     * 弹出更新提示对话框
     * */
    private void showUpdateDialog(final VersionEntity versionEntity){
        //创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置标题
        builder.setTitle("检测到新版本:" +versionEntity.versioncode);
        builder.setMessage(versionEntity.description);
        //根据服务器返回描述，设置升级描述信息
        builder.setCancelable(false);//设置不能点击返回键隐藏对话框
        builder.setIcon(R.mipmap.ic_launcher);//设置对话框图标
        //设置立即升级按钮点击事件
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initProgerssDialog();
                downloadNewApk(versionEntity.apkurl);
            }
        });
        //设置暂不升级按钮点击事件
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }
    /**
     * 初始化进度条对话框
     * */
    private void initProgerssDialog(){
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("准备下载...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
    /**
     * 下载新版本
     * */
    protected void downloadNewApk(String apkurl){
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "/mntsdcard/mobilesafe2.0.apk", new DownLoadUtils.MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }
            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String arg1) {
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }
            @Override
            public void onLoadding(long total, long current, boolean isUploading) {
                mProgressDialog.setMax((int)total);
                mProgressDialog.setMessage("正在下载...");
                mProgressDialog.setProgress((int)current);
            }
        });
    }
    //import android.os.Handler;
    private void enterHome(){
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);
    }
}
