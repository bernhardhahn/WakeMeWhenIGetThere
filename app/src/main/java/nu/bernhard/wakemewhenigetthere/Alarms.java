package nu.bernhard.wakemewhenigetthere;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Alarms {
    private List<Alarm> alarms = new ArrayList<>();
    private Integer nextId = 1;
    private List<AlarmsUpdateListener> listeners = new ArrayList<>();

    public Alarms() { }

    public Alarms(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            add(new Alarm(jsonArray.getJSONObject(i)));
        }
    }

    public Integer add(Alarm alarm) {
        alarm.setId(nextId++);
        alarms.add(alarm);
        triggerUpdateListeners();
        return alarm.getId();
    }

    public void update(Alarm alarm) {
        if (alarm.getId() == -1) {
            add(alarm);
            return;
        }
        for (int i = 0; i < alarms.size(); ++i) {
            if (alarms.get(i).getId() == alarm.getId()) {
                alarms.set(i, alarm);
                triggerUpdateListeners();
                break;
            }
        }
    }

    public Alarm get(int index) {
        return alarms.get(index);
    }

    public Alarm getById(int id) throws Exception {
        for (Alarm alarm : alarms) {
            if (alarm.getId() == id) {
                return alarm;
            }
        }
        throw new Exception("Alarms: could not find Alarm with id=" + id);
    }

    public void removeById(int id) {
        for (int i = alarms.size() - 1; i >= 0; --i) {
            if (alarms.get(i).getId() == id) {
                alarms.remove(alarms.get(i));
            }
        }
        triggerUpdateListeners();
    }

    public List<Alarm> getAll() {
        return alarms;
    }

    public int getSize() {
        return alarms.size();
    }

    public int getActivAlarmCount() {
        int activeCount = 0;
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                activeCount++;
            }
        }
        return  activeCount;
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray json = new JSONArray();
        for (Alarm alarm : alarms) {
            json.put(alarm.toJSON());
        }
        return json;
    }

    private void triggerUpdateListeners() {
        for (AlarmsUpdateListener listener : listeners) {
            listener.onAlarmsUpdate();
        }
    }

    public boolean hasActiveAlarms() {
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                return true;
            }
        }
        return false;
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
