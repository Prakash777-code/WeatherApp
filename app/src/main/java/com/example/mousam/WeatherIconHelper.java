package com.example.mousam;

public class WeatherIconHelper {
    public static int getWeatherIcon(int code){
        switch(code){
            case 0: return R.drawable.ic_sun_cloud;
            case 1: return R.drawable.ic_sun_cloud;
            case 2: return R.drawable.ic_cloudy;
            case 3: return R.drawable.ic_cloud;
            case 61: case 63: case 65: return R.drawable.ic_rain;
            case 71: case 73: case 75: return R.drawable.ic_snow;
            case 95: case 96: case 99: return R.drawable.ic_thunder;
            default: return R.drawable.ic_sun_cloud;
        }
    }
}
