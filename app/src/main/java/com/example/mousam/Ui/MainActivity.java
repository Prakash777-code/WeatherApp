package com.example.mousam.Ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mousam.Adapter.ForecastAdapter;
import com.example.mousam.Models.HourlyForecast;
import com.example.mousam.Models.LocationResponse;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.R;
import com.example.mousam.Repository.CityRepository;
import com.example.mousam.Utils.WeatherIconHelper;
import com.example.mousam.ViewModels.WeatherViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherViewModel viewModel;
    private TextView currentTempTv;
    private TextView cityStateTv;
    private TextView tempTv;
    private TextView humidityTv;
    private TextView windTv;
    private TextView sunriseTv;
    private TextView sunsetTv;
    private ImageView tempIcon;
    private RecyclerView forecastRv;
    private ForecastAdapter adapter;
    private EditText cityInput;
    private Button searchBtn;
    private CityRepository cityRepository = new CityRepository();
    private MutableLiveData<LocationResponse> locationLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        adapter = new ForecastAdapter();
        forecastRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        forecastRv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        searchBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    cityRepository.getLocation(city, locationLiveData);
                }
            }
        });

        locationLiveData.observe(this, new androidx.lifecycle.Observer<LocationResponse>() {
            @Override
            public void onChanged(LocationResponse location) {
                if (location != null && location.lat != null && location.lon != null) {
                    try {
                        double lat = Double.parseDouble(location.lat);
                        double lon = Double.parseDouble(location.lon);
                        viewModel.fetchWeather(lat, lon);
                        if (location.display_name != null) cityStateTv.setText(location.display_name);
                        else cityStateTv.setText("Unknown");
                    } catch (Exception e) {
                        cityStateTv.setText("Invalid coordinates");
                    }
                } else {
                    cityStateTv.setText("City not found");
                }
            }
        });

        viewModel.getWeatherLiveData().observe(this, new androidx.lifecycle.Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse response) {
                if (response != null) updateUI(response);
            }
        });
    }

    private void updateUI(WeatherResponse response) {

        if (response.current_weather != null) {
            if (response.current_weather.temperature != null) currentTempTv.setText(response.current_weather.temperature + "°C");
            else currentTempTv.setText("N/A");

            if (response.current_weather.temperature != null) tempTv.setText("Temp: " + response.current_weather.temperature + "°C");
            else tempTv.setText("Temp: N/A");

            if (response.current_weather.windSpeed != null) windTv.setText("Wind: " + response.current_weather.windSpeed + " km/h");
            else windTv.setText("Wind: N/A");

            int code = 0;
            if (response.current_weather.weatherCode != null) {
                try { code = Integer.parseInt(response.current_weather.weatherCode.toString()); }
                catch (Exception ignored) {}
            }
            tempIcon.setImageResource(WeatherIconHelper.getWeatherIcon(code));
        }

        int index = -1;
        if (response.hourly != null && response.current_weather != null) {
            String t = response.current_weather.time;
            for (int i = 0; i < response.hourly.time.size(); i++) {
                if (response.hourly.time.get(i).equals(t)) {
                    index = i;
                    break;
                }
            }
        }
        if (index == -1) index = 0;

        if (response.hourly != null) {
            if (response.hourly.relativeHumidity != null) humidityTv.setText("Humidity: " + response.hourly.relativeHumidity.get(index) + "%");
            else humidityTv.setText("Humidity: N/A");

            if (response.hourly.windspeed != null) windTv.setText("Wind: " + response.hourly.windspeed.get(index) + " km/h");
            else windTv.setText("Wind: N/A");
        }

        if (response.daily != null) {
            if (response.daily.sunrise != null && response.daily.sunrise.size() > 0) {
                sunriseTv.setText("Sunrise: " + response.daily.sunrise.get(0).substring(11, 16));
            } else sunriseTv.setText("Sunrise: N/A");

            if (response.daily.sunset != null && response.daily.sunset.size() > 0) {
                sunsetTv.setText("Sunset: " + response.daily.sunset.get(0).substring(11, 16));
            } else sunsetTv.setText("Sunset: N/A");
        }

        List<HourlyForecast> nextFour = viewModel.getNextFourHours(response);
        if (nextFour != null && nextFour.size() > 0) adapter.setForecast(nextFour);
    }
}
