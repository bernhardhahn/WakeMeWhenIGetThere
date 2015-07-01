package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class ShowAlarmActivity extends Activity {

    private static final String TAG = ShowAlarmActivity.class.getName();
    public static final String ALARM_KEY = "alarm";

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_alarm);

        Intent intent = getIntent();
        if (intent.hasExtra(ALARM_KEY)) {
            Alarm alarm = intent.getParcelableExtra(ALARM_KEY);
            this.alarm = alarm;
            triggerAlarm();
        } else {
            Log.d(TAG, "ShowAlarmActivity launched without Alarm");
            finish();
        }

        Button dismissButton = (Button) findViewById(R.id.dismissAlarmButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void triggerAlarm() {

    }

}
