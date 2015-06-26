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
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.radius = radius;
        this.active = active;
    }

    public Alarm(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            name = jsonObject.getString("name");
            lon = jsonObject.getDouble("lon");
            lat = jsonObject.getDouble("lat");
            radius = jsonObject.getInt("radius");
            active = jsonObject.getBoolean("active");
        } catch (JSONException e) {
            Log.d("Alarm", "Failed to parse JSON: " + jsonString);
        }
    }

    public String toJSON()  {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("name: \"" + name.toString() + "\", ");
        sb.append("lon: " + lon + ", ");
        sb.append("lat: " + lat + ", ");
        sb.append("radius: " + radius + ", ");
        sb.append("active: " + active);
        sb.append('}');

        return sb.toString();
    }

}
