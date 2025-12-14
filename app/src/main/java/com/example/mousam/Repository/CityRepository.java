package com.example.mousam.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mousam.Api.NominationApiService;
import com.example.mousam.Api.RetrofitClient;
import com.example.mousam.Models.LocationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityRepository {

    private final NominationApiService apiService;

    public CityRepository() {
        apiService = RetrofitClient
                .getNominatimClient()
                .create(NominationApiService.class);
    }

    public LiveData<LocationResponse> getLocation(String city) {
        MutableLiveData<LocationResponse> liveData = new MutableLiveData<>();

        if (city == null || city.trim().isEmpty()) {
            liveData.postValue(null);
            return liveData;
        }

        Call<List<LocationResponse>> call = apiService.getLocation(city, "json");
        call.enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(Call<List<LocationResponse>> call, Response<List<LocationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveData.postValue(response.body().get(0));
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}
