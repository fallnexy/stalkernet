package com.example.myapplication2;

import android.content.ContentValues;
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
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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


    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);  // почему здесь не R.row.map? - потому что это не картинка, а layout
        dbHelper = new DBHelper(getActivity());
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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));

                // новое запоминает точек, через базу данных
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
        });
        CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.35342867d, 40.7328d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(13.65f);
        mMap.moveCamera(newLatLng);
        mMap.animateCamera(zoomTo);
    }
    //сюда ставится карта
    public void AddGroundOverlay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
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
        try {
            onMapReady(mMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.clear();
    }
}
