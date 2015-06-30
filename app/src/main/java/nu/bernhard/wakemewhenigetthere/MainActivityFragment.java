package nu.bernhard.wakemewhenigetthere;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivityFragment extends Fragment implements Alarms.AlarmsUpdateListener {

    private static final String TAG = MainActivityFragment.class.getName();

    private ListView alarmsListView;
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
            alarmService.getAlarms().addAlarmsUpdateListener(MainActivityFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            serviceBound = false;
            Log.d(TAG, "onServiceConnected: " + className.toString());
        }
    };

    private void getAlarmsFromService() {
        if ( serviceBound ) {
            final Alarms alarms = alarmService.getAlarms();
            alarmsAdapter = new AlarmsAdapter(getActivity().getApplicationContext(), alarms);
            alarmsAdapter.registerAlarmStateObserver(new AlarmsAdapter.AlarmStateObserver() {
                @Override
                public void onAlarmStateChange(Alarm alarm, int index) {
                    Log.d(TAG, "onAlarmStateChange: " + alarm.getName() + " (id="+alarm.getId()+")  > " + index);
                    alarmService.getAlarms().update(alarm);
                }
            });
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
        alarmService.getAlarms().removeAlarmsUpdateListener(this);
        getActivity().unbindService(alarmServiceConnection);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        alarmsListView = (ListView) view.findViewById(R.id.alarmsListView);
        FloatingActionButton newAlarm = (FloatingActionButton) view.findViewById(R.id.newAlarmFab);
        newAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAlarmIntent = new Intent(getActivity(), AlarmActivity.class);
                getActivity().startActivity(newAlarmIntent);
            }
        });
        return view;
    }

    @Override
    public void onAlarmsUpdate() {
        Log.d(TAG, "Alarms updated");
    }
}
