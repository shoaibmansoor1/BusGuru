package com.novatoresols.busguru.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Haider-AndroidDevice on 16/02/2017.
 */

public class InternetConnectivity extends AppCompatActivity {

    public void NoInternet(final Activity activityName) {

            // Display message in dialog box if you have not internet connection
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityName);
            alertDialogBuilder.setTitle("No Internet Connection");
            alertDialogBuilder.setMessage("You are offline please check your internet connection");
            alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    haveNetworkConnection(activityName);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

    }

    public static boolean haveNetworkConnection(Activity activyName) {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) activyName.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
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
}
