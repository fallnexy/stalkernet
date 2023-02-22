package com.example.myapplication2;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Arrays;
import java.util.Calendar;

import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Services.ServicesHelper;

import static com.example.myapplication2.anomaly.Anomaly.BIO;
import static com.example.myapplication2.anomaly.Anomaly.PSY;
import static com.example.myapplication2.anomaly.Anomaly.RAD;

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
    // TODO убрать current и все plaeyrCharacter отсюда
    private int current = 1;
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for (Location location : locationResult.getLocations()) {
            this.MyCurrentLocation.setLatitude(location.getLatitude());
            this.MyCurrentLocation.setLongitude(location.getLongitude());
            this.MyCurrentLocation.setProvider(location.getProvider());
            this.MyCurrentLocation.setBearing(location.getBearing());
            this.MyCurrentLocation.setAccuracy(location.getAccuracy());
            locationChanged(MyCurrentLocation);
            if (!ServiceReference.playerCharacter[current].isDead() && this.ServiceReference.IsUnlocked) {

                ServiceReference.applyDischarge();
                ServiceReference.artCompass(); // артос компас, который дает неуязвимость на 15 нимут
                ServiceReference.getMovingAnomalies();
                ServiceReference.applyAnomalies();
                ServiceReference.checkLocality();
                //ServiceReference.CheckPsyForMonolith();
                ServiceReference.checkQuest();

            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getHealth()); //0
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getContaminationUnit(RAD, 0)); //1
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getContaminationUnit(BIO, 0)); //2
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getContaminationUnit(PSY, 0)); //3
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.myCurrentLocation.getLatitude()); //4
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.myCurrentLocation.getLongitude());//6
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.ScienceQR);  //qr ученого//6
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getTotalProtection(ServiceReference.playerCharacter[current].getRadProtection()[1]));//7
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getTotalProtection(ServiceReference.playerCharacter[current].getBioProtection()[1]));//8
            stringBuilder.append(":");
            stringBuilder.append(this.ServiceReference.playerCharacter[current].getTotalProtection(ServiceReference.playerCharacter[current].getPsyProtection()[1]));//9
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getRadProtection()[0]).replaceAll("\\[|\\]", "")); //10
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getBioProtection()[0]).replaceAll("\\[|\\]", "")); //11
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getPsyProtection()[0]).replaceAll("\\[|\\]", "")); //12
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getRadProtection()[1]).replaceAll("\\[|\\]", "")); //13
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getBioProtection()[1]).replaceAll("\\[|\\]", "")); //14
            stringBuilder.append(":");
            stringBuilder.append(Arrays.toString(ServiceReference.playerCharacter[current].getPsyProtection()[1]).replaceAll("\\[|\\]", "")); //15
            stringBuilder.append(":");
            stringBuilder.append(ServiceReference.MaxProtectionsAvailable); //16
            String stringBuilder2 = stringBuilder.toString();
            Intent intent = new Intent("StatsService.Update");
            intent.putExtra("Stats", stringBuilder2);
            this.ServiceReference.sendBroadcast(intent);
            Intent intent1 = new Intent("MapTab.Circle");
            intent1.putExtra("DrawAnomaly", "Draw");
            ServiceReference.sendBroadcast(intent1);
        }
        this.ServiceReference.saveStats();
    }

    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }



}
