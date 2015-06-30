package nu.bernhard.wakemewhenigetthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AlarmActivity extends AppCompatActivity {

    private Button addAlarmButton;
    private EditText newAlarmNameInput;
    private EditText newAlarmLatInput;
    private EditText newAlarmLonInput;
    private EditText newAlarmRadiusInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        addAlarmButton = (Button) findViewById(R.id.addAlarmButton);
        newAlarmNameInput = (EditText) findViewById(R.id.newAlarmName);
        newAlarmLatInput = (EditText) findViewById(R.id.newAlarmLat);
        newAlarmLonInput = (EditText) findViewById(R.id.newAlarmLon);
        newAlarmRadiusInput = (EditText) findViewById(R.id.newAlarmRadius);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alarmName = newAlarmNameInput.getText().toString();
                Double lat = Double.parseDouble(newAlarmLatInput.getText().toString());
                Double lon = Double.parseDouble(newAlarmLonInput.getText().toString());
                Integer radius = Integer.parseInt((newAlarmRadiusInput.getText().toString()));

                Alarm alarm = new Alarm(alarmName, lon, lat, radius, true);

                //if (serviceBound) {
                //    alarmService.addAlarm(alarm);
                //    alarmsAdapter.notifyDataSetChanged();
                //}

            }
        });

    }

}
