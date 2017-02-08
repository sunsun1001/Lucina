package cs160.lucina.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cs160.lucina.R;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final EditProfileActivity thisActivity = this;

        View screen = findViewById(R.id.edit_profile);
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}
