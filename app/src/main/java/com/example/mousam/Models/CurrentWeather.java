package com.example.mousam.Models;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {

    public Double temperature;

    @SerializedName("windspeed")
    public Double windSpeed;

    @SerializedName("weathercode")
    public Integer weatherCode;

    public String time;
}
