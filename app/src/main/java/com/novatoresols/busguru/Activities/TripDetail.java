package com.novatoresols.busguru.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.SnappedPoint;
import com.novatoresols.busguru.Model.Schedule;
import com.novatoresols.busguru.R;
import com.novatoresols.busguru.RealTimeLocationParser;
import com.novatoresols.busguru.Utils.AppApiUrls;
import com.novatoresols.busguru.Utils.SVProgressHUD;
import com.novatoresols.busguru.Utils.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Haider-AndroidDevice on 29/03/2017.
 */
public class TripDetail extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {


    Timer timer;
    String date = "",email;
    private GoogleMap mMap;
    List<SnappedPoint> mSnappedPoints;
    List<com.google.android.gms.maps.model.LatLng> mCapturedLocations;
    private static final int PAGE_SIZE_LIMIT = 100;
    private static final int PAGINATION_OVERLAP = 5;
    private GeoApiContext mGeoApiContext;
    final Handler handler = new Handler();

    TextView title,startLocation,endLocation,startTime,endTime,total_seats_textview;
    ImageView book_trip;
    Schedule schedule;
    SharedPreferences shf;
    boolean booked = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail);

//        schedule = (Schedule) getIntent().getSerializableExtra("scheduleObj");
        schedule = getIntent().getExtras().getParcelable("scheduleObj");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeoApiContext = new GeoApiContext().setApiKey(getString(R.string.google_map_api));
        mCapturedLocations = new ArrayList<>();

        startLocation = (TextView) findViewById(R.id.startLocation);
        endLocation = (TextView) findViewById(R.id.endLocation);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);

        book_trip = (ImageView) findViewById(R.id.book_trip);
        book_trip.setOnClickListener(this);


        //Setting Views
        if (schedule!=null){
            startLocation.setText(schedule.getStartLocation());
            endLocation.setText(schedule.getEndLocation());
            startTime.setText(schedule.getStartTime());
            endTime.setText(schedule.getEndTime());
        }

        shf = getSharedPreferences("SignupCredentials", Context.MODE_PRIVATE);
        email = shf.getString("fbemail","");

        if (booked){
//            getRealTimeLocationData(AppApiUrls.GetRealTimeLocation+schedule.getBusId());
            getRealTimeLocationData(AppApiUrls.GetRealTimeLocation);
//            callAsynchronousTask();
        }else{
            Toast.makeText(getApplicationContext(),"No Trip Booked Yet please book it right now", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        com.google.android.gms.maps.model.LatLng sydney = new com.google.android.gms.maps.model.LatLng(31.5509, 74.3532);

        Marker marker1 =  mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Zaman Park, Lahore, Pakistan").draggable(false));
        marker1.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(sydney).tilt(50).zoom(13).bearing(0).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void getRealTimeLocationData(String url) {

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String a = result.getString("success");

                        if (a.equalsIgnoreCase("true")) {
                            //Hide progress and Enable screen to touch
                            SVProgressHUD.dismiss(getApplicationContext());
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//                            date = result.getString("date");

                            JSONArray latlong = result.getJSONArray("tripLocations");

                            mCapturedLocations = RealTimeLocationParser.locationRecords(latlong);
                            Log.d("", "mCapturedLocations SIZE: " + mCapturedLocations.size());


                            ArrayList<com.google.android.gms.maps.model.LatLng> latLngs = new ArrayList<com.google.android.gms.maps.model.LatLng>();
                            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                            for (int i = 0; i < mCapturedLocations.size(); i++) { // TODO: clean up this code
                                com.google.android.gms.maps.model.LatLng latLng = (com.google.android.gms.maps.model.LatLng) mCapturedLocations.get(i);
                                com.google.android.gms.maps.model.LatLng p = new com.google.android.gms.maps.model.LatLng(latLng.latitude, latLng.longitude);
                                latLngs.add(p);
                            }
//
                            for (com.google.android.gms.maps.model.LatLng point : latLngs) {
                                boundsBuilder.include(point);
                            }
//                            draw polyline on googleMap
                            PolylineOptions rectOptions = new PolylineOptions()
                                    .color(Color.BLUE)
                                    .width(5)
                                    .visible(true)
                                    .zIndex(30)
                                    .addAll(latLngs);

                            mMap.addPolyline(rectOptions);
                            LatLngBounds bounds = boundsBuilder.build();

                            int padding = 40;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.moveCamera(cu);
                            mMap.animateCamera(cu, 2000, null);




//                            if (!(mCapturedLocations.size() ==0)){
//                                SnapToRoad task= new SnapToRoad();
//                                task.mTaskSnapToRoads.execute();
//                            }
                        } else {

//                            if (result.has("tripLocations")){
//
//                                //Get data from json obj
//                                JSONObject data = result.getJSONObject("tripLocations");
//
//                                com.google.android.gms.maps.model.LatLng endingPoint = new com.google.android.gms.maps.model.LatLng(data.getDouble("latitude"), data.getDouble("longitude"));
//
//                                Marker pos_Marker_last =  mMap.addMarker(new MarkerOptions().position(endingPoint).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Last Location").draggable(false));
//                                pos_Marker_last.showInfoWindow();
//                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(endingPoint, 40);
//                                mMap.animateCamera(cameraUpdate);
//
//                                //Hide progress and Enable screen to touch
//                                SVProgressHUD.dismiss(getApplicationContext());
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Toast.makeText(getApplicationContext(), a + "", Toast.LENGTH_SHORT).show();
//
//                            }else{
//                                //Hide progress and Enable screen to touch
//                                SVProgressHUD.dismiss(getApplicationContext());
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Toast.makeText(getApplicationContext(), a + "", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, url, null, null, null, TripDetail.this);
        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.no_connec));
            alertDialogBuilder.setMessage(getResources().getString(R.string.offline));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
//                    getRealTimeLocationData(AppApiUrls.GetRealTimeLocation +busId );
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //To Set Text Alignment at LEFT while localization Must call show() prior to fetching views
            TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.LEFT);

            TextView titleView = (TextView) alertDialog.findViewById(getApplicationContext().getResources().getIdentifier("alertTitle", "id", "android"));
            if (titleView != null) {
                titleView.setGravity(Gravity.LEFT);
            }
        }
    }

    public void callAsynchronousTask() {

        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

//                            getRealTimeLocationData(AppApiUrls.GetRealTimeLocation);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 30000); //execute in every one minute
    }

    /**
     * Snaps the points to their most likely position on roads using the Roads API.
     */

    private List<SnappedPoint> snapToRoads(GeoApiContext context) throws Exception {
        List<SnappedPoint> snappedPoints = new ArrayList<>();

        int offset = 0;
        while (offset < mCapturedLocations.size()) {
            // Calculate which points to include in this request. We can't exceed the APIs
            // maximum and we want to ensure some overlap so the API can infer a good location for
            // the first few points in each request.
            if (offset > 0) {
                offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, mCapturedLocations.size());

            // Grab the data we need for this page.
            LatLng[] page = mCapturedLocations
                    .subList(lowerBound, upperBound)
                    .toArray(new LatLng[upperBound - lowerBound]);

            // Perform the request. Because we have interpolate=true, we will get extra data points
            // between our originally requested path. To ensure we can concatenate these points, we
            // only start adding once we've hit the first new point (i.e. skip the overlap).
            SnappedPoint[] points = RoadsApi.snapToRoads(context, true, page).await();
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(point);
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.book_trip:
                if (schedule != null){

                    if (schedule.getAvailableSeats() != "0"){
                        Toast.makeText(getApplicationContext(),"Booked",Toast.LENGTH_SHORT).show();
                        //Server Call with Trip id and email params
//                        bookTrip(AppApiUrls.AppBaseUrl+AppApiUrls.BookTrip);
                    }
                }

                break;
        }

    }

    private class SnapToRoad{

        AsyncTask<Void, Void, List<SnappedPoint>> mTaskSnapToRoads = new AsyncTask<Void, Void, List<SnappedPoint>>() {
            @Override
            protected void onPreExecute() {
                SVProgressHUD.showInView(getApplicationContext(), "", true);
            }

            @Override
            protected List<SnappedPoint> doInBackground(Void... params) {
                try {
                    return snapToRoads(mGeoApiContext);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<SnappedPoint> snappedPoints) {
                mSnappedPoints = snappedPoints;
                SVProgressHUD.dismiss(getApplicationContext());

                if (!(snappedPoints == null)){
                    com.google.android.gms.maps.model.LatLng[] mapPoints =
                            new com.google.android.gms.maps.model.LatLng[mSnappedPoints.size()];
                    int i = 0;
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();

                    if (!(mSnappedPoints.size() == 0)){
                        for (SnappedPoint point : mSnappedPoints) {
                            mapPoints[i] = new com.google.android.gms.maps.model.LatLng(point.location.lng,point.location.lat);
                            bounds.include(mapPoints[i]);
                            i += 1;
                        }
                    }
//                    SnappedPoint  startPoint =  mSnappedPoints.get(0);
//                    com.google.android.gms.maps.model.LatLng p1 = new com.google.android.gms.maps.model.LatLng(startPoint.location.lat,
//                            startPoint.location.lng);
//
//
//                    Marker pos_Marker =  map.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(startPoint.location.lat,
//                            startPoint.location.lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)).title("Starting Location").draggable(false));
//
//                    pos_Marker.showInfoWindow();
//
//                    SnappedPoint  endPoint =  mSnappedPoints.get(mSnappedPoints.size()-1);
//                com.google.android.gms.maps.model.LatLng p2 = new com.google.android.gms.maps.model.LatLng(endPoint.location.lat,
//                        endPoint.location.lng);
//
//                    Marker end_pos_Marker =  map.addMarker(new MarkerOptions().position(new LatLng(endPoint.location.lat,
//                            endPoint.location.lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)).title("Ending Location").draggable(false));
//
//                    end_pos_Marker.showInfoWindow();
                    mMap.addPolyline(new PolylineOptions().add(mapPoints).color(Color.BLUE));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
                }
            }
        };

    }

    private void bookTrip(String url) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("email",email);
            obj.put("tripId",schedule.getTrip_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String a = result.getString("success");
                        String content = result.getString("message");
                        if (a.equalsIgnoreCase("true")) {
                            //Hide progress and Enable screen to touch
                            SVProgressHUD.dismiss(getApplicationContext());
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            //if Trip booked by trip id then save it and check it on RealTimeLocation Button in Home Activity

                        }else{
                            //Hide progress and Enable screen to touch
                            SVProgressHUD.dismiss(getApplicationContext());
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(), content + "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, url, obj, null, null, TripDetail.this);
        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.no_connec));
            alertDialogBuilder.setMessage(getResources().getString(R.string.offline));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
//                        bookTrip(AppApiUrls.AppBaseUrl+AppApiUrls.GetPhotos);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //To Set Text Alignment at LEFT while localization Must call show() prior to fetching views
            TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.LEFT);

            TextView titleView = (TextView) alertDialog.findViewById(getApplicationContext().getResources().getIdentifier("alertTitle", "id", "android"));
            if (titleView != null) {
                titleView.setGravity(Gravity.LEFT);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer!=null){
            timer.cancel();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        title = (TextView) findViewById(R.id.actionBarTitle);
        title.setText("REAL TIME LOCATION");

    }

    @Override
    protected void onStop() {
        super.onStop();
        SVProgressHUD.dismiss(getApplicationContext());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SVProgressHUD.dismiss(getApplicationContext());
        finish();
    }

}
