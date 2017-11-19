package Comm;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Utilities.Utilities;

/**
 * Created by abhilashk on 11/18/2017.
 */

public class HTTPClient {

    public String getDataWeather(String location)
    {
        HttpURLConnection connection = null;

        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection)(new URL(Utilities.URL_WEATHER + location)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //reading response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine())!= null){
                stringBuffer.append(line + "\r\n");
            }
            inputStream.close();
            connection.disconnect();

            JSONObject jsonObject = new JSONObject(stringBuffer.toString());
            if( jsonObject.getInt("cod") == 404 ) {
                Log.v("jsonParser: ", "404 ERROR");
                return null;
            }


            return stringBuffer.toString();
        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
