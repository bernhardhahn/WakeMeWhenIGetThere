package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import  android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * The AlarmActivity shows a single Alarm to the user
 * and lets the user change the settings of the Alarm.
 *
 * The Activity should be launched with an Intent containing
 * an Alarm set with the "ALARM_KEY"
 * If the Activity is launched without an Alarm a new Alarm
 * will be created.
 *
 * When the user presses the "Save" button the activity will
 * return a updated version of the alarm as a Result with
 * the key "ALARM_KEY"
 */
public class AlarmActivity extends VisibleActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    // TAG for logging
    private static final String TAG = AlarmActivity.class.getName();

    // Key used in Intents and Parcels to indicate an Alarm
    public static final String ALARM_KEY = "alarm";

    private Alarm alarm;
    private EditText alarmNameInput;
    private SwitchCompat alarmIsActiveInput;
    private DiscreteSeekBar alarmRadiusSeekBar;
    private MapView mapView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        final Intent intent = getIntent();
        if (savedInstanceState != null) {
            this.alarm = savedInstanceState.getParcelable(ALARM_KEY);
            //We must redirect saveInstanceState to mapView
            //but there is a bug in google maps, and it will
            //try to unbundle our parcelable but will fail
            //since it does not have the classloader.
            //Therefore s why we must remove our parcelable object
            //to avoid google maps from throwing an exception.
            savedInstanceState.remove(ALARM_KEY);
        } else if (intent.hasExtra(ALARM_KEY)) {
            this.alarm = intent.getParcelableExtra(ALARM_KEY);
            Log.d(TAG, "Alarm Activity created with Alarm: " + alarm.getName());
        } else {
            this.alarm = new Alarm();
        }

        alarmNameInput = (EditText) findViewById(R.id.alarmName);
        setupIsAlarmActiveInput();
        setupAlarmRadiusSeekBar();
        setupMapView(savedInstanceState);
        setCallbackForSaveButton();
        populateViewsFromAlarm();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * onMapReady will be called when google maps has loaded
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        setMapUiControls();
        setMapMarkerFromAlarm();
        LatLng position = getLatLngFromAlarm();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
    }

    private void setMapUiControls() {
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
    }

    private void setupIsAlarmActiveInput() {
        alarmIsActiveInput = (SwitchCompat) findViewById(R.id.alarmActive);
        alarmIsActiveInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.setActive(b);
                setMapMarkerFromAlarm();
            }
        });
    }

    private void setupAlarmRadiusSeekBar() {
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
        setRadiusLabel(alarm.getRadius());
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
    }

    private void setCallbackForSaveButton() {
        Button saveAlarmButton = (Button) findViewById(R.id.saveAlarmButton);
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
    }

    private void setRadiusLabel(Integer radiusInMetres) {
        TextView radiusLabel = (TextView) findViewById(R.id.alarmRadiusLabel);
        radiusLabel.setText(StringFormatter.radiusStringFormatter(this, radiusInMetres));
    }

    private void readUserInputToAlarm() {
        String alarmName = alarmNameInput.getText().toString();
        Integer radius = alarmRadiusSeekBar.getValue();
        Boolean active = alarmIsActiveInput.isChecked();
        alarm.setName(alarmName);
        alarm.setRadius(radius);
        alarm.setActive(active);
    }

    /**
     * Read values from Alarm and display in corresponding Views.
     */
    private void populateViewsFromAlarm() {
        alarmNameInput.setText(alarm.getName());
        alarmRadiusSeekBar.setValue(alarm.getRadius());
        alarmIsActiveInput.setChecked(alarm.isActive());
    }

    private void setMapMarkerFromAlarm() {
        //if map is not yet ready, don't try to set markers.
        if (map == null) return ;

        LatLng position = getLatLngFromAlarm();
        clearMapMarkers();
        addMarkerToMap(position);
        addRadiusCircleToMap(position);
    }

    private void addRadiusCircleToMap(LatLng position) {
        Integer imageOverlayResourceId = alarm.isActive() ?
                R.drawable.map_circle_active :
                R.drawable.map_circle_inactive;
        map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(imageOverlayResourceId))
                .anchor(0.5f, 0.5f) //center of image
                .position(position, alarm.getRadius() * 2));
    }

    private void addMarkerToMap(LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        map.addMarker(markerOptions);
    }

    private LatLng getLatLngFromAlarm() {
        return new LatLng(alarm.getLat(), alarm.getLon());
    }

    private void clearMapMarkers() {
        map.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //onSaveInstanceState must be redirected to MapView
        mapView.onSaveInstanceState(outState);
        readUserInputToAlarm();
        outState.putParcelable(ALARM_KEY, alarm);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // onDestroy must be redirected to MapView
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onResume must be redirected to MapView
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // onPause must be redirected to MapView
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // onLowMemory must be redirected to MapView
        mapView.onLowMemory();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        alarm.setLat(latLng.latitude);
        alarm.setLon(latLng.longitude);
        setMapMarkerFromAlarm();
    }

}
