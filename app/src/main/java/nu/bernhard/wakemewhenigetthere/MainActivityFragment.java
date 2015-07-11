package nu.bernhard.wakemewhenigetthere;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivityFragment extends Fragment
        implements Alarms.AlarmsUpdateListener, AlarmsAdapter.AlarmStateObserver {

    private static final String TAG = MainActivityFragment.class.getName();
    public static final int ALARM_ACTIVITY_REQUEST_CODE = 123;

    private ListView alarmsListView;
    private AlarmsAdapter alarmsAdapter;

    private AlarmService alarmService;

    /**
     * Indicate of binding to AlarmService is active
     */
    private boolean serviceBound;

    /**
     * ServiceConnection to AlarmService
     */
    private final ServiceConnection alarmServiceConnection = new ServiceConnection() {

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

    @Override
    public void onStart() {
        super.onStart();
        bindToAlarmService();
    }

    @Override
    public void onStop() {
        super.onStop();
        alarmService.getAlarms().removeAlarmsUpdateListener(this);
        alarmsAdapter.unregisterAlarmStateObserver(this);
        getActivity().unbindService(alarmServiceConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setNewAlarmButtonAction(view);
        setupAlarmsListView(view);
        return view;
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if (item.getItemId() == R.id.deleteAlarm) {
            Alarm alarm = (Alarm) alarmsAdapter.getItem(position);
            alarmService.getAlarms().removeById(alarm.getId());
            alarmsAdapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALARM_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(AlarmActivity.ALARM_KEY)) {
                Alarm alarm = data.getParcelableExtra(AlarmActivity.ALARM_KEY);
                if (serviceBound) {
                    alarmService.getAlarms().update(alarm);
                }
            }
        }
    }

    @Override
    public void onAlarmsUpdate() {
        Log.d(TAG, "Alarms updated");
    }

    /**
     * AlarmStateChanges are triggered when the user
     * toggles the Alarm's active state in the main
     * ListView
     *
     * @param alarm Alarm which has been toggles
     * @param index Index of the Alarm in AlarmsAdapter
     */
    @Override
    public void onAlarmStateChange(Alarm alarm, int index) {
        alarmService.getAlarms().update(alarm);
    }

    private void bindToAlarmService() {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        getActivity().bindService(intent, alarmServiceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
    }


    private void setupAlarmsListView(View view) {
        alarmsListView = (ListView) view.findViewById(R.id.alarmsListView);
        alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Alarm alarm = (Alarm) adapterView.getItemAtPosition(i);
                Intent showAlarmIntent = new Intent(getActivity(), AlarmActivity.class);
                showAlarmIntent.putExtra(AlarmActivity.ALARM_KEY, alarm);
                startActivityForResult(showAlarmIntent, ALARM_ACTIVITY_REQUEST_CODE);
            }
        });
        //Add context menu to alarmListView
        alarmsListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                getActivity().getMenuInflater().inflate(
                        R.menu.alarm_list_item_context_menu, contextMenu);
            }
        });
        alarmsListView.setEmptyView(view.findViewById(R.id.empty));
    }

    private void setNewAlarmButtonAction(View view) {
        FloatingActionButton newAlarmButton = (FloatingActionButton) view.findViewById(R.id.newAlarmFab);
        newAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAlarmIntent = new Intent(getActivity(), AlarmActivity.class);
                startActivityForResult(newAlarmIntent, ALARM_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void getAlarmsFromService() {
        if ( serviceBound ) {
            final Alarms alarms = alarmService.getAlarms();
            alarmsAdapter = new AlarmsAdapter(getActivity(), alarms);
            alarmsAdapter.registerAlarmStateObserver(this);
            alarmsListView.setAdapter(alarmsAdapter);
        } else {
            Log.d(TAG, "alarms service not bound: can't create AlarmAdapter");
        }
    }
}
