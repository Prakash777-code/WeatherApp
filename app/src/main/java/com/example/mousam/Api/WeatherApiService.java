package com.example.mousam.Api;

import com.example.mousam.Models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    @GET("v1/forecast")
    Call<WeatherResponse> getWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current_weather") boolean currentWeather,
            @Query("hourly") String hourly,
            @Query("daily") String daily,
            @Query("forecast_days") int forecastDays,
            @Query("timezone") String timezone
    );
}
