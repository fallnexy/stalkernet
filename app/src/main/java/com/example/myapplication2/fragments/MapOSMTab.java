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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.Discharge;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.example.myapplication2.anomaly.Anomaly;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.Locale;

import androidx.fragment.app.Fragment;

public class MapOSMTab extends Fragment {

    private Globals globals;
    private Discharge discharge;
    private Anomaly anomaly;

    private MapView map = null;
    private MapEventsOverlay mapEventsOverlay = null;
    private CompassOverlay mCompassOverlay = null;
    private RotationGestureOverlay mRotationGestureOverlay = null;
    private HashMap<String, String> anomalyFillPaintMap;
    private HashMap<String, String> anomalyOutlinePaintMap;

    private Polygon[] circleAnomaly = null;
    private Marker[] localityMarker = null;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private SQLiteDatabase databasePoint;
    private Cursor cursor;

    //TODO зачем тут глобалс?
    public MapOSMTab(Globals globals) {
        this.globals = globals;
    }


    BroadcastReceiver broadcastReceiverCircle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                anomaly.setAnomalyVisible(circleAnomaly);
                checkLocality();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_map_osm, container, false);
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.open();
        discharge = new Discharge(database, cursor);
        anomaly = new Anomaly(database, cursor);
        map = inflate.findViewById(R.id.mapOSM);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);


        // начальные координаты и зум
        IMapController mapController = map.getController();
        mapController.setZoom(14.65);
        // майдан точка
        //GeoPoint startPoint = new GeoPoint(64.35342867d, 40.7328d);
        // арх центр
        GeoPoint startPoint = new GeoPoint(64.544608, 40.546129);
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
        //Bitmap currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_2022);
        // карта Архангельск
        Bitmap currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.maparkh);
        GroundOverlay overlay = new GroundOverlay();
        overlay.setTransparency(0f);
        overlay.setImage(currentMap);
        // адмиралтейская
        //overlay.setPosition(new GeoPoint(64.574154d, 40.518798d), new GeoPoint( 64.573228d, 40.514540d));
        // майдан
        //overlay.setPosition(new GeoPoint(64.3606562,40.71272391), new GeoPoint( 64.347312, 40.75284205));
        // арх
        overlay.setPosition(new GeoPoint(64.556753, 40.493319), new GeoPoint( 64.529705, 40.593033));
        map.getOverlayManager().add(overlay);

        // возможность ставить маркеры на карту
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);
        // рисует маркеры из БД на карте
        drawMarkers();
        //
        // показывает мое местоположение
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        // создаем круглые аномалии и всякое другое
        //createThingsForMap();
        try {
            circleAnomaly = anomaly.createCircleAnomaly (map);
            createLocalityMarker(/*getNumberOfRows(DBHelper.TABLE_LOCALITY)*/);
        } catch (Exception e) {
            e.printStackTrace();
        }

        discharge.createSafeZones(map);
        // Inflate the layout for this fragment
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        database = dbHelper.open();
        // создаем круглые аномалии
        try {
            circleAnomaly = anomaly.createCircleAnomaly (map);
            createLocalityMarker(/*getNumberOfRows(DBHelper.TABLE_LOCALITY)*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //createThingsForMap();
        getActivity().registerReceiver(broadcastReceiverCircle, new IntentFilter("MapTab.Circle"));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(broadcastReceiverCircle);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void createThingsForMap(){
        if (circleAnomaly == null){
            circleAnomaly = anomaly.createCircleAnomaly (map);
        }
        if (localityMarker == null){
            createLocalityMarker(/*getNumberOfRows(DBHelper.TABLE_LOCALITY)*/);
        }
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
    private void writeMarkerToDB(GeoPoint geoPoint){
        /*
         * try/finally призвана для безопасности и быстроты
         * */
        databasePoint = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        databasePoint.beginTransaction();
        try {
            contentValues.put(DBHelper.KEY_NAME__MARKER, "name");
            contentValues.put(DBHelper.KEY_ICON__MARKER, "icon");
            contentValues.put(DBHelper.KEY_LATITUDE__MARKER, String.format(Locale.US, "%.6f", geoPoint.getLatitude()));
            contentValues.put(DBHelper.KEY_LONGITUDE__MARKER, String.format(Locale.US, "%.6f", geoPoint.getLongitude()));
            contentValues.put(DBHelper.KEY_COMMENT__MARKER, "comment");
            databasePoint.insert(DBHelper.TABLE_MARKERS, null, contentValues);
            databasePoint.setTransactionSuccessful();
        } finally {
            databasePoint.endTransaction();
        }
        // рисование последнего маркера
        cursor = databasePoint.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
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
        databasePoint.close();
    }
    // рисует маркеры на карте
    private void drawMarkers(){
        databasePoint = dbHelper.getWritableDatabase();
        cursor = databasePoint.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MARKER);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME__MARKER);
            int iconIndex = cursor.getColumnIndex(DBHelper.KEY_ICON__MARKER);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__MARKER);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__MARKER);
            int commentIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT__MARKER);
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
        databasePoint.close();
    }
    /*
    * Когда координата меняется, то происходит цепочка вызовов, которая приводит к setAnomalyVisible().
    * Этот метод проверяет в таблице DBHelper.TABLE_ANOMALY значение строки DBHelper.KEY_BOOL_SHOW_ON_MAP:
    * если "true", то показываем круг на карте setVisible(true), "false" то скрываем setVisible(false)
    */
    /*private void setAnomalyVisible(){
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"_id", "bool_show_on_map"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int boolShowOnMap = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOW_ON_MAP__ANOMALY);

            do {
                //Log.d("аномалия_bool", String.valueOf(cursor.getString(boolShowOnMap)));
                if (cursor.getString(boolShowOnMap).equals("true") && !circleAnomaly[cursor.getInt(idIndex) - 1].isVisible()) {
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(true);
                } else if(cursor.getString(boolShowOnMap).equals("false")){
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }*/
    /*
    * Вызывается в broadcastReceiverCircle и, в зависимости от DBHelper.KEY_ACCESS_STATUS_LOCALITY в DBHelper.TABLE_LOCALITY,
    * выставляет иконку локации
    * */
    private void checkLocality(){
        cursor = database.query(DBHelper.TABLE_LOCALITY, new String[]{"_id", "access_status"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__LOCALITY);
            int accessStatus = cursor.getColumnIndex(DBHelper.KEY_ACCESS_STATUS__LOCALITY);

            do {
                //Log.d("аномалия_bool", String.valueOf(cursor.getString(accessStatus)));
                if (cursor.getString(accessStatus).equals("true")) {
                    localityMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_khown_loc));
                } else if(cursor.getString(accessStatus).equals("false")){
                    localityMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_empty_loc));
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    /*
    * высчитывание количества круглых аномалий, а точнее не толькно аномалий, а количество строк в таблице
    * */
    /*private int getNumberOfRows(String table){
        int numberOfRows = 0;
        cursor = database.query(table, new String[]{"COUNT(_id)"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            numberOfRows = cursor.getInt(0);
        }
        cursor.close();
        return numberOfRows;
    }*/
    /*
    * создает карты для выбора цвета окружности и внутренности аномалий
    * */
    /*private void setAnomalyPaintMap(){
        anomalyFillPaintMap = new HashMap<>();
        anomalyOutlinePaintMap = new HashMap<>();
        anomalyFillPaintMap.put(RAD, "#1Efca800");
        anomalyFillPaintMap.put(BIO, "#1E00ff2b");
        anomalyFillPaintMap.put(PSY, "#1E0011ff");
        anomalyFillPaintMap.put(GESTALT, "#1E58585c");
        anomalyFillPaintMap.put(OASIS, "#1Ef227e1");
        anomalyOutlinePaintMap.put(RAD, "#fca800");
        anomalyOutlinePaintMap.put(BIO, "#00ff2b");
        anomalyOutlinePaintMap.put(PSY, "#0011ff");
        anomalyOutlinePaintMap.put(GESTALT, "#58585c");
        anomalyOutlinePaintMap.put(OASIS, "#f227e1");
    }*/
    /*
    * В onCreate все аномалии добавляются на карту, но с параметром setVisible(false).
    * Когда координата меняется, то происходит цепочка вызовов, в результате которой вызывается setAnomalyVisible().
    * Метод drawCirceAnomaly() проверяет следующее: если в таблице DBHelper.TABLE_ANOMALY
    * строка DBHelper.KEY_BOOL_SHOW_ON_MAP равна "true", то для аномалии ставится setVisible(true)
    */
    /*private void createCircleAnomaly(int numberOfAnomalies){
        setAnomalyPaintMap();
        circleAnomaly = new Polygon[numberOfAnomalies];
        cursor = database.query(DBHelper.TABLE_ANOMALY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int type = cursor.getColumnIndex(DBHelper.KEY_TYPE__ANOMALY);
            int radius = cursor.getColumnIndex(DBHelper.KEY_RADIUS__ANOMALY);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__ANOMALY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__ANOMALY);
            do {
                circleAnomaly[cursor.getInt(idIndex) - 1] = new Polygon();
                try {
                    circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor(anomalyFillPaintMap.get(cursor.getString(type)))); // цвет заливки
                    circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor(anomalyOutlinePaintMap.get(cursor.getString(type)))); // цвет окружности
                }catch (Exception e){
                    circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                }
                *//*switch (cursor.getString(type)){
                    case RAD:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1Efca800")); // цвет заливки
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#fca800")); // цвет окружности
                        break;
                    case BIO:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1E00ff2b")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#00ff2b"));
                        break;
                    case PSY:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1E0011ff")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#0011ff"));
                        break;
                    case GESTALT:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1E58585c")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#58585c"));
                        break;
                    case OASIS:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1Ef227e1")); //set fill color
                        circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor("#f227e1"));
                        break;
                    default:
                        circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                        break;
                }*//*
                circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setStrokeWidth(3); // толщина окружности
                circleAnomaly[cursor.getInt(idIndex) - 1].setPoints(Polygon.pointsAsCircle(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), cursor.getDouble(radius)));
                map.getOverlayManager().add(circleAnomaly[cursor.getInt(idIndex) - 1]);
                circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }*/
    /*
    * В onCreate и onResume создает маркеры для локаций.
    * Иконка локации зависит от DBHelper.KEY_ACCESS_STATUS_LOCALITY в DBHelper.TABLE_LOCALITY
    * */
    private void createLocalityMarker(/*int numberOfLocalities*/){

        cursor = database.query(DBHelper.TABLE_LOCALITY, null, null, null, null, null, null);
        localityMarker = new Marker[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__LOCALITY);
            int name = cursor.getColumnIndex(DBHelper.KEY_NAME__LOCALITY);
            int description = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__LOCALITY);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__LOCALITY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__LOCALITY);
            int accessStatus = cursor.getColumnIndex(DBHelper.KEY_ACCESS_STATUS__LOCALITY);
            do {
                localityMarker[cursor.getInt(idIndex) - 1] = new Marker(map);

                localityMarker[cursor.getInt(idIndex) - 1].setPosition(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)));
                localityMarker[cursor.getInt(idIndex) - 1].setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                if (cursor.getString(accessStatus).equals("true")) {
                    localityMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_khown_loc));
                } else {
                    localityMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_empty_loc));
                }
                localityMarker[cursor.getInt(idIndex) - 1].setTitle(cursor.getString(name));
                map.getOverlays().add(localityMarker[cursor.getInt(idIndex) - 1]);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}