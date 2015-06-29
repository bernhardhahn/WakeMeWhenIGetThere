package nu.bernhard.wakemewhenigetthere;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Alarm  {

    private String name;
    private Double lon;
    private Double lat;
    private Integer radius;
    private Boolean active;

    public Alarm() { }

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
}
