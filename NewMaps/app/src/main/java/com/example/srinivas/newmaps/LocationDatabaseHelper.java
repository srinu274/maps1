package com.example.srinivas.newmaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.example.srinivas.newmaps.LocationHandler;

import java.util.ArrayList;

/**
 * Created by Srinivas on 29-06-2016.
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "LocationData";
    private static final int VERSION_DB = 1;
    private static final String LOCATION_DATA_TABLE = "location_data";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TIME = "time";

    public LocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LOCATION_DATA_TABLE + "(" + LATITUDE + " bigint, " +
                LONGITUDE + " bigint , " + TIME + " bigint )");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long insertLocation(LocationHandler handler) {
        ContentValues values = new ContentValues();
        values.put(LATITUDE, handler.getLatitude());
        values.put(LONGITUDE, handler.getLongitude());
        values.put(TIME, handler.getTime());
        return getWritableDatabase().insert(LOCATION_DATA_TABLE, null, values);
    }

    public int deleteLocations() {
        return getWritableDatabase().delete(LOCATION_DATA_TABLE,null,null);
    }


    public LocationListElementCursor queryLocations() {
        Cursor wrapped = getReadableDatabase().query(LOCATION_DATA_TABLE,
                null, null, null, null, null, TIME + " desc ");
        return new LocationListElementCursor(wrapped);
    }

    public static class LocationListElementCursor extends CursorWrapper {

        public LocationListElementCursor(Cursor cursor) {
            super(cursor);
        }

        public LocationHandler getLocationListElement() {
            if (isAfterLast() || isBeforeFirst()) return null;
            LocationHandler handler = new LocationHandler();
            handler.setLatitude(getDouble(getColumnIndex(LATITUDE)));
            handler.setLongitude(getDouble(getColumnIndex(LONGITUDE)));
            handler.setTime(getLong(getColumnIndex(TIME)));
            return handler;
        }

        public LocationHandler getLocationListElement(int pos) {
            LocationHandler handler = null;
            if (moveToPosition(pos)) {
                handler = new LocationHandler();
                handler.setLatitude(getDouble(getColumnIndex(LATITUDE)));
                handler.setLongitude(getDouble(getColumnIndex(LONGITUDE)));
                handler.setTime(getLong(getColumnIndex(TIME)));
            }
            return handler;
        }
    }

    public ArrayList<LocationHandler> getLocations() {
        ArrayList<LocationHandler> handlers = new ArrayList<>();
        LocationListElementCursor cursor = queryLocations();
        if (cursor == null) return null;
        LocationHandler handler;
        for (int i = 0; i < cursor.getCount(); i++) {
            handler = cursor.getLocationListElement(i);
            if (handler == null) continue;
            handlers.add(handler);
        }
        cursor.close();
        return handlers;
    }
}
