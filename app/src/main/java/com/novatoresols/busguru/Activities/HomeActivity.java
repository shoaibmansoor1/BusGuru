package com.novatoresols.busguru.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
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
import com.novatoresols.busguru.R;

/**
 * Created by Haider-AndroidDevice on 27/03/2017.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    Button bus_schedule,logout,real_time_location;
    SharedPreferences shf;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(31.5509, 74.3532);
        mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Zaman Park, Lahore, Pakistan").draggable(false));

        LatLng sydney2 = new LatLng(31.5539, 74.3488);
        mMap.addMarker(new MarkerOptions().position(sydney2).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Sunderas Road").draggable(false));

        LatLng sydney3 = new LatLng(31.5592, 74.3512);
        mMap.addMarker(new MarkerOptions().position(sydney3).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Allama iqbal Road").draggable(false));

        LatLng sydney4 = new LatLng(31.5633, 74.38);
        mMap.addMarker(new MarkerOptions().position(sydney4).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Mugal Pura, Lahore, Pakistan").draggable(false));

        LatLng sydney5 = new LatLng(31.5303, 74.3688);
        mMap.addMarker(new MarkerOptions().position(sydney5).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_guru)).title("Infantry Rd, Lahore, Pakistan").draggable(false));

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(sydney).tilt(50).zoom(13).bearing(0).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

//                if a user does not book a trip then cant go to Trip detail

//                if (booked == true){
//                    startActivity(new Intent(HomeActivity.this, TripDetail.class));
//                }

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
