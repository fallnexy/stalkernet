package com.example.myapplication2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
    public String Bio;
    public ProgressBar BioBar;
    public TextView CO;
    public String CurrentBio;
    public String Health;
    public ProgressBar HealthBar;
    public ArrayList<MarkerOptions> MarkerArray = new ArrayList();
    public String MaxHealth = "200";
    public TextView Messages;
    public String Psy;
    public ProgressBar PsyBar;
    public String Rad;
    public ProgressBar RadBar;
    public ArrayList<String> StringMarkerArray = new ArrayList();
    public Location location = new Location("GPS");
    public GoogleMap map;

    private LocationManager locationManager;

    public Globals(Context mContext) {
        this.mContext = mContext;
    }

    public void UpdateStats() {
        int parseDouble;
        this.HealthBar.setMax(Integer.parseInt(this.MaxHealth));
        int i = 0;
        try {
            parseDouble = (int) Double.parseDouble(this.Health);
        } catch (Exception unused) {
            parseDouble = 0;
        }
        this.HealthBar.setProgress(parseDouble);
        try {
            parseDouble = (int) Double.parseDouble(this.Rad);
        } catch (Exception unused2) {
            parseDouble = 0;
        }
        this.RadBar.setProgress(parseDouble);
        try {
            parseDouble = (int) Double.parseDouble(this.Bio);
        } catch (Exception unused3) {
            parseDouble = 0;
        }
        this.BioBar.setProgress(parseDouble);
        this.BioBar.setSecondaryProgress((int) Double.parseDouble(this.CurrentBio));  //нужно задать Currentbio, чтоб оно работало - опоп, работает?
        try {
            i = (int) Double.parseDouble(this.Psy);
        } catch (Exception unused4) {
        }
        this.PsyBar.setProgress(i);

       // GPS изначальное, которое не работает - уже работает
        TextView textView = this.CO;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(this.location.getLatitude()));
        stringBuilder.append(" - ");
        stringBuilder.append(String.valueOf(this.location.getLongitude()));
        textView.setText(stringBuilder.toString());
    }


    public void AddGroundOverlay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
    }

    public void redrawMarkers() {
        for (int i = 0; i < this.MarkerArray.size(); i++) {
            this.map.addMarker((MarkerOptions) this.MarkerArray.get(i));
        }
    }
}
