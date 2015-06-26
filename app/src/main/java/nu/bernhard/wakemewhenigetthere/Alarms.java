package nu.bernhard.wakemewhenigetthere;

import java.util.ArrayList;
import java.util.List;

public class Alarms {
    List<Alarm> alarms = new ArrayList<>();

    public void add(Alarm alarm) {
        alarms.add(alarm);
    }

    public Alarm get(int index) {
        return alarms.get(index);
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Alarm alarm : alarms) {
            sb.append(alarm.toJSON()).append(", ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(']');

        return sb.toString();
    }
}
