package com.novatoresols.busguru.adapter;

/**
 * Created by Haider-AndroidDevice on 28/03/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novatoresols.busguru.Model.Schedule;
import com.novatoresols.busguru.R;

import java.util.List;

public class ScheduledAdapter extends RecyclerView.Adapter<ScheduledAdapter.MyViewHolder> {

    private List<Schedule> ScheduledAdapter;

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


    public ScheduledAdapter(List<Schedule> ScheduledAdapter) {
        this.ScheduledAdapter = ScheduledAdapter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Schedule obj = ScheduledAdapter.get(position);
        holder.startLocation.setText(obj.getStartLocation());
        holder.startTime.setText(obj.getStartTime());
        holder.endLocation.setText(obj.getEndLocation());
        holder.endTime.setText(obj.getEndTime());
        holder.totalSeats.setText("Total:"+ obj.getTotalSeats());
        holder.availableSeats.setText("Free:"+obj.getAvailableSeats());
    }

    @Override
    public int getItemCount() {
        return ScheduledAdapter.size();
    }
}
