package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AlarmService is the main Service of the application.
 */
public class AlarmService extends NonStoppingIntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>,Alarms.AlarmsUpdateListener {

    public static final String ACTION_SHOW_NOTIFICATION = "WMWIGT.action.SHOW_NOTIFICATION";

    private static final String TAG = "AlarmService";
    private static final String ACTION_UPDATE_NOTIFICATION = "WMWIGT.action.FOREGROUND";
    private static final String ACTION_ENTER_GEOFENCE = "WMWIGT.action.ENTER_GEOFENCE";
    private static final String EXTRA_GEOFENCE_ID = "WMWIGT.extra.GEOFENCE_ID";
    private static final String GEOFENCE_ID_PREFIX = "GeoAlarm";
    private static final String ALARMS_FILE_NAME = "alarms.data";
    private static final String NOTIFICATION_KEY = "NOTIFICATION";

    private IBinder binder  = new AlarmServiceBinder();
    private Alarms alarms = new Alarms();
    private GoogleApiClient googleApiClient;

    /**
     * Tell AlarmService to update notification
     * Calling this static method well start AlarmService with action
     * ACTION_UPDATE_NOTIFICATION
     *
     * Notifications are only shown when no Activities are visible.
     * This method is called when Activities are created or destroyed.
     *
     * @param context
     */
    public static void updateNotification(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_UPDATE_NOTIFICATION);
        context.startService(intent);
    }

    /**
     * Tell AlarmService that the user has entered a Geofence
     * Calling this static method well start AlarmService with action
     * ACTION_UPDATE_GEOFENCE
     *
     * @param context
     * @param geofenceRequestId id of the geofence entered
     */
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
        loadAlarms();
        alarms.addAlarmsUpdateListener(this);
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        saveAlarms();
        super.onDestroy();
    }

    public Alarms getAlarms() {
        return alarms;
    }

    /**
     * Callback triggered when Alarms is updated
     * When Alarms are updated Geofences must be
     * updated.
     */
    @Override
    public void onAlarmsUpdate() {
        updateGeofences();
        saveAlarms();
    }

    /**
     * Handle intents sent to AlarmService with startService()
     *
     * @param intent The value passed to NonStoppingIntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_NOTIFICATION.equals(action)) {
                handleActionUpdateNotification();
            } else if (ACTION_ENTER_GEOFENCE.equals(action)) {
                final String geofenceId = intent.getStringExtra(EXTRA_GEOFENCE_ID);
                handleActionEnterGeofence(geofenceId);
            }
        }
    }

    /**
     * Handle enter geofence action
     * This will start the AlarmAlertService
     *
     * @param geofenceId id of geofence entered
     */
    private void handleActionEnterGeofence(String geofenceId) {
        try {
            Log.d(TAG, "handleActionEnterGeofence for geofenceId: " + geofenceId);
            Intent intent = new Intent(getApplicationContext(), AlarmAlertService.class);
            Alarm alarm = getAlarmFromGeofenceId(geofenceId);
            //deactivate alarms to straight away to avoid re-triggering before
            //user reacts to the alarm.
            alarm.setActive(false);
            alarms.update(alarm);
            intent.putExtra(AlarmAlertService.ALARM_KEY, alarm);
            intent.setAction(AlarmAlertService.START_ALARM);
            startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "handleActionEnterGeofence found no alarm matching geofenceId=" + geofenceId);
        }
    }

    /**
     * Map an id received from GeofenceService to an alarm
     * @param geofenceId
     * @return  the Alarm matching the geofence id
     * @throws Exception
     */
    private Alarm getAlarmFromGeofenceId(String geofenceId) throws Exception {
        Integer id = Integer.parseInt(geofenceId.replace(GEOFENCE_ID_PREFIX, ""));
        return alarms.getById(id);
    }

    /**
     * Handle update notification action
     *
     * This will show a notification if active alarms
     * are available and no activity is visible
     *
     */
    private void handleActionUpdateNotification() {
        if (!alarms.hasActiveAlarms()) {
            Log.d(TAG, "no active alarms... don't show notification");
            stopForeground(true);
            return;
        }

        Notification notification = createNotification();

        Intent broadcastIntent = new Intent(ACTION_SHOW_NOTIFICATION);
        broadcastIntent.putExtra(NOTIFICATION_KEY, notification);
        sendOrderedBroadcast(broadcastIntent, null,
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d(TAG, "broadcastReceiver got message: " + intent.getAction());
                        Log.d(TAG, "broadcastReceiver result code: " + getResultCode());
                        //if no activity has canceled the broadcast go ahead and show the
                        //notification.
                        if (getResultCode() == Activity.RESULT_OK) {
                            Notification notification = intent.getParcelableExtra(NOTIFICATION_KEY);
                            startForeground(1, notification);
                        } else {
                            stopForeground(true);
                        }
                    }
                }, null, Activity.RESULT_OK, null, null);
    }

    /**
     * Create a notification to show which Alarms are active
     * @return a Notification showing active Alarms
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(getString(R.string.notification_content_title));
        builder.setContentText(createNotificationString());
        builder.setSmallIcon(R.drawable.notification_icon);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_large_icon);
        builder.setLargeIcon(largeIcon);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    /**
     * Returns a string to show in the notification field
     * The string contains either the name of the active alarm
     * or if more than one alarm is active, the number of
     * active alarms
     * @return message
     */
    private String createNotificationString() {
        String contentText = "";
        int activeAlarmsCount = alarms.getActiveAlarmCount();
        if (activeAlarmsCount > 1) {
            contentText = getApplicationContext().getString(
                    R.string.notification_text_alarm_count, activeAlarmsCount);
        } else {
            for (Alarm alarm : alarms.getAll()) {
                if (alarm.isActive()) {
                    contentText = getApplicationContext().getString(
                            R.string.notification_text_alarm_name, alarm.getName());
                }
            }
        }
        return contentText;
    }

    /**
     * Triggered when connection to google api is set up
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to GoogleApiClient");
        updateGeofences();
    }

    /**
     * Triggered if connection to google api fails
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.d(TAG, "Connection suspended");
    }

    /**
     * Update all geofences
     * This will add geofences for each active alarms.
     * Deactivated alarms will be removed.
     */
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
                ).setResultCallback(this); // Result processed in onResult().
            }
        } catch (SecurityException securityException) {
            Log.e(TAG, "Error updating geofences", securityException);
        }
    }

    /**
     * @return a GeofencingRequest for all active alarms
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(getLocations());
        return builder.build();
    }

    /**
     * @return  a list of Geofences corresponding to all active alarms
     */
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

    /**
     * @return pending intent for GeofenceService
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getApplicationContext(),
                GeofenceService.class);
        // FLAG_UPDATE_CURRENT will make getService return the same
        // PendingIntent next time geGeofencePendingIntent is called
        // this is great for updating geofences.
        return PendingIntent.getService(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Geofence status result receiver
     *
     * @param status status of Geofence result
     */
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

    public class AlarmServiceBinder extends Binder {
        AlarmService getService() {
            return AlarmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Save alarms to persistent storage
     */
    private void saveAlarms() {
        Log.d(TAG, "saveAlarms");
        try {
            JSONFileWriter.writeToFile(getApplicationContext(),
                    ALARMS_FILE_NAME, alarms.toJSON());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load alarms from persistent storage
     */
    private void loadAlarms() {
        Log.d(TAG, "loadAlarms");
        try {
            JSONArray jsonArray = JSONFileReader.readJSONArrayFromFile(
                    getApplicationContext(), ALARMS_FILE_NAME);
            alarms = new Alarms(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            alarms = new Alarms();
        }
    }



}
