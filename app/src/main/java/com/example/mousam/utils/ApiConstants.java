package com.example.mousam.utils;

public final class ApiConstants {

    private ApiConstants() {}

    public static final String BASE_URL = "https://api.open-meteo.com/";
    public static final boolean CURRENT_WEATHER = true;
    public static final String HOURLY_PARAMS = "temperature_2m,relative_humidity_2m,windspeed_10m";
    public static final String DAILY_PARAMS = "sunrise,sunset";
    public static final int FORECAST_DAYS = 1;
    public static final String TIMEZONE = "auto";
}
