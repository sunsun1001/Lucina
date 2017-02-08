package cs160.lucina;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import cs160.lucina.onboarding.OnboardSignupActivity;
import cs160.lucina.onboarding.OnboardSignupActivity.Gender;

/**
 * Created by Brian on 4/18/16.
 */
public class DataLayer {

    private static DataLayer instance;
    private SharedPreferences sharedPreferences;
    public SleepDataDbHelper dbHelper;
    public SQLiteDatabase sleepDataDb;

    public static DataLayer getInstance(Context context) {
        if (instance == null) {
            instance = new DataLayer(context);
            instance.dbHelper = new SleepDataDbHelper(context);
            instance.sleepDataDb = instance.dbHelper.getWritableDatabase();
        }
        return instance;
    }

    DataLayer(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_name), Context.MODE_PRIVATE);
    }

    public boolean isFirstOpen() {
        return sharedPreferences.getBoolean("IS_FIRST_OPEN", true);
    }

    public void finishedFirstOpen() {
        sharedPreferences.edit().putBoolean("IS_FIRST_OPEN", false).apply();
    }

    public void putChildName(String firstName) {
        sharedPreferences.edit().putString("child_name", firstName)
                .apply();
    }


    public void putChildGender(String gender) {
        sharedPreferences.edit().putString("child_gender", gender)
                .apply();
    }

    public void putChildBirthday(SimpleDate birthday) {
        sharedPreferences.edit()
                .putInt("child_birthday_year", birthday.getYear())
                .putInt("child_birthday_month", birthday.getMonth())
                .putInt("child_birthday_day", birthday.getDay())
                .apply();
    }

    public String getChildName() {
        return sharedPreferences.getString("child_name", "CHILD_NOT_FOUND");
    }

    public String getChildGender() {
        return sharedPreferences.getString("child_gender", "GENDER_NOT_FOUND");
    }

    public SimpleDate getChildBirthday() {
        return new SimpleDate(
                sharedPreferences.getInt("child_birthday_year", 0),
                sharedPreferences.getInt("child_birthday_month", 0),
                sharedPreferences.getInt("child_birthday_day", 0));
    }

    // TODO
    public int getChildAgeInMonths() {
        return 10;
    }

    // TODO
    public int[] getLastRecording() {
        return new int[] {};
    }

    public void setCribModeStart(long startTimestamp) {
        sharedPreferences.edit()
                .putBoolean("crib_mode_started", true)
                .putLong("crib_mode_startTimestamp", startTimestamp)
                .apply();
    }

    public void setCribModeStop() {
        sharedPreferences.edit()
                .putBoolean("crib_mode_started", false)
                .putLong("crib_mode_startTimestamp", -1)
                .apply();
    }

    public void setLastAwakeTime(long wakeTimestamp) {
        sharedPreferences.edit()
                .putLong("crib_mode_last_awake", wakeTimestamp)
                .apply();
    }

    public long getCribModeStartTime() {
        return sharedPreferences.getLong("crib_mode_startTimestamp", -1);
    }

    public long getLastAwakeTime() {
        return sharedPreferences.getLong("crib_mode_last_awake", -1);
    }

}
