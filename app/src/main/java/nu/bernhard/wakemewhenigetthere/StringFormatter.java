package nu.bernhard.wakemewhenigetthere;

import android.content.Context;

/**
 * Static Helper Class to format string
 */
public class StringFormatter {

    /**
     * Format Radius string with correct unit
     *
     * @param context           ApplicationContext of string resource
     * @param radiusInMetres    Radius in meter to be formatted as string
     * @return                  A Formatted string of radiusInMetres with unit (m or km)
     */
    public static String radiusStringFormatter(Context context, int radiusInMetres) {
        String unit = radiusInMetres >= 1000 ? "km" : "m";
        int radius = radiusInMetres >= 1000 ? radiusInMetres / 1000 : radiusInMetres;
        return context.getString(R.string.radius_title, radius, unit);
    }

}
