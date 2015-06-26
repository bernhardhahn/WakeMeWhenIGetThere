package nu.bernhard.wakemewhenigetthere;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;

import java.util.List;


/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionsIntentService.class.getName();

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            ArrayList triggeringGeofencesIdsList = new ArrayList();
            for (Geofence geofence : triggeringGeofences) {
                triggeringGeofencesIdsList.add(geofence.getRequestId());
                AlarmService.enterGeofence(getApplicationContext(), geofence.getRequestId());
            }
            String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);


            // Send notification and log the transition details.
            notifyAlarmService(triggeringGeofencesIdsString);
            Log.i(TAG, triggeringGeofencesIdsString);
        }
    }

    private void notifyAlarmService(String message) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("message", message);
        Log.d("ttt", "GeofenceService will send message to AlarmService");
        context.startService(intent);
    }

}
