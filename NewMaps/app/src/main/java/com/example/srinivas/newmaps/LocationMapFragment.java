package com.example.srinivas.newmaps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private MapView mMapView;
    private FloatingActionButton mFab;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Receiver","in receiver");
            if(!LocationResult.hasResult(intent)) {
                Log.i("Receiver","no data "+intent.toString());
                return;
            }
            LocationResult result= LocationResult.extractResult(intent);
            Location location=result.getLastLocation();
            if(location!=null) {
                mLocation=location;
                mMapView.getMapAsync(LocationMapFragment.this);
                Log.i("Receiver","true");
            } else {
                Log.i("Receiver","false");
            }
        }
    };


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_map, parent, false);
        mMapView=(MapView)v.findViewById(R.id.map);
        mFab=(FloatingActionButton)getActivity().findViewById(R.id.fab);
        mMapView.onCreate(savedInstanceState);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationData();
            }
        });
        return v;
    }

    public void setUpApiClient() {
        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.i("Google api", "connected");
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLocation != null) {
                                Toast.makeText(getActivity(), "Fetch location Success", Toast.LENGTH_SHORT).show();
                                mMapView.getMapAsync(LocationMapFragment.this);
                            } else {
                                Toast.makeText(getActivity(), "Failed to fetch location", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.i("Google api", "suspended");
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.i("Google api", "failed");
                        }
                    }).build();
        }
        mGoogleApiClient.connect();
    }

    public void setLocationData() {
        if(mGoogleApiClient==null) setUpApiClient();
        else if(!mGoogleApiClient.isConnected()) setUpApiClient();
        else {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(getActivity()==null) return;
            if (mLocation != null) {
                Toast.makeText(getActivity(), "Fetch location Success", Toast.LENGTH_SHORT).show();
                mMapView.getMapAsync(LocationMapFragment.this);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void disconnectGoogleApi() {
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if(getActivity()==null) return;
        getActivity().registerReceiver(receiver, new IntentFilter(FusedLocationReceiver.ACTION_FUSED_LOCATION));
        setLocationData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(getActivity()==null) return;
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        googleMap.addMarker(new MarkerOptions().position(location).title("Location Marker").snippet("Current location"));
    }
}
