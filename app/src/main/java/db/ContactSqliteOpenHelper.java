package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tobo on 17/3/7.
 */

public class ContactSqliteOpenHelper extends SQLiteOpenHelper{
    public ContactSqliteOpenHelper(Context context) {
        super(context, "user.db", null, 1);
    }
    public static final String TABLE="accounts";
    public static final String _ID="_id";
    public static final String USERNAME="username";
    public static final String NICKNAME="nikename";
    public static final String PINTYIN="pingying";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql="CREATE TABLE "+TABLE+" ("+_ID+" integer primary key autoincrement,"
                +USERNAME + " TEXT ,"
                +NICKNAME + " TEXT ,"
                +PINTYIN + " TEXT )";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
