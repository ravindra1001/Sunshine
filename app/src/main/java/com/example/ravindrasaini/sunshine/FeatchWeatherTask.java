package com.example.ravindrasaini.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ravindrasaini.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

/**
 * Created by ravindrasaini on 5/21/16.
 */
public class FeatchWeatherTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FeatchWeatherTask.class.getSimpleName();

    public Context mContext; // not sure about this
    public FeatchWeatherTask(Context context){
        mContext = context;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */

    @Override
    protected Void doInBackground(String... params) {


        if (params.length == 0){
            return null;
        }

        String locationQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        Log.d("MainActivityFragment", "before try block");

        String format = "json";
        String units = "metric";
        int numDays = 14;
        String key = "e1aedda5e73e72ff127e3f1aec5033c3";

        try {

            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARM = "q";
            final String FORMAT_PARM = "mode";
            final String UNITS_PARM = "units";
            final String DAYS_PARM = "cnt";
            final String APPID_PARM = "APPID";

            Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARM, params[0])
                    .appendQueryParameter(FORMAT_PARM, format)
                    .appendQueryParameter(UNITS_PARM, units)
                    .appendQueryParameter(DAYS_PARM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARM,key)
                    .build();
            Log.v(LOG_TAG, "Build Uri: " + buildUri.toString());
            URL url = new URL(buildUri.toString());

            // Create the request to OpenWeatherMap, and open the connection

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                //forecastJsonStr = null;
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Forecast JSON String : " + forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }



        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        final String OWM_LIST = "list";

        final String OWM_DATETIME = "dt";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
//        final String OWM_WINDSPEED = "wind";

        final String OWM_WIND_DIRECTION = "deg";
//        final String OWM_WIND_DIRECTION = "degrees";


        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";


        try{
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject coordJSON = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = coordJSON.getLong(OWM_LATITUDE);
            double cityLongitude = coordJSON.getLong(OWM_LONGITUDE);

            Log.v(LOG_TAG, cityName + ", with coord: " + cityLatitude + " " + cityLongitude);


            long locationID = addLocation(locationQuery, cityName, cityLatitude, cityLongitude);

            Vector<ContentValues> cVVector = new Vector <ContentValues>(weatherArray.length());

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                dateTime = dayForecast.getLong(OWM_DATETIME);

                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);


                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                        WeatherContract.getDbDateString(new Date(dateTime * 1000L)));
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int rowsInserted = mContext.getContentResolver()
                        .bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,cvArray);
                Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");
            }

        }
        catch (JSONException e){
            Log.e(LOG_TAG,e.getMessage(),e);
            e.printStackTrace();
        }
        return null;
    }

    private long addLocation(String locationSetting, String cityNmae, double lat, double lon){
        Log.v(LOG_TAG,"inserting " + cityNmae + ", with coord: " + lat + ", " + lon);
        long locationId;
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null
        );

        Log.d(LOG_TAG, String.valueOf(cursor));
        if (cursor.moveToFirst()){
            Log.d(LOG_TAG,"Oooooooooooooooo");
            Log.v(LOG_TAG,"Found it in the database");
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = cursor.getLong(locationIndex);
        }
        else{
            Log.v(LOG_TAG,"Didn't find it in the database, inserting now");
            ContentValues locationValues = new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,cityNmae);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,lon);


            Uri locationInsertUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI,locationValues);
            locationId = ContentUris.parseId(locationInsertUri);
        }
        cursor.close();
        return locationId;
    }
}