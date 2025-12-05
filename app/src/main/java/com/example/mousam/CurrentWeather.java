package com.example.mousam;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {

    public Double temperature;

    @SerializedName("windspeed")
    public Double windSpeed;

    @SerializedName("weathercode")
    public Integer weatherCode;

    public String time;
}
