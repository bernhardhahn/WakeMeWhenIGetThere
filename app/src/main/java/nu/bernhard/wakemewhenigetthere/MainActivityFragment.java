package nu.bernhard.wakemewhenigetthere;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getName();

    private Button addAlarmButton;
    private ListView alarmsListView;
    private EditText newAlarmRadiusInput;
    private EditText newAlarmNameInput;
    private EditText newAlarmLatInput;
    private EditText newAlarmLonInput;
    private AlarmsAdapter alarmsAdapter;

    private AlarmService alarmService;
    private boolean serviceBound;
    private ServiceConnection alarmServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AlarmService.AlarmServiceBinder binder = (AlarmService.AlarmServiceBinder) service;
            alarmService = binder.getService();
            serviceBound = true;
            Log.d(TAG, "onServiceConnected: " + className.toString());
            getAlarmsFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
            Log.d(TAG, "onServiceConnected: " + className.toString());
        }
    };

    private void getAlarmsFromService() {
        if ( serviceBound ) {
            Alarms alarms = alarmService.getAlarms();
            alarmsAdapter = new AlarmsAdapter(getActivity().getApplicationContext(), alarms);
            alarmsListView.setAdapter(alarmsAdapter);
        } else {
            Log.d(TAG, "alarms service not bound: can't create AlarmAdapter");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), AlarmService.class);
        getActivity().bindService(intent, alarmServiceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(alarmServiceConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        addAlarmButton = (Button) view.findViewById(R.id.addAlarmButton);
        alarmsListView = (ListView) view.findViewById(R.id.alarmsListView);

        newAlarmNameInput = (EditText) view.findViewById(R.id.newAlarmName);
        newAlarmLatInput = (EditText) view.findViewById(R.id.newAlarmLat);
        newAlarmLonInput = (EditText) view.findViewById(R.id.newAlarmLon);
        newAlarmRadiusInput = (EditText) view.findViewById(R.id.newAlarmRadius);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alarmName = newAlarmNameInput.getText().toString();
                Double lat = Double.parseDouble(newAlarmLatInput.getText().toString());
                Double lon = Double.parseDouble(newAlarmLonInput.getText().toString());
                Integer radius = Integer.parseInt((newAlarmRadiusInput.getText().toString()));

                Alarm alarm = new Alarm(alarmName, lon, lat, radius, true);

                if(serviceBound) {
                    alarmService.addAlarm(alarm);
                    alarmsAdapter.notifyDataSetChanged();
                }

            }
        });

        return view;
    }

}
