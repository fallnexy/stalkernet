package com.example.myapplication2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MapTab extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private Globals globals;
    public MarkerOptions LastMarker;
    private GoogleMap mMap;
    DBHelper dbHelper;
    public MapTab(Globals globals) {
        this.globals = globals;
    }
    SQLiteDatabase database;
    Cursor cursor;
    Circle mapCircle = null;

    long checkTime_in = 1620988200; // 14 мая 13:30 // 1620988200
    long checkTime_out = 1621167600; // 16 мая 15:20 // 1621167600
    long delta_time = 180;
    Circle[] mapCircle_save = new Circle[8];
    SuperSaveZone[] superSaveZones = new SuperSaveZone[8];

    public void Create_mapCircles(){
        mapCircle_save[0] = null;
        mapCircle_save[1] = null;
        mapCircle_save[2] = null;
        mapCircle_save[3] = null;
        mapCircle_save[4] = null;
        mapCircle_save[5] = null;
        mapCircle_save[6] = null;
        mapCircle_save[7] = null;
    }
    public void Create_super_save_zones(){
        superSaveZones[0] = new SuperSaveZone(checkTime_in + 900, 0, delta_time, 20d, "stalkers_in");
        superSaveZones[1] = new SuperSaveZone((checkTime_in + delta_time / 2 + 900), 1, delta_time, 20d, "stalkers_in");
        superSaveZones[2] = new SuperSaveZone((checkTime_in), 0, delta_time, 20d, "military_in");
        superSaveZones[3] = new SuperSaveZone((checkTime_in + delta_time / 2), 1, delta_time, 20d, "military_in");
        superSaveZones[4] = new SuperSaveZone(checkTime_out, 0, delta_time, 30d, "stalkers_out");
        superSaveZones[5] = new SuperSaveZone((checkTime_out + delta_time / 2), 1, delta_time, 30d, "stalkers_out");
        superSaveZones[6] = new SuperSaveZone(checkTime_out, 0, delta_time, 20d, "green_out");
        superSaveZones[7] = new SuperSaveZone((checkTime_out + delta_time / 2), 1, delta_time, 20d, "green_out");
    }
    BroadcastReceiver broadcastReceiverCircle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawCirceAnomaly();
        }
    };

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);  // почему здесь не R.row.map? - потому что это не картинка, а layout
        dbHelper = new DBHelper(getActivity());
        Create_mapCircles();
        Create_super_save_zones();
        return inflate;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        globals.map = this.mMap;
        mMap.setMapType(0);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        AddGroundOverlay(this.mMap);

        drawMarkers();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                writeMarkerToDB(latLng);
            }
        });
        // экран выставляется по центру майдана
        /*
        CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.35342867d, 40.7328d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(13.65f);
        */
        // экран выставляется по центру курятника
        CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.53203d, 40.151296d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(14.8f);
        mMap.moveCamera(newLatLng);
        mMap.animateCamera(zoomTo);


        if (mapCircle!=null) {
            mapCircle.remove();
            mapCircle = null;
        }

    }
    //сюда ставится карта
    public void AddGroundOverlay(GoogleMap googleMap) {
        // карта майдана
        //googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2020v1)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
        // карта около Адмиралтейской 6
        //googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.mapadm6)).positionFromBounds(new LatLngBounds(new LatLng(64.573228d, 40.514540d), new LatLng(64.574154d, 40.518798d))));
        // курятник
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.kury)).positionFromBounds(new LatLngBounds(new LatLng(64.527070d, 40.142495d), new LatLng(64.536994d, 40.160006d))));
    }

     private void drawCirceAnomaly(){

        if (mapCircle==null){
            mapCircle = mMap.addCircle(globals.circleOptions);
        }
        if (globals.anomalyRadius == 0){
            mapCircle.remove();
            mapCircle = null;
        }

        Check_super_save_zone(checkTime_in, 0, (mapCircle_save.length / 2));
        Check_super_save_zone(checkTime_out, (mapCircle_save.length / 2), mapCircle_save.length);

    }

    public void Check_super_save_zone(long time, int first_i, int final_i){
        if (((Calendar.getInstance().getTimeInMillis() / 1000) >= time) && ((Calendar.getInstance().getTimeInMillis() / 1000) <= (time + 3600))) {
            for(int i = first_i; i < final_i; i++){
                if (mapCircle_save[i] != null){
                    mapCircle_save[i].remove();
                    mapCircle_save[i] = null;
                }

                try {
                    mapCircle_save[i] = mMap.addCircle(superSaveZones[i].Draw_save_zone());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // функция рисует маркеры на карте
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
                mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex))).title(cursor.getString(idIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }
    // функция записывает маркеры в базу данных
    private void writeMarkerToDB(LatLng latLng){
        /*
         * try/finally призвана для безопасности и быстроты
         * */
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        database.beginTransaction();
        try {
            contentValues.put(DBHelper.KEY_NAME, "name");
            contentValues.put(DBHelper.KEY_ICON, "icon");
            contentValues.put(DBHelper.KEY_LATITUDE, latLng.latitude); //если написать здесь format для округления о 6 знаков после запятой, то всё ломается
            contentValues.put(DBHelper.KEY_LONGITUDE, latLng.longitude);
            contentValues.put(DBHelper.KEY_COMMENT, "comment");
            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        dbHelper.close();
    }

    // эта штука меняла иконки у маркера, но работала не фонтан
    int iconNumber = 0;
    @Override
    public boolean onMarkerClick(Marker marker) {
        /*switch (iconNumber) {
            case 0:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.radsymbol2));
                break;
            case 1:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.biosymbol2));
                break;
            case 2:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.psysymbol2));
                break;
            case 3:
                marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                break;
        }
        iconNumber++;
        if (iconNumber == 4){
            iconNumber = 0;
        }*/

        return false;
    }

    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiverCircle, new IntentFilter("MapTab.Circle"));
        //anomalyIndex[1] = -1;
        try {
            onMapReady(mMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiverCircle);
        mMap.clear();
    }
}
