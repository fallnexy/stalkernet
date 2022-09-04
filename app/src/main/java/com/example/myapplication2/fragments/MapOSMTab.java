package com.example.myapplication2.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.HOUR_OF_DAY;

public class MapOSMTab extends Fragment {

    private Globals globals;
    private MapView map = null;
    MapEventsOverlay mapEventsOverlay = null;
    CompassOverlay mCompassOverlay = null;
    RotationGestureOverlay mRotationGestureOverlay = null;

    Polygon[] circleAnomaly = null;

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    public MapOSMTab(Globals globals) {
        this.globals = globals;
    }


    BroadcastReceiver broadcastReceiverCircle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawCirceAnomaly();
        }
    };

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
        mapController.setZoom(14.65);
        GeoPoint startPoint = new GeoPoint(64.35342867d, 40.7328d);
        mapController.setCenter(startPoint);
        // вращение карты
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);
        // добавляет компас на карту
        mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);

        // накладывает карту
        // карта около Адмиралтейской
        //Bitmap currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.mapadm6);
        // карта МАйдан
        Bitmap currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map2021);
        GroundOverlay overlay = new GroundOverlay();
        overlay.setTransparency(0f);
        overlay.setImage(currentMap);
        // адмиралтейская
        //overlay.setPosition(new GeoPoint(64.574154d, 40.518798d), new GeoPoint( 64.573228d, 40.514540d));
        // майдан
        overlay.setPosition(new GeoPoint(64.3606562,40.71272391), new GeoPoint( 64.34758838, 40.75284205));
        map.getOverlayManager().add(overlay);

        // возможность ставить маркеры на карту
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);
        // рисует маркеры из БД на карте
        try {
            drawMarkers();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        //
        // показывает мое местоположение
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        // создаем круглые аномалии
        createCircleAnomaly (getNumberOfAnomalies());
        // Inflate the layout for this fragment
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiverCircle, new IntentFilter("MapTab.Circle"));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(broadcastReceiverCircle);
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
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
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
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
                startMarker.setTitle(cursor.getString(idIndex));
                map.getOverlays().add(startMarker);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }
    /*
    * Когда координата меняется, то происходит цепочка вызовов, которая приводит к drawCirceAnomaly().
    * Этот метод проверяет в таблице DBHelper.TABLE_ANOMALY значение строки DBHelper.KEY_BOOL_SHOW_ON_MAP:
    * если "true", то показываем круг на карте setVisible(true), "false" то скрываем setVisible(false)
    */
    private void drawCirceAnomaly(){
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"_id", "bool_show_on_map"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID_ANOMALY);
            int boolShowOnMap = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOW_ON_MAP);

            do {
                Log.d("аномалия_bool", String.valueOf(cursor.getString(boolShowOnMap)));
                if (cursor.getString(boolShowOnMap).equals("true") && !circleAnomaly[cursor.getInt(idIndex) - 1].isVisible()) {
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(true);
                } else if(cursor.getString(boolShowOnMap).equals("false")){
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }
    // высчитывание количества круглых аномалий
    private int getNumberOfAnomalies(){
        int numberOfAnomalies = 0;
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"COUNT(_id)"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            numberOfAnomalies = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        Log.d("аномалия", String.valueOf(numberOfAnomalies));
        return numberOfAnomalies;
    }
    /*
    * В onCreate все аномалии добавляются на карту, но с параметром setVisible(false).
    * Когда координата меняется, то происходит цепочка вызовов, в результате которой вызывается drawCirceAnomaly().
    * Метод drawCirceAnomaly() проверяет следующее: если в таблице DBHelper.TABLE_ANOMALY
    * строка DBHelper.KEY_BOOL_SHOW_ON_MAP равна "true", то для аномалии ставится setVisible(true)
    */
    private void createCircleAnomaly(int numberOfAnomalies){
        circleAnomaly = new Polygon[numberOfAnomalies];
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_ANOMALY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID_ANOMALY);
            int type = cursor.getColumnIndex(DBHelper.KEY_TYPE);
            int radius = cursor.getColumnIndex(DBHelper.KEY_RADIUS);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE_ANOMALY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE_ANOMALY);
            int boolShowOnMap = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOW_ON_MAP);
            do {
                circleAnomaly[cursor.getInt(idIndex) - 1] = new Polygon();
                switch (cursor.getString(type)){
                    case "Rad":
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1Efca800")); // цвет заливки
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#fca800")); // цвет окружности
                        break;
                    case "Bio":
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1E00ff2b")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#00ff2b"));
                        break;
                    case "Psy":
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1E0011ff")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#0011ff"));
                        break;
                    default:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                        break;
                }
                circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setStrokeWidth(3); // толщина окружности
                circleAnomaly[cursor.getInt(idIndex) - 1].setPoints(Polygon.pointsAsCircle(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), cursor.getDouble(radius)));
                //circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                map.getOverlayManager().add(circleAnomaly[cursor.getInt(idIndex) - 1]);
                circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();

    }

}