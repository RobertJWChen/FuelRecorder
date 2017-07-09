package studio.robert.fuelrecorder.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import studio.robert.fuelrecorder.data.source.local.Contract.*;

/**
 * Created by robert on 2017/5/24.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "historydb";
    public static final int DB_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String REAL_TYPE = "REAL";
    private static final String COMMA_SEP = ",";

    private static final String CREATE_TABLE = "CREATE TABLE "+ RecordEntry.TABLE_NAME+" ("+
            RecordEntry._ID+TEXT_TYPE+" PRIMARY KEY,"+
            RecordEntry.COL_NAME_ENTRY_ID+TEXT_TYPE+COMMA_SEP+
            RecordEntry.COL_DATE + INTEGER_TYPE +" DEFAULT CURRENT_TIMESTAMP,"+
            RecordEntry.COL_AMOUNT + REAL_TYPE+COMMA_SEP+
            RecordEntry.COL_PRICE + REAL_TYPE+COMMA_SEP+
            RecordEntry.COL_MILEAGE + REAL_TYPE+COMMA_SEP+
            RecordEntry.COL_NOTE+TEXT_TYPE+
            ")";

    private static final String DESTROY_TABLE = "DROP TABLE IF EXIST "+RecordEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //none
    }
}
