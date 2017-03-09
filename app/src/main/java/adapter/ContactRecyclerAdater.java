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
 * Created by tobo on 17/3/7.
 * 联系人界面
 */

public class ContactRecyclerAdater extends RecyclerViewCursorAdapter<ContactRecyclerAdater.MyViewHolder> {
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
    public ContactRecyclerAdater(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final Cursor cursor, final int postion) {
        String userName=cursor.getString(cursor.getColumnIndex("username"));
        String nikeName=cursor.getString(cursor.getColumnIndex("nikename"));
        holder.mTextView.setText(userName+"\r\n"+nikeName);
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
    public void onBindViewHolder(ContactRecyclerAdater.MyViewHolder holder, final Cursor cursor) {

    }
    @Override
    protected void onContentChanged() {

    }

    @Override
    public ContactRecyclerAdater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=mLayoutInflater.inflate(R.layout.contact_item,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView= (TextView) itemView.findViewById(R.id.tv);
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
