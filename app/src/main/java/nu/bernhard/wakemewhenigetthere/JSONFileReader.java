package nu.bernhard.wakemewhenigetthere;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static helper class to read JSONObjects or JSONArrays
 * from file
 */
public class JSONFileReader {

    /**
     * Read JSONObject data from file
     *
     * @param context       Application Context
     * @param filename      Filename of file to read from
     * @return              JSONObject with content from filename
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject readJSONObjectFromFile(Context context, String filename)

            throws IOException, JSONException {
        return (JSONObject) readJSONTokenerFromFile(context, filename).nextValue();
    }

    /**
     * Read JSONArray data from file
     *
     * @param context       Application Context
     * @param filename      Filename of file to read from
     * @return              JSONArray with content from filename
     * @throws IOException
     * @throws JSONException
     */
    public static JSONArray readJSONArrayFromFile(Context context, String filename)
            throws IOException, JSONException {
        return (JSONArray) readJSONTokenerFromFile(context, filename).nextValue();
    }

    private static JSONTokener readJSONTokenerFromFile(Context context, String filename)
            throws IOException {
        BufferedReader reader = null;
        try {
            InputStream in = context.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return new JSONTokener(jsonString.toString());
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
