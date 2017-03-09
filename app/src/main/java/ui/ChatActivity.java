package ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.docwei.xmppdemo.R;

import adapter.ConversationRecyclerAdater;
import service.IMService;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    ui
 *  @文件名:   ChatActivity
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/7 22:18
 *  @描述：    TODO
 */
public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ChatActivity";
    private static final int LOAD_ID = 3;
    private TextView             mTv_title;
    private EditText             mBody;
    private String               mUsername;


    private RecyclerView mRecyclerView;
    private ConversationRecyclerAdater mAdapter;
    private LoaderManager mLoaderManager;
    private MyServiceConnection mConnection;
    private IMService mImService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        setLoadManagerOrRestartLoad();
        registerContentObserver();
    }

    private void initData() {
        mTv_title = (TextView) findViewById(R.id.title);
        Button btn_send = (Button)findViewById(R.id.send);
        btn_send.setOnClickListener(mSend);
        mBody = (EditText) findViewById(R.id.et_content);
        mUsername = getIntent().getStringExtra("username");
        String nickname=getIntent().getStringExtra("nickname");
        mTv_title.setText("与"+nickname+"聊天中");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerAdater(this,null,0);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=new Intent(this,IMService.class);
        mConnection = new MyServiceConnection();
        bindService(intent,mConnection, BIND_AUTO_CREATE);
    }

    private View.OnClickListener mSend=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String body = mBody.getText().toString();
            if (TextUtils.isEmpty(body)) {
                return;
            }
            //调用服务中的方法：发消息
            mImService.sendMessage(mUsername,body);
            //点击之后要清空
            mBody.setText(null);
        }

    };


    public static final String FROMACCOUNT="from_account";
    public static final String TOACCOUNT="to_account";
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //这里不做限制查询的话，是不对的，我与其他人聊天，那么只能查询我与该对象的记录，别人的记录必要查询
        String selection="(from_account = ? and to_account = ?)or(from_account = ? and to_account = ?) ";
        System.out.println("mUsername--"+mUsername);
        String[] selectionArgs=new String[]{IMService.currentAccount,mUsername,mUsername,IMService.currentAccount};

        return new CursorLoader(this,IMService.Content_URI,null,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       mAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void registerContentObserver(){
        System.out.println(IMService.Content_URI);
       getContentResolver().registerContentObserver(IMService.Content_URI,true,mMyContentObserver);
    }
    public void unregisterContentObserver(){
        getContentResolver().unregisterContentObserver(mMyContentObserver);
    }
    //注册
    //反注册
    //通知
    public MyContentObserver mMyContentObserver=new MyContentObserver(new Handler());
    class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //数据发生改变，此时如果没有manager，我们就去创建manager，
            setLoadManagerOrRestartLoad();
        }
    }
    private void setLoadManagerOrRestartLoad() {
        if(mLoaderManager==null){
            mLoaderManager= getSupportLoaderManager();
            mLoaderManager.initLoader(LOAD_ID, null, this);
        }else{
            mLoaderManager.restartLoader(LOAD_ID,null,this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
        if(mConnection!=null) {
            unbindService(mConnection);
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
              IMService.MyBinder binder= (IMService.MyBinder) service;
            mImService = binder.getIMService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
