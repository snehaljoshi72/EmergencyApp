package com.example.snehaljoshi.locationplay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

public class AlertActivity extends AppCompatActivity {

    private Location secondlocation;
    private Location myloc;
    private String senderobjectId;
    protected LatLng start;
    protected LatLng end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        setTitle("Emergency");
        Bundle extras = getIntent().getExtras();
        secondlocation = (Location) extras.get("secondlocation");
        //Log.i("In maps activity", "value of seconf location" + String.valueOf(secondlocation));
        myloc = (Location) extras.get("mylocation");
       // Log.i("In maps activity", "value of my location" + String.valueOf(myloc));
        senderobjectId = extras.getString("userobjectId");
       // Log.i("In maps activity", "value of sender object Id" + senderobjectId);


        new AlertDialog.Builder(AlertActivity.this)   // it generates alertbox to process the request
                .setMessage("You got Emergency request") // if it respond positively then new activity will start
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pupInt = new Intent(AlertActivity.this,DirectionActivity.class);
                        pupInt.putExtra("mylocation",myloc);
                        pupInt.putExtra("secondlocation", secondlocation);
                        pupInt.putExtra("userobjectId",senderobjectId);
                        startActivity(pupInt);
                        finish();
                    }
                })
                .show();


    }
}
