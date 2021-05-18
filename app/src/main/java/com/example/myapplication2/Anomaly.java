package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlPolygon;

import java.util.Calendar;
import java.util.Date;

public class Anomaly {
    public LatLng center;
    public String Figure;
 /*   private boolean FirstTimeInside = true;*/
    public Boolean IsInside = Boolean.FALSE;
    private Date LastTimeInside;
    private StatsService Service;
    public String Type;
   /* private int hours;
    private long mills;
    private int mins;*/
    public double minstrenght = 0.001;
    public Polygon poly;
    public Double radius;
    public Double strenght;
    public Integer gesStatus;
    public Boolean toShow;

    public Anomaly(String str, String str2, Double d, Polygon polygon, StatsService statsService) {
        this.Figure = str;
        this.Type = str2;
        this.strenght = d;
        this.poly = polygon;
        this.Service = statsService;
    }

    public Anomaly(String str, String str2, Double d, Double d2, LatLng latLng, StatsService statsService, Integer gestaltStatus, Boolean boolShow) {
        Figure = str;
        Type = str2;
        strenght = d;
        center = latLng;
        radius = d2;
        Service = statsService;
        gesStatus = gestaltStatus;
        toShow = boolShow;
    }
//для сталкерской рулетки
    public Anomaly(String str, String str2, Double d, Double d2, StatsService statsService){
        Figure = str;
        Type = str2;
        strenght = d;
        radius = d2;
        Service = statsService;
    }

// метод внутри apply()
    public void AnomalyResult(double distanceToAnomaly){
        double damage;
        Service.LastTimeHitBy = Type;
        IsInside = Boolean.TRUE;
        Service.TypeAnomalyIn = Type;
        damage = strenght * (1 - Math.pow(distanceToAnomaly / radius, 2));
        if (damage <= minstrenght) {
            damage = minstrenght;
        }
        switch (Type){
            case "RadF": // уникальная аномалия свободы
                Service.Rad += damage * (0.3 - Service.RadProtection / 100d);
                if (Service.Rad >= 1000.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "H");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Rad":
                Service.Rad += damage * (1 - Service.RadProtection / 100d);
                Service.setHealth(Service.Health - damage * (1 - Service.RadProtection / 100d));
                if (Service.Rad >= 1000.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "H");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Bio": ;
                Service.Bio += damage * (1 - Service.BioProtection / 100d);
                Service.setHealth(Service.Health - damage * (1 - Service.BioProtection / 100d));
                if (Service.Bio >= 1000.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "P");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Psy":
                Service.Psy += damage * (1 - Service.PsyProtection / 100d);
                Service.setHealth(Service.Health - damage * (1 - Service.PsyProtection / 100d));
                if (Service.Psy >= 1000.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "P");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Ges":

                return;
        }
    }

    //гештальт, надо добавить защиту и сообщение исправить
// Service.GestaltOpen - если гештальт открыт, то его надо закрыть.  1 - закрыто, 2 - открыто
    public void Gestalt(double distanceToAnomaly){
            if (distanceToAnomaly > radius && gesStatus == 2){
                double damage;
                Service.LastTimeHitBy = Type;
                Service.TypeAnomalyIn = Type;

                damage = strenght; /* (distanceToAnomaly / radius))*/ //урон не зависит от расстояния, потому что сталкер может из далека зацепить гештальт
                if (damage <= minstrenght) {
                    damage = minstrenght;
                }
                if (Service.Health > (Service.MaxHealth / 3)) {
                    Service.setHealth(Service.Health - damage);
                }
                if (Service.Health <= 0.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "H");
                    Service.sendBroadcast(intent2);
                }
                this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                this.Service.EM.StopActions();
            }

    }

