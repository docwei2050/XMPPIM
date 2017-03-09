package db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by tobo on 17/3/7.
 */

public class MyContentProvider extends ContentProvider {
    public static String AUTHORITIES = "com.docwei.xmppdemo.db.MyContentProvider";
    private ContactSqliteOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new ContactSqliteOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    static               UriMatcher matcher     = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int        MATCHEDCODE = 1;

    static {
        matcher.addURI(AUTHORITIES, "accounts", MATCHEDCODE);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                long result = db.insert(ContactSqliteOpenHelper.TABLE, null, values);
                db.close();
                if (result != -1) {
                    //插入成功
                    System.out.println("插入成功------");
                    uri = ContentUris.withAppendedId(uri, result);
                    //null的话，是通知所有的
                    getContext().getContentResolver()
                                .notifyChange(uri, null);
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
                int result = db.delete(ContactSqliteOpenHelper.TABLE, selection, selectionArgs);
                db.close();
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
                int result = db.update(ContactSqliteOpenHelper.TABLE,
                                       values,
                                       selection,
                                       selectionArgs);
                
                if (result != -1) {
                    //更新成功
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

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder)
    {
        switch (matcher.match(uri)) {
            case MATCHEDCODE:
                SQLiteDatabase db = mHelper.getReadableDatabase();
                Cursor cursor = db.query(ContactSqliteOpenHelper.TABLE,
                                         null,
                                         selection,
                                         selectionArgs,
                                         null,
                                         null,
                                         sortOrder);
                if (cursor != null) {
                    return cursor;
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
