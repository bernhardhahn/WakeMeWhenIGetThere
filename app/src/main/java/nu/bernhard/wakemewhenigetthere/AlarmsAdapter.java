package nu.bernhard.wakemewhenigetthere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Bernhard on 2015-06-27.
 */
public class AlarmsAdapter extends BaseAdapter {
    private Context context;
    private Alarms alarms;

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
        if(view==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarm_list_item, viewGroup,false);
        }

        TextView title = (TextView)view.findViewById(R.id.alarm_list_item_title);
        TextView coords = (TextView)view.findViewById(R.id.alarm_list_item_coords);
        TextView radius = (TextView)view.findViewById(R.id.alarm_list_item_radius);
        TextView status = (TextView)view.findViewById(R.id.alarm_list_item_active);


        Alarm alarm = alarms.get(i);

        title.setText(alarm.getName());
        coords.setText(alarm.getLat() + "/" + alarm.getLon());
        radius.setText("Radius: " + alarm.getRadius() + " m");
        status.setText(alarm.isActive()? "active":"inactive");

        return view;
    }
}
