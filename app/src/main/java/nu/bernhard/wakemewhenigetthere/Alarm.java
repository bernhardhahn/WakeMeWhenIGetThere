package nu.bernhard.wakemewhenigetthere;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Alarm implements Parcelable {

    public static final double DEFAULT_LON = 58d;
    public static final double DEFAULT_LAT = 12d;
    public static final int DEFAULT_RADIUS = 250;
    public static final boolean DEFAULT_ACTIVE = true;
    private Integer id = -1;
    private String name = "";
    private Double lon = DEFAULT_LON;
    private Double lat = DEFAULT_LAT;
    private Integer radius = DEFAULT_RADIUS;
    private Boolean active = DEFAULT_ACTIVE;

    public Alarm() { }

    public Alarm(Integer id, String name, Double lon, Double lat, Integer radius, Boolean active) {
        this(name, lon, lat, radius, active);
        this.id = id;
    }

    public Alarm(String name, Double lon, Double lat, Integer radius, Boolean active) {
        this.setName(name);
        this.setLon(lon);
        this.setLat(lat);
        this.setRadius(radius);
        this.setActive(active);
    }

    public Alarm(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            setId(jsonObject.getInt("id"));
            setName(jsonObject.getString("name"));
            setLon(jsonObject.getDouble("lon"));
            setLat(jsonObject.getDouble("lat"));
            setRadius(jsonObject.getInt("radius"));
            setActive(jsonObject.getBoolean("active"));
        } catch (JSONException e) {
            Log.d("Alarm", "Failed to parse JSON: " + jsonString);
        }
    }

    public String toJSON()  {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("id: " + getId() + ", ");
        sb.append("name: \"" + getName().toString() + "\", ");
        sb.append("lon: " + getLon() + ", ");
        sb.append("lat: " + getLat() + ", ");
        sb.append("radius: " + getRadius() + ", ");
        sb.append("active: " + isActive());
        sb.append('}');

        return sb.toString();
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
    protected Alarm(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        lon = in.readByte() == 0x00 ? null : in.readDouble();
        lat = in.readByte() == 0x00 ? null : in.readDouble();
        radius = in.readByte() == 0x00 ? null : in.readInt();
        byte activeVal = in.readByte();
        active = activeVal == 0x02 ? null : activeVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
