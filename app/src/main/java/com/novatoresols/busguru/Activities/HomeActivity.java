package com.novatoresols.busguru.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novatoresols.busguru.Model.Bus;
import com.novatoresols.busguru.R;
import com.novatoresols.busguru.Utils.AppApiUrls;
import com.novatoresols.busguru.Utils.SVProgressHUD;
import com.novatoresols.busguru.Utils.WebRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Haider-AndroidDevice on 27/03/2017.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    Button bus_schedule, logout, real_time_location;
    SharedPreferences shf;
    TextView title;
    ArrayList<Bus> busArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        busArrayList = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        logout = (Button) findViewById(R.id.logout);
        bus_schedule = (Button) findViewById(R.id.bus_schedule);
        real_time_location = (Button) findViewById(R.id.real_time_location);

        logout.setOnClickListener(this);
        bus_schedule.setOnClickListener(this);
        real_time_location.setOnClickListener(this);

        fetchBusesLocation();
    }

    private void fetchBusesLocation() {

        final Type listType = new TypeToken<ArrayList<Bus>>() {
        }.getType();
        final Gson gson = new Gson();

        if (WebRequest.haveNetworkConnection(HomeActivity.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        JSONArray buses = result.getJSONArray("buses");

                        if (buses.length() > 0) {

                            busArrayList = gson.fromJson(buses.toString(), listType);
                            updateLocations();

                        } else {
                            //Hide progress and Enable screen to touch
                            SVProgressHUD.dismiss(getApplicationContext());
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(), "No Bus Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Hide progress and Enable screen to touch
                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }, AppApiUrls.getBuses, null, null, null, HomeActivity.this);
        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.no_connec));
            alertDialogBuilder.setMessage(getResources().getString(R.string.offline));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
//                    getRealTimeLocationData(AppApiUrls.AppBaseUrl + AppApiUrls.GetRealTimeLocation +imei+ "&date=" + 0);
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

    private void updateLocations() {

        for (int i = 0; i < busArrayList.size(); i++) {
            mMap.clear();
            double lat = Double.parseDouble(busArrayList.get(i).getLatitude());
            double lng = Double.parseDouble(busArrayList.get(i).getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Towards " + busArrayList.get(i).getDestination()));
            if (i == busArrayList.size() - 1){

                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(marker.getPosition()).tilt(50).zoom(13).bearing(0).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void popUpAlertForLogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Logout?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.bus_guru);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Saving signin credientials
                shf = getSharedPreferences("SignupCredentials", MODE_PRIVATE);
                shf.edit().clear().commit();
                Toast.makeText(getApplicationContext(),"You are logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this,SignIn.class));
                finish();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
//                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        title = (TextView) findViewById(R.id.actionBarTitle);
        title.setText("BUS LOCATIONS");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.real_time_location:

            break;
            case R.id.logout:
                popUpAlertForLogout();
                break;
            case R.id.bus_schedule:
                startActivity(new Intent(HomeActivity.this, BusScheduledActivity.class));
                break;
        }
    }
}
