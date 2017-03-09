package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.docwei.xmppdemo.MainActivity;
import com.docwei.xmppdemo.R;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import service.IMService;
import utils.ThreadUtils;
import utils.ToastUtil;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    ui
 *  @文件名:   LoginActivity
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 21:12
 *  @描述：    TODO
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText mEt_username;
    private EditText mEt_pwd;
    public static final String IP="192.168.22.204";
    public static final String SERVERNAME="docweo.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
    }

    private void initData() {
        mEt_username = (EditText) findViewById(R.id.username);
        mEt_pwd = (EditText) findViewById(R.id.password);
    }
    public void login(View view){


        final String usrName =mEt_username.getText().toString();
        final String pwd     =mEt_pwd.getText().toString();
        if(TextUtils.isEmpty(usrName)||TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"密码或者账号为空",Toast.LENGTH_SHORT).show();
          return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建连接192.168.1.106  5222是openfire的默认端口
                    //创建连接配置对象
                    ConnectionConfiguration config=new ConnectionConfiguration(IP,5222);
                    //额外的配置--开启调试模式
                    config.setDebuggerEnabled(true);
                    //创建连接对象
                    XMPPConnection conn =new XMPPConnection (config);
                    //开始连接
                    conn.connect();
                    //开始登陆
                    conn.login(usrName,pwd);
                    //登录成功
                    ToastUtil.toastSafe(LoginActivity.this,"登陆成功");
                    //跳转到主界面
                    Intent intent2=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                    IMService.conn=conn;
                    IMService.currentAccount=usrName+"@"+SERVERNAME;
                    //开启服务，去同步联系人和监听花名册改变
                    Intent intent=new Intent(LoginActivity.this,IMService.class);
                    startService(intent);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    ToastUtil.toastSafe(LoginActivity.this,"登陆失败3");
                }
            }
        });

    }
}
