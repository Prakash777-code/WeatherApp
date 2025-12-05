package com.example.mousam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<HourlyForecast> list = new ArrayList<>();

    public ForecastAdapter() {
    }

    public void setForecast(List<HourlyForecast> updatedList) {
        list = updatedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        HourlyForecast item = list.get(position);

        if (item.time != null) holder.timeTv.setText(item.time);
        else holder.timeTv.setText("N/A");

        if (item.temp != null) holder.tempTv.setText(item.temp + "°C");
        else holder.tempTv.setText("N/A");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView timeTv;
        TextView tempTv;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.timeTv);
            tempTv = itemView.findViewById(R.id.tempTv);
        }
    }
}
