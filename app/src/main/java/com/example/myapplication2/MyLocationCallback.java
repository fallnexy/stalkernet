package com.example.myapplication2;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Arrays;
import java.util.Calendar;

import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Services.ServicesHelper;

//вызывается, когда координаты изменяются
public class MyLocationCallback extends LocationCallback  implements LocationServiceInterface {
    private Calendar cal = Calendar.getInstance();
    private int Hour = this.cal.get(10);
    private int Minutes = this.cal.get(12);
    private Location MyCurrentLocation;
    private StatsService ServiceReference;
    private int dayInt = this.cal.get(5);

    public MyLocationCallback(Location location, StatsService statsService) {
        this.MyCurrentLocation = location;
        this.ServiceReference = statsService;
        ServicesHelper.addLocationServiceInterface(this);
    }

    @Override
    public void locationChanged(Location location) {
        /*MyCurrentLocation.setLatitude(location.getLatitude());
        MyCurrentLocation.setLongitude(location.getLongitude());
        Log.d("локация_после", String.valueOf(MyCurrentLocation.getLongitude()));*/

    }

    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for (Location location : locationResult.getLocations()) {
            this.MyCurrentLocation.setLatitude(location.getLatitude());
            this.MyCurrentLocation.setLongitude(location.getLongitude());
            this.MyCurrentLocation.setProvider(location.getProvider());
            this.MyCurrentLocation.setBearing(location.getBearing());
            this.MyCurrentLocation.setAccuracy(location.getAccuracy());
           // Log.d("локация____до", String.valueOf(MyCurrentLocation.getLongitude()));
            locationChanged(MyCurrentLocation);
            if (!this.ServiceReference.IsDead && this.ServiceReference.IsUnlocked) {
                GetTime();
                TimeToDischarge();
                ServiceReference.artCompass(); // артос компас, который дает неуязвимость на 15 нимут
                ServiceReference.Super_save_zone_check();
                ServiceReference.getMovingAnomalies();
                ServiceReference.CheckAnomalies();
                ServiceReference.CheckIfInAnyAnomaly();
                ServiceReference.CheckPsyForMonolith();
                ServiceReference.GetTime();

            }
            if (this.ServiceReference.Health <= 0.0d) {
                this.ServiceReference.IsDead = Boolean.TRUE;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.ServiceReference.Health); //0
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Rad); //1
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Bio); //2
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.Psy); //3
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLatitude()); //4
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.MyCurrentLocation.getLongitude());//6
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.ScienceQR);  //qr ученого//6
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.TotalProtection(ServiceReference.RadProtectionArr));//7
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.TotalProtection(ServiceReference.BioProtectionArr));//8
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.TotalProtection(ServiceReference.PsyProtectionArr));//9
            stringBuilder.append(":");
            /*stringBuilder.append(ServiceReference.latLngAnomaly.latitude);//10
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.latLngAnomaly.longitude);//11
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.radiusAnomaly);//12
            stringBuilder.append(":");*/
            stringBuilder.append(Arrays.toString(ServiceReference.RadProtectionCapacityArr).replaceAll("\\[|\\]", "")); //13
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.MaxRadProtectionCapacityArr).replaceAll("\\[|\\]", "")); //14
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.BioProtectionCapacityArr).replaceAll("\\[|\\]", "")); //15
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.MaxBioProtectionCapacityArr).replaceAll("\\[|\\]", "")); //16
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.PsyProtectionCapacityArr).replaceAll("\\[|\\]", "")); //17
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.MaxPsyProtectionCapacityArr).replaceAll("\\[|\\]", "")); //18
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.RadProtectionArr).replaceAll("\\[|\\]", "")); //19
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.BioProtectionArr).replaceAll("\\[|\\]", "")); //20
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.PsyProtectionArr).replaceAll("\\[|\\]", "")); //21
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.MaxProtectionsAvailable); //22
            Log.d("гагарин", String.valueOf("гагарин".hashCode()));
            Log.d("выброс", String.valueOf("выброс".hashCode()));
            Log.d("координаты", String.valueOf(ServiceReference.latLngAnomaly.longitude));
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
        this.Hour = this.cal.get(Calendar.HOUR_OF_DAY);
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
            dischargeTime(7, 17, 23);
            dischargeTime(8, 10, 47);
            dischargeTime(9, 13, 37);
        }
    }


}
