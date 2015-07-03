package nu.bernhard.wakemewhenigetthere;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class JSONFileWriter {

    public static void writeToFile(Context context, String filename, JSONArray json)
            throws IOException {
        writeStringToFile(context, filename, json.toString());
    }

    public static void writeToFile(Context context, String filename, JSONObject json)
            throws IOException {
        writeStringToFile(context, filename, json.toString());
    }

    private static void writeStringToFile(Context context, String filename, String str)
            throws IOException {
        Writer writer = null;
        try {
            OutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            String jsonStr = str;
            writer.write(jsonStr);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
