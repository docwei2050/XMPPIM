package fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.docwei.xmppdemo.R;

import adapter.SessionRecyclerAdapter;
import db.SmsProvider;
import db.SmsSqliteOpenHelper;
import service.IMService;
import ui.ChatActivity;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    fragment
 *  @文件名:   ConversationFragment
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 22:45
 *  @描述：    TODO
 */
public class ConversationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ConversationFragment";
    private static final int LOAD_SESSIONACCOUNT = 1;
    private static final int LOAD_NUMBER = 2;
    public static final Uri Content_URI = Uri.parse("content://" + SmsProvider.AUTHORITES + "/sms/session");
    private SessionRecyclerAdapter mAdapter;
    private LoaderManager mLoadmanager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //要查询的sql语句---获取到会话的
        //select * from  sms where from_account='haha@docweo.com' or to_account='haha@docweo.com' group by session_account
        //获取到当前会话记录有多少条
        //select count(*) from  sms where from_account='haha@docweo.com' and  to_account='docwei@docweo.com' or  from_account='docwei@docweo.com' and to_account='haha@docweo.com'
        setLoadManagerOrRestartLoad();
        registerContentObserver();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        setLoadManagerOrRestartLoad();
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_conversation, null);
        RecyclerView recyclerview = (RecyclerView) view.findViewById(R.id.recycler_coversation);
        mAdapter = new SessionRecyclerAdapter(getActivity(), null, 0);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SessionRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onitemClick(View view, int position) {
                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);
                String username = cursor.getString(cursor.getColumnIndex(SmsSqliteOpenHelper.SESSIONACCOUNT));
                String nickname = cursor.getString(cursor.getColumnIndex(SmsSqliteOpenHelper.SESSIONACCOUNT));
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("nickname", nickname.substring(0, nickname.indexOf("@")));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
        System.out.println("Fragemtn销毁了onCreateonCreateonCreateonCreateonCreateonCreate");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOAD_SESSIONACCOUNT) {
            System.out.println("contentUri-会话的-"+Content_URI);
            //CursorLoader(Context context, Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder)
            return new CursorLoader(getActivity(), Content_URI, null, null, new String[]{IMService.currentAccount, IMService.currentAccount}, null);
        } else if (id == LOAD_NUMBER) {
            String sessionAccount = args.getString("sessionAccount");
            return new CursorLoader(getActivity(), Content_URI, new String[]{"count(*)"}, "from_account=? and  to_account=? or  from_account=? and to_account=?", new String[]{IMService.currentAccount, sessionAccount, sessionAccount, IMService.currentAccount}, null);
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        //第一个查询完成之后在查询第二个
       /* if(data!=null&&data.getColumnIndex("session_account")!=-1) {
            String sessionAccount=data.getString(data.getColumnIndex("session_account"));
            Bundle bundle=new Bundle();
            bundle.putString("sessionAccount",sessionAccount);
            mLoadmanager.initLoader(LOAD_NUMBER,bundle,this);
        }*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void registerContentObserver() {
        getActivity().getContentResolver().registerContentObserver(Content_URI, true, mMyContentObserver);
    }

    public void unregisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    //注册
    //反注册
    //通知
    public MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

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
        if (mLoadmanager == null) {
            mLoadmanager = getActivity().getSupportLoaderManager();
            mLoadmanager.initLoader(LOAD_SESSIONACCOUNT, null, this);
        } else {
            mLoadmanager.restartLoader(LOAD_SESSIONACCOUNT, null, this);
        }
    }

}
