package com.example.mousam.Repository;

import androidx.lifecycle.MutableLiveData;

import com.example.mousam.Models.LocationResponse;
import com.example.mousam.Api.NominationApiService;

import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityRepository {

    private NominationApiService apiService;

    public CityRepository() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("User-Agent", "MousamApp/1.0")
                                .build()
                ))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(NominationApiService.class);
    }

    public void getLocation(String city, MutableLiveData<LocationResponse> liveData) {
        if (city == null || city.trim().isEmpty()) {
            liveData.postValue(null);
            return;
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
    }
}
