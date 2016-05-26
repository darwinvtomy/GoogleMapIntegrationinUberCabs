package com.darwinvtomy.showcities.showcities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class geonamesAdapter extends RecyclerView.Adapter<geonamesAdapter.MyViewHolder> {

    private ArrayList<geonames> geonamesArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cityName, latitude, longtitude;

        public MyViewHolder(View view) {
            super(view);
            cityName = (TextView) view.findViewById(R.id.title);
            longtitude = (TextView) view.findViewById(R.id.latitude);
            latitude = (TextView) view.findViewById(R.id.longitude);
        }
    }


    public geonamesAdapter(ArrayList<geonames> moviesList) {
        this.geonamesArrayList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        geonames geonames = geonamesArrayList.get(position);
        holder.cityName.setText(geonames.getName());
        holder.longtitude.setText(geonames.getLat());
        holder.latitude.setText(geonames.getLng());
    }

    @Override
    public int getItemCount() {
        return geonamesArrayList.size();
    }
}
