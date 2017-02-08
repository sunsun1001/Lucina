package cs160.lucina.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import cs160.lucina.DataLayer;
import cs160.lucina.HomeActivity;
import cs160.lucina.R;
import cs160.lucina.SimpleDate;
import cs160.lucina.onboarding.OnboardSignupActivity.Gender;

/**
 * Created by M117 on 4/18/16.
 */
public class OnboardGuide1Activity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_guide_1);

        final OnboardGuide1Activity thisActivity = this;

        final DataLayer dataLayer = DataLayer.getInstance(thisActivity);
        String name = dataLayer.getChildName();
        String gender = dataLayer.getChildGender();
        String genderPro;
        if (gender.equals("M")) {
            genderPro = "he";
        } else if (gender.equals("F")) {
            genderPro = "she";
        } else {
            genderPro = "they";
        }

        TextView text1 = (TextView) findViewById(R.id.text_1);
        TextView text2 = (TextView) findViewById(R.id.text_2);
        TextView text3 = (TextView) findViewById(R.id.text_3);

        text2.setText("I help you keep watch of " + name + " by using your smartwatch's microphone to listen for crying, and its accelerometer to measure any movement.");
        text3.setText("Use Crib Mode to get notifications when " + name + " wakes up at night. If " + genderPro + " starts to cry, I'll let you know.");

        FrameLayout okButton = (FrameLayout) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataLayer.finishedFirstOpen();
                Intent nextScreenIntent = new Intent(thisActivity, HomeActivity.class);
                nextScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                nextScreenIntent.putExtra("FROM_ONBOARD", true);
                thisActivity.startActivity(nextScreenIntent);
            }
        });
    }

}
