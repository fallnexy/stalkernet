package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

public class MonolithAnomaly extends Anomaly {
    public LatLng Center;
    public Boolean IsInside = Boolean.valueOf(false);
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

    public MonolithAnomaly(String str, String str2, Double d, Double d2, LatLng latLng, StatsService statsService) {
        super(str, str2, d, d2, latLng,statsService);
        this.Figure = str;
        this.Center = latLng;
        this.radius = d;
        this.Service = statsService;
    }

    public void Apply() {
        Intent intent;
        if (this.Figure == KmlPolygon.GEOMETRY_TYPE) {
            if (Boolean.valueOf(PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)).booleanValue()) {
                intent = new Intent("Command");
                intent.putExtra("Command", "Monolith2");
                this.Service.getApplicationContext().sendBroadcast(intent);
            } else {
                this.IsInside = Boolean.valueOf(false);
            }
        }
        if (this.Figure == "Circle") {
            Location location = new Location("");
            location.setLatitude(this.Center.latitude);
            location.setLongitude(this.Center.longitude);
            if (((double) Float.valueOf(location.distanceTo(this.Service.MyCurrentLocation)).floatValue()) <= this.radius.doubleValue()) {
                this.IsInside = Boolean.valueOf(true);
                intent = new Intent("Command");
                intent.putExtra("Command", "Monolith2");
                this.Service.getApplicationContext().sendBroadcast(intent);
                return;
            }
            this.IsInside = Boolean.valueOf(false);
        }
    }
}
