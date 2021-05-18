package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

//вызывается, когда координаты изменяются
public class MyLocationCallback extends LocationCallback {
    private Calendar cal = Calendar.getInstance();
    private int Hour = this.cal.get(10);
    private int Minutes = this.cal.get(12);
    private Location MyCurrentLocation;
    private StatsService ServiceReference;
    private int dayInt = this.cal.get(5);

    public MyLocationCallback(Location location, StatsService statsService) {
        this.MyCurrentLocation = location;
        this.ServiceReference = statsService;
    }

    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for (Location location : locationResult.getLocations()) {
            this.MyCurrentLocation.setLatitude(location.getLatitude());
            this.MyCurrentLocation.setLongitude(location.getLongitude());
            this.MyCurrentLocation.setProvider(location.getProvider());
            this.MyCurrentLocation.setBearing(location.getBearing());
            this.MyCurrentLocation.setAccuracy(location.getAccuracy());
            if (!this.ServiceReference.IsDead && this.ServiceReference.IsUnlocked) {
                GetTime();
                TimeToDischarge();
                ServiceReference.Super_save_zone_check();
                ServiceReference.CheckAnomalies();
                ServiceReference.CheckIfInAnyAnomaly();
                ServiceReference.GetTime();

            }
            if (this.ServiceReference.Health <= 0.0d) {
                this.ServiceReference.IsDead = Boolean.TRUE;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.ServiceReference.Health);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Rad);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Bio);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Psy);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLatitude());
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLongitude());
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.ScienceQR);  //qr ученого
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.RadProtection);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.BioProtection);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.PsyProtection);
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.latLngAnomaly.latitude);
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.latLngAnomaly.longitude);
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.radiusAnomaly);
           // stringBuilder.append(":");
            //stringBuilder.append(ServiceReference.anomalyIndex);
            //Log.d("qwerty1", String.valueOf(ServiceReference.anomalyIndex));
            String stringBuilder2 = stringBuilder.toString();
            Intent intent = new Intent("StatsService.Update");
            intent.putExtra("Stats", stringBuilder2);
            this.ServiceReference.sendBroadcast(intent);
            Intent intent1 = new Intent("MapTab.Circle");
            intent1.putExtra("DrawAnomaly", "Draw");
            ServiceReference.sendBroadcast(intent1);
        }
        this.ServiceReference.SaveStats();
    }

    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }
//getTime
    private void GetTime() {
        this.cal = Calendar.getInstance();
        this.dayInt = this.cal.get(5);
        this.Hour = this.cal.get(11);
        this.Minutes = this.cal.get(12);
    }
    // функция, которая задает время выброса
    private void dischargeTime(int day, int hours, int minutes){
        if (this.dayInt == day && this.Minutes == minutes && this.Hour == hours) {
            this.ServiceReference.Discharge();
            this.ServiceReference.IsDischarging = Boolean.TRUE;
        }
    }
//timeToDischarge
    private void TimeToDischarge() {
        if (!this.ServiceReference.IsDischarging) {
            dischargeTime(17, 16, 0);
            dischargeTime(18, 9, 0);
        }
    }

}
