package ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.docwei.xmppdemo.R;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    ui
 *  @文件名:   SplashActivity
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 21:06
 *  @描述：    TODO
 */
public class SplashActivity extends AppCompatActivity{
    private static final String TAG = "SplashActivity";
    private Handler mHandler=new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
    }
}
