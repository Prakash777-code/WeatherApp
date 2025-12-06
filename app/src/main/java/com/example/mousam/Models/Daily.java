package com.example.mousam.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Daily {

    public List<String> sunrise;
    public List<String> sunset;

    @SerializedName("uv_index_max")
    public List<Double> uv_index_max;
}
