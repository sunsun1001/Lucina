package cs160.lucina;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXIndexComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class AllDataActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);

        final AllDataActivity thisActivity = this;
        Resources res = getResources();
        DataLayer dLayer = DataLayer.getInstance(getApplicationContext());
        SleepDataDbHelper dbHelper = dLayer.dbHelper;
        SQLiteDatabase sleepDb = dLayer.sleepDataDb;

        Typeface blogger = Typeface.createFromAsset(getAssets(), "fonts/Blogger_Sans.ttf");

        // TODO: get child's name
        String childName = dLayer.getChildName();

        // set attributes for UI elements
        TextView title = (TextView) findViewById(R.id.all_data_header);
        TextView avgSleepDuration = (TextView) findViewById(R.id.avg_sleep_duration);
        title.setText(childName + "\'s Data");
        title.setTypeface(blogger);
        title.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        // show button if came from home screen
        Intent intent = getIntent();
        FrameLayout homeButton = (FrameLayout) findViewById(R.id.home_button);
        if (intent != null && intent.getBooleanExtra("FROM_CRIB", false)) {
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextScreenIntent = new Intent(thisActivity, HomeActivity.class);
                    startActivity(nextScreenIntent);
                }
            });
        }
        else {
            homeButton.setVisibility(View.INVISIBLE);
        }

        LineChart sleepDurationChart = (LineChart) findViewById(R.id.avg_sleep_duration_chart);
        sleepDurationChart.setDescription(null);
        sleepDurationChart.setScaleEnabled(false);
        sleepDurationChart.setBackgroundColor(res.getColor(R.color.darkBlue));

        XAxis xAxis = sleepDurationChart.getXAxis();
        YAxis yAxisLeft = sleepDurationChart.getAxisLeft();
        yAxisLeft.setDrawLabels(false); // no axis labels
        yAxisLeft.setDrawAxisLine(false); // no axis line
        yAxisLeft.setDrawGridLines(true); // no grid lines
        yAxisLeft.setDrawZeroLine(true); // draw a zero line
        yAxisLeft.setZeroLineColor(res.getColor(R.color.lightText));
        yAxisLeft.setGridColor(res.getColor(R.color.lightText));
        sleepDurationChart.getAxisRight().setEnabled(false); // no right axis

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setTextColor(res.getColor(R.color.lightText));
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(res.getColor(R.color.lightText));


        // setting fake data into db
        // TODO: remove later when real data coming in

        dbHelper.insertSleepDuration(sleepDb, 2016, 4, 29, 4.5f);
        dbHelper.insertSleepDuration(sleepDb, 2016, 4, 30, 3.5f);
        dbHelper.insertSleepDuration(sleepDb, 2016, 5, 1, 7.5f);
        dbHelper.insertSleepDuration(sleepDb, 2016, 5, 2, 2.5f);
        dbHelper.insertSleepDuration(sleepDb, 2016, 5, 3, 10.5f);

        // bind data to graph
        Calendar cal = Calendar.getInstance();
        int today, thisMonth, thisYear;
        int PAST_DAYS = 7;
        float past7DayTotalSleepDuration = 0.0f;
        int past7DatTotalSleep = 0;
        ArrayList<Entry> past7DayAvgSleepDuration = new ArrayList<Entry>();
        String[] xAxisLabels = new String[PAST_DAYS];

        for (int i = 0; i < PAST_DAYS; i++) {
            today = cal.get(Calendar.DAY_OF_MONTH);
            thisMonth = cal.get(Calendar.MONTH) + 1; //java month starts with 0!
            thisYear = cal.get(Calendar.YEAR);

            xAxisLabels[6-i] = Integer.toString(thisMonth) + "/" + Integer.toString(today);

            float[] durations = dbHelper.getSleepDurationForDay(sleepDb, thisYear, thisMonth, today);
            float sum = 0.0f;
            for (int j = 0; j < durations.length; j++) {
                sum += durations[j];
                past7DayTotalSleepDuration += durations[j];
                past7DatTotalSleep += 1;
            }

            if (durations.length == 0) {
                past7DayAvgSleepDuration.add(new Entry(0.0f, 6 - i));
            } else {
                past7DayAvgSleepDuration.add(new Entry(sum / durations.length, 6 - i));
            }

            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        avgSleepDuration.setText(String.format("%.2f", past7DayTotalSleepDuration / past7DatTotalSleep)  + " hours");

        LineDataSet sleepDurationDataSet = new LineDataSet(past7DayAvgSleepDuration, null);
        sleepDurationDataSet.setValueTextColor(res.getColor(R.color.lightText));
        sleepDurationDataSet.setValueTextSize(10f);

        LineData sleepDurationData = new LineData(Arrays.asList(xAxisLabels),
                sleepDurationDataSet);
        sleepDurationChart.setData(sleepDurationData);
        sleepDurationChart.invalidate();
    }
}
