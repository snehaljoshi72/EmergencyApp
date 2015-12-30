package com.example.snehaljoshi.locationplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

import java.util.Objects;

//import android.os.Handler;
//import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    protected Location lon;
    private final String TAG = "in Main activity";
   protected LocationRequest mLocationRequest = LocationRequest.create();
    protected Location mCurrentLocation;
    protected GoogleApiClient mGoogleApiClient;
   //s private ParseGeoPoint geoPoint;
    protected Button nextAct;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    Boolean isInternetPresent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isInternetPresent = isNetworkAvailable(MainActivity.this);

        ImageView splashscreen = (ImageView) findViewById(R.id.splashscreen);    // This activity set the first splash screen for 3 second
        splashscreen.setImageResource(R.drawable.splash);                        // in that time it gets current location of app user
        setTitle("Emergency");
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (mCurrentLocation != null && isInternetPresent) {            // this code is check whether this is current user or not which is logged in
                    if (currentUser != null) {                                  // if it is current user than it takes to new user activity
                        // do stuff with the user
                        if(Objects.equals(currentUser.getClassName(), "_User")){
                            Intent intent = new Intent(MainActivity.this, MapsActivity_U1 .class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("currentloaction", mCurrentLocation);
                            intent.putExtras(bundle);
                            intent.putExtra("currentlocation", mCurrentLocation);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);   // if it is not current user than it takes to login activity
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("currentloaction", mCurrentLocation);
                        intent.putExtras(bundle);
                        intent.putExtra("currentlocation", mCurrentLocation);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                   new AlertDialog.Builder(MainActivity.this)                               // if there is no location and network available then it gives
                           .setTitle("There is some error")                                 // alert box to user to on GPS and internet
                           .setMessage("Either your GPS or Internet is not working /n Press ok to close application")
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                  finish();
                                   System.exit(0);
                               }
                           })
                   .show();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    protected void createLocationRequest() {
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");

        if (mCurrentLocation == null) {
            Log.i(TAG, "onConnected but not get a location");
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //getUser();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }
    public static boolean isNetworkAvailable(Context context)
    {   ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {    NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {   for (int i = 0; i < info.length; i++)
                {   Log.i("Class", info[i].getState().toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
