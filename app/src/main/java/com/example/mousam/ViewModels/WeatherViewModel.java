package com.example.mousam.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.mousam.Models.CurrentWeather;
import com.example.mousam.Models.Hourly;
import com.example.mousam.Models.HourlyForecast;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.Repository.WeatherRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class WeatherViewModel extends ViewModel {

    private WeatherRepository repository = new WeatherRepository();
    private MutableLiveData<WeatherResponse> weatherLiveData = new MutableLiveData<>();
    private MutableLiveData<List<HourlyForecast>> forecastLiveData = new MutableLiveData<>();
    private MutableLiveData<Double> humidityLiveData = new MutableLiveData<>();

    public void fetchWeather(double lat, double lon) {
        final LiveData<WeatherResponse> source = repository.getWeather(lat, lon);

        source.observeForever(new Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse response) {
                weatherLiveData.postValue(response);
                processForecast(response);
                processHumidity(response);
                source.removeObserver(this);
            }
        });
    }

    private void processForecast(final WeatherResponse response) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<HourlyForecast> list = new ArrayList<>();
                if (response == null || response.hourly == null || response.current_weather == null) {
                    forecastLiveData.postValue(list);
                    return;
                }

                String currentTime = response.current_weather.time;
                String matchHour = "";
                if (currentTime != null && currentTime.length() >= 13) {
                    matchHour = currentTime.substring(0, 13);
                }

                int index = 0;
                for (int i = 0; i < response.hourly.time.size(); i++) {
                    String t = response.hourly.time.get(i);
                    if (t != null && t.startsWith(matchHour)) {
                        index = i;
                        break;
                    }
                }

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                SimpleDateFormat output = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                for (int i = 0; i < 4; i++) {
                    int pos = index + i;
                    if (pos >= response.hourly.time.size()) {
                        break;
                    }

                    String raw = response.hourly.time.get(pos);
                    String formatted = raw;
                    try {
                        Date d = input.parse(raw);
                        if (d != null) {
                            formatted = output.format(d);
                        }
                    } catch (Exception ignored) {}

                    Double temp = null;
                    if (response.hourly.temperature != null && pos < response.hourly.temperature.size()) {
                        temp = response.hourly.temperature.get(pos);
                    }

                    list.add(new HourlyForecast(formatted, temp));
                }

                forecastLiveData.postValue(list);
            }
        });
    }

    private void processHumidity(WeatherResponse response) {
        if (response == null || response.hourly == null || response.current_weather == null) {
            humidityLiveData.postValue(null);
            return;
        }

        String currentTime = response.current_weather.time;
        String matchHour = "";
        if (currentTime != null && currentTime.length() >= 13) {
            matchHour = currentTime.substring(0, 13);
        }

        int index = 0;
        for (int i = 0; i < response.hourly.time.size(); i++) {
            String t = response.hourly.time.get(i);
            if (t != null && t.startsWith(matchHour)) {
                index = i;
                break;
            }
        }

        Double humidity = null;
        if (response.hourly.relativeHumidity != null && index < response.hourly.relativeHumidity.size()) {
            humidity = response.hourly.relativeHumidity.get(index);
        }

        humidityLiveData.postValue(humidity);
    }

    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherLiveData;
    }

    public LiveData<List<HourlyForecast>> getForecastLiveData() {
        return forecastLiveData;
    }

    public LiveData<Double> getHumidityLiveData() {
        return humidityLiveData;
    }
}
