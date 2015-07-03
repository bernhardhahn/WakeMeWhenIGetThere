package nu.bernhard.wakemewhenigetthere;

import android.os.Bundle;


public class MainActivity extends VisibleActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
