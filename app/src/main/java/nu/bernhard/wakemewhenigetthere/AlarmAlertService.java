package nu.bernhard.wakemewhenigetthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

/**
 * AlarmAlertService is a Service to run audio and vibrate when an
 * Alarm is triggered.
 *
 * AlarmAlertService should be started by an Intent with the
 * Action START_ALARM. The Intent should also contain an Alarm
 * under the key ALARM_KEY.
 *
 * Once AlarmAlertService is started it will launch AlarmAlertActivity
 * and show a Notification. Both of these can send an Intent
 * with the Action STOP_ALARM. This will cause AlarmAlertService
 * to stop the Alarm and shut down the Notification and AlarmAlertActivity.
 *
 */
public class AlarmAlertService extends NonStoppingIntentService {

    //Key used as Action to start playing an Alarm
    public static final String START_ALARM = "START_ALARM";
    //Key used as Action to stop playing an Alarm
    public static final String STOP_ALARM = "STOP_ALARM";
    //Key used to indicate Alarm in Intent when starting AlarmAlertService
    public static final String ALARM_KEY = "alarm";

    //TAG for logging
    private static final String TAG = AlarmAlertService.class.getName();

    private static final long[] vibratePattern = new long[] { 500, 500 };
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int systemAlarmVolumeSetting;
    private boolean audioRunning;

    public AlarmAlertService() {
        super("AlarmAlertService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Log.d(TAG, "onHandleIntent: action: " + intent.getAction());

            //Handle START action
            if (intent.getAction().equals(START_ALARM)) {
                if (intent.hasExtra(ALARM_KEY)) {
                    Alarm alarm = intent.getParcelableExtra(ALARM_KEY);
                    startAlarm();
                    showNotification(alarm);
                    showActivity(alarm);
                } else {
                    Log.d(TAG, "AlarmAlertService launched without Alarm");
                    stopSelf();
                }
            }

            //Handle STOP action
            if (intent.getAction().equals(STOP_ALARM)) {
                stopAlarm();
                hideNotification();
                broadcastActivityClose();
                stopSelf();
            }
        }
    }

    private void showActivity(Alarm alarm) {
        Intent alarmAlertActivityIntent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
        alarmAlertActivityIntent.putExtra(ALARM_KEY, alarm);
        alarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        startActivity(alarmAlertActivityIntent);
    }

    private void startAlarm() {
        startAudioAlarm();
        startVibration();
    }
    private void stopAlarm() {
        endVibration();
        endAudioAlarm();
    }

    /**
     * ShowNotification will display a Notification by putting
     * changing AlarmAlertService to run in the foreground
     *
     * @param alarm Alarm to be displayed in notification
     */
    private void showNotification(Alarm alarm) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.notification_content_title));
        builder.setContentText(alarm.getName());
        builder.setSmallIcon(R.drawable.notification_icon);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.notification_large_icon);
        builder.setLargeIcon(largeIcon);
        Intent intent = new Intent(getApplicationContext(), AlarmAlertService.class);
        intent.setAction(STOP_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        builder.addAction(R.drawable.icon_flat, "Dismiss", pendingIntent);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        this.startForeground(2, notification);
    }

    private void startAudioAlarm() {
        if (audioRunning) return;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // save current volume
        systemAlarmVolumeSetting = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "Error playing audio alarm."
                            + "\nwhat: " + what
                            + "\nextra: " + extra);
                    endAudioAlarm();
                    return true;
                }
            });
        }

        try {
            Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer.setDataSource(this, ringtone);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            audioManager.requestAudioFocus(null, AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            mediaPlayer.start();
        } catch (IOException e) {
            mediaPlayer.reset();
            e.printStackTrace();
        }

        audioRunning = true;
    }

    /**
     * Stop playing audio
     */
    private void endAudioAlarm() {
        if (audioRunning) {
            audioRunning = false;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                //restore audio volume
                audioManager.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        systemAlarmVolumeSetting, 0);
                audioManager.abandonAudioFocus(null);
                audioManager = null;
            }
        }
    }

    private void startVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibratePattern, 0);
    }

    private void endVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }

    private void broadcastActivityClose() {
        sendBroadcast(new Intent(AlarmAlertActivity.CLOSE_ACTIVITY),
                Manifest.permission.PRIVATE);
    }

    private void hideNotification() {
        stopForeground(true);
    }

}
