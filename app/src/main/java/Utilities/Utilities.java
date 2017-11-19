package Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhilashk on 11/18/2017.
 */

public class Utilities {

    public static final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String URL_ICON = "http://api.openweathermap.org/img/w/";

    public static JSONObject getObject( String tag, JSONObject json) throws JSONException{
        JSONObject obj = json.getJSONObject(tag);
        return obj;
    }

    public static String getString( String tag, JSONObject json) throws JSONException{
        return json.getString(tag);
    }

    public static float getFloat( String tag, JSONObject json) throws JSONException{
        return (float)json.getDouble(tag);
    }

    public static double getDouble( String tag, JSONObject json) throws JSONException{
        return json.getDouble(tag);
    }

    public static int getInt( String tag, JSONObject json) throws JSONException{
        return json.getInt(tag);
    }
}
