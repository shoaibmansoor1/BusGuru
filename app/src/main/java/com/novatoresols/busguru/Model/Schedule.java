package com.novatoresols.busguru.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Haider-AndroidDevice on 28/03/2017.
 */

public class Schedule implements Parcelable {

    String startLocation, endLocation, startTime, endTime, totalSeats, availableSeats,trip_id,busId;

    public Schedule() {}

    protected Schedule(Parcel in) {
        startLocation = in.readString();
        endLocation = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        totalSeats = in.readString();
        availableSeats = in.readString();
        trip_id = in.readString();
        busId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startLocation);
        dest.writeString(endLocation);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(totalSeats);
        dest.writeString(availableSeats);
        dest.writeString(trip_id);
        dest.writeString(busId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(String totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }
}
