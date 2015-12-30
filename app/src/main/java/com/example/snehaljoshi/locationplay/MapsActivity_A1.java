package com.example.snehaljoshi.locationplay;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity_A1 extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    Location currentLocation;
    private GoogleMap mMap;
    private ParseGeoPoint locat;
    String servicename;
    ParseObject currentobject;
    String currentobjectid;
    List<ParseGeoPoint> firelist = new ArrayList<>();
    List<ParseGeoPoint> medicallist = new ArrayList<>();
    List<ParseGeoPoint> policelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity__a1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle("Emergency");
        Bundle extras = getIntent().getExtras();

        if(extras!=null){
             currentLocation = extras.getParcelable("currentlocation");
            locat = new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());

            servicename = extras.getString("service");
            Log.i("In map cat A1","service name is"+servicename);

            currentobjectid = extras.getString("objectId");
            Log.i("In map cat A1", "objectID  is" + currentobjectid);

        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(locat.getLatitude(), locat.getLongitude())));
        float zoomLevel = (float) 13.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locat.getLatitude(), locat.getLongitude()), zoomLevel));
        if(currentLocation!=null && (servicename.compareTo("Fire")==0)){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Fire");
            query.setLimit(3);
            query.whereNear("location", locat);
            Log.i("Maps Act!", "in fire if");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {                       // it shows current authority location and other authority location
                    if (e == null) {                                      // on the map for all 3 different authorities
                        for (int i = 0; i < objects.size(); i++) {
                            firelist.add(objects.get(i).getParseGeoPoint("location"));
                        }
                    } else {
                        Log.i("Maps A1", "query not executed");

                    }
                    LatLng positions[]= new LatLng[firelist.size()];
                    for (int i = 0; i < firelist.size(); i++) {
                        positions[i] = new LatLng(firelist.get(i).getLatitude(), firelist.get(i).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(positions[i]));
                    }
                }

            });
            Log.i("Maps A!", String.valueOf(firelist.size()));

        }
        else  if (currentLocation != null && (servicename.compareTo("Medical")==0)) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Medical");
            query.setLimit(3);
            query.whereNear("location", locat);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            medicallist.add(objects.get(i).getParseGeoPoint("location"));
                        }
                    } else {
                        Log.i("Maps A1", "query not executed");
                    }
                    LatLng positions1[]= new LatLng[medicallist.size()];
                    for (int i = 0; i < medicallist.size(); i++) {
                        positions1[i] = new LatLng(medicallist.get(i).getLatitude(), medicallist.get(i).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(positions1[i]));
                    }
                }

            });
            Log.i("Maps A!", String.valueOf(medicallist.size()));
        }else  if (currentLocation != null && (servicename.compareTo("Police")==0)) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Police");
            query.setLimit(3);
            query.whereNear("location", locat);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < objects.size(); i++) {
                            policelist.add(objects.get(i).getParseGeoPoint("location"));
                            Log.i("Maps A---", String.valueOf(policelist.size()));
                        }
                    } else {
                        Log.i("Maps A1", "query not executed");
                    }
                    LatLng positions1[]= new LatLng[policelist.size()];
                    for (int i = 0; i < policelist.size(); i++) {
                        positions1[i] = new LatLng(policelist.get(i).getLatitude(), policelist.get(i).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(positions1[i]));
                    }
                }

            });
            Log.i("Maps A!", String.valueOf(policelist.size()));
        }
        else{
            Log.i("in map a1","no satatement worked");
            Log.i("in map a1","value of current location  "+String.valueOf(currentLocation));
            Log.i("in map a1","no value of servicename "+servicename);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation= location;
        currentobject.put("location",currentLocation);
        currentobject.saveInBackground();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
}
