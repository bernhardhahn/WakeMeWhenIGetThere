package nu.bernhard.wakemewhenigetthere;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class AlarmService extends IntentService {
    private static final String ACTION_FOO = "nu.bernhard.wakemewhenigetthere.action.FOO";
    private static final String ACTION_BAZ = "nu.bernhard.wakemewhenigetthere.action.BAZ";

    private static final String EXTRA_PARAM1 = "nu.bernhard.wakemewhenigetthere.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "nu.bernhard.wakemewhenigetthere.extra.PARAM2";
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
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    private void handleActionFoo(String param1, String param2) {

    }

    private void handleActionBaz(String param1, String param2) {

    }
}
