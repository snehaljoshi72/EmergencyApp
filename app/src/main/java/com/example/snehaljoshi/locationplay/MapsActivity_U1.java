package com.example.snehaljoshi.locationplay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity_U1 extends FragmentActivity implements OnMapReadyCallback , LocationListener, RoutingListener{


    private GoogleMap mMap;
    protected Switch fires;
    protected Switch medicals;
    protected Switch polices;
    protected Button request;
    protected TextView message;
    protected TextView message1;
    protected TextView message2;
    protected LinearLayout lh;
    protected LinearLayout lv;
    protected Location currentLocation;
    protected LatLng start;
    protected LatLng fireLocation;
    protected ParseGeoPoint fire_location;
    protected LatLng policeLocation;
    protected ParseGeoPoint police_location;
    protected LatLng medicalLocation;
    protected ParseGeoPoint medical_location;
    ParseGeoPoint locat;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    int durationtime;
    ParseObject fire_po;
    ParseObject medical_po;
    ParseObject police_po;


    private Handler mHandler = new Handler();
    private Handler mHandler1 = new Handler();
    private Handler mHandler2 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity__u1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle("Emergency");

        Bundle extras = getIntent().getExtras();
        currentLocation = extras.getParcelable("currentlocation");
         locat = new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());


        lh = (LinearLayout) findViewById(R.id.ll_ver);
        lv = (LinearLayout) findViewById(R.id.ll_vert);
        fires = (Switch) findViewById(R.id.sw_fire);
        medicals = (Switch) findViewById(R.id.sw_medical);
        polices = (Switch) findViewById(R.id.sw_police);
        message = (TextView) findViewById(R.id.tv_msg);
        message1 = (TextView) findViewById(R.id.tv_msg1);
        message2 = (TextView) findViewById(R.id.tv_msg2);
        request = (Button) findViewById(R.id.bt_request);

        start = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());



        final ParsePush push = new ParsePush();
        final ParsePush push1 = new ParsePush();
        final ParsePush push2 = new ParsePush();



        final JSONObject data = new JSONObject();

        try {                                                           // this are general data to send authority via push message
            data.put("alert","Emergency is there");                    // this is the message and notification name that will be received
            data.put("loc_lat",currentLocation.getLatitude());         // this is user location latitude
            data.put("loc_long",currentLocation.getLongitude());       // this is user location longitude
            data.put("userobjectId",ParseUser.getCurrentUser().getObjectId()); // this is user object id


        } catch (JSONException e) {
            e.printStackTrace();
        }


        request.setOnClickListener(new View.OnClickListener() {    //this event done after request button clicked

            @Override
            public void onClick(View v) {
                request.setEnabled(false);
                new AlertDialog.Builder(MapsActivity_U1.this)    // this generate alert box to notify that user requested for services
                        // Log.i("in maps act U1","in alert box");
                        .setTitle("Sure you want to send request")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean value = fires.isChecked();
                                boolean value1 = medicals.isChecked();
                                boolean value2 = polices.isChecked();
                                final ParseObject pos = new ParseObject("Requested");        // this create object of requested class in which all requests are stored
                                pos.put("user", ParseUser.getCurrentUser());
                                push.setData(data);
                                if (value) {
                                    ParseQuery<ParseObject> fire = new ParseQuery<ParseObject>("Fire");
                                    ParseQuery pushquery = ParseInstallation.getQuery();     // this is query to find nearest location of fire vehicle if fire is selected
                                    fire.whereNear("location", locat);                      // using current user location it finds nearest fire location
                                    fire.whereEqualTo("availability", true);                // checks the availability of that vehicle
                                    pos.put("fire", true);
                                    try {
                                        fire_po = fire.getFirst();  // it takes first value of result from query
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    fire.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null && objects.size() != 0) {
                                                objects.get(0).put("availability", false);  // makes availability of result vehicle to false
                                                objects.get(0).saveInBackground();
                                            }
                                        }
                                    });
                                    pos.put("FirevehicleId", fire_po.getObjectId());
                                    fire_location = fire_po.getParseGeoPoint("location");
                                    try {
                                        data.put("loc1_lat",fire_location.getLatitude()); // this data is sent to resulted fire vehicle with their location
                                        data.put("loca1_long",fire_location.getLongitude());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    fireLocation = new LatLng(fire_location.getLatitude(),fire_location.getLongitude());
                                    Routing routing = new Routing.Builder()
                                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                                            .withListener(MapsActivity_U1.this)
                                            .waypoints(start, fireLocation)
                                            .build();
                                    routing.execute();
                                    // get estimate time here
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Date now = new Date();
                                            now.getTime();   // it gives time to be taken by the vehicle to arrive from its location to user location
                                            // it it will show on the screen
                                            message2.append(durationtime / 60 + " Mins  " + durationtime % 60 + "   seconds");
                                            mMap.addMarker(new MarkerOptions().position(fireLocation)  // this will add location on map of nearest and requested
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fireengineicon1)));//services
                                        }
                                    }, 1000);
                                    pushquery.whereEqualTo("deviceobjectID", fire_po.getObjectId());
                                    Log.i("object value for fire", String.valueOf(fire_po.get("installationID")));
                                    push.setQuery(pushquery);
                                    push.setMessage("Alert!!!!!!!There is Emergency");
                                    push.setData(data);
                                    push.sendInBackground();    // this will send the push notification with data to requested authority
                                    pos.put("location", locat);
                                    pos.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.i("u1", "fire object pos is saved");
                                        }
                                    });
                                }else{
                                    message2.setText("fire is not selected");
                                }
                                if (value1) {
                                   final ParseQuery pushquery1 = ParseInstallation.getQuery();
                                    mHandler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ParseObject pos1 = new ParseObject("Requested");
                                            pos1.put("user", ParseUser.getCurrentUser());
                                            Log.i("in map u1 ", "object pos1 user object value" +ParseUser.getCurrentUser().getObjectId());
                                            pos1.put("Medical",true);

                                            ParseQuery<ParseObject> medical = new ParseQuery<ParseObject>("Medical");
                                            medical.whereNear("location", locat);
                                            medical.whereEqualTo("availability", true);
                                            try {
                                                medical_po = medical.getFirst();
                                                Log.i("in medical query", "value of medical po" + String.valueOf(medical_po));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            pos1.put("MediclevehicleId", medical_po.getObjectId());
                                            medical.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    objects.get(0).put("availability", false);
                                                    objects.get(0).saveInBackground();
                                                }
                                            });

                                            pos1.saveInBackground();
                                            medical_location = medical_po.getParseGeoPoint("location");
                                            try {
                                                data.put("loc1_lat", medical_location.getLatitude());
                                                Log.i("in u1 act", "calue of medical location" + String.valueOf(medical_location));
                                                data.put("loca1_long", medical_location.getLongitude());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            medicalLocation = new LatLng(medical_location.getLatitude(), medical_location.getLongitude());
                                            Routing routing1 = new Routing.Builder()
                                                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                                                    .withListener(MapsActivity_U1.this)
                                                    .waypoints(start, medicalLocation)
                                                    .build();
                                            routing1.execute();
                                            pushquery1.whereEqualTo("deviceobjectID", medical_po.getObjectId());
                                            Log.i("object value for medical", String.valueOf(medical_po.get("installationID")));

                                            push1.setQuery(pushquery1);
                                            push1.setData(data);
                                            push1.sendInBackground();
                                            pos1.put("location", locat);
                                            pos1.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Log.i("in u1","Medical pos is saved");
                                                }
                                            });
                                        }
                                    }, 2000);
                                    mHandler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            message1.append(durationtime / 60 + " mins  " + durationtime % 60 + "   seconds");
                                            mMap.addMarker(new MarkerOptions().position(medicalLocation)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.medimage1)));
                                        }
                                    }, 4000);


                                }else{
                                    message1.setText("medical is not selected");

                                }
                                if (value2) {
                                    ParseObject pos2 = new ParseObject("Requested");
                                    final ParseQuery pushquery2 = ParseInstallation.getQuery();
                                    ParseQuery<ParseObject> police = new ParseQuery<ParseObject>("Police");
                                    police.whereNear("location", locat);
                                    police.whereEqualTo("availability", true);
                                    try {
                                        police_po = police.getFirst();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    police.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            objects.get(0).put("availability", false);
                                            objects.get(0).saveInBackground();
                                        }
                                    });
                                    police_location = police_po.getParseGeoPoint("location");
                                    policeLocation = new LatLng(police_location.getLatitude(),police_location.getLongitude());

                                    try {
                                        data.put("loc1_lat", police_location.getLatitude());
                                        Log.i("in u1 act", "calue of medical location" + String.valueOf(police_location));
                                        data.put("loca1_long", police_location.getLongitude());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Routing routing2 = new Routing.Builder()
                                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                                            .withListener(MapsActivity_U1.this)
                                            .waypoints(start, policeLocation)
                                            .build();
                                    routing2.execute();
                                    pushquery2.whereEqualTo("deviceobjectID", police_po.getObjectId());
                                    Log.i("object value for medical", String.valueOf(medical_po.get("installationID")));
                                    push2.setQuery(pushquery2);
                                    push2.setData(data);
                                    push2.sendInBackground();
                                    pos2.put("location", locat);
                                    pos2.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.i("in u1", "Medical pos is saved");
                                        }
                                    });
                                }else{
                                    message.setText("police is not selected");
                                }
                                fires.setEnabled(false);
                                medicals.setEnabled(false);
                                polices.setEnabled(false);
                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                request.setEnabled(true);
                            }
                        })
                .show();
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
        if(currentLocation != null) {
            LatLng mylocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocat1))
                    .position(mylocation));
            float zoomLevel = (float) 15.0;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, zoomLevel));

        } else {
            Log.i("Maps activity_U1", "current location  is null");
        }
        if(medical_location!=null){
            mMap.addMarker(new MarkerOptions().position(medicalLocation));
        }
        if(fire_location!=null){
            mMap.addMarker(new MarkerOptions().position(fireLocation));
        }
        if(police_location!=null){
            mMap.addMarker(new MarkerOptions().position(policeLocation));
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
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
    @Override
    public void onRoutingFailure() {
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        for (int i = 0; i <route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            durationtime = route.get(i).getDurationValue();
        }
    }
    @Override
    public void onRoutingCancelled() {

    }
}
