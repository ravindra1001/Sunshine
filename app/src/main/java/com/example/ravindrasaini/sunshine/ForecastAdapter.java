package com.example.ravindrasaini.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link \ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0) ? VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType == VIEW_TYPE_TODAY){
            layoutId = R.layout.list_item_forecast_today;
        }
        else {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        //return LayoutInflater.from(context).inflate(layoutId, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        int  wetherId = cursor.getInt(ForecastFragement.COL_WEATHER_TYPE);

        switch (viewType){
            case VIEW_TYPE_TODAY:{
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(wetherId));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY:{
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(wetherId));
                break;
            }

        }

        String dateString = cursor.getString(ForecastFragement.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,dateString));
        String description = cursor.getString(ForecastFragement.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(description);
        boolean isMetric = Utility.isMetric(context);

        float high = cursor.getFloat(ForecastFragement.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context,high,isMetric));



        float low = cursor.getFloat(ForecastFragement.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context,low,isMetric));

    }

    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}























