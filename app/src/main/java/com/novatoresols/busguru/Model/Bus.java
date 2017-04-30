package com.novatoresols.busguru.Model;

import java.io.Serializable;

/**
 * Created by mac on 30/04/2017.
 */

public class Bus  implements Serializable{

    private String id;

    private String total_seats;

    private String updated_at;

    private String name;

    private String remaining_seats;

    private String origin;

    private String created_at;

    private String start_time;

    private String longitude;

    private String latitude;

    private String destination;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(String total_seats) {
        this.total_seats = total_seats;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemaining_seats() {
        return remaining_seats;
    }

    public void setRemaining_seats(String remaining_seats) {
        this.remaining_seats = remaining_seats;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
