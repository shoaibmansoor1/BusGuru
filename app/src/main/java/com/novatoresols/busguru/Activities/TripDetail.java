package com.novatoresols.busguru.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.novatoresols.busguru.Model.Bus;
import com.novatoresols.busguru.R;
import com.novatoresols.busguru.Utils.AppApiUrls;
import com.novatoresols.busguru.Utils.SVProgressHUD;
import com.novatoresols.busguru.Utils.WebRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

/**
 * Created by Haider-AndroidDevice on 29/03/2017.
 */
public class TripDetail extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {


    Timer timer;
    String date = "",email,userid;
    private GoogleMap mMap;

    final Handler handler = new Handler();

    TextView title,startLocation,endLocation,startTime,endTime,total_seats_textview;
    ImageView book_trip;
    Button cancelBooking;
    Bus bus;
    SharedPreferences shf;
    boolean booked = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail);

        mHandler = new Handler();
        bus = (Bus)getIntent().getSerializableExtra("scheduleObj");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocation = (TextView) findViewById(R.id.startLocation);
        endLocation = (TextView) findViewById(R.id.endLocation);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        cancelBooking = (Button)findViewById(R.id.btnCancelBooking);
        cancelBooking.setOnClickListener(this);

        book_trip = (ImageView) findViewById(R.id.book_trip);
        book_trip.setOnClickListener(this);


        //Setting Views
        if (bus!=null){
            startLocation.setText(bus.getOrigin());
            endLocation.setText(bus.getDestination());
            startTime.setText(bus.getStart_time());
    //        endTime.setText(schedule.getEndTime());
        }

        shf = getSharedPreferences("SignupCredentials", Context.MODE_PRIVATE);
        email = shf.getString("fbemail","");
        userid = shf.getString("userId","");

        getBusRemainingSeats();
        startRepeatingTask();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(Double.parseDouble(bus.getLatitude()),Double.parseDouble( bus.getLongitude()));

        Marker marker1 =  mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Towards " + bus.getDestination()).draggable(false));
        marker1.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(sydney).tilt(50).zoom(13).bearing(0).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void getRealTimeLocationData() {

        String url = AppApiUrls.fetchLocation + bus.getId();

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                        Double lat = result.getDouble("latitude");
                        Double lng = result.getDouble("longitude");

                        mMap.clear();

                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Towards " + bus.getDestination()).draggable(false));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },url , null, null, null, TripDetail.this);
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

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                getRealTimeLocationData();
            }
            finally
            {
                mHandler.postDelayed(mStatusChecker, 30000);
            }
        }
    };



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.book_trip:
                if (bus != null){

                    if (bus.getRemaining_seats() != "0"){

                        bookTrip();
                    }
                }

                break;
            case R.id.btnCancelBooking:
                cancelTrip();
                break;
        }

    }

    private void bookTrip() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id",userid);
            obj.put("bus_id",bus.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        String content = result.getString("message");
                        if (content.equalsIgnoreCase("Successfully Booked")) {
                            //Hide progress and Enable screen to touch

                            book_trip.setVisibility(View.GONE);
                            cancelBooking.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(),"Trip Booked Succesfully",Toast.LENGTH_SHORT).show();

                        }else{

                            Toast.makeText(getApplicationContext(), content + "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, AppApiUrls.reserveSeat, obj, null, null, TripDetail.this);
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

    private void cancelTrip() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id",userid);
            obj.put("bus_id",bus.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        String content = result.getString("message");
                        if (content.equalsIgnoreCase("Successfully Canceled")) {
                            cancelBooking.setVisibility(View.GONE);
                            book_trip.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Trip Cancelled Succesfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), content + "", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, AppApiUrls.cancelSeat, obj, null, null, TripDetail.this);
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

    private void getBusRemainingSeats() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("user_id",userid);
            obj.put("bus_id",bus.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (WebRequest.haveNetworkConnection(TripDetail.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        boolean isBooked = result.getBoolean("is_booked");
                        double availableSeats = result.getDouble("avaliable_seats");

                        if (isBooked){
                            book_trip.setVisibility(View.GONE);
                            cancelBooking.setVisibility(View.VISIBLE);

                        }else{
                            if (availableSeats > 0) {
                                cancelBooking.setVisibility(View.GONE);
                                book_trip.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(getApplicationContext(),"All seats are Already Reserved",Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, AppApiUrls.remainingSeats, obj, null, null, TripDetail.this);
        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.no_connec));
            alertDialogBuilder.setMessage(getResources().getString(R.string.offline));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

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
        stopRepeatingTask();
        SVProgressHUD.dismiss(getApplicationContext());
        finish();
    }

}
