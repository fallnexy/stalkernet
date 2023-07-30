package com.example.stalkernet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

import static com.example.stalkernet.fragments.PointTab.INTENT_POINT_TAB;
import static com.example.stalkernet.fragments.PointTab.INTENT_POINT_TAB_RENEW;

/*
* Создан 01.03.23
* Класс для маркеорв - меток на карте
* Использует дазу данных, которая не привнесена из вне, а создана приложение
* */
public class Points {

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    private Context context;

    public Points(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    private void openDatabase(){
        if (database == null){
            database = dbHelper.getWritableDatabase();
        }
    }
    /*
    * Запись точки в базу данных
    * */
    public void insert(double latitude, double longitude){
        openDatabase();
        ContentValues contentValues = new ContentValues();
        database.beginTransaction();
        try {
            contentValues.put(DBHelper.KEY_NAME__MARKER, "name");
            contentValues.put(DBHelper.KEY_ICON__MARKER, "icon");
            contentValues.put(DBHelper.KEY_LATITUDE__MARKER, String.format(Locale.US, "%.6f", latitude));
            contentValues.put(DBHelper.KEY_LONGITUDE__MARKER, String.format(Locale.US, "%.6f", longitude));
            contentValues.put(DBHelper.KEY_COMMENT__MARKER, "comment");
            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        Intent intent = new Intent(INTENT_POINT_TAB);
        intent.putExtra(INTENT_POINT_TAB_RENEW, "true");
        context.sendBroadcast(intent);
    }
    /*
    * Рисование точек на карте
    * */
    public void draw(boolean onlyLast, MapView map){
        cursor = database.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
        cursor.moveToLast();
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MARKER);
        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME__MARKER);
        int iconIndex = cursor.getColumnIndex(DBHelper.KEY_ICON__MARKER);
        int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__MARKER);
        int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__MARKER);
        int commentIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT__MARKER);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //startMarker.setIcon(getResources().getDrawable(R.drawable.location_active));
        startMarker.setTitle(cursor.getString(idIndex));
        map.getOverlays().add(startMarker);
        cursor.close();
        database.close();
    }
}
