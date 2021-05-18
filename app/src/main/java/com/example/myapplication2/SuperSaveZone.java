package com.example.myapplication2;

import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class SuperSaveZone {

    long st_time;
    long st_i;
    long delta_time;
    double circle_radius;
    String save_zones;
    LatLng[] stalkers_save_zones_in = new LatLng[26];
    LatLng[] military_save_zones_in = new LatLng[20];
    LatLng[] red_save_zones_out = new LatLng[24];
    LatLng[] green_save_zones_out = new LatLng[24];



    public SuperSaveZone( long start_time, long start_i, long delta, double radius, String zones){
        st_time = start_time;
        st_i = start_i;
        delta_time = delta;
        circle_radius = radius;
        save_zones = zones;
        create_stalkers_zones_in();
        create_red_zones_out();
        create_military_zones_in();
        create_green_zones_out();
    }

    public void create_stalkers_zones_in(){
        LatLng[] latLngs = new LatLng[26];
        latLngs[0] = new LatLng(64.534519d, 40.154603d);
        latLngs[1] = new LatLng(64.534272d, 40.154759d);
        latLngs[2] = new LatLng(64.534004d, 40.154893d);
        latLngs[3] = new LatLng(64.533747d, 40.155033d);
        latLngs[4] = new LatLng(64.533484d, 40.155183d);
        latLngs[5] = new LatLng(64.533219d, 40.155322d);
        latLngs[6] = new LatLng(64.532960d, 40.155472d);
        latLngs[7] = new LatLng(64.532702d, 40.155751d);
        latLngs[8] = new LatLng(64.532439d, 40.155890d);
        latLngs[9] = new LatLng(64.532178d, 40.156067d);
        latLngs[10] = new LatLng(64.531924d, 40.156217d);
        latLngs[11] = new LatLng(64.531640d, 40.156212d);
        latLngs[12] = new LatLng(64.531386d, 40.156352d);
        latLngs[13] = new LatLng(64.531204d, 40.156689d);
        latLngs[14] = new LatLng(64.531042d, 40.157038d);
        latLngs[15] = new LatLng(64.530855d, 40.157209d);
        latLngs[16] = new LatLng(64.530691d, 40.157295d);
        latLngs[17] = new LatLng(64.530541d, 40.157381d);
        latLngs[18] = new LatLng(64.530440d, 40.157451d);
        latLngs[19] = new LatLng(64.530322d, 40.157515d);
        latLngs[20] = new LatLng(64.530204d, 40.157569d);
        latLngs[21] = new LatLng(64.530070d, 40.157601d);
        latLngs[22] = new LatLng(64.529924d, 40.157617d); //
        latLngs[23] = new LatLng(64.529650d, 40.157510d);
        latLngs[24] = new LatLng(64.529594d, 40.156920d);
        latLngs[25] = new LatLng(64.529648d, 40.156437d);
        stalkers_save_zones_in = latLngs;
    }
    public void create_military_zones_in(){
        LatLng[] latLngs = new LatLng[20];
        latLngs[0] = new LatLng(64.534519d, 40.154603d);
        latLngs[1] = new LatLng(64.534272d, 40.154759d);
        latLngs[2] = new LatLng(64.534004d, 40.154893d);
        latLngs[3] = new LatLng(64.533747d, 40.155033d);
        latLngs[4] = new LatLng(64.533484d, 40.155183d);
        latLngs[5] = new LatLng(64.533219d, 40.155322d);
        latLngs[6] = new LatLng(64.532960d, 40.155472d);
        latLngs[7] = new LatLng(64.532702d, 40.155751d);
        latLngs[8] = new LatLng(64.532439d, 40.155890d);
        latLngs[9] = new LatLng(64.532178d, 40.156067d);
        latLngs[10] = new LatLng(64.531924d, 40.156217d);
        latLngs[11] = new LatLng(64.531640d, 40.156212d);
        latLngs[12] = new LatLng(64.531386d, 40.156352d);
        latLngs[13] = new LatLng(64.531305d, 40.155756d);
        latLngs[14] = new LatLng(64.531237d, 40.155161d);
        latLngs[15] = new LatLng(64.531174d, 40.154546d);
        latLngs[16] = new LatLng(64.530960d, 40.154246d);
        latLngs[17] = new LatLng(64.530700d, 40.154412d);
        latLngs[18] = new LatLng(64.530441d, 40.154562d);
        latLngs[19] = new LatLng(64.530173d, 40.154662d);
        military_save_zones_in = latLngs;
    }
    public void create_red_zones_out(){
        LatLng[] latLngs = new LatLng[24];
        latLngs[0] = new LatLng(64.529827d, 40.154909d);
        latLngs[1] = new LatLng(64.530166d, 40.154597d);
        latLngs[2] = new LatLng(64.530531d, 40.154565d);
        latLngs[3] = new LatLng(64.530880d, 40.154313d);
        latLngs[4] = new LatLng(64.531219d, 40.154005d);
        latLngs[5] = new LatLng(64.531563d, 40.153774d);
        latLngs[6] = new LatLng(64.531903d, 40.153554d);
        latLngs[7] = new LatLng(64.532108d, 40.152889d);
        latLngs[8] = new LatLng(64.532016d, 40.152133d);
        latLngs[9] = new LatLng(64.531947d, 40.151258d);
        latLngs[10] = new LatLng(64.532062d, 40.150518d);
        latLngs[11] = new LatLng(64.532418d, 40.150261d);
        latLngs[12] = new LatLng(64.532769d, 40.150051d);
        latLngs[13] = new LatLng(64.533023d, 40.150733d);
        latLngs[14] = new LatLng(64.533167d, 40.151465d);
        latLngs[15] = new LatLng(64.533365d, 40.152181d);
        latLngs[16] = new LatLng(64.533732d, 40.152390d);
        latLngs[17] = new LatLng(64.534071d, 40.152133d);
        latLngs[18] = new LatLng(64.534436d, 40.152131d);
        latLngs[19] = new LatLng(64.534792d, 40.151943d);
        latLngs[20] = new LatLng(64.535136d, 40.152013d);
        latLngs[21] = new LatLng(64.535187d, 40.152796d);
        latLngs[22] = new LatLng(64.535265d, 40.153574d);
        latLngs[23] = new LatLng(64.535231d, 40.154502d);
        red_save_zones_out = latLngs;
    }
    public void create_green_zones_out(){
        LatLng[] latLngs = new LatLng[24];
        latLngs[0] = new LatLng(64.529662d, 40.155804d);
        latLngs[1] = new LatLng(64.529597d, 40.156453d);
        latLngs[2] = new LatLng(64.529523d, 40.157064d);
        latLngs[3] = new LatLng(64.529736d, 40.157440d);
        latLngs[4] = new LatLng(64.530001d, 40.157161d);
        latLngs[5] = new LatLng(64.530255d, 40.156962d);
        latLngs[6] = new LatLng(64.530514d, 40.156785d);
        latLngs[7] = new LatLng(64.530787d, 40.156630d);
        latLngs[8] = new LatLng(64.531060d, 40.156667d);
        latLngs[9] = new LatLng(64.531307d, 40.156420d);
        latLngs[10] = new LatLng(64.531558d, 40.156152d);
        latLngs[11] = new LatLng(64.531778d, 40.155863d);
        latLngs[12] = new LatLng(64.532013d, 40.155621d);
        latLngs[13] = new LatLng(64.532248d, 40.155279d);
        latLngs[14] = new LatLng(64.532403d, 40.155000d);
        latLngs[15] = new LatLng(64.532645d, 40.154828d);
        latLngs[16] = new LatLng(64.532914d, 40.154784d);
        latLngs[17] = new LatLng(64.532967d, 40.155337d);
        latLngs[18] = new LatLng(64.533235d, 40.155482d);
        latLngs[19] = new LatLng(64.533515d, 40.155348d);
        latLngs[20] = new LatLng(64.533778d, 40.155187d);
        latLngs[21] = new LatLng(64.534032d, 40.155020d);
        latLngs[22] = new LatLng(64.534211d, 40.154913d);
        latLngs[23] = new LatLng(64.534525d, 40.154822d);
        green_save_zones_out = latLngs;
    }

    public LatLng[] Choose_save_zones(){
        LatLng[] zones;
        switch (save_zones){
            case "stalkers_in":
                zones = stalkers_save_zones_in;
                break;
            case "stalkers_out":
                zones = red_save_zones_out;
                break;
            case "military_in":
                zones = military_save_zones_in;
                break;
            case "green_out":
                zones = green_save_zones_out;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + save_zones);
        }
        return zones;
    }

    public CircleOptions Draw_save_zone(){
        LatLng[] zones = Choose_save_zones();
        CircleOptions circleOptions = null;
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        for (long i = 0; i < (zones.length / 2); i++){
            if ((timeInSeconds >= (st_time + i * delta_time)) && (timeInSeconds <= (st_time + (i + 1) * delta_time))){
                    circleOptions = new CircleOptions().center(zones[(int) (2 * i + st_i)]).radius(circle_radius).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE);
                    break;
            } else {
                    circleOptions = null;
            }

        }
        return circleOptions;
    }

    public LatLng Check_super_save_zone(){
        LatLng[] zones = Choose_save_zones();
        int j = -1;
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        for (long i = 0; i < (zones.length / 2); i++){
            if ((timeInSeconds >= (st_time + i * delta_time)) && (timeInSeconds <= (st_time + (i + 1) * delta_time))){
                j = (int) (2 * i + st_i);
                break;
            }

        }

        if (j == -1) {
            return new LatLng(0, 0);
        } else{
            return zones[j];
        }
    }



}
