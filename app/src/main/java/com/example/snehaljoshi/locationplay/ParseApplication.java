package com.example.snehaljoshi.locationplay;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by snehaljoshi on 12/8/15.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FdPYesGXFjjgUUd1CfV6AND2rQRr7NVmlobuidvq", "PiVCYQl8xibCSl7V7XdFsUahEdQMg97e6rvRUJVu");
    }
}