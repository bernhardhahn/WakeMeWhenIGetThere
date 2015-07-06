package nu.bernhard.wakemewhenigetthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmAlertService extends NonStoppingIntentService {

    public static final String START_ALARM = "START_ALARM";
    public static final String STOP_ALARM = "STOP_ALARM";

    public AlarmAlertService() {
        super("AlarmAlertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Log.d("AlarmAlertService", "onHandleIntent: action: " + intent.getAction());

            if (intent.getAction().equals(START_ALARM)) {
                if (intent.hasExtra(AlarmAlertActivity.ALARM_KEY)) {
                    Alarm alarm = intent.getParcelableExtra(AlarmAlertActivity.ALARM_KEY);
                    startAlarm();
                    showNotification(alarm);
                    showActivity(alarm);
                } else {
                    Log.d("AlarmAlertService", "AlarmAlertService launched without Alarm");
                    stopSelf();
                }
            }

            if (intent.getAction().equals(STOP_ALARM)) {
                stopAlarm();
                hideNotification();
                stopSelf();
            }
        }
    }

    private void showActivity(Alarm alarm) {
        Intent alarmAlertActivityIntent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
        alarmAlertActivityIntent.putExtra(AlarmAlertActivity.ALARM_KEY, alarm);
        alarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        startActivity(alarmAlertActivityIntent);
    }

    private void hideNotification() {
        stopForeground(true);
    }

    private void stopAlarm() {

    }

    private void showNotification(Alarm alarm) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.notification_content_title));
        builder.setContentText(alarm.getName());
        builder.setSmallIcon(R.drawable.notification_icon);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_large_icon);
        builder.setLargeIcon(largeIcon);
        Intent intent = new Intent(getApplicationContext(), AlarmAlertService.class);
        intent.setAction(STOP_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        builder.addAction(R.drawable.notification_icon, "Dismiss", pendingIntent);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        this.startForeground(2, notification);
    }

    private void startAlarm() {

    }
}
