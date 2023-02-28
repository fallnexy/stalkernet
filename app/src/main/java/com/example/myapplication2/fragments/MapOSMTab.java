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
import android.util.Log;
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
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Locale;

import androidx.fragment.app.Fragment;

import static com.example.myapplication2.StatsService.LOG_CHE;

public class MapOSMTab extends Fragment {

    public static final String INTENT_MAP = "MapTab.Circle";
    public static final String INTENT_MAP_VISIBLE = "visible";
    public static final String INTENT_MAP_UPDATE = "update";
    public static final int ARKH = 0;
    public static final int ADMIRAL = 1;
    public static final int MAIDAN = 3;

    private Globals globals;
    private Discharge discharge;
    private Anomaly anomaly;

    private MapView map = null;
    private MapEventsOverlay mapEventsOverlay = null;
    private RotationGestureOverlay mRotationGestureOverlay = null;
    private GroundOverlay overlay;
    private Bitmap currentMap;
    private GeoPoint startPoint;
    private IMapController mapController;


    private Polygon[] anomalyPolygons = null;
    private Marker[] localityMarker = null;
    private Marker playerPosition = null;

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
            String income = intent.getStringExtra(INTENT_MAP_VISIBLE);

            if (income != null){
                Log.d(LOG_CHE,income);
                String[] visibleString = income.split(":");
                anomalyPolygons[Integer.parseInt(visibleString[0])].setVisible(visibleString[1].equals("true"));
            }
            income = intent.getStringExtra(INTENT_MAP_UPDATE);
            if (income != null){
                checkLocality();
                playerPosition.setPosition(new GeoPoint(globals.location.getLatitude(), globals.location.getLongitude()));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_map_osm, container, false);
        dbHelper = new DBHelper(getActivity());
        dbHelper.create_db();
        database = dbHelper.open();
        discharge = new Discharge(database, cursor);
        anomaly = new Anomaly(database, cursor);
        map = inflate.findViewById(R.id.mapOSM);
        setUpMap(ADMIRAL);

        // рисует маркеры из БД на карте
        drawMarkers();
        // создаем круглые аномалии и всякое другое
        try {
            anomalyPolygons = anomaly.createPolygons(map);
            createLocalityMarker();
        } catch (Exception e) {
            e.printStackTrace();
        }
        discharge.createSafeZones(map);
        // Inflate the layout for this fragment
        return inflate;
    }
    /*
    * настройки карты
    * */
    public void setUpMap(int location){
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // вращение карты
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(false);
        map.getOverlays().add(this.mRotationGestureOverlay);
        // возможность ставить маркеры на карту
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);
        overlay = new GroundOverlay();
        overlay.setTransparency(0f);
        switch (location){
            case MAIDAN:
                startPoint = new GeoPoint(64.35342867d, 40.7328d);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_2022);
                overlay.setPosition(new GeoPoint(64.3606562,40.71272391), new GeoPoint( 64.347312, 40.75284205));
                break;
            case ADMIRAL:
                startPoint = new GeoPoint(64.573749, 40.516295);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_adm);
                overlay.setPosition(new GeoPoint(64.575250, 40.511678), new GeoPoint( 64.572320, 40.522501));
                break;
            case ARKH:
            default:
                startPoint = new GeoPoint(64.544608, 40.546129);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.maparkh);
                overlay.setPosition(new GeoPoint(64.592084, 40.425673), new GeoPoint( 64.498119, 40.772684));
        }

        overlay.setImage(currentMap);
        mapController = map.getController();
        mapController.setZoom(14.65);
        mapController.setCenter(startPoint);
        map.setMinZoomLevel(14.0);
        map.getOverlayManager().add(overlay);
        // показывает мое местоположение
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        // установка маркера игрока
        playerPosition = new Marker(map);
        playerPosition.setIcon(getResources().getDrawable(R.drawable.point));
        playerPosition.setTitle("its me");
        playerPosition.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(playerPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        database = dbHelper.open();
        try {
            createLocalityMarker();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //createThingsForMap();
        getActivity().registerReceiver(broadcastReceiverCircle, new IntentFilter(INTENT_MAP));
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
        if (anomalyPolygons == null){
            anomalyPolygons = anomaly.createPolygons(map);
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
    * В onCreate и onResume создает маркеры для локаций.
    * Иконка локации зависит от DBHelper.KEY_ACCESS_STATUS_LOCALITY в DBHelper.TABLE_LOCALITY
    * */
    private void createLocalityMarker(){

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