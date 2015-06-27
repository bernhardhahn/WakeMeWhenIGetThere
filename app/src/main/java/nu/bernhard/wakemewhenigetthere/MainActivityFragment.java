package nu.bernhard.wakemewhenigetthere;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Button startServiceButton;
    private Button startServiceForegroundButton;
    private Button stopServiceForegroundButton;
    private ListView alarmsListView;

    private List<Alarm> alarms = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        startServiceButton = (Button) view.findViewById(R.id.startServiceButton);
        startServiceForegroundButton = (Button) view.findViewById(R.id.startServiceForegroundButton);
        stopServiceForegroundButton = (Button) view.findViewById(R.id.stopServiceForegroundButton);
        alarmsListView = (ListView) view.findViewById(R.id.alarmsListView);


        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity().getApplicationContext();
                Intent alarmServiceIntent = new Intent(context, AlarmService.class);
                context.startService(alarmServiceIntent);
            }
        });

        startServiceForegroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmService.setForeground(getActivity().getApplicationContext(), true);
            }
        });

        stopServiceForegroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmService.setForeground(getActivity().getApplicationContext(), false);
            }
        });

        initAlarms();
        alarmsListView.setAdapter(new AlarmsAdapter(getActivity().getApplicationContext(), alarms));

        return view;
    }

    private void initAlarms() {
        String alarmJson = "{name: \"Test Alarm\", lon: 15.566608, lat: 58.412103, radius: 250, active: true}";
        String alarmJson2 = "{name: \"Test Alarm22\", lon: 15.566608, lat: 58.412103, radius: 500, active: true}";
        String alarmJson3 = "{name: \"Test Alarm333\", lon: 15.566608, lat: 58.412103, radius: 1250, active: true}";

        Alarm alarm = new Alarm(alarmJson);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson2);
        alarms.add(alarm);
        alarm = new Alarm(alarmJson3);
        alarms.add(alarm);
    }

}
