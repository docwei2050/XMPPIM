package db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    db
 *  @文件名:   SmsProvider
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/7 22:30
 *  @描述：    TODO
 */
public class SmsProvider extends ContentProvider{
    private static final String TAG       = "SmsProvider";
    public static final String AUTHORITES ="com.docwei.xmppdemo.db.SmsProvider";
    static UriMatcher matcher             =new UriMatcher(UriMatcher.NO_MATCH);
    private static final int  MATCHEDCODE = 1;

    private static final int MATCHER_NUMBER_CODE = 2;

    static {
        matcher.addURI(AUTHORITES,"sms", MATCHEDCODE);
        matcher.addURI(AUTHORITES,"sms/session",MATCHER_NUMBER_CODE);
    }

    private SmsSqliteOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new SmsSqliteOpenHelper(getContext());
        if(mHelper !=null){
            return true;
        }
        return false;
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                long result = db.insert(SmsSqliteOpenHelper.TABLE, null, values);
                
                if (result != -1) {
                    //插入成功
                    System.out.println("插入成功------");
                    //这样就能
                    //null的话，是通知所有的
                    getContext().getContentResolver().notifyChange(uri, null);

                    uri = ContentUris.withAppendedId(uri, result);
                }
                return uri;
            default:
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                int result = db.delete(SmsSqliteOpenHelper.TABLE, selection, selectionArgs);
                
                if (result != -1) {
                    //删除成功
                    getContext().getContentResolver()
                                .notifyChange(uri, null);
                    return result;
                }
                return -1;
            default:
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                int result = db.update(SmsSqliteOpenHelper.TABLE,
                                       values,
                                       selection,
                                       selectionArgs);
                
                if (result != -1) {
                    //更新成功
                    getContext().getContentResolver().notifyChange(uri, null);
                    return result;
                }
                return -1;
            default:
                break;
        }
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getReadableDatabase();
                Cursor cursor = db.query(SmsSqliteOpenHelper.TABLE, null, selection, selectionArgs, null, null, sortOrder);
                if (cursor != null) {
                    return cursor;
                }
                break;
            case MATCHER_NUMBER_CODE:
                SQLiteDatabase db2 = mHelper.getReadableDatabase();
                //因为涉及到表中表，所以只能使用原生的sql语句了
                String sql="select * from (select * from  sms where (from_account=? or to_account=? ) order by time) group by session_account order by time asc";
                Cursor cursor2 = db2.rawQuery(sql,selectionArgs);
                if (cursor2 != null) {
                    return cursor2;
                }
                break;
            default:
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }


}
