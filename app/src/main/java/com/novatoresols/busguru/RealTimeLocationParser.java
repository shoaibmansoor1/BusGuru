package com.novatoresols.busguru;

import com.google.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haider-AndroidDevice on 07/11/2016.
 */

public class RealTimeLocationParser {

    public static List<com.google.android.gms.maps.model.LatLng> locationRecords(JSONArray content) {

        try {
            List<com.google.android.gms.maps.model.LatLng> listFeeds=new ArrayList<>();

            for (int i = 0; i < content.length(); i++) {

                JSONObject jsonObject = content.getJSONObject(i);

                com.google.android.gms.maps.model.LatLng model = new com.google.android.gms.maps.model.LatLng(jsonObject.getDouble("longitude"),jsonObject.getDouble("latitude"));
                listFeeds.add(model);
            }

            return listFeeds;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
