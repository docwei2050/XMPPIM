package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static db.ContactSqliteOpenHelper.PINTYIN;

/**
 * Created by tobo on 17/3/7.
 */

public class SmsSqliteOpenHelper extends SQLiteOpenHelper{
    public SmsSqliteOpenHelper(Context context) {
        super(context, "sms.db", null, 1);
    }
    public static final String TABLE="sms";
    public static final String _ID="_id";
    public static final String FROMACCOUNT="from_account";
    public static final String TOACCOUNT="to_account";
    public static final String BODY="body";
    public static final String STATUS="status";
    public static final String TYPE="type";
    public static final String TIME="time";
    public static final String SESSIONACCOUNT="session_account";
    //登录的用户和谁聊天，谁就是会话者


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql="CREATE TABLE "+TABLE+" ("+_ID+" integer primary key autoincrement,"
                +FROMACCOUNT + " TEXT ,"
                +TOACCOUNT + " TEXT ,"
                + BODY + " TEXT ,"
                +STATUS + " TEXT ,"
                +TYPE + " TEXT ,"
                +TIME + " TEXT ,"
                +SESSIONACCOUNT + " TEXT ,"
                +PINTYIN + " TEXT )";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
