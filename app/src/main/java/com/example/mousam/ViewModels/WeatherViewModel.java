package com.example.mousam.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mousam.Models.HourlyForecast;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.Repository.WeatherRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherViewModel extends ViewModel {

    private final WeatherRepository repository = new WeatherRepository();
    private final MutableLiveData<WeatherResponse> weatherLiveData = new MutableLiveData<>();

    public void fetchWeather(double lat, double lon) {
        repository.getWeather(lat, lon, weatherLiveData);
    }

    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherLiveData;
    }

    public List<HourlyForecast> getNextFourHours(WeatherResponse response) {
        List<HourlyForecast> list = new ArrayList<>();

        if (response == null) return list;
        if (response.hourly == null) return list;
        if (response.current_weather == null) return list;

        String currentTime = response.current_weather.time;
        String matchHour = "";

        if (currentTime != null && currentTime.length() >= 13) {
            matchHour = currentTime.substring(0, 13);
        }

        int index = -1;
        int size = response.hourly.time.size();

        for (int i = 0; i < size; i++) {
            String t = response.hourly.time.get(i);
            if (t != null && t.length() >= 13) {
                if (t.substring(0, 13).equals(matchHour)) {
                    index = i;
                    break;
                }
            }
        }

        if (index == -1) index = 0;

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for (int i = 0; i < 4; i++) {
            int pos = index + i;
            if (pos >= size) break;

            String raw = response.hourly.time.get(pos);
            String formatted = raw;

            try {
                Date d = input.parse(raw);
                if (d != null) formatted = output.format(d);
            } catch (Exception ignored) {}

            Double temp = null;
            if (response.hourly.temperature != null) {
                if (pos < response.hourly.temperature.size()) {
                    temp = response.hourly.temperature.get(pos);
                }
            }

            HourlyForecast hf = new HourlyForecast(formatted, temp);
            list.add(hf);
        }

        return list;
    }
}
