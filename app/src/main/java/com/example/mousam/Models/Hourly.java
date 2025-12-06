package com.example.mousam.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Hourly {

    @SerializedName("time")
    public List<String> time;

    @SerializedName("temperature_2m")
    public List<Double> temperature;

    @SerializedName("relative_humidity_2m")
    public List<Double> relativeHumidity;

    @SerializedName("windspeed_10m")
    public List<Double> windspeed;
}
