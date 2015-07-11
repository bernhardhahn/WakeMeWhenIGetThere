package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * VisibleActivity is a abstract base class for activities
 * which want to cancel the AlarmService's broadcast
 * ACTION_SHOW_NOTIFICATION. By canceling this broadcast
 * AlarmService will not display a notification.
 */
public abstract class VisibleActivity extends AppCompatActivity {

    /**
     * BroadcastReceiver to cancel received Intents
     */
    private final BroadcastReceiver showNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("VisibleActivity", "Got intent: " + intent.getAction());
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    /**
     * Register BroadcastReceiver
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(AlarmService.ACTION_SHOW_NOTIFICATION);
        registerReceiver(showNotificationReceiver, intentFilter,
                Manifest.permission.PRIVATE, null);
    }

    /**
     * Unregister BroadcastReceiver
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(showNotificationReceiver);
    }

    /**
     * Indicate to AlarmService that a new notification
     * broadcast show be sent
     */
    @Override
    protected void onStart() {
        super.onStart();
        AlarmService.updateNotification(this);
    }

    /**
     * Indicate to AlarmService that a new notification
     * broadcast show be sent
     */
    @Override
    protected void onStop() {
        super.onStop();
        AlarmService.updateNotification(this);
    }

}
