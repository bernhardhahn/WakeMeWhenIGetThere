package nu.bernhard.wakemewhenigetthere;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class to display Alarms in a ListView
 * Each row item view has a switch to toggle the active state
 * of the Alarm. Users can attach observers (listeners)
 * by calling registerAlarmStateObserver() with an implementation
 * of AlarmStateObserver to receive notifications when an
 * Alarm's active state it toggled.
 *
 */
public class AlarmsAdapter extends BaseAdapter {
    private final Context context;
    private final Alarms alarms;
    private final List<AlarmStateObserver> observers = new ArrayList<>();

    public AlarmsAdapter(Context context, Alarms alarms) {
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public int getCount() {
        return alarms.getSize();
    }

    @Override
    public Object getItem(int id) {
        return alarms.get(id);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarm_list_item, viewGroup, false);
        }

        TextView title = (TextView)view.findViewById(R.id.alarm_list_item_title);
        TextView radius = (TextView)view.findViewById(R.id.alarm_list_item_radius);
        SwitchCompat status = (SwitchCompat)view.findViewById(R.id.alarm_list_item_active);

        final Alarm alarm = alarms.get(i);
        title.setText(alarm.getName());
        radius.setText(StringFormatter.radiusStringFormatter(context, alarm.getRadius()));
        status.setChecked(alarm.isActive());
        final int index = i;
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setActive(!alarm.isActive());
                triggerAlarmStateChange(alarm, index);
            }
        });

        return view;
    }

    /**
     * Register an observer to receive notifications when
     * an alarm's active state is changed.
     *
     * @param observer to receive notifications
     */
    public void registerAlarmStateObserver(AlarmStateObserver observer) {
        observers.add(observer);
    }

    /**
     * Unregister an observer
     *
     * @param observer to remove
     */
    public void unregisterAlarmStateObserver(AlarmStateObserver observer) {
        observers.remove(observer);
    }

    private void triggerAlarmStateChange(Alarm alarm, int index) {
        for (AlarmStateObserver observer : observers) {
            observer.onAlarmStateChange(alarm, index);
        }
    }

    /**
     * Interface to receive update on Alarm's active state
     * changes.
     */
    public interface AlarmStateObserver {
        void onAlarmStateChange(Alarm alarm, int index);
    }
}
