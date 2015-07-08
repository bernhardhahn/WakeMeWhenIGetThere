package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import  android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class AlarmActivity extends VisibleActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public static final String ALARM_KEY = "alarm";
    private static final String TAG = AlarmActivity.class.getName();

    private EditText alarmNameInput;
    private SwitchCompat alarmIsActiveInput;
    private DiscreteSeekBar alarmRadiusSeekBar;
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
            //We must redirect saveInstanceState to mapView
            //but there is a bug in google map, and it will
            //try to unbundle our parcelable but will fail
            //since it does not have the classloader.
            //That's why we must remove our parcelable.
            savedInstanceState.remove(ALARM_KEY);
        } else if (intent.hasExtra(ALARM_KEY)) {
            Log.d(TAG, "has extra");
            this.alarm = intent.getParcelableExtra(ALARM_KEY);
            Log.d(TAG, "Alarm: " + alarm.getName());
        } else {
            Log.d(TAG, "no extra :((");
            this.alarm = new Alarm();
        }

        Button saveAlarmButton = (Button) findViewById(R.id.saveAlarmButton);
        alarmNameInput = (EditText) findViewById(R.id.alarmName);
        alarmIsActiveInput = (SwitchCompat) findViewById(R.id.alarmActive);
        alarmRadiusSeekBar = (DiscreteSeekBar) findViewById(R.id.alarmRadiusSeekbar);
        alarmRadiusSeekBar.setValue(alarm.getRadius());
        alarmRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Integer radius = ((DiscreteSeekBar) seekBar).getValue();
                Log.d(TAG, "Radius: " + radius);
                alarm.setRadius(radius);
                setRadiusLabel(radius);
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

        saveAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readUserInputToAlarm();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ALARM_KEY, alarm);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        populateViewsFromAlarm();
        setRadiusLabel(alarm.getRadius());
    }

    private void setRadiusLabel(Integer radiusInMetres) {
        TextView radiusLabel = (TextView) findViewById(R.id.alarmRadiusLabel);
        String unit = radiusInMetres >= 1000 ? "km" : "m";
        int radius = radiusInMetres >= 1000 ? radiusInMetres / 1000 : radiusInMetres;
        radiusLabel.setText(getString(R.string.radius_title, radius, unit));
    }

    private void readUserInputToAlarm() {
        String alarmName = alarmNameInput.getText().toString();
        Integer radius = alarmRadiusSeekBar.getValue();
        Boolean active = alarmIsActiveInput.isChecked();
        alarm.setName(alarmName);
        alarm.setRadius(radius);
        alarm.setActive(active);
    }

    private void populateViewsFromAlarm() {
        alarmNameInput.setText(alarm.getName());
        alarmRadiusSeekBar.setValue(alarm.getRadius());
        alarmIsActiveInput.setChecked(alarm.isActive());
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
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        LatLng position = setMapMarkerFromAlarm();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        map.setMyLocationEnabled(true);
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
        setMapMarkerFromAlarm();
    }

}
