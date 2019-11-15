package com.example.myapplication2;

import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

public class SafeZone {
    public LatLng Center;
    public String Figure;
    public Boolean IsInside = Boolean.valueOf(false);
    private StatsService Service;
    public Polygon poly;
    public Double radius;

    public SafeZone(String str, Polygon polygon, StatsService statsService) {
        this.Figure = str;
        this.poly = polygon;
        this.Service = statsService;
    }

    public SafeZone(String str, Double d, LatLng latLng, StatsService statsService) {
        this.Figure = str;
        this.Center = latLng;
        this.radius = d;
        this.Service = statsService;
    }

    public void Apply() {
        if (this.Figure == KmlPolygon.GEOMETRY_TYPE) {
            if (Boolean.valueOf(PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)).booleanValue()) {
                this.Service.IsInsideSafeZone = Boolean.valueOf(true);
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
                Toast.makeText(this.Service.getApplicationContext(), Boolean.toString(this.IsInside.booleanValue()), Toast.LENGTH_SHORT).show();
                this.Service.IsInsideSafeZone = Boolean.valueOf(true);
                return;
            }
            this.IsInside = Boolean.valueOf(false);
        }
    }
}
