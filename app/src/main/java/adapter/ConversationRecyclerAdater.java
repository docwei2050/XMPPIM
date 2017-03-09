package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.docwei.xmppdemo.R;

import service.IMService;

/**
 * Created by tobo on 17/3/7.
 * 沟通界面
 */

public class ConversationRecyclerAdater extends RecyclerViewCursorAdapter<ConversationRecyclerAdater.MyViewHolder> {
    public static final int FROMME=0;
    public static final int FROMOTHER=1;

    public Context mContext;
    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter;
     *                Currently it accept {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public LayoutInflater mLayoutInflater;
    public ConversationRecyclerAdater(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final Cursor cursor, final int postion) {
        if(cursor!=null) {
            holder.mTv_nickname.setText(filterAccount(cursor.getString(cursor.getColumnIndex("from_account"))));
            holder.mTv_time.setText(cursor.getString(cursor.getColumnIndex("time")));
            holder.mTv_msg.setText(cursor.getString(cursor.getColumnIndex("body")));
        }
    }
    @Override
    public void onBindViewHolder(ConversationRecyclerAdater.MyViewHolder holder, final Cursor cursor) {

    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor=getCursor();
        cursor.moveToPosition(position);
        String username=cursor.getString(cursor.getColumnIndex("from_account"));
        if(username.equals(IMService.currentAccount)){
            return FROMME;
        }else{
            return FROMOTHER;
        }
    }


    @Override
    protected void onContentChanged() {

    }

    @Override
    public ConversationRecyclerAdater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==FROMME){
            view=mLayoutInflater.inflate(R.layout.conversation_item_right,parent,false);
        }else{
            view=mLayoutInflater.inflate(R.layout.conversation_item_left,parent,false);
        }
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTv_nickname,mTv_msg,mTv_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTv_nickname= (TextView) itemView.findViewById(R.id.tv_nickname);
            mTv_msg= (TextView) itemView.findViewById(R.id.tv_msg);
            mTv_time= (TextView) itemView.findViewById(R.id.time);
        }
    }
    public String filterAccount(String account){
        return account.substring(0,account.indexOf("@"));
    }
}
