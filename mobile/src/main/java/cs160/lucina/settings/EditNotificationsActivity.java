package cs160.lucina.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import cs160.lucina.R;

/**
 * Created by M117 on 4/18/16.
 */
public class EditNotificationsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnotifications);

        final EditNotificationsActivity thisActivity = this;

        View screen = findViewById(R.id.edit_notifications);
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }

}
