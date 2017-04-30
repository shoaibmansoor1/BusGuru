package com.novatoresols.busguru.adapter;

/**
 * Created by Haider-AndroidDevice on 28/03/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novatoresols.busguru.Model.Bus;
import com.novatoresols.busguru.R;

import java.util.ArrayList;

public class ScheduledAdapter extends RecyclerView.Adapter<ScheduledAdapter.MyViewHolder> {

    private ArrayList<Bus> busAdapter;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView startLocation, endLocation, startTime, endTime, totalSeats, availableSeats;

        public MyViewHolder(View view) {
            super(view);
            startLocation = (TextView) view.findViewById(R.id.startLocation);
            startTime = (TextView) view.findViewById(R.id.startTime);
            endLocation = (TextView) view.findViewById(R.id.endLocation);
            endTime = (TextView) view.findViewById(R.id.endTime);
            totalSeats = (TextView) view.findViewById(R.id.total_seats_textview);
            availableSeats = (TextView) view.findViewById(R.id.available_seats_textview);

        }

    }


    public ScheduledAdapter(ArrayList<Bus> BusAdapter) {
        this.busAdapter = BusAdapter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bus obj = busAdapter.get(position);
        holder.startLocation.setText(obj.getOrigin());
        holder.startTime.setText(obj.getStart_time());
        holder.endLocation.setText(obj.getDestination());
//        holder.endTime.setText(obj.get());
        holder.totalSeats.setText("Total:"+ obj.getTotal_seats());
        holder.availableSeats.setText("Free:"+obj.getRemaining_seats());
    }

    @Override
    public int getItemCount() {
        return busAdapter.size();
    }
}
