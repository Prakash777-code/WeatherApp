package com.example.mousam.Ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mousam.Adapter.ForecastAdapter;
import com.example.mousam.Models.HourlyForecast;
import com.example.mousam.Models.LocationResponse;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.R;
import com.example.mousam.ViewModels.WeatherViewModel;
import com.example.mousam.utils.NetworkUtils;
import com.example.mousam.utils.WeatherIconHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherViewModel viewModel;

    private TextView currentTempTv, cityStateTv, tempTv, humidityTv,
            windTv, sunriseTv, sunsetTv, tvError;
    private ImageView tempIcon;
    private RecyclerView forecastRv;
    private ForecastAdapter adapter;
    private EditText cityInput;
    private Button searchBtn;

    private boolean isWeatherRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecycler();

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        checkInternetOnStart();
        setupSearchButton();
        observeLocation();
        observeWeather();
        observeForecast();
        observeHumidity();
    }

    private void initViews() {
        currentTempTv = findViewById(R.id.currentTempTv);
        cityStateTv = findViewById(R.id.cityStateTv);
        tempTv = findViewById(R.id.tempTv);
        humidityTv = findViewById(R.id.humidityTv);
        windTv = findViewById(R.id.windTv);
        sunriseTv = findViewById(R.id.sunriseTv);
        sunsetTv = findViewById(R.id.sunsetTv);
        tempIcon = findViewById(R.id.tempIcon);
        forecastRv = findViewById(R.id.forecastRecyclerView);
        cityInput = findViewById(R.id.cityInput);
        searchBtn = findViewById(R.id.searchBtn);
        tvError = findViewById(R.id.tvError);
    }

    private void setupRecycler() {
        adapter = new ForecastAdapter();
        forecastRv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        forecastRv.setAdapter(adapter);
    }

    private void checkInternetOnStart() {
        if (!NetworkUtils.isInternetAvailable(this)) {
            tvError.setText("Please check your internet connection!");
            tvError.setVisibility(android.view.View.VISIBLE);
        } else {
            tvError.setVisibility(android.view.View.GONE);
        }
    }

    private void setupSearchButton() {
        searchBtn.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (city.isEmpty()) return;

            if (!NetworkUtils.isInternetAvailable(this)) {
                tvError.setText("No Internet");
                tvError.setVisibility(android.view.View.VISIBLE);
                clearText();
                return;
            }

            tvError.setVisibility(android.view.View.GONE);
            isWeatherRequested = false;


            viewModel.searchCity(city);
        });
    }

    private void observeLocation() {
        viewModel.getLocationLiveData().observe(this, location -> {
            if (location == null || location.lat == null || location.lon == null) {
                cityStateTv.setText("City not found");
                return;
            }

            if (!NetworkUtils.isInternetAvailable(this)) {
                tvError.setText("No Internet");
                tvError.setVisibility(android.view.View.VISIBLE);
                return;
            }

            try {
                double lat = Double.parseDouble(location.lat);
                double lon = Double.parseDouble(location.lon);

                if (!isWeatherRequested) {
                    isWeatherRequested = true;
                    viewModel.fetchWeather(lat, lon);
                }

                cityStateTv.setText(location.display_name != null ? location.display_name : "Unknown");

            } catch (Exception e) {
                cityStateTv.setText("Invalid coordinates");
            }
        });
    }

    private void observeWeather() {
        viewModel.getWeatherLiveData().observe(this, response -> {
            if (response != null) updateUI(response);
        });
    }

    private void observeForecast() {
        viewModel.getForecastLiveData().observe(this, list -> {
            if (list != null && !list.isEmpty()) adapter.setForecast(list);
        });
    }

    private void observeHumidity() {
        viewModel.getHumidityLiveData().observe(this, h -> {
            humidityTv.setText(h != null ? h.intValue() + "%" : "N/A");
        });
    }

    private void updateUI(WeatherResponse response) {
        if (response.current_weather != null) {
            if (response.current_weather.temperature != null) {
                currentTempTv.setText(response.current_weather.temperature + "°C");
                tempTv.setText(response.current_weather.temperature + "°C");
            } else {
                currentTempTv.setText("N/A");
                tempTv.setText("Temp: N/A");
            }

            windTv.setText(response.current_weather.windSpeed != null
                    ? response.current_weather.windSpeed + " km/h"
                    : "Wind: N/A");

            int code = response.current_weather.weatherCode != null
                    ? response.current_weather.weatherCode
                    : 0;

            tempIcon.setImageResource(WeatherIconHelper.getWeatherIcon(code));
        }

        if (response.daily != null) {
            if (response.daily.sunrise != null && !response.daily.sunrise.isEmpty()) {
                String sunrise = response.daily.sunrise.get(0);
                if (sunrise.length() >= 16) sunriseTv.setText(sunrise.substring(11, 16));
            }
            if (response.daily.sunset != null && !response.daily.sunset.isEmpty()) {
                String sunset = response.daily.sunset.get(0);
                if (sunset.length() >= 16) sunsetTv.setText(sunset.substring(11, 16));
            }
        }
    }

    private void clearText() {
        cityStateTv.setText("");
        currentTempTv.setText("");
        tempTv.setText("");
        humidityTv.setText("");
        windTv.setText("");
        sunriseTv.setText("");
        sunsetTv.setText("");
        tempIcon.setImageResource(R.drawable.img);
    }
}
