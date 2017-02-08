package cs160.lucina;

import android.provider.BaseColumns;

/**
 * Created by jianqiao on 4/26/16.
 */
public final class SleepDataContract {

    public SleepDataContract () {}

    public static abstract class SleepData implements BaseColumns {
        public static final String TABLE_NAME = "sleep_data";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_DURATION = "duration";


        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SleepData.TABLE_NAME + " (" +
                        SleepData._ID + " INTEGER PRIMARY KEY," +
                        SleepData.COLUMN_NAME_YEAR + INT_TYPE + COMMA_SEP +
                        SleepData.COLUMN_NAME_MONTH + INT_TYPE + COMMA_SEP +
                        SleepData.COLUMN_NAME_DAY + INT_TYPE + COMMA_SEP +
                        SleepData.COLUMN_NAME_DURATION + " REAL" +
                " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SleepData.TABLE_NAME;
    }
}
