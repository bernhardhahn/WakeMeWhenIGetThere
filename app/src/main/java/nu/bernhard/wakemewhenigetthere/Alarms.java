package nu.bernhard.wakemewhenigetthere;

import java.util.ArrayList;
import java.util.List;

public class Alarms {
    List<Alarm> alarms = new ArrayList<>();
    private Integer nextId = 1;
    private List<AlarmsUpdateListener> listeners = new ArrayList<>();

    public Integer add(Alarm alarm) {
        alarm.setId(nextId++);
        alarms.add(alarm);
        return alarm.getId();
    }

    public void update(Alarm alarm) {
        for (int i = 0; i < alarms.size(); ++i) {
            if (alarms.get(i).getId() == alarm.getId()) {
                alarms.set(i, alarm);
                break;
            }
        }
    }

    public Alarm get(int index) {
        return alarms.get(index);
    }

    public List<Alarm> getAll() {
        return alarms;
    }

    public int getSize() {
        return alarms.size();
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

    private void triggerUpdateListeners() {
        for (AlarmsUpdateListener listener : listeners) {
            listener.onAlarmsUpdate();
        }
    }

    public void addAlarmsUpdateListener(AlarmsUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeAlarmsUpdateListener(AlarmsUpdateListener listener) {
        listeners.remove(listener);
    }

    public interface AlarmsUpdateListener {
        public void onAlarmsUpdate();
    }
}
