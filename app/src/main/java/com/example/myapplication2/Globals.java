package com.example.myapplication2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;

public class Globals {
    Context mContext;
    public ArrayAdapter<MarkerOptions> Adapter;
    public String Health, Rad, Bio, Psy;
    public ProgressBar HealthBar, RadBar, BioBar, PsyBar;
    public String MaxHealth = "200", MaxRad = "100", MaxBio = "100", MaxPsi = "100";
    public TextView CO;
    public TextView Messages;
    public TextView HealthPercent;
    public String CurrentBio;
    public ArrayList<MarkerOptions> MarkerArray = new ArrayList();
    public ArrayList<String> StringMarkerArray = new ArrayList();
    public Location location = new Location("GPS");
    public GoogleMap map;
    public int ScienceQR;          // не работает

    private LocationManager locationManager;

    public Globals(Context mContext) {
        this.mContext = mContext;
    }
    //именно эта штука обновяет статы, которые есть в GeneralTab
    public void UpdateStats() {

        int parseDouble;
        HealthBar.setMax(Integer.parseInt(MaxHealth));
        int i = 0;
        try {
            parseDouble = (int) Double.parseDouble(Health);
        } catch (Exception unused) {
            parseDouble = 0;
        }
        HealthBar.setProgress(parseDouble);
        String healthPercent = 100 * parseDouble / Double.parseDouble(MaxHealth) +"%";
        HealthPercent.setText(healthPercent);

        this.RadBar.setMax(Integer.parseInt(MaxRad));                                           //
        try {
            parseDouble = (int) Double.parseDouble(Rad);
        } catch (Exception unused2) {
            parseDouble = 0;
        }
        RadBar.setProgress(parseDouble);

        RadBar.setMax(Integer.parseInt(MaxBio));
        try {
            parseDouble = (int) Double.parseDouble(Bio);
        } catch (Exception unused3) {
            parseDouble = 0;
        }
        BioBar.setProgress(parseDouble);
        BioBar.setSecondaryProgress((int) Double.parseDouble(CurrentBio));  //нужно задать Currentbio, чтоб оно работало - опоп, работает?

        RadBar.setMax(Integer.parseInt(MaxPsi));
        try {
            i = (int) Double.parseDouble(Psy);
        } catch (Exception unused4) {
        }
        PsyBar.setProgress(i);

       // GPS изначальное, которое не работает - уже работает
        TextView textView = CO;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(location.getLatitude()));
        stringBuilder.append(" - ");
        stringBuilder.append(String.valueOf(location.getLongitude()));
        textView.setText(stringBuilder.toString());
    }

    //карта
    public void AddGroundOverlay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
    }
    // маркеры на карте
    public void redrawMarkers() {
        for (int i = 0; i < MarkerArray.size(); i++) {
            map.addMarker((MarkerOptions) MarkerArray.get(i));
        }
    }

}