// этот метод вызыается в StatService
    public void Apply() {
        if (this.Figure.equals(KmlPolygon.GEOMETRY_TYPE)) {
            if (PolyUtil.containsLocation(new LatLng(this.Service.MyCurrentLocation.getLatitude(), this.Service.MyCurrentLocation.getLongitude()), this.poly.getPoints(), false)) {
                this.IsInside = Boolean.TRUE;
                if (this.Type.equals("Rad")) {
                    this.Service.Rad = this.strenght;
                    this.Service.Health = this.Service.Health - this.Service.Rad * (1 - this.Service.RadProtection / 100d);
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type.equals("Bio")) {
                    this.Service.Bio = this.strenght;
                    this.Service.Health -= this.Service.Bio * (1 - this.Service.BioProtection / 100d);
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type.equals("Psy")) {
                    this.Service.Psy = this.strenght;
                    this.Service.Health -= this.Service.Psy  * (1 - this.Service.PsyProtection / 100d);
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
            } else {
                this.IsInside = Boolean.FALSE;
            }
        }
        if (Figure.equals("QR")){
            double valueOf = 0;
            AnomalyResult(valueOf);
            this.Service.LastTimeChanged = Calendar.getInstance().getTime();
            this.Service.EM.StopActions();
            if (!this.Service.IsDead) {
                this.Service.EM.PlaySound(this.Type, this.strenght);
                if (this.Service.Vibrate) {
                    this.Service.EM.VibrateInPattern();
                }
            }
        }
        if (Figure.equals("Circle")) {
            Location location = new Location("");
            location.setLatitude(center.latitude);
            location.setLongitude(center.longitude);
            double distanceToAnomaly = location.distanceTo(Service.MyCurrentLocation);

            if (Type.equals("Ges")){
                Gestalt(distanceToAnomaly);
            }

            if (distanceToAnomaly <= radius) {
                if (Type.equals("Ges")){
                    Intent intent2 = new Intent("StatsService.Message");  // отправляет сообщение на главный экран "Не выходи из виброзоны, пока не закроешь гештальт."
                    intent2.putExtra("Message", "G");
                    Service.sendBroadcast(intent2);
                }
                AnomalyResult(distanceToAnomaly);
                /*int round;
                if (this.Type.equals("Rad")) {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.TRUE;
                    this.Service.TypeAnomalyIn = "Rad";
                    round = (int) Math.round(this.strenght * (1 - (distanceToAnomaly / this.radius)));
                    if (round <= this.minstrenght) {
                        round = this.minstrenght;
                    }
                    round -= (round / 100) * this.Service.RadProtection;
                    this.Service.Rad += round*//*(double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.RadProtection))))*/;
                   /* this.Service.setHealth(this.Service.Health - *//*this.Service.Rad*/ /*round);
                    if (this.Service.Rad >= 100.0d) {
                        this.Service.setDead(Boolean.TRUE);
                        this.Service.setHealth(0.0d);
                        Intent intent2 = new Intent("StatsService.Message");
                        intent2.putExtra("Message", "H");
                        this.Service.sendBroadcast(intent2);
                    }*/
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                    this.Service.EM.StopActions();
                    if (!this.Service.IsDead) {
                        this.Service.EM.PlaySound(this.Type, this.strenght);
                        if (this.Service.Vibrate) {
                            this.Service.EM.VibrateInPattern();
                        }
                    }
              /*  }*/
               /* if (this.Type.equals("Bio")) {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.TRUE;
                    Intent intent;*/
                   /* if (this.FirstTimeInside) {
                        this.FirstTimeInside = false;*/
                      /*  this.LastTimeInside = Calendar.getInstance().getTime();
                        this.Service.TypeAnomalyIn = "Bio";
                        int  round = (int) Math.round(this.strenght * (1 - (distanceToAnomaly / this.radius)));
                        if (round <= this.minstrenght) {
                            round = this.minstrenght;
                        }
                        round -= (round / 100) * this.Service.BioProtection;
                        this.Service.Bio += round *//*(double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.BioProtection))))*/;
                      /*  this.Service.setHealth(this.Service.Health - round*//*this.Service.Bio*//*);*/
                      /*  if (this.Service.*//*Current*//*Bio >= 100.0d) {
                            this.Service.setHealth(0.0d);
                            intent = new Intent("StatsService.Message");
                            intent.putExtra("Message", "P");
                            this.Service.sendBroadcast(intent);
                        }
                        this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                        this.Service.EM.StopActions();
                        if (!this.Service.IsDead) {
                            this.Service.EM.PlaySound(this.Type, this.strenght);
                            if (this.Service.Vibrate) {
                                this.Service.EM.VibrateInPattern();
                            }
                        }*/
                        // непонятная фигня, которая предполагает, что если умер в один момент, то жди контролера и зомби
                  /*  }*/ /*else {
                        this.Service.LastTimeHitBy = this.Type;
                        this.Service.TypeAnomalyIn = "Bio";
                        round = (int) Math.round(this.strenght * (1 - (((double) (float) distanceToAnomaly) / this.radius)));
                        if (round <= this.minstrenght) {
                            round = this.minstrenght;
                        }
                        round -= (round / 100) * this.Service.BioProtection;
                        this.Service.Bio = (double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.BioProtection))));
                        this.Service.setHealth(this.Service.Health - this.Service.Bio);
                        this.mills = Calendar.getInstance().getTime().getTime() - this.LastTimeInside.getTime();
                        this.hours = ((int) this.mills) / 3600000;
                        this.mins = ((int) (this.mills / 60000)) % 60;

                        this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                        this.Service.EM.StopActions();
                        if (!this.Service.IsDead) {
                            this.Service.EM.PlaySound(this.Type, this.strenght);
                            if (this.Service.Vibrate) {
                                this.Service.EM.VibrateInPattern();
                            }
                        }
                        this.LastTimeInside = Calendar.getInstance().getTime();
                    }*/
              /*  }
                if (this.Type.equals("Psy")) {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.TRUE;
                    this.Service.TypeAnomalyIn = "Psy";
                    this.Service.IsInsideAnomaly = Boolean.TRUE;
                    int round2 = (int) Math.round(this.strenght * (1 - (distanceToAnomaly / this.radius)));
                    if (round2 <= this.minstrenght) {
                        round2 = this.minstrenght;
                    }
                    round2 -= (int) ((((double) round2) / 100.0d) * ((double) this.Service.PsyProtection));
                    this.Service.Psy += round2*//*(double) (round2 - ((round2 / 100) * this.Service.PsyProtection));*/
                  /*  this.Service.setHealth(this.Service.Health - (this.Service.Psy / 10.0d));
                    if (this.Service.Psy >= 100.0d) {
                        this.Service.setDead(Boolean.TRUE);
                        this.Service.setHealth(0.0d);
                        Intent intent2 = new Intent("StatsService.Message");
                        intent2.putExtra("Message", "P");
                        this.Service.sendBroadcast(intent2);
                    }
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                    this.Service.EM.StopActions();
                    if (!this.Service.IsDead) {
                        this.Service.EM.PlaySound(this.Type, this.strenght);
                        if (this.Service.Vibrate) {
                            this.Service.EM.VibrateInPattern();
                            return;
                        }
                        return;
                    }
                    return;
                }*/
                return;
            }
            this.IsInside = Boolean.FALSE;
        }
    }
}
