package Comm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.abhilashk.myweatherapp.MainWeatherActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Model.citySharedPrefs;
import Model.dataLocation;
import Model.dataWeather;
import Utilities.Utilities;
import Model.dataClouds;

/**
 * Created by abhilashk on 11/18/2017.
 */

public class jsonParser {

    public static dataWeather getWeather(String data)
    {
        dataWeather weather = new dataWeather();
        //create JSONObject from data
        try{
            if(data == null)
                return null;

            JSONObject jsonObject = new JSONObject(data);

           if( jsonObject.getInt("cod") == 404 ) {
               Log.v("jsonParser: ", "404 ERROR");
               return null;

           }

            dataLocation place = new dataLocation();
            JSONObject coordObj = Utilities.getObject ("coord",jsonObject);
            place.setLat(Utilities.getFloat("lat", coordObj));

            //get the  sys object
            JSONObject sysObj = Utilities.getObject("sys", jsonObject);
            place.setCountry(Utilities.getString("country", sysObj));
            place.setLastupdate(Utilities.getInt("dt", jsonObject));
            place.setSunrise(Utilities.getInt("sunrise", sysObj));
            place.setSunset(Utilities.getInt("sunset", sysObj));
            place.setCity(Utilities.getString("name", jsonObject));
            weather.place = place;

            //This is an array for weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");

            //we could loop through the array, but we are just interested in getting the first index (0)
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utilities.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(Utilities.getString("description", jsonWeather));
            weather.currentCondition.setCondition(Utilities.getString("main", jsonWeather));
            weather.currentCondition.setIcon(Utilities.getString("icon", jsonWeather));

            //Let's get the main object
            JSONObject mainObj = Utilities.getObject("main", jsonObject);
            weather.currentCondition.setHumidity(Utilities.getInt("humidity", mainObj));
            weather.currentCondition.setPressure(Utilities.getInt("pressure", mainObj));
            weather.currentCondition.setMinTemp(Utilities.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(Utilities.getFloat("temp_max", mainObj));
            weather.currentCondition.setTemperature(Utilities.getDouble("temp", mainObj));


            //Let's setup wind
            JSONObject windObj = Utilities.getObject("wind", jsonObject);
            weather.wind.setSpeed(Utilities.getFloat("speed", windObj));
            weather.wind.setDeg(Utilities.getFloat("deg", windObj));


            //Setup clouds
            JSONObject cloudObj = Utilities.getObject("clouds", jsonObject);
            weather.clouds.setCloud_val(Utilities.getInt("all", cloudObj));

            return weather;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }
}
