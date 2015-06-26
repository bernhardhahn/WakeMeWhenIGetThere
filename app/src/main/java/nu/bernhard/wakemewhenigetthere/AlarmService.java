package nu.bernhard.wakemewhenigetthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends NonStoppingIntentService {
    private static final String ACTION_FOREGROUND =
            "nu.bernhard.wakemewhenigetthere.action.FOREGROUND";

    private static final String EXTRA_FOREGROUND_VALUE =
            "nu.bernhard.wakemewhenigetthere.extra.FOREGROUND_VALUE";
    private static final String TAG = "AlarmService";

    public static void setForeground(Context context, boolean foreground) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(ACTION_FOREGROUND);
        intent.putExtra(EXTRA_FOREGROUND_VALUE, foreground);
        context.startService(intent);
    }

    public AlarmService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_FOREGROUND.equals(action)) {
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

}
