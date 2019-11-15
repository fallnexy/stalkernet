package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

import java.util.Calendar;
import java.util.Date;

public class Anomaly {
    public LatLng Center;
    public String Figure;
    private boolean FirstTimeInside = true;
    public Boolean IsInside = Boolean.valueOf(false);
    private Date LastTimeInside;
    private StatsService Service;
    public String Type;
    private int hours;
    private long mills;
    private int mins;
    public int minstrenght = 1;
    public Polygon poly;
    public Double radius;
    public Double strenght;

    public Anomaly(String str, String str2, Double d, Polygon polygon, StatsService statsService) {
        this.Figure = str;
        this.Type = str2;
        this.strenght = d;
        this.poly = polygon;
        this.Service = statsService;
    }

    public Anomaly(String str, String str2, Double d, Double d2, LatLng latLng, StatsService statsService) {
        this.Figure = str;
        this.Type = str2;
        this.strenght = d;
        this.Center = latLng;
        this.radius = d2;
        this.Service = statsService;
    }

    public void Apply() {
        if (this.Figure == KmlPolygon.GEOMETRY_TYPE) {
            if (Boolean.valueOf(PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)).booleanValue()) {
                this.IsInside = Boolean.valueOf(true);
                if (this.Type == "Rad") {
                    this.Service.Rad = this.strenght.doubleValue();
                    this.Service.Health -= this.Service.Rad;
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type == "Bio") {
                    this.Service.Bio = this.strenght.doubleValue();
                    this.Service.Health -= this.Service.Bio;
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type == "Psy") {
                    this.Service.Psy = this.strenght.doubleValue();
                    this.Service.Health -= this.Service.Psy / 2.0d;
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
            } else {
                this.IsInside = Boolean.valueOf(false);
            }
        }
        if (this.Figure == "Circle") {
            Location location = new Location("");
            location.setLatitude(this.Center.latitude);
            location.setLongitude(this.Center.longitude);
            Float valueOf = Float.valueOf(location.distanceTo(this.Service.MyCurrentLocation));
            if (((double) valueOf.floatValue()) <= this.radius.doubleValue()) {
                int round;
                if (this.Type == "Rad") {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.valueOf(true);
                    this.Service.TypeAnomalyIn = "Rad";
                    round = (int) Math.round((this.strenght.doubleValue() / 100.0d) * Double.valueOf(100.0d - (((double) valueOf.floatValue()) / (this.radius.doubleValue() / 100.0d))).doubleValue());
                    if (round <= this.minstrenght) {
                        round = this.minstrenght;
                    }
                    round -= (round / 100) * this.Service.RadProtection;
                    this.Service.Rad = (double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.RadProtection))));
                    this.Service.setHealth(this.Service.Health - this.Service.Rad);
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                  //  this.Service.EM.StopActions();
                 /*   if (!this.Service.IsDead.booleanValue()) {
                        this.Service.EM.PlaySound(this.Type, this.strenght.doubleValue());
                        if (this.Service.Vibrate.booleanValue()) {
                            this.Service.EM.VibrateInPattern();
                        }
                    }*/
                }
                if (this.Type == "Bio") {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.valueOf(true);
                    Intent intent;
                    if (this.FirstTimeInside) {
                        this.FirstTimeInside = false;
                        this.LastTimeInside = Calendar.getInstance().getTime();
                        this.Service.TypeAnomalyIn = "Bio";
                        round = (int) Math.round((this.strenght.doubleValue() / 100.0d) * Double.valueOf(100.0d - (((double) valueOf.floatValue()) / (this.radius.doubleValue() / 100.0d))).doubleValue());
                        if (round <= this.minstrenght) {
                            round = this.minstrenght;
                        }
                        round -= (round / 100) * this.Service.BioProtection;
                        this.Service.Bio = (double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.BioProtection))));
                        this.Service.setHealth(this.Service.Health - this.Service.Bio);
                        this.Service.CurrentBio += this.strenght.doubleValue();
                        if (this.Service.CurrentBio >= 100.0d) {
                            this.Service.setHealth(0.0d);
                            intent = new Intent("StatsService.Message");
                            intent.putExtra("Message", "P");
                            this.Service.sendBroadcast(intent);
                        }
                        this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                     /*   this.Service.EM.StopActions();
                        if (!this.Service.IsDead.booleanValue()) {
                            this.Service.EM.PlaySound(this.Type, this.strenght.doubleValue());
                            if (this.Service.Vibrate.booleanValue()) {
                                this.Service.EM.VibrateInPattern();
                            }
                        }*/
                    } else {
                        this.Service.LastTimeHitBy = this.Type;
                        this.Service.TypeAnomalyIn = "Bio";
                        round = (int) Math.round((this.strenght.doubleValue() / 100.0d) * Double.valueOf(100.0d - (((double) valueOf.floatValue()) / (this.radius.doubleValue() / 100.0d))).doubleValue());
                        if (round <= this.minstrenght) {
                            round = this.minstrenght;
                        }
                        round -= (round / 100) * this.Service.BioProtection;
                        this.Service.Bio = (double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.BioProtection))));
                        this.Service.setHealth(this.Service.Health - this.Service.Bio);
                        this.mills = Calendar.getInstance().getTime().getTime() - this.LastTimeInside.getTime();
                        this.hours = ((int) this.mills) / 3600000;
                        this.mins = ((int) (this.mills / 60000)) % 60;
                        if (this.mins >= 1 || this.hours > 0) {
                            this.Service.CurrentBio += this.strenght.doubleValue();
                            if (this.Service.CurrentBio >= 100.0d) {
                                this.Service.setDead(Boolean.valueOf(true));
                                this.Service.setHealth(0.0d);
                                intent = new Intent("StatsService.Message");
                                intent.putExtra("Message", "H");
                                this.Service.sendBroadcast(intent);
                            }
                        }
                        this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                      /*  this.Service.EM.StopActions();
                        if (!this.Service.IsDead.booleanValue()) {
                            this.Service.EM.PlaySound(this.Type, this.strenght.doubleValue());
                            if (this.Service.Vibrate.booleanValue()) {
                                this.Service.EM.VibrateInPattern();
                            }
                        }*/
                        this.LastTimeInside = Calendar.getInstance().getTime();
                    }
                }
                if (this.Type == "Psy") {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.valueOf(true);
                    this.Service.TypeAnomalyIn = "Psy";
                    this.Service.IsInsideAnomaly = Boolean.valueOf(true);
                    int round2 = (int) Math.round((this.strenght.doubleValue() / 100.0d) * Double.valueOf(100.0d - (((double) valueOf.floatValue()) / (this.radius.doubleValue() / 100.0d))).doubleValue());
                    if (round2 <= this.minstrenght) {
                        round2 = this.minstrenght;
                    }
                    round2 -= (int) ((((double) round2) / 100.0d) * ((double) this.Service.PsyProtection));
                    this.Service.Psy = (double) (round2 - ((round2 / 100) * this.Service.PsyProtection));
                    this.Service.setHealth(this.Service.Health - (this.Service.Psy / 10.0d));
                    if (this.Service.Psy >= 100.0d) {
                        this.Service.setDead(Boolean.valueOf(true));
                        this.Service.setHealth(0.0d);
                        Intent intent2 = new Intent("StatsService.Message");
                        intent2.putExtra("Message", "P");
                        this.Service.sendBroadcast(intent2);
                    }
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                   /* this.Service.EM.StopActions();
                    if (!this.Service.IsDead.booleanValue()) {
                        this.Service.EM.PlaySound(this.Type, this.strenght.doubleValue());
                        if (this.Service.Vibrate.booleanValue()) {
                            this.Service.EM.VibrateInPattern();
                            return;
                        }
                        return;
                    }*/
                    return;
                }
                return;
            }
            this.IsInside = Boolean.valueOf(false);
        }
    }
}
