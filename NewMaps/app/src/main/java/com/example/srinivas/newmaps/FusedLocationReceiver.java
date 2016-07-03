package com.example.srinivas.newmaps;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Srinivas on 26-04-2016.
 */
public class FusedLocationReceiver extends BroadcastReceiver {

    private static GoogleApiClient mGoogleApiClient;
    public static final String ACTION_FUSED_LOCATION = "FusedLocationReceiver.Location";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("FusedLocationReceiver","in receiver");
        if(!LocationResult.hasResult(intent)) {
            Log.i("FusedLocationReceiver","no data "+intent.toString());
            return;
        }
        LocationResult result= LocationResult.extractResult(intent);
        Location location=result.getLastLocation();
        if(location!=null) {
            Intent service = new Intent(context, FusedLocationService.class);
            service.putExtra(FusedLocationService.EXTRA_LOCATION,location);
            context.startService(service);
            Log.i("FusedLocationReceiver","true");
        } else {
            Log.i("FusedLocationReceiver","false");
        }
        onReceived(location);
    }

    public void onReceived(Location location) {

    }

    public static void startUpdates(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new FusedConnectionCallback(context))
                    .addOnConnectionFailedListener(new FusedConnectionFailedCallback(context))
                    .build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            startLocationUpdates(context);
        }
    }

    private static void startLocationUpdates(final Context context) {
        Log.i("FusedLocationReceiver", "In start location updates");
        final int interval = 1000*60;
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(interval);
        Intent i = new Intent(ACTION_FUSED_LOCATION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("FusedLocationReceiver", "No permission");
            return;
        }
        Log.i("FusedLocationReceiver", "before pending result");
        final com.google.android.gms.common.api.PendingResult<Status> result = LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, request, pi);
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                com.google.android.gms.common.api.Status status=result.await();
                if (status.isSuccess()) {
                    SettingsPref.setLocationUpdateStarted(context, true);
                    Log.i("FusedLocationReceiver", "Start success");
                }
                return null;
            }
        }.execute();
    }

    public static void stopUpdates(final Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Intent i = new Intent(context,FusedLocationReceiver.class);
                            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
                            final com.google.android.gms.common.api.PendingResult<Status> result=
                                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,pi);
                            new AsyncTask<Void,Void,Void>() {
                                @Override
                                protected Void doInBackground(Void[] params) {
                                    com.google.android.gms.common.api.Status status=result.await();
                                    if (status.isSuccess()) {
                                        SettingsPref.setLocationUpdateStarted(context,false);
                                    }
                                    return null;
                                }
                            }.execute();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new FusedConnectionFailedCallback(context))
                    .build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            Intent i = new Intent(context,FusedLocationReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,pi);
        }
    }

    private static class FusedConnectionCallback implements GoogleApiClient.ConnectionCallbacks {

        Context context;
        int time;

        private FusedConnectionCallback(Context context) {
            this.context=context;
            this.time=time;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i("FusedLocationReceiver", "Connection Success");
            startLocationUpdates(context);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i("FusedLocationReceiver", "Connection Suspened");
        }
    }

    private static class FusedConnectionFailedCallback implements GoogleApiClient.OnConnectionFailedListener {

        Context context;

        private FusedConnectionFailedCallback(Context context) {
            Log.i("FusedLocationReceiver", "Connection failed");
            this.context=context;
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }
}
