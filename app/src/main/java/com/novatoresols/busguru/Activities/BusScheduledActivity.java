package com.novatoresols.busguru.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novatoresols.busguru.Model.Bus;
import com.novatoresols.busguru.R;
import com.novatoresols.busguru.Utils.AppApiUrls;
import com.novatoresols.busguru.Utils.RecyclerItemClickListener;
import com.novatoresols.busguru.Utils.SVProgressHUD;
import com.novatoresols.busguru.Utils.WebRequest;
import com.novatoresols.busguru.adapter.ScheduledAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Haider-AndroidDevice on 28/03/2017.
 */

public class BusScheduledActivity extends Activity implements RecyclerItemClickListener.OnItemClickListener{

    private RecyclerView recyclerView;
    private ScheduledAdapter mAdapter;
    TextView title;

    ArrayList<Bus> busArrayList;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_scheduled_layout);

        busArrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ScheduledAdapter(busArrayList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this));

        CallingDataFromServer();

    }

    private void CallingDataFromServer() {

        final Type listType = new TypeToken<ArrayList<Bus>>(){}.getType();
        final Gson gson = new Gson();

        if (WebRequest.haveNetworkConnection(BusScheduledActivity.this)) {

            SVProgressHUD.showInView(getApplicationContext(), "", true);

            WebRequest.sendRequest(new WebRequest.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                        SVProgressHUD.dismiss(getApplicationContext());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        JSONArray buses = result.getJSONArray("buses");

                        if (buses.length() > 0){

                            busArrayList = gson.fromJson(buses.toString(),listType);
                            updateNewsFeed();

                        }else{
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
            }, AppApiUrls.getBuses, null, null, null, BusScheduledActivity.this);
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

    private void updateNewsFeed(){

        mAdapter = new ScheduledAdapter(busArrayList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        title = (TextView) findViewById(R.id.actionBarTitle);
        title.setText("BUS LOCATIONS");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {

        Bus bus = busArrayList.get(position);

        Intent i = new Intent(BusScheduledActivity.this,TripDetail.class);
        i.putExtra("scheduleObj", bus);
        startActivity(i);

    }

    @Override
    public void onItemLongPress(View view, int position) {

    }
}
