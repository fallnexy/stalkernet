package com.example.myapplication2.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.GroundOverlay2;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapOSMTab extends Fragment {

    private Globals globals;
    private MapView map = null;
    MapEventsOverlay mapEventsOverlay = null;
    CompassOverlay mCompassOverlay = null;

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    public MapOSMTab(Globals globals) {
        this.globals = globals;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_map_osm, container, false);
        dbHelper = new DBHelper(getActivity());

        map = inflate.findViewById(R.id.mapOSM);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);


        // начальные координаты и зум
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(64.573632, 40.5164);
        mapController.setCenter(startPoint);
        // показывает мое местоположение
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        // добавляет компас на карту
        mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);
        // накладывает карту
        Bitmap currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map2021);
        GroundOverlay overlay = new GroundOverlay();
        overlay.setTransparency(0f);
        overlay.setImage(currentMap);
        overlay.setPosition(new GeoPoint(64.3606562,40.71272391), new GeoPoint( 64.34758838, 40.75284205));
        map.getOverlayManager().add(overlay);
        // возможность ставить маркеры на карту
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);

        try {
            drawMarkers();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    // обработчик нажатий на карту, долгое - добавление маркер
    MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            return false;
        }

        @Override
        public boolean longPressHelper(GeoPoint p) {
            writeMarkerToDB(p);
            return false;
        }
    };
    // записывает маркеры в БД и рисует последний
    private void writeMarkerToDB(GeoPoint p){
        /*
         * try/finally призвана для безопасности и быстроты
         * */
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        database.beginTransaction();
        try {
            contentValues.put(DBHelper.KEY_NAME, "name");
            contentValues.put(DBHelper.KEY_ICON, "icon");
            contentValues.put(DBHelper.KEY_LATITUDE, p.getLatitude()); //если написать здесь format для округления о 6 знаков после запятой, то всё ломается
            contentValues.put(DBHelper.KEY_LONGITUDE, p.getLongitude());
            contentValues.put(DBHelper.KEY_COMMENT, "comment");
            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        // рисование последнего маркера
        cursor = database.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
        cursor.moveToLast();
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
        int iconIndex = cursor.getColumnIndex(DBHelper.KEY_ICON);
        int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
        int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
        int commentIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        startMarker.setTitle(cursor.getString(idIndex));
        map.getOverlays().add(startMarker);
        cursor.close();
        dbHelper.close();
    }
    // рисует маркеры на карте
    private void drawMarkers(){
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int iconIndex = cursor.getColumnIndex(DBHelper.KEY_ICON);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
            int commentIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            do {
                Marker startMarker = new Marker(map);
                startMarker.setPosition(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
                startMarker.setTitle(cursor.getString(idIndex));
                map.getOverlays().add(startMarker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }

}