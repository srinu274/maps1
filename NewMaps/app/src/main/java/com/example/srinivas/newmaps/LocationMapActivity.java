package com.example.srinivas.newmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LocationMapActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.content);
        if(fragment==null) {
            fm.beginTransaction().replace(R.id.content,new LocationMapFragment()).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpTabs();
    }

    private void setUpTabs() {
        mTabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addTab(mTabLayout.newTab().setText("Current Location").setTag("Tab1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Location List").setTag("Tab2"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Locations Route Map").setTag("Tab3"));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm=getSupportFragmentManager();
                Fragment fragment;
                switch ((String)tab.getTag()) {
                    case "Tab1":
                        fragment=fm.findFragmentById(R.id.content);
                        fm.beginTransaction().replace(R.id.content,new LocationMapFragment()).commit();
                        break;
                    case "Tab2":
                        fragment=fm.findFragmentById(R.id.content);
                        fm.beginTransaction().replace(R.id.content,new LocationListFragment()).commit();
                        break;
                    case "Tab3":
                        fragment=fm.findFragmentById(R.id.content);
                        fm.beginTransaction().replace(R.id.content,new LocationMapRouteFragment()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onResume() {
        super.onResume();
        Log.i("Settings Pref","stored "+SettingsPref.isLocationStored(this)+" start "+SettingsPref.isLocationUpdateStarted(this));
        if(SettingsPref.isLocationStored(this)&&!SettingsPref.isLocationUpdateStarted(this)) {
            FusedLocationReceiver.startUpdates(this);
        } else if(!SettingsPref.isLocationUpdateStarted(this) && SettingsPref.isLocationUpdateStarted(this)) {
            FusedLocationReceiver.stopUpdates(this);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem locationItem=menu.findItem(R.id.action_should_update_location);
        locationItem.setChecked(SettingsPref.isLocationStored(this));
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.user_info){
            Intent intent=new Intent(this,UserInfoActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_should_update_location) {
            synchronized (item) {
                boolean shouldStore = SettingsPref.isLocationStored(this);
                SettingsPref.setLocationStored(this, !shouldStore);
                if(!shouldStore) {
                    FusedLocationReceiver.stopUpdates(this);
                } else {
                    FusedLocationReceiver.startUpdates(this);
                }
                item.setChecked(!shouldStore);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
