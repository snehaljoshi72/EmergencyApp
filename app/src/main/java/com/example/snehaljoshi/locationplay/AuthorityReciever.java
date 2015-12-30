package com.example.snehaljoshi.locationplay;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by snehaljoshi on 12/5/15.
 */
public class AuthorityReciever extends ParsePushBroadcastReceiver {
    private static final String TAG = "MyAuthorityReceiver";
    Location secondloc;
    Location myloc;
    String userobjectId;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");                 // this is custom receiver for notification
        } else {
            // Parse push message and handle accordingly
                    processPush(context, intent);
        }

    }
    private void processPush(Context context, Intent intent) {
        double latitude1=0;
        double longitude1=0;
        double latitude = 0;
        double longitude=0;
        String action = intent.getAction();
        Log.d("in my custom reciever", "got action " + action);

            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();         // this gets all data from notification and saved in different variables
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    // Extract custom push data
                    if (key.equals("loc_lat")) {
                        // Handle push notification by invoking activity directly
                         latitude = (double) json.get("loc_lat");
                    } if (key.equals("loc_long")) {
                     longitude = (double) json.get("loc_long");
                    }
                    if(key.equals("loc1_lat")){
                        latitude1 = (double) json.get("loc1_lat");

                    }if(key.equals("loca1_long")){
                        longitude1 = (double) json.get("loca1_long");

                    }if(key.equals("userobjectId")){
                        userobjectId = json.getString("userobjectId");
                    }

                    Log.d(TAG, "..." + key + " => " + json.getString(key));
                }

                secondloc = new Location("");
                secondloc.setLatitude(latitude);
                secondloc.setLongitude(longitude);
                myloc = new Location("");
                myloc.setLatitude(latitude1);
                myloc.setLongitude(longitude1);
               launchSomeActivity(context, secondloc, myloc,userobjectId); // after getting all require data we launch activity using method

            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }

    }
    private void launchSomeActivity(final Context context, final Location datavalue, final Location datavalue1,String userobjectId) {
       Log.i("in custom reciever current context", String.valueOf(context.getClass()));

        // getting current activity name from screen and if it is authority main activity then its process the push and launch another activity
        ActivityManager am =(ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
       // ComponentName componentInfo = taskInfo.get(0).topActivity;

        if(taskInfo.get(0).topActivity.getClassName().equals("com.example.snehaljoshi.locationplay.MapsActivity_A1"))
        {
            Log.i("in custom reciever", "launching activity");
            Intent pupInt = new Intent(context, AlertActivity.class);  // it launches alert activity with passing all data
            pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pupInt.putExtra("mylocation",datavalue1);
            pupInt.putExtra("secondlocation", datavalue);
            pupInt.putExtra("userobjectId",userobjectId);
            context.getApplicationContext().startActivity(pupInt);
        }
    }
}
