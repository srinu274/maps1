package com.example.srinivas.newmaps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Srinivas on 26-04-2016.
 */
public class FusedLocationService extends IntentService {
    private static final String TAG="FusedLocationService";
    public static final String EXTRA_LOCATION="FusedLocation.Extra";

    public FusedLocationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("service loc","loca");
        Location location=(Location)intent.getParcelableExtra(EXTRA_LOCATION);
        if(location!=null) {
            Log.i("service loc",""+location.getLongitude());
            LocationHandler handler=new LocationHandler();
            handler.setLatitude(location.getLatitude());
            handler.setLongitude(location.getLongitude());
            handler.setTime(System.currentTimeMillis());
            new LocationDatabaseHelper(this).insertLocation(handler);
        } else {
            Log.i("service loc","failed");
        }
    }


}
