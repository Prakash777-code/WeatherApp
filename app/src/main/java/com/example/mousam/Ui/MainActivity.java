package com.example.mousam.Ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mousam.Adapter.ForecastAdapter;
import com.example.mousam.Models.HourlyForecast;
import com.example.mousam.Models.LocationResponse;
import com.example.mousam.Models.WeatherResponse;
import com.example.mousam.R;
import com.example.mousam.Repository.CityRepository;
import com.example.mousam.ViewModels.WeatherViewModel;
import com.example.mousam.utils.WeatherIconHelper;



import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherViewModel viewModel;
    private TextView currentTempTv, cityStateTv, tempTv, humidityTv, windTv, sunriseTv, sunsetTv, tvError;
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
        forecastRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        forecastRv.setAdapter(adapter);
    }

    private void checkInternetOnStart() {
        if (!isInternetOn()) {
            tvError.setText("Please check your internet connection!");
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
        }
    }

    private void setupSearchButton() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (city.length() == 0) return;

                if (!isInternetOn()) {
                    tvError.setText("No Internet");
                    tvError.setVisibility(View.VISIBLE);
                    clearText();
                    return;
                }

                tvError.setVisibility(View.GONE);
                cityRepository.getLocation(city, locationLiveData);
            }
        });
    }

    private void observeLocation() {
        locationLiveData.observe(this, new Observer<LocationResponse>() {
            @Override
            public void onChanged(LocationResponse location) {
                if (location != null && location.lat != null && location.lon != null) {
                    try {
                        double lat = Double.parseDouble(location.lat);
                        double lon = Double.parseDouble(location.lon);

                        viewModel.fetchWeather(lat, lon);

                        if (location.display_name != null) {
                            cityStateTv.setText(location.display_name);
                        } else {
                            cityStateTv.setText("Unknown");
                        }

                    } catch (Exception e) {
                        cityStateTv.setText("Invalid coordinates");
                    }
                } else {
                    cityStateTv.setText("City not found");
                }
            }
        });
    }

    private void observeWeather() {
        viewModel.getWeatherLiveData().observe(this, new Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse response) {
                if (response != null) {
                    updateUI(response);
                }
            }
        });
    }

    private void observeForecast() {
        viewModel.getForecastLiveData().observe(this, new Observer<List<HourlyForecast>>() {
            @Override
            public void onChanged(List<HourlyForecast> list) {
                if (list != null && list.size() > 0) {
                    adapter.setForecast(list);
                }
            }
        });
    }

    private void observeHumidity() {
        viewModel.getHumidityLiveData().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double h) {
                if (h != null) {
                    humidityTv.setText(h.intValue() + "%");
                } else {
                    humidityTv.setText("N/A");
                }
            }
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

            if (response.current_weather.windSpeed != null) {
                windTv.setText(response.current_weather.windSpeed + " km/h");
            } else {
                windTv.setText("Wind: N/A");
            }

            int code = 0;
            if (response.current_weather.weatherCode != null) {
                try {
                    code = response.current_weather.weatherCode;
                } catch (Exception ignored) {}
            }
            tempIcon.setImageResource(WeatherIconHelper.getWeatherIcon(code));
        }

        if (response.daily != null) {
            if (response.daily.sunrise != null && response.daily.sunrise.size() > 0) {
                sunriseTv.setText(response.daily.sunrise.get(0).substring(11, 16));
            } else {
                sunriseTv.setText("Sunrise: N/A");
            }

            if (response.daily.sunset != null && response.daily.sunset.size() > 0) {
                sunsetTv.setText(response.daily.sunset.get(0).substring(11, 16));
            } else {
                sunsetTv.setText("Sunset: N/A");
            }
        }
    }

    public boolean isInternetOn() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void clearText() {
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
