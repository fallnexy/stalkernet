package com.example.stalkernet;

import android.location.Location;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

//вызывается, когда координаты изменяются
public class MyLocationCallback extends LocationCallback {

    private Location previousLocation;
    private StatsService service;

    public MyLocationCallback(Location previousLocation, StatsService statsService) {
        this.previousLocation = previousLocation;
        service = statsService;
    }

    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for (Location location : locationResult.getLocations()) {
            /*myCurrentLocation.setLatitude(location.getLatitude());
            myCurrentLocation.setLongitude(location.getLongitude());
            myCurrentLocation.setProvider(location.getProvider());
            myCurrentLocation.setBearing(location.getBearing());
            myCurrentLocation.setAccuracy(location.getAccuracy());*/
            if (previousLocation.getLatitude() < 1){
                previousLocation = location;
            }

            Location newLocation = simpleLocationPredictor(previousLocation, location);
            newLocation.setSpeed(getSpeed(previousLocation, location));
            //Log.d(LOG_CHE_CHE, "getSpeed " + getSpeed(previousLocation, location));

            // Store the new location as the previous location for the next iteration
            previousLocation = newLocation;
            service.updateLocation(newLocation);

        }

    }

    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }
    /*
    * если координта поменялась больше, чем на service.getMaxDrift() метров, то подменяет координату на такую,
    * что расстояние поменялось на service.getDriftCorrection() метра
    * */
    private Location simpleLocationPredictor(Location previousLocation, Location currentLocation) {
        double distance = previousLocation.distanceTo(currentLocation);
        if (distance > service.getMaxDrift()) {
            double factor = service.getDriftCorrection() / distance;
            double x = previousLocation.getLatitude() + factor * (currentLocation.getLatitude() - previousLocation.getLatitude());
            double y = previousLocation.getLongitude() + factor * (currentLocation.getLongitude() - previousLocation.getLongitude());
            currentLocation.setLatitude(x);
            currentLocation.setLongitude(y);
        }
        return currentLocation;
    }
    /*
    * высчитывает скорость
    * */
    private float getSpeed(Location previousLocation, Location currentLocation){
        float distance = previousLocation.distanceTo(currentLocation);
        float time = (currentLocation.getTime() / 1000 - previousLocation.getTime() / 1000) ;
        return distance / time;
    }
}
