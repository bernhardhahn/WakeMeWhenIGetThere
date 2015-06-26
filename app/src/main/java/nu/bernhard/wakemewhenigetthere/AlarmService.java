package nu.bernhard.wakemewhenigetthere;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmService extends NonStoppingIntentService {
    private static final String ACTION_FOO = "nu.bernhard.wakemewhenigetthere.action.FOO";
    private static final String ACTION_BAZ = "nu.bernhard.wakemewhenigetthere.action.BAZ";
    private static final String ACTION_FOREGROUND =
            "nu.bernhard.wakemewhenigetthere.action.FOREGROUND";

    private static final String EXTRA_PARAM1 = "nu.bernhard.wakemewhenigetthere.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "nu.bernhard.wakemewhenigetthere.extra.PARAM2";
    private static final String EXTRA_FOREGROUND_VALUE =
            "nu.bernhard.wakemewhenigetthere.extra.FOREGROUND_VALUE";
    private static final String TAG = "AlarmService";

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    public static void setForeground(Context context, boolean foreground) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_FOREGROUND);
        intent.putExtra(EXTRA_FOREGROUND_VALUE, foreground);
        context.startService(intent);
    }

    public AlarmService() {
        super(TAG);
        Log.d(TAG, "AlarmService constructor");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int superResult = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "AlarmService onStartCommand: superRes: " + superResult);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "AlarmService onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(TAG, "AlarmService onHandleIntent action: " + action);

            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            } else if (ACTION_FOREGROUND.equals(action)) {
                final Boolean foreground = intent.getBooleanExtra(EXTRA_FOREGROUND_VALUE, false);
                handleActionForeground(foreground);
            }
        }
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

    private void handleActionFoo(String param1, String param2) {

    }

    private void handleActionBaz(String param1, String param2) {

    }

}
