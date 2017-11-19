package com.example.abhilashk.myweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Comm.HTTPClient;
import Comm.jsonParser;
import Model.citySharedPrefs;
import Model.dataWeather;
import Utilities.Utilities;

public class MainWeatherActivity extends AppCompatActivity {

    private TextView cityName;
    private ImageView iconThumb;
    private TextView temp;
    private TextView cloud;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView conditions;
    private String city;
    private String oldCity;
    dataWeather weather = new dataWeather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v("MainWeatherActivity: ", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);

        cityName = (TextView) findViewById(R.id.cityName);
        iconThumb = (ImageView) findViewById(R.id.iconThumb);
        temp = (TextView) findViewById(R.id.temperatureCurrent);
        //cloud = (TextView) findViewById(R.id.cloudCurrent);
        humidity = (TextView) findViewById(R.id.humidityCurrent);
        wind = (TextView) findViewById(R.id.windCurrent);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        conditions = (TextView) findViewById(R.id.condition);

        citySharedPrefs sharedPreference = new citySharedPrefs(MainWeatherActivity.this);

        Log.v("MainWeatherActivity: ", "onCreate city : " + sharedPreference.getCity());

        drawWeatherData(sharedPreference.getCity());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.changeCity){
            ChangeCityAlertDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ChangeCityAlertDialogue(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainWeatherActivity.this);
        alertBuilder.setTitle("Change City");

        final EditText inputCity = new EditText(MainWeatherActivity.this);
        inputCity.setInputType(InputType.TYPE_CLASS_TEXT);
        inputCity.setHint("Alpharetta,US");
        alertBuilder.setView(inputCity);
        alertBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                citySharedPrefs cityPreference = new citySharedPrefs(MainWeatherActivity.this);
                oldCity = cityPreference.getCity();
                Log.v("MainWeatherActivity: ", "ChangeCityAlertDialogue  " + oldCity);
                cityPreference.setCity(inputCity.getText().toString());
                String newCity = inputCity.getText().toString();

                drawWeatherData(newCity);
            }
        });
        alertBuilder.show();


    }

    public void drawWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=imperial&APPID=d106ccde4b18b9c482b55b2abdc99523"});

    }

    private class WeatherTask extends AsyncTask<String, Void, dataWeather> {

        @Override
        protected dataWeather doInBackground(String... params) {
            String data = ((new HTTPClient()).getDataWeather(params[0]));
            weather = jsonParser.getWeather (data);

            if(weather == null){
                citySharedPrefs cityPreference = new citySharedPrefs(MainWeatherActivity.this);
                cityPreference.setCity(oldCity);
                return null;
            }
            //Retrive the icon
            weather.iconData = weather.currentCondition.getIcon();
            Log.v("ICON DATA VALUE IS: ", String.valueOf(weather.currentCondition.getIcon()));

            //We call our ImageDownload task after the weather.iconData is set!
            IconAsyncTask downloadIconTask = new IconAsyncTask();
            downloadIconTask.execute(weather.iconData);


            return weather;
        }

        @Override
        protected void onPostExecute(dataWeather weather) {
            if(weather == null)
                return;

            super.onPostExecute(weather);
            DateFormat df = DateFormat.getTimeInstance();

            String sunrisedt = df.format(new Date(weather.place.getSunrise()));
            String sunsetdt = df.format(new Date(weather.place.getSunset()));
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());
            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + tempFormat + "°F");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            wind.setText("Wind: " + weather.wind.getSpeed() + "mph");
            sunrise.setText("Sunrise : " + sunrisedt);
            sunset.setText("Sunset: " + sunsetdt);
            conditions.setText( weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");


        }
    }


    private class IconAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("IconAsyncTask: ", "doInBackground");

            return downloadIconBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            Log.v("IconAsyncTask: ", "onPostExecute");

            // super.onPostExecute(bitmap);
            iconThumb.setImageBitmap(bitmap);
            iconThumb.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        private Bitmap downloadIconBitmap(String code) {

            Log.v("IconAsyncTask: ", "downloadIconBitmap");

            try {

                HttpURLConnection connection = (HttpURLConnection)(new URL(Utilities.URL_ICON + code + ".png")).openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("downloadIconBitmap", "Something went wrong while" +
                        " retrieving bitmap from " + Utilities.URL_ICON + e.toString());
                return null;
            }

           /* final DefaultHttpClient client = new DefaultHttpClient();
           final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");
            try {
                HttpResponse response = client.execute(getRequest);
                //check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode +
                            " while retrieving bitmap from " + Utils.ICON_URL + code + ".png");
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();
                        // decoding stream data back into image Bitmap that android understands
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for IOException
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while" +
                        " retrieving bitmap from " + Utils.ICON_URL + e.toString());
            }

            return null; */
        }
    }

}
