package cs160.lucina.onboarding;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.GregorianCalendar;

import cs160.lucina.DataLayer;
import cs160.lucina.R;
import cs160.lucina.SimpleDate;

/**
 * Created by M117 on 4/18/16.
 */
public class OnboardSignupActivity extends Activity {

    public static class Gender {
        public static final String M = "M";
        public static final String F = "F";
        public static final String STAR = "STAR";
    };
    private String genderSelected = Gender.STAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_signup);

        initializeStyle();

        final OnboardSignupActivity thisActivity = this;

        final TextView mButton = (TextView) findViewById(R.id.m_button);
        final TextView fButton = (TextView) findViewById(R.id.f_button);
        final TextView starButton = (TextView) findViewById(R.id.star_button);

        View.OnClickListener genderButtonOCL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView selectedGenderButton = (TextView) v;
                if (selectedGenderButton.getId() == R.id.m_button) {
                    genderSelected = Gender.M;
                }
                else if(selectedGenderButton.getId() == R.id.f_button) {
                    genderSelected = Gender.F;
                }
                else {
                    genderSelected = Gender.STAR;
                }
                mButton.setTextColor(ContextCompat.getColor(thisActivity, R.color.lightText));
                fButton.setTextColor(ContextCompat.getColor(thisActivity, R.color.lightText));
                starButton.setTextColor(ContextCompat.getColor(thisActivity, R.color.lightText));
                selectedGenderButton.setTextColor(ContextCompat.getColor(thisActivity, R.color.teal));
            }
        };

        mButton.setOnClickListener(genderButtonOCL);
        fButton.setOnClickListener(genderButtonOCL);
        starButton.setOnClickListener(genderButtonOCL);


        FrameLayout okButton = (FrameLayout) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFormCompleted()) {
                    DataLayer dataLayer = DataLayer.getInstance(thisActivity);

                    EditText nameET = (EditText) findViewById(R.id.name_input);
                    String name = nameET.getText().toString();
                    dataLayer.putChildName(name);
                    dataLayer.putChildGender(genderSelected);

                    Intent nextScreenIntent = new Intent(thisActivity, OnboardBirthdayActivity.class);
                    thisActivity.startActivity(nextScreenIntent);
                }
            }
        });
    }

    private void initializeStyle() {
        TextView welcomeToTV = (TextView) findViewById(R.id.welcome_to);
        TextView lucinaTV = (TextView) findViewById(R.id.lucina_title);

        Typeface blogger = Typeface.createFromAsset(getAssets(), "fonts/Blogger_Sans.ttf");
        welcomeToTV.setTypeface(blogger);
        lucinaTV.setTypeface(blogger);
    }

    private boolean checkFormCompleted() {
        return true;
    }
}