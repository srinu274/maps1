package com.example.srinivas.newmaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Srinivas on 03-04-2016.
 */
public class LocationListFragment extends Fragment {

    private static final int UI_UPDATE = 5;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    private Handler mHandler=new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message input) {
            if (input.what == UI_UPDATE) {
                ArrayList<LocationHandler> data = (ArrayList<LocationHandler>) input.obj;
                ((ListAdapter)mRecyclerView.getAdapter()).setData(data);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    };

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
                LocationHandler handler=new LocationHandler();
                handler.setLongitude(location.getLongitude());
                handler.setLatitude(location.getLatitude());
                handler.setTime(System.currentTimeMillis());
                ((ListAdapter)mRecyclerView.getAdapter()).addLocation(handler);
                Log.i("Receiver","true");
            } else {
                Log.i("Receiver","false");
            }
        }
    };


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_location_list,parent,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ListAdapter());
        mFab=(FloatingActionButton)getActivity().findViewById(R.id.fab);
        return view;
    }



    private class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        ArrayList<LocationHandler> data;

        ListAdapter() {
            data = null;
        }

        ListAdapter(ArrayList<LocationHandler> data) {
            this.data = data;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_location, parent, false);
            return new ListHolder(view);
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            LocationHandler handler = data.get(position);
            holder.setData(handler);
        }

        public void setData(ArrayList<LocationHandler> data) {
            this.data=data;
        }

        public ArrayList<LocationHandler> getData() {
            return data;
        }

        public void addLocation(LocationHandler handler) {
            if(data==null) data=new ArrayList<>();
            data.add(handler);
            notifyItemInserted(data.size()-1);
        }

    }

    private class ListHolder extends RecyclerView.ViewHolder {
        TextView locationText;
        TextView timeText;

        public ListHolder(View itemView) {
            super(itemView);
            locationText = (TextView) itemView.findViewById(R.id.location_text);
            timeText= (TextView) itemView.findViewById(R.id.location_time_text);
        }

        public void setData(LocationHandler location) {
            locationText.setText("Location Data" + " : [" + location.getLatitude() + "," + location.getLongitude() + "]");
            Date date=new Date(location.getTime());
            String formatDate= DateFormat.format("HH:mm:ss a on yyyy/MM/dd",date).toString();
            timeText.setText(formatDate);
        }
    }

    private class LocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<LocationHandler> list=new LocationDatabaseHelper(getActivity()).getLocations();
            Message message=mHandler.obtainMessage(UI_UPDATE);
            message.obj=list;
            message.sendToTarget();
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()==null) return;
        getActivity().unregisterReceiver(receiver);
        if(mFab!=null) {
            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new LocationTask().execute();
        if(getActivity()==null) return;
        getActivity().registerReceiver(receiver,new IntentFilter(FusedLocationReceiver.ACTION_FUSED_LOCATION));
        if(mFab!=null) {
            mFab.setVisibility(View.GONE);
        }
    }
}
