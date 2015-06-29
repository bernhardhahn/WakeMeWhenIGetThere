package nu.bernhard.wakemewhenigetthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class AlarmService extends NonStoppingIntentService {
    private static final String ACTION_FOREGROUND =
            "nu.bernhard.wakemewhenigetthere.action.FOREGROUND";
    private static final String ACTION_ENTER_GEOFENCE =
            "nu.bernhard.wakemewhenigetthere.action.ENTER_GEOFENCE";

    private static final String EXTRA_FOREGROUND_VALUE =
            "nu.bernhard.wakemewhenigetthere.extra.FOREGROUND_VALUE";
    private static final String EXTRA_GEOFENCE_ID =
            "nu.bernhard.wakemewhenigetthere.extra.GEOFENCE_ID";
    private static final String TAG = "AlarmService";
    private IBinder binder  = new AlarmServiceBinder();;

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

    private void handleActionEnterGeofence(String geofenceId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle("AlarmService");
        builder.setContentText("Enter: " + geofenceId);
        builder.setSmallIcon(android.R.drawable.ic_media_play);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, notification);
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

}
