package nu.bernhard.wakemewhenigetthere;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = MainActivityFragment.class.getName();

    private Button startServiceButton;
    private Button startServiceForegroundButton;
    private Button stopServiceForegroundButton;
    private ListView alarmsListView;

    private Alarms alarms;
    private GoogleApiClient googleApiClient;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        startServiceButton = (Button) view.findViewById(R.id.startServiceButton);
        startServiceForegroundButton = (Button) view.findViewById(R.id.startServiceForegroundButton);
        stopServiceForegroundButton = (Button) view.findViewById(R.id.stopServiceForegroundButton);
        alarmsListView = (ListView) view.findViewById(R.id.alarmsListView);


        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity().getApplicationContext();
                Intent alarmServiceIntent = new Intent(context, AlarmService.class);
                context.startService(alarmServiceIntent);
            }
        });

        startServiceForegroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmService.setForeground(getActivity().getApplicationContext(), true);
            }
        });

        stopServiceForegroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmService.setForeground(getActivity().getApplicationContext(), false);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        initAlarms();
        alarmsListView.setAdapter(new AlarmsAdapter(getActivity().getApplicationContext(), alarms));

        return view;
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        addGeofences();
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

    public void addGeofences() {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getResources().getText(R.string.not_connected).toString(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
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
            locations.add(new Geofence.Builder()
                    .setRequestId(alarm.getName())
                    .setCircularRegion(alarm.getLat(), alarm.getLon(), alarm.getRadius())
                    .setExpirationDuration(24 * 60 * 60 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        return locations;
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getActivity().getApplicationContext(),
                GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity().getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Add geofence: Success");
            Log.d(TAG, "status: " + status.getStatusMessage());
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(
                    getActivity().getApplicationContext(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    private void initAlarms() {
        String alarmJson = "{name: \"Test Alarm\", lon: 15.566608, lat: 58.412103, radius: 250, active: true}";
        String alarmJson2 = "{name: \"Test Alarm22\", lon: 15.566608, lat: 58.412103, radius: 500, active: true}";
        String alarmJson3 = "{name: \"Test Alarm333\", lon: 15.566608, lat: 58.412103, radius: 1250, active: true}";

        alarms = new Alarms();
        Alarm alarm = new Alarm(alarmJson);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson2);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson3);
        alarms.add(alarm);
    }

}
