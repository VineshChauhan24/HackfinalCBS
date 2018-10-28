package org.thosp.HackCBS.model;

public interface WeatherForecastResultHandler {
    void processResources(CompleteWeatherForecast completeWeatherForecast, long lastUpdate);
    void processError(Exception e);
}
