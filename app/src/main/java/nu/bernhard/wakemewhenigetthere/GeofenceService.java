package nu.bernhard.wakemewhenigetthere;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


/**
 * GeofenceService receives geofence transition changes from
 * Google Play Services geofencing api
 *
 * All enter transitions are forwarded to AlarmService
 */
public class GeofenceService extends IntentService {

    private static final String TAG = GeofenceService.class.getName();

    public GeofenceService() {
        super(TAG);
    }

    /**
     * Handle incomming messages from google play services geofencing api
     *
     * @param intent sent by geofencing api containing geofence transitions
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "GeofenceTransitionsIntentService onHandleIntent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage =
                    GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.d(TAG, errorMessage);
            return;
        }
        // Only forward Geofence enter transitions
        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : triggeringGeofences) {
                Log.d(TAG, "handle geofence with requestId: " + geofence.getRequestId());
                AlarmService.enterGeofence(getApplicationContext(), geofence.getRequestId());
            }
        }
    }

}
