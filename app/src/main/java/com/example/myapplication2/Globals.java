package com.example.myapplication2;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import androidx.annotation.NonNull;

public class Globals {
    Context mContext;
    public ArrayAdapter<MarkerOptions> Adapter;
    public String Health, Rad, Bio, Psy, TotalProtectionRad, TotalProtectionBio, TotalProtectionPsy,
            CapacityProtectionRad, CapacityProtectionBio, CapacityProtectionPsy,
            MaxCapacityProtectionRad, MaxCapacityProtectionBio, MaxCapacityProtectionPsy,
            ProtectionRadArr, ProtectionBioArr, ProtectionPsyArr,
            RadProtectionTot, BioProtectionTot, PsyProtectionTot;
    public ProgressBar HealthBar, RadBar, BioBar, PsyBar;
    public String MaxHealth = "2000", MaxRad = "1000", MaxBio = "1000", MaxPsy = "1000";
    public TextView CO;
    public TextView Messages, MaxProtectionAvailable;
    public TextView HealthPercent, RadPercent, BioPercent, PsyPercent,
            RadProtectionPercent, BioProtectionPercent, PsyProtectionPercent,
            RadCapacityPercent, BioCapacityPercent, PsyCapacityPercent;
    public Location location = new Location("GPS");
    public GoogleMap map;
    /*public LatLng anomalyCenter = new LatLng(0, 0);
    public Double anomalyRadius =0d;*/
  //  public int anomalyIndex;
   /* public CircleOptions circleOptions;*/

    public int ScienceQR;          // не работает или работает? - вроде как обновляет вместе с обновлением координат

    private LocationManager locationManager;

    public Globals(Context mContext) {
        this.mContext = mContext;
    }

    // для жизней
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
    // для всего остального
    private void updateBar(@NonNull ProgressBar barName, String stringMax, String stringName, TextView perCent, String capacity,
                           String maxCapacity, TextView perCentCapacity, String protection, TextView perCentProtection, String totalProtection, String legendProtection){
        int parseDouble;
        double parseMaxCapacitySuit, parseMaxCapacityArt, parseMaxCapacityQuest;
        double parseCapacitySuit, parseCapacityArt, parseCapacityQuest;
        double parseProtectionSuit, parseProtectionArt, parseProtectionQuest;
        double parseTotalProtection;
        int parseLegendProtection;
        barName.setMax(Integer.parseInt(stringMax));
        try {
            parseDouble = (int) Double.parseDouble(stringName);
            parseCapacitySuit = Double.parseDouble(capacity.split(", ")[0]);
            parseCapacityArt = Double.parseDouble(capacity.split(", ")[1]);
            parseCapacityQuest = Double.parseDouble(capacity.split(", ")[2]);
            parseMaxCapacitySuit = Double.parseDouble(maxCapacity.split(", ")[0]);
            parseMaxCapacityArt = Double.parseDouble(maxCapacity.split(", ")[1]);
            parseMaxCapacityQuest = Double.parseDouble(maxCapacity.split(", ")[2]);
            parseProtectionSuit = Double.parseDouble(protection.split(", ")[0]);
            parseProtectionArt = Double.parseDouble(protection.split(", ")[1]);
            parseProtectionQuest = Double.parseDouble(protection.split(", ")[2]);
            parseTotalProtection = Double.parseDouble(totalProtection);
            parseLegendProtection = Integer.parseInt(legendProtection);
        } catch (Exception unused) {
            parseDouble = 0;
            parseCapacitySuit = 0;
            parseCapacityArt = 0;
            parseCapacityQuest = 0;
            parseMaxCapacitySuit = 0;
            parseMaxCapacityArt = 0;
            parseMaxCapacityQuest = 0;
            parseProtectionSuit = 0;
            parseProtectionArt = 0;
            parseProtectionQuest = 0;
            parseTotalProtection = 0;
            parseLegendProtection = 0;
        }
        Log.d("capacity", capacity + " " + maxCapacity);
        barName.setProgress(parseDouble);
        String percent = 100 * parseDouble / Double.parseDouble(stringMax) +"%";
        perCent.setText(percent);

        String percentCapacity = "Прочность: ";
        if (parseMaxCapacitySuit > 0) {
            percentCapacity += "Бр " + String.format(Locale.US,"%.1f", 100 - 100 * parseCapacitySuit / parseMaxCapacitySuit) + "%; ";
        } else {
            percentCapacity += "Бр 0%; ";
        }
        if (parseMaxCapacityArt > 0) {
            percentCapacity += "Арт " + String.format(Locale.US,"%.1f", 100 - 100 * parseCapacityArt / parseMaxCapacityArt) + "%; ";
        } else {
            percentCapacity += "Арт 0%; ";
        }
        if (parseMaxCapacityQuest > 0) {
            percentCapacity += "Кв " + String.format(Locale.US,"%.1f", 100 - 100 * parseCapacityQuest / parseMaxCapacityQuest) + "% ";
        } else {
            percentCapacity += "Кв 0% ";
        }
        perCentCapacity.setText(percentCapacity);

        double newTotalProtection;
        if (parseTotalProtection + parseLegendProtection >= 100){
            newTotalProtection = 100;
        } else {
            newTotalProtection = parseTotalProtection + parseLegendProtection;
        }
        String percentProtection = String.format(Locale.US,"Защита: Бр %.2f; Арт %.2f; Кв %.2f; ∑ %.4f", parseProtectionSuit, parseProtectionArt, parseProtectionQuest, newTotalProtection);
        perCentProtection.setText(percentProtection);

    }
    //эта штука вызывается в MainActivity и обновяет статы, которые есть в GeneralTab
    public void UpdateStats() {

        updateBar(HealthBar, MaxHealth, Health, HealthPercent);
        updateBar(RadBar, MaxRad, Rad, RadPercent, CapacityProtectionRad, MaxCapacityProtectionRad, RadCapacityPercent, ProtectionRadArr, RadProtectionPercent, TotalProtectionRad, RadProtectionTot);
        updateBar(BioBar, MaxBio, Bio, BioPercent, CapacityProtectionBio, MaxCapacityProtectionBio, BioCapacityPercent, ProtectionBioArr, BioProtectionPercent, TotalProtectionBio, BioProtectionTot);
        updateBar(PsyBar, MaxPsy, Psy, PsyPercent, CapacityProtectionPsy, MaxCapacityProtectionPsy, PsyCapacityPercent, ProtectionPsyArr, PsyProtectionPercent, TotalProtectionPsy, PsyProtectionTot);

        /*try {
            circleOptions = new CircleOptions().center(anomalyCenter).radius(anomalyRadius).strokeColor(Color.BLUE).strokeWidth(3).zIndex(Float.MAX_VALUE);
        } catch (Exception e) {
            circleOptions = new CircleOptions().center(new LatLng(0, 0)).radius(0).strokeColor(Color.GREEN).strokeWidth(3).zIndex(Float.MAX_VALUE);
        }
        Log.d("координаты", String.valueOf(circleOptions.getCenter().latitude));*/
        // GPS
        TextView textView = CO;
        String stringBuilder = location.getLatitude() +
                                " - " +
                                location.getLongitude();
        textView.setText(stringBuilder);
    }
}
