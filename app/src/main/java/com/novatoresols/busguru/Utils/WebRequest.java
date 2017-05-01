package com.novatoresols.busguru.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by macbookpro on 5/11/16.
 */
public class WebRequest extends Activity {

    static ConnectivityManager cm;
    static NetworkInfo[] netInfo;


    public static JSONObject sendRequest(VolleyCallback callback, String url, JSONObject bodyParameters, String userId, String token, Activity activity) {
        JSONObject result = null;
        //Get Request
        if (bodyParameters == null) {
            MakeRequestGET(callback, url, userId, token, activity);
        }
        //Post Request
        else {
            result = MakeRequestPOST(callback, url, bodyParameters, userId, token, activity);
        }
        return result;
    }

    public static void MakeRequestGET(final VolleyCallback callback, String url, final String userId, final String token, final Activity activity) {
        JSONObject resultGet = null;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Response Error", error.toString());

                        String name = "";

                        if (error instanceof NoConnectionError) {
                            name = "No Connection Found";
                        } else if (error instanceof TimeoutError) {
                            name = "TimeoutError";
                        } else if (error instanceof ServerError) {
                            name = "ServerError";
                        } else if (error instanceof NetworkError) {
                            name = "NetworkError";
                        } else if (error instanceof ParseError) {
                            name = "ParseError";
                        } else if (error instanceof AuthFailureError) {
                            name = "AuthFailureError";
                        }

                        SVProgressHUD.dismiss(activity);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        displayMessage(name, activity);
                    }
                }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                if (userId != null) {
                    headers.put("userID", userId);
                }
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };
        int socketTimeout = 50000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    public static JSONObject MakeRequestPOST(final VolleyCallback callback, String url, JSONObject bodyParams, final String userId, final String token, final Activity activity) {

        JSONObject resultPost = null;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, bodyParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Server error handling
                        String name = error.toString();

                        if (error instanceof TimeoutError) {
                            name = "TimeoutError";
                        } else if (error instanceof NoConnectionError) {
                            name = "NoConnectionError";
                        } else if (error instanceof ServerError) {
                            name = "Function cannot be performed.";
                        } else if (error instanceof NetworkError) {
                            name = "NetworkError";
                        } else if (error instanceof ParseError) {
                            name = "ParseError";
                        }

                        SVProgressHUD.dismiss(activity);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        displayMessage(name, activity);
                    }
                }
        ) {

            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                if (userId != null) {
                    headers.put("userID", userId);
                }
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }

        };
        int socketTimeout = 50000;//50 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        VolleyApplication.getInstance().getRequestQueue().add(request);
        return resultPost;
    }

    //Somewhere that has access to a context
    public static void displayMessage(String toastString, Activity activity) {

        if(!((Activity) activity).isFinishing())
        {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage(toastString);
            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }


    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null)
        {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean haveNetworkConnection(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
