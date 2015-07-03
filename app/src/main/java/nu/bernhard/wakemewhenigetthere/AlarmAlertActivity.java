package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;


public class AlarmAlertActivity extends Activity {

    private static final String TAG = AlarmAlertActivity.class.getName();
    public static final String ALARM_KEY = "alarm";
    private static final long[] vibratePattern = new long[] { 500, 500 };

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int systemAlarmVolumeSetting;
    private boolean audioRunning;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                //special flag to let windows be shown when the screen is locked.
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                //when set the window will cause the keyguard to be dismissed,
                // only if it is not a secure lock keyguard.
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                //as long as this window is visible to the user, keep the
                // device's screen turned on and bright.
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                //when set as a window is being added or made visible,
                // once the window has been shown then the system will
                // poke the power manager's user activity (as if the
                // user had woken up the device) to turn the screen on.
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_show_alarm);

        Intent intent = getIntent();
        if (intent.hasExtra(ALARM_KEY)) {
            Alarm alarm = intent.getParcelableExtra(ALARM_KEY);
            this.alarm = alarm;
            triggerAlarm();
        } else {
            Log.d(TAG, "ShowAlarmActivity launched without Alarm");
            finish();
        }

        Button dismissButton = (Button) findViewById(R.id.dismissAlarmButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
                finish();
            }
        });
    }

    /*
    / Ignore back button. We don't want the user to accidentally
    / dismiss the alarm
    */
    @Override
    public void onBackPressed() {
        return;
    }

    private void triggerAlarm() {
        startVibration();
        startAudioAlarm();
    }

    private void stopAlarm() {
        endVibration();
        endAudioAlarm();
    }

    private void startAudioAlarm() {
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

    private void endAudioAlarm() {
        if (audioRunning) {
            audioRunning = false;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                //restore audio volume
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, systemAlarmVolumeSetting, 0);
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

}
