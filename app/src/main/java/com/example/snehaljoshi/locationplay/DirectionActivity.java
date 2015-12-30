package com.example.snehaljoshi.locationplay;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback,RoutingListener {

    private TextView tv_details;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_email;
    private Button reqcomp;
    private GoogleMap mMap;
    private Location secondlocation;
    private Location myloc;
    private String senderobjectId;
    boolean value = false;
    boolean value1 = false;
    String name;
    String phone;
    String email;
    protected LatLng start;
    protected LatLng end;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle("Emergency");
        tv_email = (TextView) findViewById(R.id.tv_uemaila);
        tv_name = (TextView) findViewById(R.id.tv_u_name);
        tv_phone = (TextView) findViewById(R.id.tv_uphone);
        reqcomp = (Button) findViewById(R.id.bt_compreq);
        tv_details = (TextView) findViewById(R.id.tv_details);
        Bundle extras = getIntent().getExtras();
        secondlocation = (Location) extras.get("secondlocation");
        myloc = (Location) extras.get("mylocation");
        senderobjectId = extras.getString("userobjectId");
        end = new LatLng(secondlocation.getLatitude()+0.0009000,secondlocation.getLongitude());
        start = new LatLng(myloc.getLatitude(), myloc.getLongitude());
        if(start!=null && end !=null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(DirectionActivity.this)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }else{
            Log.i("in maps activity","start value"+String.valueOf(start));
            Log.i("in maps activity","end value"+String.valueOf(end));
        }
        if(senderobjectId!=null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", senderobjectId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {   // this will give all information about the request user to the authority
                    if (e == null) {
                        Log.i("in maps act", "user name is" + (String) objects.get(0).get("name"));
                        name = (String) objects.get(0).get("name");
                        Log.i("in maps act", "user phone is" + (String) objects.get(0).get("phone"));
                        phone = (String) objects.get(0).get("phone");
                        Log.i("in maps act", "user email is" + (String) objects.get(0).get("username"));
                        email = (String) objects.get(0).get("username");
                        tv_name.setText(name);
                        tv_phone.setText(phone);
                        tv_email.setText(email);
                        value = true;
                    }
                }
            });
        }
        reqcomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                Log.i("in direction activity", "installation id is" + installation.getObjectId());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Fire");
                query.whereEqualTo("installationID", installation.getObjectId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {    // after serving user successfully  it makes authority again available
                        if (e == null && objects.size() != 0) {                        // and redirect ot main authority activity
                            objects.get(0).put("availability", true);
                            objects.get(0).saveInBackground();
                            Intent intent = new Intent(DirectionActivity.this,MapsActivity_A1.class);
                            intent.putExtra("currentlocation",myloc);
                            intent.putExtra("objectId",objects.get(0).getObjectId());
                            intent.putExtra("service","Fire");
                            startActivity(intent);
                        } else {
                            value1 = true;
                        }
                        if (value1) {
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Medical");
                            query1.whereEqualTo("installationID", installation.getObjectId());
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null && objects.size() != 0) {
                                        objects.get(0).put("availability", true);
                                        objects.get(0).saveInBackground();
                                        Intent intent = new Intent(DirectionActivity.this,MapsActivity_A1.class);
                                        intent.putExtra("currentlocation",myloc);
                                        intent.putExtra("objectId",objects.get(0).getObjectId());
                                        intent.putExtra("service", "Medical");
                                        startActivity(intent);
                                    } else {
                                        value1 = true;
                                    }
                                }
                            });
                        }
                    }
                });
            }

        });
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
        MarkerOptions options = new MarkerOptions();
        Log.i("maps routing sucess start location val", String.valueOf(start.latitude));
        options.position(start);
        mMap.addMarker(options);
        Log.i("maps routing sucess end location val", String.valueOf(end.latitude));
        options.position(end);
        mMap.addMarker(options);
        float zoomLevel = (float) 14.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(end, zoomLevel));
    }
    @Override
    public void onRoutingFailure() {
        Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        Log.i("maps routing sucess"," on Routing Success");
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        mMap.moveCamera(center);
        Log.i("maps routing sucess polylines size", String.valueOf(polylines.size()));
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions(); // this will generate the path to authority from it's location to user
            polyOptions.width(10 + i * 3);                       // add the path to the map
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
}
