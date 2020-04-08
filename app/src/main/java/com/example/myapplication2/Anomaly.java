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
 /*   private boolean FirstTimeInside = true;*/
    public Boolean IsInside = Boolean.FALSE;
    private Date LastTimeInside;
    private StatsService Service;
    public String Type;
   /* private int hours;
    private long mills;
    private int mins;*/
    public int minstrenght = 1;
    public Polygon poly;
    public Double radius;
    public Double strenght;
    public Integer gesStatus;

    public Anomaly(String str, String str2, Double d, Polygon polygon, StatsService statsService) {
        this.Figure = str;
        this.Type = str2;
        this.strenght = d;
        this.poly = polygon;
        this.Service = statsService;
    }

    public Anomaly(String str, String str2, Double d, Double d2, LatLng latLng, StatsService statsService, Integer gestaltStatus) {
        Figure = str;
        Type = str2;
        strenght = d;
        Center = latLng;
        radius = d2;
        Service = statsService;
        gesStatus = gestaltStatus;
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
    public void AnomalyResult(double valueOf){
        int round;
        Service.LastTimeHitBy = Type;
        IsInside = Boolean.TRUE;
        Service.TypeAnomalyIn = Type;
        round = (int) Math.round(strenght * (1 - (valueOf / radius)));
        if (round <= minstrenght) {
            round = minstrenght;
        }
        switch (Type){
            case "Rad":
                round -= (round / 100) * Service.RadProtection;
                Service.Rad += round;
                Service.setHealth(Service.Health - round);
                if (Service.Rad >= 100.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "H");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Bio":
                round -= (round / 100) * Service.BioProtection;
                Service.Bio += round;
                Service.setHealth(Service.Health - round);
                if (Service.Bio >= 100.0d) {
                    Service.setDead(Boolean.TRUE);
                    Service.setHealth(0.0d);
                    Intent intent2 = new Intent("StatsService.Message");
                    intent2.putExtra("Message", "P");
                    Service.sendBroadcast(intent2);
                }
                return;
            case "Psy":
                round -= (round / 100) * Service.PsyProtection;
                Service.Psy += round;
                Service.setHealth(Service.Health - round);
                if (Service.Psy >= 100.0d) {
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
            int round;
            Service.LastTimeHitBy = Type;
            Service.TypeAnomalyIn = Type;
            round = (int) Math.round(strenght * (radius / distanceToAnomaly));
            if (round <= minstrenght) {
                round = minstrenght;
            }
            Service.setHealth(Service.Health - round);
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
                    this.Service.Health -= this.Service.Rad;
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type.equals("Bio")) {
                    this.Service.Bio = this.strenght;
                    this.Service.Health -= this.Service.Bio;
                    this.Service.LastTimeChanged = Calendar.getInstance().getTime();
                }
                if (this.Type.equals("Psy")) {
                    this.Service.Psy = this.strenght;
                    this.Service.Health -= this.Service.Psy / 2.0d;
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
            location.setLatitude(Center.latitude);
            location.setLongitude(Center.longitude);
            double valueOf = location.distanceTo(Service.MyCurrentLocation);

            if (Type.equals("Ges")){
                Gestalt(valueOf);
            }

            if (valueOf <= radius) {
                if (Type.equals("Ges")){
                    Intent intent2 = new Intent("StatsService.Message");  // отправляет сообщение на главный экран "Не выходи из виброзоны, пока не закроешь гештальт."
                    intent2.putExtra("Message", "G");
                    Service.sendBroadcast(intent2);
                }
                AnomalyResult(valueOf);
                /*int round;
                if (this.Type.equals("Rad")) {
                    this.Service.LastTimeHitBy = this.Type;
                    this.IsInside = Boolean.TRUE;
                    this.Service.TypeAnomalyIn = "Rad";
                    round = (int) Math.round(this.strenght * (1 - (valueOf / this.radius)));
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
                        int  round = (int) Math.round(this.strenght * (1 - (valueOf / this.radius)));
                        if (round <= this.minstrenght) {
                            round = this.minstrenght;
                        }
                        round -= (round / 100) * this.Service.BioProtection;
                        this.Service.Bio += round *//*(double) (round - ((int) ((((double) round) / 100.0d) * ((double) this.Service.BioProtection))))*/;
                      /*  this.Service.setHealth(this.Service.Health - round*//*this.Service.Bio*//*);*/
                        //this.Service.CurrentBio += this.strenght;
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
                        round = (int) Math.round(this.strenght * (1 - (((double) (float) valueOf) / this.radius)));
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
                            this.Service.CurrentBio += this.strenght;
                            if (this.Service.CurrentBio >= 100.0d) {
                                this.Service.setDead(Boolean.TRUE);
                                this.Service.setHealth(0.0d);
                                intent = new Intent("StatsService.Message");
                                intent.putExtra("Message", "H");
                                this.Service.sendBroadcast(intent);
                            }
                        }
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
                    int round2 = (int) Math.round(this.strenght * (1 - (valueOf / this.radius)));
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
