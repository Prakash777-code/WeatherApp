package com.example.mousam;

import com.google.gson.annotations.SerializedName;

public class LocationResponse {
    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;

    @SerializedName("display_name")
    public String display_name;
}
