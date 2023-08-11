package com.example.stalkernet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.Discharge;
import com.example.stalkernet.Globals;
import com.example.stalkernet.Points;
import com.example.stalkernet.PuzzleGameDialog;
import com.example.stalkernet.R;
import com.example.stalkernet.anomaly.Anomaly;
import com.example.stalkernet.map.RBPItem;
import com.example.stalkernet.map.VitalItem;
import com.google.android.material.button.MaterialButton;

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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.stalkernet.StatsService.INTENT_SERVICE;
import static com.example.stalkernet.StatsService.INTENT_SERVICE_TEST_COORDINATE;
import static com.example.stalkernet.StatsService.LOG_CHE;

public class MapOSMTab extends Fragment {

    public static final String INTENT_MAP = "MapTab.Circle";
    public static final String INTENT_MAP_VISIBLE = "visible";
    public static final String INTENT_MAP_UPDATE = "update";
    public static final int ARKH = 0;
    public static final int ADMIRAL = 1;
    public static final int MAIDAN = 3;
    public static final int SECOND_LES_ZAVOD = 2;
    public static final int GOOGLE_MAP = 4;

    private int current_map = 3;

    private Globals globals;
    private Discharge discharge;
    private Anomaly anomaly;
    private Points points;

    private MapView map = null;
    private MapEventsOverlay mapEventsOverlay = null;
    private RotationGestureOverlay mRotationGestureOverlay = null;
    private GroundOverlay overlay;
    private Bitmap currentMap;
    private GeoPoint startPoint;
    private IMapController mapController;
    private MyLocationNewOverlay mLocationOverlay;


    private Polygon[] anomalyPolygons = null;
    private Marker[] localityMarker = null;
    private Marker[] mileStoneMarker = null;
    private Marker playerPosition = null;
    private Marker testMarker = null;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private SQLiteDatabase databasePoint;
    private Cursor cursor;

    private DisplayMetrics displayMetrics;
    private int screenWidth;
    private int screenHeight;


    public MapOSMTab(Globals globals) {
        this.globals = globals;
    }

    BroadcastReceiver broadcastReceiverCircle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String income = intent.getStringExtra(INTENT_MAP_VISIBLE);

            if (income != null){
                String[] visibleString = income.split(":");
                anomalyPolygons[Integer.parseInt(visibleString[0])].setVisible(visibleString[1].equals("true"));
            }
            income = intent.getStringExtra(INTENT_MAP_UPDATE);
            if (income != null){
                checkLocality();
                checkMileStone();
                playerPosition.setPosition(new GeoPoint(globals.location.getLatitude(), globals.location.getLongitude()));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_map_osm, container, false);
        getDisplaySize();
        dbHelper = new DBHelper(getActivity());
        dbHelper.create_db();
        database = dbHelper.open();

        discharge = new Discharge(database, cursor);
        anomaly = new Anomaly(database, cursor);

        map = inflate.findViewById(R.id.mapOSM);
        setUpMap(current_map);

        setVitalStatus(inflate);
        setTestMarker(true);
        setBtnToUser(inflate);
        setBtnOpenHub(inflate);
        setCoordinate(inflate);
        setTxtGestalt(inflate);
        setBtnMapChange(inflate);
        setBtnHideOSMUserMarker(inflate);

