package com.example.mousam.Api;

import com.example.mousam.Models.LocationResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominationApiService {
    @GET("search")
    Call<List<LocationResponse>> getLocation(
            @Query("q") String city,
            @Query("format") String format
    );
}
