package nu.bernhard.wakemewhenigetthere;

import android.content.Context;

public class StringFormatter {

    public static String radiusStringFormatter(Context context, int radiusInMetres) {
        String unit = radiusInMetres >= 1000 ? "km" : "m";
        int radius = radiusInMetres >= 1000 ? radiusInMetres / 1000 : radiusInMetres;
        return context.getString(R.string.radius_title, radius, unit);
    }

}
