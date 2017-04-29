package com.novatoresols.busguru.adapter;

import com.novatoresols.busguru.Model.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haider-AndroidDevice on 01/04/2017.
 */

public class ScheduledParser {

    public static List<Schedule> scheduledRecords(JSONArray jsonArray){

        List<Schedule> scheduledList  = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){

            try {
                JSONObject object = jsonArray.getJSONObject(i);
                Schedule schedule = new Schedule();
                schedule.setStartLocation(object.getString("startLocation"));
                schedule.setEndLocation(object.getString("endLocation"));
                schedule.setStartTime(object.getString("startTime"));
                schedule.setEndTime(object.getString("endTime"));
                schedule.setTotalSeats(object.getString("totalseats"));
                schedule.setAvailableSeats(object.getString("availableSeats"));
                schedule.setBusId(object.getString("busId"));

                scheduledList.add(schedule);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return scheduledList;
    }

}
