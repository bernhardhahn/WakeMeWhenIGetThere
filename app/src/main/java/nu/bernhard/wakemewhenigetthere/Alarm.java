package nu.bernhard.wakemewhenigetthere;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An Alarm has a name, a location (lat/lon) and a radius in metres
 * An Alarm can be active.
 * Each Alarm contains an id which should be set to a unique value.
 * An Alarm can be exported to e JSONObject by calling toJSON and
 * can be created from a JSONObject using to appropriate constructor.
 * This is used to store Alarms in persistent storage.
 *
 * Alarm implements Parcelable
 */
public class Alarm implements Parcelable {

    private static final double DEFAULT_LON = 18.057345d;
    private static final double DEFAULT_LAT = 59.332234d;
    private static final int DEFAULT_RADIUS = 1000;
    private static final boolean DEFAULT_ACTIVE = true;
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_LON = "lon";
    private static final String JSON_LAT = "lat";
    private static final String JSON_RADIUS = "radius";
    private static final String JSON_ACTIVE = "active";
    private Integer id = -1;
    private String name = "";
    private Double lon = DEFAULT_LON;
    private Double lat = DEFAULT_LAT;
    private Integer radius = DEFAULT_RADIUS;
    private Boolean active = DEFAULT_ACTIVE;


    /**
     * Empty constructor needed for Parcelable objects
     */
    public Alarm() { }


    /**
     * Constructor for setting all private fields
     * @param id    id of the alarm
     * @param name  name of the alarm
     * @param lon   longitude coordinate of the alarm
     * @param lat   latitude coordinate of the alarm
     * @param radius radius of the alarm in metres
     * @param active boolean indicating if the alarm is active of not
     */
    public Alarm(Integer id, String name, Double lon, Double lat, Integer radius, Boolean active) {
        this(name, lon, lat, radius, active);
        this.id = id;
    }

    /**
     * Constructor for setting all private fields except id
     * @param name  name of the alarm
     * @param lon   longitude coordinate of the alarm
     * @param lat   latitude coordinate of the alarm
     * @param radius radius of the alarm in metres
     * @param active boolean indicating if the alarm is active of not
     */
    public Alarm(String name, Double lon, Double lat, Integer radius, Boolean active) {
        this.setName(name);
        this.setLon(lon);
        this.setLat(lat);
        this.setRadius(radius);
        this.setActive(active);
    }


    /**
     * Constructor for setting all fields from a JSONObject
     * If the JSONObject is not well formatted and contains
     * an Alarm object (as created by toJSON()) all fields
     * will not be set. No exception will be thrown!
     * @param jsonObject JSONObject containing all fields of an Alarm
     */
    public Alarm(JSONObject jsonObject) {
        try {
            setId(jsonObject.getInt(JSON_ID));
            setName(jsonObject.getString(JSON_NAME));
            setLon(jsonObject.getDouble(JSON_LON));
            setLat(jsonObject.getDouble(JSON_LAT));
            setRadius(jsonObject.getInt(JSON_RADIUS));
            setActive(jsonObject.getBoolean(JSON_ACTIVE));
        } catch (JSONException e) {
            Log.d("Alarm", "Failed to parse JSON: " + jsonObject.toString());
        }
    }

    /**
     * @return A JSONObject containing all fields of the Alarm
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID , getId());
        json.put(JSON_NAME, getName());
        json.put(JSON_LON, getLon());
        json.put(JSON_LAT, getLat());
        json.put(JSON_RADIUS, getRadius());
        json.put(JSON_ACTIVE, isActive());
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Auto-generated!
     * Constructor for Parcel objects
     * @param in Parcel object containing an Alarm
     */
    protected Alarm(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        lon = in.readByte() == 0x00 ? null : in.readDouble();
        lat = in.readByte() == 0x00 ? null : in.readDouble();
        radius = in.readByte() == 0x00 ? null : in.readInt();
        byte activeVal = in.readByte();
        active = activeVal == 0x02 ? null : activeVal != 0x00;
    }

    /**
     * Auto-generated!
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Auto-generated!
     * Write Alarm to Parcel
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (lon == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lon);
        }
        if (lat == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lat);
        }
        if (radius == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(radius);
        }
        if (active == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (active ? 0x01 : 0x00));
        }
    }

    /**
     * Auto-generated!
     */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
}
