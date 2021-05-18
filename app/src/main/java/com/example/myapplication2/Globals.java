package com.example.myapplication2;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Globals {
    Context mContext;
    public ArrayAdapter<MarkerOptions> Adapter;
    public String Health, Rad, Bio, Psy, ProtectionRad, ProtectionBio, ProtectionPsy;
    public ProgressBar HealthBar, RadBar, BioBar, PsyBar;
    public String MaxHealth = "2000", MaxRad = "1000", MaxBio = "1000", MaxPsy = "1000";
    public TextView CO;
    public TextView Messages;
    public TextView HealthPercent, RadPercent, BioPercent, PsyPercent;
    public Location location = new Location("GPS");
    public GoogleMap map;
    public LatLng anomalyCenter = new LatLng(0, 0);
    public Double anomalyRadius =0d;
  //  public int anomalyIndex;
    public CircleOptions circleOptions;

    public int ScienceQR;          // не работает

    private LocationManager locationManager;

    public Globals(Context mContext) {
        this.mContext = mContext;
    }

    private void updateBar(@NonNull ProgressBar barName, String stringMax, String stringName, TextView perCent){
        int parseDouble;
        barName.setMax(Integer.parseInt(stringMax));
        try {
            parseDouble = (int) Double.parseDouble(stringName);
        } catch (Exception unused) {
            parseDouble = 0;
        }
        barName.setProgress(parseDouble);
        String percent = 100 * parseDouble / Double.parseDouble(stringMax) +"%";
        perCent.setText(percent);
    }
    //эта штука вызывается в MainActivity и обновяет статы, которые есть в GeneralTab
    public void UpdateStats() {

        updateBar(HealthBar, MaxHealth, Health, HealthPercent);
        updateBar(RadBar, MaxRad, Rad, RadPercent);
        updateBar(BioBar, MaxBio, Bio, BioPercent);
        updateBar(PsyBar, MaxPsy, Psy, PsyPercent);

        try {
            circleOptions = new CircleOptions().center(anomalyCenter).radius(anomalyRadius).strokeColor(Color.BLUE).strokeWidth(3).zIndex(Float.MAX_VALUE);
        } catch (Exception e) {
            circleOptions = new CircleOptions().center(new LatLng(0, 0)).radius(0).strokeColor(Color.GREEN).strokeWidth(3).zIndex(Float.MAX_VALUE);
        }

        // GPS
        TextView textView = CO;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(location.getLatitude()));
        stringBuilder.append(" - ");
        stringBuilder.append(String.valueOf(location.getLongitude()));
        textView.setText(stringBuilder.toString());
    }
}
