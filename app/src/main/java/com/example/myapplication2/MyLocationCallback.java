package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Calendar;

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
                this.ServiceReference.CheckAnomalies();
                this.ServiceReference.CheckIfInAnyAnomaly();
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
            stringBuilder.append(this.ServiceReference.CurrentBio);
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLatitude());
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLongitude());
            stringBuilder.append(":");                               //qr ученого
            stringBuilder.append(this.ServiceReference.ScienceQR);
            String stringBuilder2 = stringBuilder.toString();
            Intent intent = new Intent("StatsService.Update");
            intent.putExtra("Stats", stringBuilder2);
            this.ServiceReference.sendBroadcast(intent);
        }
        this.ServiceReference.SaveStats();
    }

    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }
//getTime
    public void GetTime() {
        this.cal = Calendar.getInstance();
        this.dayInt = this.cal.get(5);
        this.Hour = this.cal.get(11);
        this.Minutes = this.cal.get(12);
    }
//timeToDischarge
    private void TimeToDischarge() {
        if (!this.ServiceReference.IsDischarging) {
            if (this.dayInt == 2 && this.Minutes == 30 && this.Hour == 21) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 29 && this.Minutes == 10 && this.Hour == 1) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 16 && this.Minutes == 30 && this.Hour == 5) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 12 && this.Minutes == 0 && this.Hour == 18) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 12 && this.Minutes == 20 && this.Hour == 18) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 13 && this.Minutes == 0 && this.Hour == 10) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 13 && this.Minutes == 20 && this.Hour == 10) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 13 && this.Minutes == 0 && this.Hour == 12) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
            if (this.dayInt == 13 && this.Minutes == 20 && this.Hour == 12) {
                this.ServiceReference.Discharge();
                this.ServiceReference.IsDischarging = Boolean.TRUE;
            }
        }
    }

}
