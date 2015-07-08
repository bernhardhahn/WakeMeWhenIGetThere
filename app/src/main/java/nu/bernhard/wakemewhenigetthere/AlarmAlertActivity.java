package nu.bernhard.wakemewhenigetthere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class AlarmAlertActivity extends VisibleActivity {

    private static final String TAG = AlarmAlertActivity.class.getName();
    public static final String CLOSE_ACTIVITY = "CLOSE_ACTIVITY";
    private static final String ALARM_KEY = "ALARM";
    private Alarm alarm;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlarmAlertActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                //special flag to let windows be shown when the screen is locked.
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                //when set the window will cause the keyguard to be dismissed,
                // only if it is not a secure lock keyguard.
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                //as long as this window is visible to the user, keep the
                // device's screen turned on and bright.
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                //when set as a window is being added or made visible,
                // once the window has been shown then the system will
                // poke the power manager's user activity (as if the
                // user had woken up the device) to turn the screen on.
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_alert);

        if (savedInstanceState != null && savedInstanceState.containsKey(ALARM_KEY)) {
            alarm = savedInstanceState.getParcelable(ALARM_KEY);
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(AlarmAlertService.ALARM_KEY)) {
                Alarm alarm = intent.getParcelableExtra(AlarmAlertService.ALARM_KEY);
                this.alarm = alarm;
            } else {
                Log.d(TAG, "ShowAlarmActivity launched without Alarm");
                finish();
            }
        }

        Button dismissButton = (Button) findViewById(R.id.dismissAlarmButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmAlertService.class);
                intent.setAction(AlarmAlertService.STOP_ALARM);
                startService(intent);

            }
        });

        TextView locationName = (TextView) findViewById(R.id.nowEnteringLocationName);
        locationName.setText(alarm.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(CLOSE_ACTIVITY),
                Manifest.permission.PRIVATE, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ALARM_KEY, alarm);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // Ignore back button. We don't want the user to accidentally  dismiss the alarm
        return;
    }

}
