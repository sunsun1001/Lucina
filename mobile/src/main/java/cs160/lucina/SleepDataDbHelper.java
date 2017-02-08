package cs160.lucina;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jianqiao on 4/26/16.
 */
public class SleepDataDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SleepData.db";

    public SleepDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SleepDataContract.SleepData.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SleepDataContract.SleepData.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertSleepDuration(SQLiteDatabase db, int year, int month, int day, float duration) {
        ContentValues values = new ContentValues();
        values.put(SleepDataContract.SleepData.COLUMN_NAME_YEAR, year);
        values.put(SleepDataContract.SleepData.COLUMN_NAME_MONTH, month);
        values.put(SleepDataContract.SleepData.COLUMN_NAME_DAY, day);
        values.put(SleepDataContract.SleepData.COLUMN_NAME_DURATION, duration);

        db.insert(SleepDataContract.SleepData.TABLE_NAME, null, values);
    }

    public float[] getSleepDurationForDay(SQLiteDatabase db, int year, int month, int day) {
        String[] projection = {
                SleepDataContract.SleepData.COLUMN_NAME_DURATION
        };

        String selection =
                SleepDataContract.SleepData.COLUMN_NAME_YEAR + "=? AND " +
                SleepDataContract.SleepData.COLUMN_NAME_MONTH + "=? AND " +
                SleepDataContract.SleepData.COLUMN_NAME_DAY + "=?";

        String[] selectionVals = {
                Integer.toString(year),
                Integer.toString(month),
                Integer.toString(day)
        };

        Cursor c = db.query(SleepDataContract.SleepData.TABLE_NAME,
                projection, selection, selectionVals, null, null, null);

        System.out.println(DatabaseUtils.dumpCursorToString(c));


        c.moveToFirst();
        int duration_index = c.getColumnIndexOrThrow(SleepDataContract.SleepData.COLUMN_NAME_DURATION);
        float[] durations = new float[c.getCount()];

        int i = 0;
        while (!c.isAfterLast()) {
            durations[i] = c.getFloat(duration_index);
            c.moveToNext();
            i += 1;
        }

        return durations;

    }
}
