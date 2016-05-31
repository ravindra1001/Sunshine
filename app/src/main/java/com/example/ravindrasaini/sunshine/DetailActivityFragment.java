package com.example.ravindrasaini.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ravindrasaini.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER = 0;
    public static final String DATE_KEY = "date";
    public static final String LOCATION_KEY = "location";

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String mForecastStr;
    private String mLocation;
    private DetailActivity mActivity;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (DetailActivity) context;
    }
    public TextView dateView;


    public ImageView mIconView;// = (ImageView)rootView.findViewById(R.id.detail_icon);
    public TextView mDateView;// = (TextView) rootView.findViewById(R.id.detail_date_textview);
    public TextView mFriendlyDateView;// = (TextView) rootView.findViewById(R.id.detail_day_textview);
    public TextView mDescriptionView;// = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
    public TextView mHighTempView;// = (TextView)rootView.findViewById(R.id.detail_high_textview);
    public TextView mLowTempView;// = (TextView)rootView.findViewById(R.id.detail_low_textview);
    public TextView mHumidityView;// = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
    public TextView mWindView;// = (TextView)rootView.findViewById(R.id.detail_wind_textview);
    public TextView mPressureView;// = (TextView) rootView.findViewById(R.id.detail_pressure_textview);


    //check this function again
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconView = (ImageView)rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView)rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView)rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != savedInstanceState) {
             mLocation = savedInstanceState.getString(LOCATION_KEY); // still not sure
        }
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String dateString = getActivity().getIntent().getStringExtra(DATE_KEY);
        String[] columns = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
        };
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation,dateString);//daubt

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mLocation){
            outState.putString(LOCATION_KEY,mLocation);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            int weatherId = data.getInt(data.getColumnIndex(
                    WeatherContract.WeatherEntry.CLOUMN_WEATHER_ID
            ));
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            String date = data.getString(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT
            ));
            String friendlyDateText = Utility.getDayName(getContext(),date);
            String dateText = Utility.getFormattedMonthDay(getActivity(),date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            String description = data.getString(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
            ));
            mDescriptionView.setText(description);

            boolean isMetric = Utility.isMetric(getActivity());
            double high = data.getDouble(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP
            ));
            double low = data.getDouble(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
            ));

            String highString = Utility.formatTemperature(getActivity(),high,isMetric);
            mHighTempView.setText(highString);

            String lowString = Utility.formatTemperature(getActivity(),low,isMetric);
            mLowTempView.setText(lowString);

            float humidity = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            mHumidityView.setText(getActivity().getString(R.string.format_humidity,humidity));

            float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
            float windDirStr = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
            mWindView.setText(Utility.getFormatedWind(getActivity(),windSpeedStr,windDirStr));

            float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
            mPressureView.setText(getActivity().getString(R.string.format_pressure,pressure));

            mForecastStr = String.format("%s - %s - %s/%s",dateText,description,highString,lowString);
            mActivity.setShareData(mForecastStr);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mLocation && !mLocation.equals(Utility.getPreferredLocation(getActivity()))){
         getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
