package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class AlarmAlertActivity extends Activity {

    private static final String TAG = AlarmAlertActivity.class.getName();
    public static final String ALARM_KEY = "alarm";

    private Alarm alarm;
    private boolean serviceBound;
    private AlarmService alarmService;
    private ServiceConnection alarmServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AlarmService.AlarmServiceBinder binder = (AlarmService.AlarmServiceBinder) service;
            alarmService = binder.getService();
            serviceBound = true;
            Log.d(TAG, "onServiceConnected: " + className.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
            Log.d(TAG, "onServiceConnected: " + className.toString());
        }
    };;

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
                stopAlarm();
                if (serviceBound) {
                    alarm.setActive(false);
                    alarmService.getAlarms().update(alarm);
                }

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, alarmServiceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(alarmServiceConnection);

    }
    private void stopAlarm() {

    }

    private void triggerAlarm() {

    }

}
