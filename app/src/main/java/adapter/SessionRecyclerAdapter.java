package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.docwei.xmppdemo.R;

/**
 * Created by tobo on 17/3/9.
 */

public class SessionRecyclerAdapter extends RecyclerViewCursorAdapter<SessionRecyclerAdapter.MyViewHolder> {
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
    public SessionRecyclerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final Cursor cursor, final int postion) {
        //设置nickname
        //设置会话条数
        //设置最后一次会话的内容
        String nickname=cursor.getString(cursor.getColumnIndex("session_account"));
        String content=cursor.getString(cursor.getColumnIndex("body"));
        holder.mTv_sessionNickname.setText(nickname.substring(0,nickname.indexOf("@")));
        holder.mTv_sessioncontent.setText(content);

//        holder.mTv_sessionNumber.setText(cursor.getString(cursor.getColumnIndex("count(*)")));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onitemClick(v, postion);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final Cursor cursor) {

    }
    @Override
    protected void onContentChanged() {

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.session_item,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTv_sessioncontent,mTv_sessionNickname;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTv_sessioncontent= (TextView) itemView.findViewById(R.id.session_content);
            mTv_sessionNickname= (TextView) itemView.findViewById(R.id.session_nickname);
        }
    }
    public interface OnItemClickListener{
        void onitemClick(View view,int position);
    }
    public OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }
}
