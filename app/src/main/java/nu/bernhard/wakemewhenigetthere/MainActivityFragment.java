package nu.bernhard.wakemewhenigetthere;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Button startServiceButton;
    private Button startServiceForegroundButton;
    private Button stopServiceForegroundButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        startServiceButton = (Button) view.findViewById(R.id.startServiceButton);
        startServiceForegroundButton = (Button) view.findViewById(R.id.startServiceForegroundButton);
        stopServiceForegroundButton = (Button) view.findViewById(R.id.stopServiceForegroundButton);

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

        return view;
    }

}
