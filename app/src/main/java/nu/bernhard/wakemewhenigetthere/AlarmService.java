package nu.bernhard.wakemewhenigetthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class AlarmService extends NonStoppingIntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>,Alarms.AlarmsUpdateListener {

    private static final String ACTION_FOREGROUND = "nu.bernhard.wakemewhenigetthere.action.FOREGROUND";
    private static final String ACTION_ENTER_GEOFENCE = "nu.bernhard.wakemewhenigetthere.action.ENTER_GEOFENCE";

    private static final String EXTRA_FOREGROUND_VALUE = "nu.bernhard.wakemewhenigetthere.extra.FOREGROUND_VALUE";
    private static final String EXTRA_GEOFENCE_ID = "nu.bernhard.wakemewhenigetthere.extra.GEOFENCE_ID";
    private static final String TAG = "AlarmService";
    public static final String GEOFENCE_ID_PREFIX = "GeoAlarm";
    private IBinder binder  = new AlarmServiceBinder();
    private Alarms alarms = new Alarms();
    private GoogleApiClient googleApiClient;

    public static void setForeground(Context context, boolean foreground) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_FOREGROUND);
        intent.putExtra(EXTRA_FOREGROUND_VALUE, foreground);
        context.startService(intent);
    }

    public static void enterGeofence(Context context, String geofenceRequestId) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_ENTER_GEOFENCE);
        intent.putExtra(EXTRA_GEOFENCE_ID, geofenceRequestId);
        context.startService(intent);
    }

    public AlarmService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAlarms();
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public Alarms getAlarms() {
        return alarms;
    }

    @Override
    public void onAlarmsUpdate() {
        updateGeofences();
    }

    public class AlarmServiceBinder extends Binder {
        AlarmService getService() {
            return AlarmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOREGROUND.equals(action)) {
                final Boolean foreground = intent.getBooleanExtra(EXTRA_FOREGROUND_VALUE, false);
                handleActionForeground(foreground);
            } else if (ACTION_ENTER_GEOFENCE.equals(action)) {
                final String geofenceId = intent.getStringExtra(EXTRA_GEOFENCE_ID);
                handleActionEnterGeofence(geofenceId);
            }
        }
    }

    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
        updateGeofences();
    }

    private void handleActionEnterGeofence(String geofenceId) {
        try {
            Log.d(TAG, "handleActionEnterGeofence for geofenceId: " + geofenceId);
            Intent intent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
            Alarm alarm = getAlarmFromGeofenceId(geofenceId);
            //deactivate alarms to straight away to avoid re-triggering before
            //user reacts to the alarm.
            alarm.setActive(false);
            alarms.update(alarm);
            intent.putExtra(AlarmAlertActivity.ALARM_KEY, alarm);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "handleActionEnterGeofence found no alarm matching geofenceId=" + geofenceId);
        }
    }

    private Alarm getAlarmFromGeofenceId(String geofenceId) throws Exception {
        Integer id = Integer.parseInt(geofenceId.replace(GEOFENCE_ID_PREFIX, ""));
        return alarms.getById(id);
    }

    private void handleActionForeground(Boolean foreground) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("AlarmService");
        builder.setContentText("yolo!");
        builder.setSmallIcon(android.R.drawable.ic_media_play);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,  intent, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        if (foreground) {
            startForeground(1, notification);
        } else {
            stopForeground(true);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        updateGeofences();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
    }

    private void updateGeofences() {
        if (!googleApiClient.isConnected()) {
            //TODO: raise error
            Log.d(TAG, "addGeofences: googleApiClient is not connected");
            return;
        }

        try {
            if (alarms.hasActiveAlarms()) {
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            } else {
                Log.d(TAG, "updateGeofences: no active alarms: remove all geofences");
                LocationServices.GeofencingApi.removeGeofences(
                        googleApiClient,
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            }
        } catch (SecurityException securityException) {
            Log.e(TAG, "Invalid location permission. " +
                    "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(getLocations());
        return builder.build();
    }

    private List<Geofence> getLocations() {
        List<Geofence> locations = new ArrayList<>();
        for (Alarm alarm : alarms.getAll()) {
            if (alarm.isActive()) {
                locations.add(new Geofence.Builder()
                        .setRequestId(GEOFENCE_ID_PREFIX + alarm.getId())
                        .setCircularRegion(alarm.getLat(), alarm.getLon(), alarm.getRadius())
                        .setExpirationDuration(24 * 60 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }
        }
        return locations;
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getApplicationContext(),
                GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Geofence onResult: Success");
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(
                    getApplicationContext(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private void initAlarms() {
        String alarmJson = "{name: \"Test Alarm\", lon: 15.566608, lat: 58.412103, radius: 250, active: true, id: 1}";
        String alarmJson2 = "{name: \"Test Alarm22\", lon: 15.566608, lat: 58.412103, radius: 500, active: false, id: 2}";
        String alarmJson3 = "{name: \"Test Alarm333\", lon: 15.566608, lat: 58.412103, radius: 1250, active: false, id: -1}";

        alarms = new Alarms();
        Alarm alarm = new Alarm(alarmJson);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson2);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson3);
        alarms.add(alarm);
        alarms.addAlarmsUpdateListener(this);
    }

}
