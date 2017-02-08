package cs160.lucina.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import cs160.lucina.DataLayer;
import cs160.lucina.R;
import cs160.lucina.SimpleDate;

public class OnboardBirthdayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_birthday);

        FrameLayout button = (FrameLayout) findViewById(R.id.ok_button);
        final Activity thisActivity = this;
        final DataLayer dataLayer = DataLayer.getInstance(thisActivity);

        String name = dataLayer.getChildName();

        TextView topText = (TextView) findViewById(R.id.top_text);
        topText.setText("The way infants move in their sleep changes as they get older. \n \nEnter " + name + "'s birthday to help the Crib Monitor work better.");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker birthdayPicker = (DatePicker) findViewById(R.id.birthday_picker);
                SimpleDate birthday = new SimpleDate(
                        birthdayPicker.getYear(),
                        birthdayPicker.getMonth(),
                        birthdayPicker.getDayOfMonth());
                dataLayer.putChildBirthday(birthday);
                Intent nextScreenIntent = new Intent(thisActivity, OnboardGuide1Activity.class);
                thisActivity.startActivity(nextScreenIntent);
            }
        });
    }
}
