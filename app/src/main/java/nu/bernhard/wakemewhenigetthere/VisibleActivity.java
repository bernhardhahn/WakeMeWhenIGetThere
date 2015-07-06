package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public abstract class VisibleActivity extends AppCompatActivity {

    private BroadcastReceiver showNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("VisibleActivity", "Got intent: " + intent.getAction());
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(AlarmService.ACTION_SHOW_NOTIFICATION);
        registerReceiver(showNotificationReceiver, intentFilter,
                Manifest.permission.PRIVATE, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(showNotificationReceiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        AlarmService.updateNotification(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AlarmService.updateNotification(this);
    }

}
