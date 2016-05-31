package com.example.ravindrasaini.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ravindrasaini.sunshine.data.WeatherContract;
import com.example.ravindrasaini.sunshine.sync.SunshineSyncAdapter;

import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragement extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mLocation;
    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_SETTING = 5;
    public static final int COL_WEATHER_TYPE = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;


    //    private ArrayAdapter<String> mForecastAdapter;
    private ForecastAdapter mForecastAdapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
    }

    public ForecastFragement() {
    }


    private String formatHighLows(double high, double low){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = sharedPrefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));

        if(unitType.equals(getString(R.string.pref_units_imperial))){
            high = (high*1.8)+32;
            low = (low*1.8)+32;
        }
        else if (!unitType.equals(getString(R.string.pref_units_metric))){
            Log.d("LOG_TAG","Unit type not found" + unitType);
        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    @Override
    public void onStart() {
        //updateWeather();
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragement, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);
        listView.setAdapter(mForecastAdapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                SimpleCursorAdapter adapter = (SimpleCursorAdapter) adapterView.getAdapter();
                ForecastAdapter adapter = (ForecastAdapter) adapterView.getAdapter();
                Cursor cursor = adapter.getCursor();

                if(null != cursor && cursor.moveToPosition(position)){
                    boolean isMetric = Utility.isMetric(getActivity());
                    String forecast = String.format("%s - %s - %s/%s",
                            Utility.formatDate(cursor.getString(COL_WEATHER_DATE)),
                            cursor.getString(COL_WEATHER_DESC),
                            Utility.formatTemperature(getContext(),cursor.getDouble(COL_WEATHER_MAX_TEMP),isMetric),
                            Utility.formatTemperature(getContext(),cursor.getDouble(COL_WEATHER_MIN_TEMP),isMetric)
                    );

                    Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra(DetailActivityFragment.DATE_KEY,cursor.getString(COL_WEATHER_DATE));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)){
            //updateWeather();
            getLoaderManager().restartLoader(FORECAST_LOADER,null,this);

        }
    }

    private void openPreferredLocationInMap(){
    if (null!= mForecastAdapter){
        Cursor c = mForecastAdapter.getCursor();
        if (null != c){
            c.moveToPosition(0);
            String posLat = c.getString(COL_COORD_LAT);
            String posLong = c.getString(COL_COORD_LONG);
            Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);


            Intent chooserIntent = Intent.createChooser(intent, "Choose map application");
            startActivity(chooserIntent);
        }
    }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startDate = WeatherContract.getDbDateString(new Date());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(mLocation,startDate);
        Log.d("Forecast frogment"," Uri : "+ weatherForLocationUri.toString());
        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }
}


