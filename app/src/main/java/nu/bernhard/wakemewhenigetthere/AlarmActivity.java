package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class AlarmActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public static final String ALARM_KEY = "alarm";
    private static final String TAG = AlarmActivity.class.getName();

    private Button addAlarmButton;
    private EditText newAlarmNameInput;
    private EditText newAlarmLatInput;
    private EditText newAlarmLonInput;
    private Switch newAlarmActiveInput;
    private DiscreteSeekBar newAlarmRadiusSeekBar;
    private Alarm alarm;
    private GoogleMap map;
    private MapView mapView;

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
        newAlarmActiveInput = (Switch) findViewById(R.id.newAlarmActive);
        newAlarmRadiusSeekBar = (DiscreteSeekBar) findViewById(R.id.newAlarmRadiusSeekbar);
        newAlarmRadiusSeekBar.setValue(alarm.getRadius());
        newAlarmRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Integer radius = ((DiscreteSeekBar) seekBar).getValue();
                Log.d(TAG, "Radius: " + radius);
                alarm.setRadius(radius);
                setMapMarkerFromAlarm();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mapView = (MapView) findViewById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

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
        Integer radius = newAlarmRadiusSeekBar.getValue();
        Boolean active = newAlarmActiveInput.isChecked();
        alarm.setName(alarmName);
        alarm.setLat(lat);
        alarm.setLon(lon);
        alarm.setRadius(radius);
        alarm.setActive(active);
    }

    private void populateViewsFromAlarm() {
        newAlarmNameInput.setText(alarm.getName());
        setLatLonInputViews();
        newAlarmRadiusSeekBar.setValue(alarm.getRadius());
        newAlarmActiveInput.setChecked(alarm.isActive());
    }

    private void setLatLonInputViews() {
        newAlarmLatInput.setText(String.valueOf(alarm.getLat()));
        newAlarmLonInput.setText(String.valueOf(alarm.getLon()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        readUserInputToAlarm();
        outState.putParcelable(ALARM_KEY, alarm);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LatLng position = setMapMarkerFromAlarm();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        map.setOnMapClickListener(this);
    }

    private LatLng setMapMarkerFromAlarm() {
        clearMapMarkers();
        LatLng position = new LatLng(alarm.getLat(), alarm.getLon());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        map.addMarker(markerOptions);
        CircleOptions circleOptions = getCircleOptions(position, alarm.getRadius());
        map.addCircle(circleOptions);
        return position;
    }

    private void clearMapMarkers() {
        map.clear();
    }

    private CircleOptions getCircleOptions(LatLng position, Integer radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(position);
        circleOptions.radius(radius);
        circleOptions.fillColor(0x447EC386);
        circleOptions.strokeWidth(4f);
        circleOptions.strokeColor(0xFF7EC386);
        return circleOptions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        alarm.setLat(latLng.latitude);
        alarm.setLon(latLng.longitude);
        setLatLonInputViews();
        setMapMarkerFromAlarm();
    }

}
