package org.thosp.HackCBS.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.thosp.HackCBS.R;
import org.thosp.HackCBS.model.DetailedWeatherForecast;
import org.thosp.HackCBS.model.WeatherCondition;
import org.thosp.HackCBS.utils.AppPreference;
import org.thosp.HackCBS.utils.TemperatureUtil;
import org.thosp.HackCBS.utils.Utils;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class WeatherForecastItemViewHolder  extends RecyclerView.ViewHolder {

    private final String TAG = "ForecastViewHolder";

    private DetailedWeatherForecast mWeatherForecast;

    private Context mContext;

    private TextView mTime;
    private TextView mIcon;
    private TextView mTemperature;
    private TextView mApparentTemperature;
    private TextView mWind;
    private TextView windDirection;
    private TextView mHumidity;
    private TextView mPressure;
    private TextView mRainSnow;
    private TextView mDescription;

    public WeatherForecastItemViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;

        mTime = (TextView) itemView.findViewById(R.id.forecast_time);
        mIcon = (TextView) itemView.findViewById(R.id.forecast_icon);
        mTemperature = (TextView) itemView.findViewById(R.id.forecast_temperature);
        mApparentTemperature = (TextView) itemView.findViewById(R.id.forecast_apparent_temperature);
        mWind = (TextView) itemView.findViewById(R.id.forecast_wind);
        windDirection = (TextView) itemView.findViewById(R.id.forecast_wind_direction);
        mHumidity = (TextView) itemView.findViewById(R.id.forecast_humidity);
        mPressure = (TextView) itemView.findViewById(R.id.forecast_pressure);
        mRainSnow = (TextView) itemView.findViewById(R.id.forecast_rainsnow);
        mDescription = (TextView) itemView.findViewById(R.id.forecast_description);
    }

    void bindWeather(Context context,
                     double latitude,
                     DetailedWeatherForecast weather,
                     Set<Integer> visibleColumns) {
        mWeatherForecast = weather;

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        WeatherCondition weatherCondition = weather.getFirstWeatherCondition();

        if (visibleColumns.contains(1)) {
            mTime.setVisibility(View.VISIBLE);
            Date date = new Date(weather.getDateTime() * 1000);
            mTime.setText(AppPreference.getLocalizedTime(context, date));
            if (AppPreference.is12TimeStyle(context)) {
                ViewGroup.LayoutParams params=mTime.getLayoutParams();
                params.width = Utils.spToPx(85, context);
                mTime.setLayoutParams(params);
            }
        } else {
            mTime.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(2)) {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setTypeface(typeface);
            if (weatherCondition != null) {
                mIcon.setText(Utils.getStrIcon(mContext, weatherCondition.getIcon()));
            }
        } else {
            mIcon.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(3)) {
            mDescription.setVisibility(View.VISIBLE);
            if (weatherCondition != null) {
                mDescription.setText(weather.getFirstWeatherCondition().getDescription());
            }
        } else {
            mDescription.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(4)) {
            mTemperature.setVisibility(View.VISIBLE);
            String temperature = mContext.getString(R.string.temperature_with_degree,
                    TemperatureUtil.getForecastedTemperatureWithUnit(mContext, weather));
            mTemperature.setText(temperature);
        } else {
            mTemperature.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(5)) {
            mApparentTemperature.setVisibility(View.VISIBLE);
            String apparentTemperature = mContext.getString(R.string.temperature_with_degree,
                    TemperatureUtil.getForecastedApparentTemperatureWithUnit(mContext, latitude, weather));
            mApparentTemperature.setText(apparentTemperature);
        } else {
            mApparentTemperature.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(6)) {
            mWind.setVisibility(View.VISIBLE);
            mWind.setText(AppPreference.getWindInString(mContext, weather.getWindSpeed()));
        } else {
            mWind.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(7)) {
            windDirection.setVisibility(View.VISIBLE);
            windDirection.setText(AppPreference.getWindDirection(mContext, weather.getWindDegree()));
        } else {
            windDirection.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(8)) {
            mRainSnow.setVisibility(View.VISIBLE);
            boolean noRain = weather.getRain() < 0.1;
            boolean noSnow = weather.getSnow() < 0.1;
            if (noRain && noSnow) {
                mRainSnow.setText("");
            } else {
                String rain = AppPreference.getFormatedRainOrSnow(context, weather.getRain());
                String snow = AppPreference.getFormatedRainOrSnow(context, weather.getSnow());
                if (!noRain && !noSnow) {
                    mRainSnow.setText(rain + "/" + snow);
                } else if (noSnow) {
                    mRainSnow.setText(rain);
                } else {
                    mRainSnow.setText(snow);
                }
            }
            ViewGroup.LayoutParams params=mRainSnow.getLayoutParams();
            params.width = Utils.spToPx(AppPreference.getRainOrSnowForecastWeadherWidth(context), context);
            mRainSnow.setLayoutParams(params);
        } else {
            mRainSnow.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(9)) {
            mHumidity.setVisibility(View.VISIBLE);
            mHumidity.setText(String.format(Locale.getDefault(), "%d", weather.getHumidity()));
        } else {
            mHumidity.setVisibility(View.GONE);
        }
        if (visibleColumns.contains(10)) {
            mPressure.setVisibility(View.VISIBLE);
            mPressure.setText(AppPreference.getPressureInString(mContext, weather.getPressure()));
        } else {
            mPressure.setVisibility(View.GONE);
        }
    }
}