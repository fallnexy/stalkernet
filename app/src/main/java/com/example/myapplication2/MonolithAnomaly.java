package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

public class MonolithAnomaly extends Anomaly {
    public LatLng center;
    public Boolean IsInside = Boolean.FALSE;
    private StatsService Service;
    public Polygon poly;
    public Double radius;
    // решение: добавить super
    public MonolithAnomaly(String str, String str2, Double d, Polygon polygon, StatsService statsService) {
        super(str, str2, d, polygon, statsService);
        this.Figure = str;
        this.poly = polygon;
        this.Service = statsService;
    }

    public MonolithAnomaly(String str, String str2, Double d, Double d2, LatLng latLng, StatsService statsService, Integer gesStatus, Boolean boolShow) {
        super(str, str2, d, d2, latLng, statsService, gesStatus, boolShow);
        this.Figure = str;
        this.center = latLng;
        this.radius = d2;
        this.Service = statsService;
    }

    public void Apply() {
        Intent intent;
        if (this.Figure.equals(KmlPolygon.GEOMETRY_TYPE)) {
            if (PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)) {
                intent = new Intent("Command");
                intent.putExtra("Command", "Monolith2");
                this.Service.getApplicationContext().sendBroadcast(intent);
            } else {
                this.IsInside = Boolean.FALSE;
            }
        }
        if (this.Figure.equals("Circle")) {
            Location location = new Location("");
            location.setLatitude(this.center.latitude);
            location.setLongitude(this.center.longitude);
            if (((double) location.distanceTo(this.Service.MyCurrentLocation)) <= this.radius) {
                this.IsInside = Boolean.TRUE;
                intent = new Intent("Command");
                intent.putExtra("Command", "Monolith2");
                this.Service.getApplicationContext().sendBroadcast(intent);
                return;
            }
            this.IsInside = Boolean.FALSE;
        }
    }
}
