package nu.bernhard.wakemewhenigetthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String alarmJson = "{name: \"Test Alarm\", lon: 15.566608, lat: 58.412103, radius: 250, active: true}";
        String alarmJson2 = "{name: \"Test Alarm22\", lon: 15.566608, lat: 58.412103, radius: 500, active: true}";
        String alarmJson3 = "{name: \"Test Alarm333\", lon: 15.566608, lat: 58.412103, radius: 1250, active: true}";
        Alarms alarms = new Alarms();
        Alarm alarm = new Alarm(alarmJson);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson2);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson3);
        alarms.add(alarm);
        String json = alarms.toJSON();
        Log.d("json", json);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlarmService.setForeground(this, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AlarmService.setForeground(this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
