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
    public Boolean IsInside = Boolean.FALSE;
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
        if (this.Figure.equals(KmlPolygon.GEOMETRY_TYPE)) {
            if (PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)) {
                this.Service.IsInsideSafeZone = Boolean.TRUE;
            } else {
                this.IsInside = Boolean.FALSE;
            }
        }
        if (this.Figure.equals("Circle")) {
            Location location = new Location("");
            location.setLatitude(this.Center.latitude);
            location.setLongitude(this.Center.longitude);
            if (((double) location.distanceTo(this.Service.MyCurrentLocation)) <= this.radius) {
                this.IsInside = Boolean.TRUE;
                Toast.makeText(this.Service.getApplicationContext(), Boolean.toString(this.IsInside), Toast.LENGTH_SHORT).show();
                this.Service.IsInsideSafeZone = Boolean.TRUE;
                return;
            }
            this.IsInside = Boolean.FALSE;
        }
    }
}
