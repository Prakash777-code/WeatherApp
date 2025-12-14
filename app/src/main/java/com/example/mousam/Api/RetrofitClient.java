package com.example.mousam.Api;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    public static final String NOMINATIM_BASE_URL =
            "https://nominatim.openstreetmap.org/";

    public static final String WEATHER_BASE_URL =
            "https://api.open-meteo.com/";

    private static Retrofit nominatimRetrofit;
    private static Retrofit weatherRetrofit;

    private static OkHttpClient getHttpClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .header("User-Agent", "MousamApp/1.0")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }

    public static Retrofit getNominatimClient() {

        if (nominatimRetrofit == null) {
            nominatimRetrofit = new Retrofit.Builder()
                    .baseUrl(NOMINATIM_BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return nominatimRetrofit;
    }

    public static Retrofit getWeatherClient() {

        if (weatherRetrofit == null) {
            weatherRetrofit = new Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return weatherRetrofit;
    }
}
