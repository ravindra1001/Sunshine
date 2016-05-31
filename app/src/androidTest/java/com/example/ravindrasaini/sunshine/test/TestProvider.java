package com.example.ravindrasaini.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.ravindrasaini.sunshine.data.WeatherContract;
import com.example.ravindrasaini.sunshine.data.WeatherDbHelper;

import junit.framework.AssertionFailedError;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ravindrasaini on 5/20/16.
 */
public class TestProvider extends AndroidTestCase{

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public void testDeleteDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }

    public String TEST_CITY_NAME = "North Pole";

    ContentValues getLocationContentValues(){

        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,testLongitude);
        return values;
    }

    static public ContentValues getWeatherContentValues(long locationRowId){
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,"Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,5.5);
        weatherValues.put(WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID,321);

        return  weatherValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor){
        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry:valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue,valueCursor.getString(idx));
        }
    }

/*    public void testGetType(){
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE,type);
        String testDate = "20140612";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE,type);
    }*/

    public void testInsertRead(){
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values  = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG,"New row id:" + locationRowId);

//        String[] columns = {
//                WeatherContract.LocationEntry._ID,
//                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
//                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
//                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
//                WeatherContract.LocationEntry.COLUMN_COORD_LONG
//        };

        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()){
            validateCursor(values,cursor);
//        int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
//        String location = cursor.getString(locationIndex);
//
//        int nameIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_CITY_NAME));
//        String name = cursor.getString(nameIndex);
//
//        int latIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LAT));
//        double latitude = cursor.getDouble(latIndex);
//
//        int longIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LONG));
//        double longitude = cursor.getDouble(longIndex);
//
//        assertEquals(testName,name);
//        assertEquals(testLocationSetting,location);
//        assertEquals(testLatitude, latitude);
//        assertEquals(testLongitude, longitude);

//        ContentValues weatherValues = new ContentValues();
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,1.1);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,1.2);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,1.3);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,75);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,65);
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,"Asteroids");
//        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,5.5);
//        weatherValues.put(WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID,321);


            ContentValues weatherValues = getWeatherContentValues(locationRowId);

            long weatherRowId;
            weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,weatherValues);
            assertTrue(weatherRowId == -1);

            Cursor weatherCursor = db.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );



//        String[] columns1 = {
//                WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
//                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
//                WeatherContract.WeatherEntry.COLUMN_DEGREES,
//                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
//                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
//                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
//                WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID
//        };

//        Cursor weatherCursor = db.query(
//                WeatherContract.WeatherEntry.TABLE_NAME,
//                columns1,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
            if (weatherCursor.moveToFirst()){
                validateCursor(weatherValues,weatherCursor);
            /*int dateIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
            String date = weatherCursor.getString(dateIndex);

            int degreeIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
            double degrees = weatherCursor.getDouble(degreeIndex);

            int humidIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
            double humidity = weatherCursor.getDouble(humidIndex);

            int pressureIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
            double pressure = weatherCursor.getDouble(pressureIndex);

            int maxIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            double max = weatherCursor.getDouble(maxIndex);

            int minIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            double min = weatherCursor.getDouble(minIndex);


            int descIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
            double desc = weatherCursor.getDouble(descIndex);

            int windIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
            double windSpeed = weatherCursor.getDouble(windIndex);

            int weatherIdIndex = weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID);
            double weather_id = weatherCursor.getDouble(weatherIdIndex);
        }else {
            try {
                fail("no weather data returned");
            } catch (AssertionFailedError e) {}*/
            }
        }
        else {
            fail("No values returned :(");
        }

    }
}
