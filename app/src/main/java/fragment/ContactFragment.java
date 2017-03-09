package fragment;


import android.content.ContentValues;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.docwei.xmppdemo.R;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

import adapter.ContactRecyclerAdater;
import adapter.MyDividerItem;
import db.ContactSqliteOpenHelper;
import db.MyContentProvider;
import ui.ChatActivity;
import utils.PingYingUtil;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    fragment
 *  @文件名:   ContactFragment
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 22:59
 *  @描述：    TODO
 */
public class ContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ContactFragment";
    private static final int LOAD_ID = 100;
    private RecyclerView mRecyclerView;
    public static Uri content_uri=Uri.parse("content://"+MyContentProvider.AUTHORITIES+"/accounts");
    private ContactRecyclerAdater mAdapter;
    //private SimpleCursorAdapter mAdapter;
    private SimpleCursorAdapter mAdapter1;
    private Roster mRoster;
    private LoaderManager mManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerContentObserver();
    }
   public Handler mHandler=new Handler();
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view=View.inflate(getActivity(), R.layout.fragment_contact, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mAdapter = new ContactRecyclerAdater(getActivity(), null, 0);
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new MyDividerItem(getActivity(), 0));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ContactRecyclerAdater.OnItemClickListener() {
            @Override
            public void onitemClick(View view, int position) {
                Cursor cursor=mAdapter.getCursor();
                cursor.moveToPosition(position);
                String username=cursor.getString(cursor.getColumnIndex(ContactSqliteOpenHelper.USERNAME));
                String nickname=cursor.getString(cursor.getColumnIndex(ContactSqliteOpenHelper.NICKNAME));
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
            }
        });
        getActivity().getContentResolver().registerContentObserver(content_uri, false, new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                if(selfChange) {
                    getLoaderManager().restartLoader(LOAD_ID, null, ContactFragment.this);
                }
            }
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                getLoaderManager().restartLoader(LOAD_ID, null, ContactFragment.this);
                super.onChange(selfChange, uri);
            }
        });
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection=null;
        String selection= null;
        //((display_name NOTNULL) AND (has_phone_number =1) AND (display_name != ''))
        return new CursorLoader(getActivity(),content_uri,projection,selection,null, ContactSqliteOpenHelper.PINTYIN);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    private void initData() {
        //加载进数据库
        setLoadManagerOrRestartLoad();
        //获取花名册
        //联系人同步应该放在子线程里面的
    }

    private void updateOrInsertEntry(RosterEntry entry) {
        ContentValues values=new ContentValues();
        String username=entry.getUser();
        String nickname=entry.getName();
        values.put(ContactSqliteOpenHelper.NICKNAME,nickname);
        values.put(ContactSqliteOpenHelper.PINTYIN, PingYingUtil.getPingYing(nickname));
        if(!TextUtils.isEmpty(username)){
            int number=getActivity().getContentResolver()
                         .update(content_uri,values,ContactSqliteOpenHelper.USERNAME+" = ?",new String[]{username});
            if(number==0){
               values.put(ContactSqliteOpenHelper.USERNAME,username);
                getActivity().getContentResolver().insert(content_uri,values);
            }
        }

    }
    public MyRosterListener mMyRosterListener=new MyRosterListener();
    private class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> collection) {
            System.out.println("tianjia ");
            for(String username:collection){
                //根据花名册拿到entry
                RosterEntry entry=mRoster.getEntry(username);
                updateOrInsertEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            for(String username:collection){
                RosterEntry entry=mRoster.getEntry(username);
                updateOrInsertEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            for(String username:collection){
                getContext().getContentResolver().delete(content_uri,ContactSqliteOpenHelper.USERNAME+"=?",new String[]{username});
            }

        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    }
    public void registerContentObserver(){
        getActivity().getContentResolver().registerContentObserver(content_uri,true,mMyContentObserver);
    }
    public void unregisterContentObserver(){
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }
    //注册
    //反注册
    //通知
    public MyContentObserver mMyContentObserver=new MyContentObserver(new Handler());
    class MyContentObserver extends ContentObserver{
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
        if(mManager==null){
            mManager = getActivity().getSupportLoaderManager();
            mManager.initLoader(LOAD_ID, null, ContactFragment.this);
        }else{
            mManager.restartLoader(LOAD_ID,null,ContactFragment.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
        if(mMyRosterListener!=null&&mRoster!=null){
            mRoster.removeRosterListener(mMyRosterListener);
        }
    }
}
