package com.example.mousam;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {

    private final WeatherApiService apiService;

    public WeatherRepository() {
        apiService = RetrofitClient.getInstance("https://api.open-meteo.com/")
                .create(WeatherApiService.class);
    }

    public void getWeather(double lat, double lon, MutableLiveData<WeatherResponse> liveData) {

        Call<WeatherResponse> call = apiService.getWeather(
                lat,
                lon,
                true,
                "temperature_2m,relative_humidity_2m,windspeed_10m",
                "sunrise,sunset",
                1,
                "auto"
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }
}
