package com.example.myapplication2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;

public class Globals {
    Context mContext;
    public ArrayAdapter<MarkerOptions> Adapter;
    public String Bio;
    public ProgressBar BioBar;
    public TextView CO;
    public String CurrentBio;
    public String Health;
    public ProgressBar HealthBar;
    public ArrayList<MarkerOptions> MarkerArray = new ArrayList();
    public String MaxHealth = "200";
    public TextView Messages;
    public String Psy;
    public ProgressBar PsyBar;
    public String Rad;
    public ProgressBar RadBar;
    public ArrayList<String> StringMarkerArray = new ArrayList();
    public Location location = new Location("GPS");
    public GoogleMap map;

    private LocationManager locationManager;

    public Globals(Context mContext) {
        this.mContext = mContext;
    }

    public void UpdateStats() {
        int parseDouble;
        this.HealthBar.setMax(Integer.parseInt(this.MaxHealth));
        int i = 0;
        try {
            parseDouble = (int) Double.parseDouble(this.Health);
        } catch (Exception unused) {
            parseDouble = 0;
        }
        this.HealthBar.setProgress(parseDouble);
        try {
            parseDouble = (int) Double.parseDouble(this.Rad);
        } catch (Exception unused2) {
            parseDouble = 0;
        }
        this.RadBar.setProgress(parseDouble);
        try {
            parseDouble = (int) Double.parseDouble(this.Bio);
        } catch (Exception unused3) {
            parseDouble = 0;
        }
        this.BioBar.setProgress(parseDouble);
       // this.BioBar.setSecondaryProgress((int) Double.parseDouble(this.CurrentBio));  нужно задать Currentbio, чтоб оно работало
        try {
            i = (int) Double.parseDouble(this.Psy);
        } catch (Exception unused4) {
        }
        this.PsyBar.setProgress(i);

       // GPS изначальное, которое не работает
        /*TextView textView = this.CO;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(this.location.getLatitude()));
        stringBuilder.append(" - ");
        stringBuilder.append(String.valueOf(this.location.getLongitude()));
        textView.setText(stringBuilder.toString());*/

        // обновляет координаты GPS в реальном времени
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
    }
    //Сраное GPS из интернета
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            this.CO.setText(formatLocation(location));
        }
    }
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.6f, %2$.6f, %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }
    //конец сраного GPS

    public void AddGroundOverlay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
    }

    public void redrawMarkers() {
        for (int i = 0; i < this.MarkerArray.size(); i++) {
            this.map.addMarker((MarkerOptions) this.MarkerArray.get(i));
        }
    }
}
