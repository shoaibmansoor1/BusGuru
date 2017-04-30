package com.novatoresols.busguru.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.novatoresols.busguru.R;
import com.novatoresols.busguru.Utils.AppApiUrls;
import com.novatoresols.busguru.Utils.InternetConnectivity;
import com.novatoresols.busguru.Utils.SVProgressHUD;
import com.novatoresols.busguru.Utils.WebRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class SignIn extends Activity {

    ImageView facebookButton;
    private List<String> permissions;
    public static CallbackManager callbackmanager;
    String fbId="", fbFirstName="", fbLastname="", fbemail="", fbprofilePic = "";

    public SignIn(){
        callbackmanager = CallbackManager.Factory.create();
        permissions = Arrays.asList("email","user_photos","public_profile");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        facebookButton = (ImageView) findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //it took 2 days remember it always
                LoginManager.getInstance().logOut();

                if (InternetConnectivity.haveNetworkConnection(SignIn.this)){
                    onFblogin();
                }

            }
        });

    }

    public void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            fbId = object.getString("id");
                            fbFirstName=object.getString("first_name");
                            fbLastname=object.getString("last_name");
                            fbemail=object.getString("email");
                            //1.
                            Profile profile = Profile.getCurrentProfile();

                            if (Profile.getCurrentProfile()!=null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                Uri profilePicUri = Profile.getCurrentProfile().getProfilePictureUri(200, 200);
                                fbprofilePic = profilePicUri.toString();
                            }

                            SignUpNetworkCall(fbFirstName,fbLastname,fbemail);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id,first_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("facebook request cancel","On cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Error is facebook Code", error.toString());
            }
        });
    }

    private void SignUpNetworkCall(final String fbFirstName, String fbLastname, final String fbemail) {

        JSONObject signupObject=new JSONObject();
        try {
            signupObject.put("first_name",fbFirstName);
            signupObject.put("last_name",fbLastname);
            signupObject.put("email",fbemail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SVProgressHUD.showInView(getApplicationContext(), "Loading", true);
        WebRequest.sendRequest(new WebRequest.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    SVProgressHUD.dismiss(getApplicationContext());
                    String userId = result.getString("user_id");

                    SharedPreferences shf = getSharedPreferences("SignupCredentials", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shf.edit();

                    editor.putString("fbemail",fbemail);
                    editor.putString("userId",userId);
                    editor.putString("name",fbFirstName);

                    editor.commit();
                    editor.apply();

                    Toast.makeText(getApplicationContext(),"Welcome " +fbFirstName ,Toast.LENGTH_SHORT).show();

                    if (InternetConnectivity.haveNetworkConnection(SignIn.this)){
                        Intent i = new Intent(SignIn.this, HomeActivity.class);
                        startActivity(i);
                        SignIn.this.finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, AppApiUrls.signup, signupObject,null,null,SignIn.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //necessary add this for facebook callback
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

}
