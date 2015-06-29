package nu.bernhard.wakemewhenigetthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}
