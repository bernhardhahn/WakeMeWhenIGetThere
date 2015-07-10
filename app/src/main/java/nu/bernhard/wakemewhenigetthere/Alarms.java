package nu.bernhard.wakemewhenigetthere;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * Alarms contains a list of all Alarms.
 * This is the main model of the application
 *
 * Users of this class can attach Listeners by calling
 * addAlarmsUpdateListener() with an implementation of
 * Alarms.AlarmsUpdateListener to receive notifications
 * when the list of Alarms is updated.
 */
public class Alarms {
    private Integer nextId = 1;
    private final List<Alarm> alarms = new ArrayList<>();
    private final List<AlarmsUpdateListener> listeners = new ArrayList<>();

    public Alarms() { }

    /**
     * Constructor to initialize alarms from an JSONArray
     * The jsonArray must contain a well-formatted JSONArray
     * such as return by toJSON.
     * @param jsonArray jsonArray containing a list of Alarm.
     * @throws JSONException
     */
    public Alarms(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            add(new Alarm(jsonArray.getJSONObject(i)));
        }
    }

    /**
     * Add a Alarm to Alarms
     * This will override any value of alarm.id and return
     * a new id.
     * @param alarm Alarm to be added to Alarms
     * @return      new id set to the Alarm added
     */
    public Integer add(Alarm alarm) {
        alarm.setId(nextId++);
        alarms.add(alarm);
        triggerUpdateListeners();
        return alarm.getId();
    }

    /**
     * Update an Alarm in Alarms
     * If alarm.id is not set (-1) a new id will be set.
     *
     * @param alarm Alarm to be updated
     */
    public void update(Alarm alarm) {
        if (alarm.getId() == -1) {
            add(alarm);
            return;
        }
        for (int i = 0; i < alarms.size(); ++i) {
            if (alarms.get(i).getId().equals(alarm.getId())) {
                alarms.set(i, alarm);
                triggerUpdateListeners();
                break;
            }
        }
    }

    /**
     * Return an Alarm at a given index in the Alarms list
     *
     * @param index Index of Alarm to be returned
     * @return      Alarm at index
     */
    public Alarm get(int index) {
        return alarms.get(index);
    }

    /**
     * Get alarm by alarm.id
     *
     * @param id    id of the alarm to get
     * @return      alarm with matching id
     * @throws Exception if alarm with matching id is not found
     */
    public Alarm getById(int id) throws Exception {
        for (Alarm alarm : alarms) {
            if (alarm.getId() == id) {
                return alarm;
            }
        }
        throw new Exception("Alarms: could not find Alarm with id=" + id);
    }

    /**
     * Remove an Alarm by alarm.id
     *
     * @param id of the alarm to remove
     */
    public void removeById(int id) {
        for (int i = alarms.size() - 1; i >= 0; --i) {
            if (alarms.get(i).getId() == id) {
                alarms.remove(alarms.get(i));
            }
        }
        triggerUpdateListeners();
    }

    /**
     * @return a list of all Alarms
     */
    public List<Alarm> getAll() {
        return alarms;
    }

    /**
     * @return the number of Alarms in Alarms
     */
    public int getSize() {
        return alarms.size();
    }

    public int getActiveAlarmCount() {
        int activeCount = 0;
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                activeCount++;
            }
        }
        return  activeCount;
    }

    /**
     * Serialize all Alarms to an JSONArray
     * @return a JSONArray of all Alarms
     * @throws JSONException
     */
    public JSONArray toJSON() throws JSONException {
        JSONArray json = new JSONArray();
        for (Alarm alarm : alarms) {
            json.put(alarm.toJSON());
        }
        return json;
    }

    /**
     * @return true if Alarms contains any active alarms
     */
    public boolean hasActiveAlarms() {
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a listener to receive messages when Alarms is updated
     * @param listener to receive messages
     */
    public void addAlarmsUpdateListener(AlarmsUpdateListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener
     * @param listener to remove
     */
    public void removeAlarmsUpdateListener(AlarmsUpdateListener listener) {
        listeners.remove(listener);
    }

    private void triggerUpdateListeners() {
        for (AlarmsUpdateListener listener : listeners) {
            listener.onAlarmsUpdate();
        }
    }

    public interface AlarmsUpdateListener {
        void onAlarmsUpdate();
    }
}
