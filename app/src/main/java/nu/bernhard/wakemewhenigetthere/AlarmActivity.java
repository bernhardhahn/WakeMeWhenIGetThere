package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;


public class AlarmActivity extends AppCompatActivity {
    public static final String ALARM_KEY = "alarm";
    private static final String TAG = AlarmActivity.class.getName();

    private Button addAlarmButton;
    private EditText newAlarmNameInput;
    private EditText newAlarmLatInput;
    private EditText newAlarmLonInput;
    private EditText newAlarmRadiusInput;
    private Switch newAlarmActiveInput;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        final Intent intent = getIntent();
        if (savedInstanceState != null) {
            this.alarm = savedInstanceState.getParcelable(ALARM_KEY);
        } else if (intent.hasExtra(ALARM_KEY)) {
            Log.d(TAG, "has extra");
            this.alarm = intent.getParcelableExtra(ALARM_KEY);
            Log.d(TAG, "Alarm: " + alarm.getName());
        } else {
            Log.d(TAG, "no extra :((");
            this.alarm = new Alarm();
        }

        addAlarmButton = (Button) findViewById(R.id.addAlarmButton);
        newAlarmNameInput = (EditText) findViewById(R.id.newAlarmName);
        newAlarmLatInput = (EditText) findViewById(R.id.newAlarmLat);
        newAlarmLonInput = (EditText) findViewById(R.id.newAlarmLon);
        newAlarmRadiusInput = (EditText) findViewById(R.id.newAlarmRadius);
        newAlarmActiveInput = (Switch) findViewById(R.id.newAlarmActive);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readUserInputToAlarm();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ALARM_KEY, alarm);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        populateViewsFromAlarm();

    }

    private void readUserInputToAlarm() {
        String alarmName = newAlarmNameInput.getText().toString();
        Double lat = Double.parseDouble(newAlarmLatInput.getText().toString());
        Double lon = Double.parseDouble(newAlarmLonInput.getText().toString());
        Integer radius = Integer.parseInt((newAlarmRadiusInput.getText().toString()));
        Boolean active = newAlarmActiveInput.isChecked();
        alarm.setName(alarmName);
        alarm.setLat(lat);
        alarm.setLon(lon);
        alarm.setRadius(radius);
        alarm.setActive(active);
    }

    private void populateViewsFromAlarm() {
        newAlarmNameInput.setText(alarm.getName());
        newAlarmLatInput.setText(String.valueOf(alarm.getLat()));
        newAlarmLonInput.setText(String.valueOf(alarm.getLon()));
        newAlarmRadiusInput.setText(String.valueOf(alarm.getRadius()));
        newAlarmActiveInput.setChecked(alarm.isActive());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        readUserInputToAlarm();
        outState.putParcelable(ALARM_KEY, alarm);
        super.onSaveInstanceState(outState);
    }

}
