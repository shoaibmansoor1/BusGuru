package com.novatoresols.busguru.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.novatoresols.busguru.R;

/**
 * Created by macbookpor on 22/08/2016.
 */
public class SplashActivity extends Activity {

    ConnectivityManager cm;
    private Boolean exit = false;
    NetworkInfo[] netInfo;
    SharedPreferences shf;
    String fbEmail;
    //Interval For Splash Screen
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);

        //Hiding Action Bar
        try {
            getActionBar().hide();
        } catch (Exception e) {}

        shf = getSharedPreferences("SignupCredentials", Context.MODE_PRIVATE);
        fbEmail = shf.getString("fbemail","");

        callActivity();
    }

    public void callActivity() {

        if (haveNetworkConnection()) {

            //pausing screen for four seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }else {


                    if (TextUtils.isEmpty(fbEmail)){
                        startActivity(new Intent(SplashActivity.this, SignIn.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    }
                        SplashActivity.this.finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);


        } else {
            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.no_connec));
            alertDialogBuilder.setMessage(getResources().getString(R.string.offline));
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    callActivity();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //To Set Text Alignment at LEFT while localization Must call show() prior to fetching views
            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.LEFT);

            TextView titleView = (TextView)alertDialog.findViewById(getApplicationContext().getResources().getIdentifier("alertTitle", "id", "android"));
            if (titleView != null) {
                titleView.setGravity(Gravity.LEFT);
            }
        }
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
                // back press within 3 seconds, it closes the application.
            }, 3 * 500);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    callActivity();
                } else {
                    // permission denied, boo!
                    callActivity();
                }
                return;
            }
        }
    }
}