        points = new Points(getContext());
        // рисует маркеры из БД на карте
        drawMarkers();
        // создаем круглые аномалии и всякое другое
        try {
            anomalyPolygons = anomaly.createPolygons(map);
            createLocalityMarker();
            createMileStoneMarker();
        } catch (Exception e) {
            e.printStackTrace();
        }
        discharge.createSafeZones(map);
        // Inflate the layout for this fragment
        return inflate;
    }
    /*
    * выясняет размеры дисплея
    * */
    private void getDisplaySize(){
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
    /*
    * настройки карты
    * */
    private void setUpMap(int location){
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        // возможность ставить маркеры на карту
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        map.getOverlays().add(mapEventsOverlay);
        overlay = new GroundOverlay();
        overlay.setTransparency(0f);
        switch (location){
            case MAIDAN:
                startPoint = new GeoPoint(64.35342867d, 40.7328d);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_2022);
                overlay.setPosition(new GeoPoint(/*64.3606562*/64.36029,40.71272391), new GeoPoint( /*64.347312*/64.347652, 40.75284205));
                break;
            case ADMIRAL:
                startPoint = new GeoPoint(64.573749, 40.516295);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_adm);
                overlay.setPosition(new GeoPoint(64.575250, 40.511678), new GeoPoint( 64.572320, 40.522501));
                break;
            case SECOND_LES_ZAVOD:
                startPoint = new GeoPoint(64.492765, 40.711437);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map_secondlz);
                overlay.setPosition(new GeoPoint(64.494202, 40.706212), new GeoPoint( 64.491258, 40.717042));
                break;
            case GOOGLE_MAP:
                startPoint = new GeoPoint(64.35342867d, 40.7328d);
                currentMap = BitmapFactory.decodeResource(getResources(), R.drawable.map2023g);
                overlay.setPosition(new GeoPoint(64.359857,40.712084), new GeoPoint( 64.348411, 40.7575));
                overlay.setTransparency(0.2f);
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
        //map.setMinZoomLevel(14.0);
        map.getOverlayManager().add(overlay);
        // показывает мое местоположение
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.setEnabled(false);
        map.getOverlays().add(mLocationOverlay);
        // установка маркера игрока
        playerPosition = new Marker(map);
        playerPosition.setIcon(getResources().getDrawable(R.drawable.point));
        playerPosition.setTitle("it's me");
        playerPosition.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(playerPosition);
    }

    /*
    * установка текстового обозначения открытости гештальта
    * */
    private void setTxtGestalt(View inflate){
        globals.tvGestaltOpen = inflate.findViewById(R.id.txtGestalt);
    }
    /*
    * установка координаты
    * */
    private void setCoordinate(View inflate){
        globals.tvCoordinate = inflate.findViewById(R.id.txtCoordinate);
    }

    /*
    * установка жизненных показателей
    * */
    private void setVitalStatus(View inflate){
        VitalItem health = new VitalItem(inflate, screenWidth, screenHeight);
        health.setItem(R.id.ivHealth, R.id.pbHealth, R.id.tvHealth);
        globals.healthBar = health.getProgressBar();
        globals.healthText = health.getTextView();

        RBPItem rad = new RBPItem(inflate, screenWidth, screenHeight);
        rad.setItem(R.id.ivRad, R.id.pbRad, R.id.tvRad);
        rad.setProtectionBars(R.id.pbRadSuit, R.id.pbRadArt, R.id.pbRadQuest);
        globals.radBar = rad.getProgressBar();
        globals.radText = rad.getTextView();
        globals.radQuestBar = rad.getProtectionBar(0);
        globals.radArtBar = rad.getProtectionBar(1);
        globals.radSuitBar = rad.getProtectionBar(2);

        RBPItem bio = new RBPItem(inflate, screenWidth, screenHeight);
        bio.setItem(R.id.ivBio, R.id.pbBio, R.id.tvBio);
        bio.setProtectionBars(R.id.pbBioSuit, R.id.pbBioArt, R.id.pbBioQuest);
        globals.bioBar = bio.getProgressBar();
        globals.bioText = bio.getTextView();
        globals.bioQuestBar = bio.getProtectionBar(0);
        globals.bioArtBar = bio.getProtectionBar(1);
        globals.bioSuitBar = bio.getProtectionBar(2);

        RBPItem psy = new RBPItem(inflate, screenWidth, screenHeight);
        psy.setItem(R.id.ivPsy, R.id.pbPsy, R.id.tvPsy);
        psy.setProtectionBars(R.id.pbPsySuit, R.id.pbPsyArt, R.id.pbPsyQuest);
        globals.psyBar = psy.getProgressBar();
        globals.psyText = psy.getTextView();
        globals.psyQuestBar = psy.getProtectionBar(0);
        globals.psyArtBar = psy.getProtectionBar(1);
        globals.psySuitBar = psy.getProtectionBar(2);
    }
    /*
    * кнопка переходи на пользователя
    * */
    private void setBtnToUser(View inflate){
        MaterialButton toUser = inflate.findViewById(R.id.btnToUser);
        toUser.setOnClickListener(view -> {
            mapController.setCenter(new GeoPoint(globals.location.getLatitude(), globals.location.getLongitude()));
        });
    }
    /*
    * кнопка смены карты с красивой на гугл
    * */
    private void setBtnMapChange(View inflate){
        MaterialButton mapChange = inflate.findViewById(R.id.btnMapChange);
        mapChange.setOnClickListener(view -> {
            if (current_map != MAIDAN){
                current_map = MAIDAN;
                setUpMap(MAIDAN);

            }else {
                current_map = GOOGLE_MAP;
                setUpMap(GOOGLE_MAP);

            }
            onPause();
            onResume();
            /*map.getOverlayManager().remove(overlay);
            map.invalidate();*/
        });
    }
    /*
    * кнопка, чтобы скрыть-показать маркер OSM человечка
    * */
    private void setBtnHideOSMUserMarker(View inflate){
        MaterialButton btnHideOSMUserMarker = inflate.findViewById(R.id.btnHideOSMUserMarker);
        btnHideOSMUserMarker.setIcon(getResources().getDrawable(R.drawable.man_off));
        btnHideOSMUserMarker.setOnClickListener(view -> {
          mLocationOverlay.setEnabled(!mLocationOverlay.isEnabled());
          if (mLocationOverlay.isEnabled()){
              btnHideOSMUserMarker.setIcon(getResources().getDrawable(R.drawable.man_on));
          } else{
              btnHideOSMUserMarker.setIcon(getResources().getDrawable(R.drawable.man_off));
          }
        });
    }
    /*
    * кнопка перехода в кучу фрагментов
    * */
    private void setBtnOpenHub(View inflate){
        MaterialButton openhub = inflate.findViewById(R.id.btnOpenHub);
        openhub.setOnClickListener(view -> {
            // Create a new instance of the fragment you want to show
            FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);

// Create a new fragment and replace the existing one
            HubFragment hubFragment = new HubFragment(globals);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentContainer.getId(), hubFragment);
            fragmentTransaction.commit();
            Log.d(LOG_CHE, "wtf");
        });

    }
    /*
    * тестовый маркер который можно передвигать на карте
    * */
    private void setTestMarker(boolean isOn){
        if (isOn){
            testMarker = new Marker(map);
            testMarker.setIcon(getResources().getDrawable(R.drawable.quest));
            testMarker.setPosition(new GeoPoint(64.573749, 40.516295));
            testMarker.setVisible(true);
            testMarker.setDraggable(true);
            map.getOverlays().add(testMarker);
            testMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Intent intent = new Intent(INTENT_SERVICE);
                    intent.putExtra(INTENT_SERVICE_TEST_COORDINATE, testMarker.getPosition().toString());
                    getActivity().sendBroadcast(intent);
                }

                @Override
                public void onMarkerDragStart(Marker marker) {

                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        database = dbHelper.open();
        try {
            createLocalityMarker();
            //createMileStoneMarker();
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

    /*private void createThingsForMap(){
        if (anomalyPolygons == null){
            anomalyPolygons = anomaly.createPolygons(map);
        }
        if (localityMarker == null){
            createLocalityMarker(*//*getNumberOfRows(DBHelper.TABLE_LOCALITY)*//*);
        }
    }*/
    // обработчик нажатий на карту, долгое - добавление маркер
    MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            return false;
        }

        @Override
        public boolean longPressHelper(GeoPoint p) {
            insertPoint(p);
            return false;
        }
    };

    // записывает маркеры в БД и рисует последний
    private void insertPoint(GeoPoint geoPoint){

        points.insert(geoPoint.getLatitude(),geoPoint.getLongitude());

        // рисование последнего маркера
        databasePoint = dbHelper.getWritableDatabase();
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
                String title = cursor.getString(nameIndex).equals("name") ? cursor.getString(idIndex) : cursor.getString(nameIndex);
                startMarker.setTitle(title);
                startMarker.setSubDescription(cursor.getString(commentIndex));
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

    private void checkMileStone(){
        cursor = database.query(DBHelper.TABLE_MILESTONE, new String[]{DBHelper.KEY_ID__MILESTONE, DBHelper.KEY_FINISH_STATUS__MILESTONE}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MILESTONE);
            int accessStatus = cursor.getColumnIndex(DBHelper.KEY_FINISH_STATUS__MILESTONE);

            do {
                if (cursor.getString(accessStatus).equals("true")) {
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_khown_loc));
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setDragOffset(0);
                } else if(cursor.getString(accessStatus).equals("false")){
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.status_active_0521));
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setDragOffset(8);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    private void createMileStoneMarker(){

        cursor = database.query(DBHelper.TABLE_MILESTONE, null, null, null, null, null, null);
        mileStoneMarker = new Marker[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MILESTONE);
            int name = cursor.getColumnIndex(DBHelper.KEY_NAME__MILESTONE);
            int description = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__MILESTONE);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__MILESTONE);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__MILESTONE);
            int finishStatus = cursor.getColumnIndex(DBHelper.KEY_FINISH_STATUS__MILESTONE);
            do {
                mileStoneMarker[cursor.getInt(idIndex) - 1] = new Marker(map);

                mileStoneMarker[cursor.getInt(idIndex) - 1].setPosition(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)));
                mileStoneMarker[cursor.getInt(idIndex) - 1].setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                if (cursor.getString(finishStatus).equals("true")) {
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_khown_loc));
                } else {
                    mileStoneMarker[cursor.getInt(idIndex) - 1].setIcon(getResources().getDrawable(R.drawable.icon_empty_loc));
                }
                mileStoneMarker[cursor.getInt(idIndex) - 1].setTitle(cursor.getString(name));
                mileStoneMarker[cursor.getInt(idIndex) - 1].setSubDescription(cursor.getString(description));
                mileStoneMarker[cursor.getInt(idIndex) - 1].setId(cursor.getString(idIndex));
                mileStoneMarker[cursor.getInt(idIndex) - 1].setOnMarkerClickListener((marker, mapView) -> {
                    showPuzzleGameDialog(Integer.parseInt(marker.getId()), marker.getTitle(), marker.getSubDescription(), (int) marker.getDragOffset());
                    return false;
                });
                map.getOverlays().add(mileStoneMarker[cursor.getInt(idIndex) - 1]);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showPuzzleGameDialog(int id, String name, String description, int enable) {
        PuzzleGameDialog dialog = PuzzleGameDialog.newInstance(id, name, description, enable);
        dialog.show(getFragmentManager(), "PuzzleGameDialog");

    }

}