package com.example.mousam.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mousam.Api.RetrofitClient;
import com.example.mousam.Api.WeatherApiService;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.utils.ApiConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {

    private final WeatherApiService apiService;

    public WeatherRepository() {
        apiService = RetrofitClient
                .getWeatherClient()
                .create(WeatherApiService.class);
    }

    public LiveData<WeatherResponse> getWeather(double lat, double lon) {

        final MutableLiveData<WeatherResponse> liveData = new MutableLiveData<>();

        Call<WeatherResponse> call = apiService.getWeather(
                lat,
                lon,
                ApiConstants.CURRENT_WEATHER,
                ApiConstants.HOURLY_PARAMS,
                ApiConstants.DAILY_PARAMS,
                ApiConstants.FORECAST_DAYS,
                ApiConstants.TIMEZONE
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call,
                                   Response<WeatherResponse> response) {

                if (response.isSuccessful()) {
                    WeatherResponse body = response.body();
                    if (body != null) {
                        liveData.postValue(body);
                    } else {
                        liveData.postValue(null);
                    }
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}
