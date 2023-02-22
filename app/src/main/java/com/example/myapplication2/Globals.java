package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;

import static com.example.myapplication2.playerCharacter.PlayerCharacter.MAX_BIO_KEY;
import static com.example.myapplication2.playerCharacter.PlayerCharacter.MAX_HEALTH_KEY;
import static com.example.myapplication2.playerCharacter.PlayerCharacter.MAX_PROTECTION_STRENGTH;
import static com.example.myapplication2.playerCharacter.PlayerCharacter.MAX_PSY_KEY;
import static com.example.myapplication2.playerCharacter.PlayerCharacter.MAX_RAD_KEY;
import static com.example.myapplication2.playerCharacter.PlayerCharacter.PREFERENCE_NAME;

public class Globals {
    public static final String PREFERENCE_GLOBALS = "globals_preferences";
    public static final String GESTALT_GLOBALS_KEY = "gestalt_globals_preferences";
    public static final String MASSAGE_GLOBALS_KEY = "massage_globals_preferences";

    Context mContext;

    private boolean gestaltOpen = false;
    private String massage;
    public TextView tvCoordinate, tvMessages, tvGestaltOpen;

    public String Health, Rad, Bio, Psy, TotalProtectionRad, TotalProtectionBio, TotalProtectionPsy,
            CapacityProtectionRad, CapacityProtectionBio, CapacityProtectionPsy,
            ProtectionRadArr, ProtectionBioArr, ProtectionPsyArr;
    public ProgressBar HealthBar, RadBar, BioBar, PsyBar;
    public TextView HealthPercent, RadPercent, BioPercent, PsyPercent,
            RadProtectionPercent, BioProtectionPercent, PsyProtectionPercent,
            RadCapacityPercent, BioCapacityPercent, PsyCapacityPercent, MaxProtectionAvailable;
    public Location location = new Location("GPS");

    public int ScienceQR;          // не работает или работает? - вроде как обновляет вместе с обновлением координат


    public Globals(Context mContext) {
        this.mContext = mContext;
    }

    // фиктвный сеттер и геттер
    public boolean isGestaltOpen() {
        return gestaltOpen;
    }
    public void setGestaltOpen(boolean gestaltOpen) {
        this.gestaltOpen = gestaltOpen;
    }
    public String getMassage() {
        return massage;
    }
    public void setMassage(String massage) {
        this.massage = massage;
    }

    // для жизней
    private void updateBar(@NonNull ProgressBar barName, int maxHealth, String strHealth, TextView perCent){
        float health;
        barName.setMax(maxHealth);
        try {
            health = Float.parseFloat(strHealth);
        } catch (Exception unused) {
            health = 0;
        }
        barName.setProgress((int) health);
        String percent = String.format(Locale.US,"%.0f", health) + "/" +maxHealth;
        perCent.setText(percent);
    }
    // для всего остального
    private void updateBar(@NonNull ProgressBar barName, int anomalyMax, String anomalyType, TextView perCent, String capacity,
                           TextView perCentCapacity, String protection, TextView perCentProtection, String totalProtection){
        float currentAnomalyInjury;
        double parseCapacityQuest, parseCapacityArt, parseCapacitySuit;
        double parseProtectionQuest, parseProtectionArt, parseProtectionSuit;
        double parseTotalProtection;
        barName.setMax(anomalyMax);
        try {
            currentAnomalyInjury = Float.parseFloat(anomalyType);
            parseCapacityQuest = Double.parseDouble(capacity.split(", ")[0]);
            parseCapacityArt = Double.parseDouble(capacity.split(", ")[1]);
            parseCapacitySuit = Double.parseDouble(capacity.split(", ")[2]);
            parseProtectionQuest = Double.parseDouble(protection.split(", ")[0]);
            parseProtectionArt = Double.parseDouble(protection.split(", ")[1]);
            parseProtectionSuit = Double.parseDouble(protection.split(", ")[2]);
            parseTotalProtection = Double.parseDouble(totalProtection);
        } catch (Exception unused) {
            currentAnomalyInjury = 0;
            parseCapacityQuest = 0;
            parseCapacityArt = 0;
            parseCapacitySuit = 0;
            parseProtectionQuest = 0;
            parseProtectionArt = 0;
            parseProtectionSuit = 0;
            parseTotalProtection = 0;
        }
        barName.setProgress((int) currentAnomalyInjury);
        String percent = String.format(Locale.US,"%.1f", currentAnomalyInjury) + "/" + anomalyMax;
        perCent.setText(percent);

        String percentCapacity = "Прочность: " +
                "Бр " + String.format(Locale.US,"%.1f", 100 * parseCapacitySuit / MAX_PROTECTION_STRENGTH[2]) + "%; " +
                "Арт " + String.format(Locale.US,"%.1f", 100 * parseCapacityArt / MAX_PROTECTION_STRENGTH[1]) + "%; " +
                "Кв " + String.format(Locale.US,"%.1f", 100 * parseCapacityQuest / MAX_PROTECTION_STRENGTH[0]) + "% ";

        perCentCapacity.setText(percentCapacity);

        String percentProtection = String.format(Locale.US,"Защита: Бр %.0f; Арт %.0f; Кв %.0f; ∑ %.2f", parseProtectionSuit, parseProtectionArt, parseProtectionQuest, parseTotalProtection);
        perCentProtection.setText(percentProtection);

    }
    /*
    * методы добавленные после начала переделки
    * */
    private void setTvMessages(){
        tvMessages.setText(massage);
    }
    private void setTvGestaltOpen(){
        int visibility = gestaltOpen ? View.VISIBLE : View.INVISIBLE;
        tvGestaltOpen.setVisibility(visibility);
    }
    private void setCoordinate(){
        String coordinate = String.format(Locale.US,"%.6f", location.getLatitude()) +
                            " - " +
                            String.format(Locale.US,"%.6f", location.getLongitude());
        tvCoordinate.setText(coordinate);
    }
    //эта штука вызывается в MainActivity и обновяет статы, которые есть в GeneralTab
    public void UpdateStats() {
        loadSome(mContext);
        updateBar(HealthBar,maxHealth, Health, HealthPercent);
        updateBar(RadBar, maxRad, Rad, RadPercent, CapacityProtectionRad, RadCapacityPercent, ProtectionRadArr, RadProtectionPercent, TotalProtectionRad);
        updateBar(BioBar, maxBio, Bio, BioPercent, CapacityProtectionBio, BioCapacityPercent, ProtectionBioArr, BioProtectionPercent, TotalProtectionBio);
        updateBar(PsyBar, maxPsy, Psy, PsyPercent, CapacityProtectionPsy, PsyCapacityPercent, ProtectionPsyArr, PsyProtectionPercent, TotalProtectionPsy);
        setTvGestaltOpen();
        setTvMessages();
        setCoordinate();
    }

    private int maxHealth;
    private int maxRad, maxBio, maxPsy;
    private void loadSome(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        maxHealth = sharedPreferences.getInt(MAX_HEALTH_KEY, 2000);
        maxRad = sharedPreferences.getInt(MAX_RAD_KEY, 1000);
        maxBio = sharedPreferences.getInt(MAX_BIO_KEY, 1000);
        maxPsy = sharedPreferences.getInt(MAX_PSY_KEY, 1000);
    }
    SharedPreferences sharedPreferences;
    public void loadStats() {
        sharedPreferences = mContext.getSharedPreferences(PREFERENCE_GLOBALS, Context.MODE_PRIVATE);
        setGestaltOpen(sharedPreferences.getBoolean(GESTALT_GLOBALS_KEY, false));
        setMassage(sharedPreferences.getString(MASSAGE_GLOBALS_KEY, ""));

    }

    public void saveStats() {
        sharedPreferences = mContext.getSharedPreferences(PREFERENCE_GLOBALS,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(GESTALT_GLOBALS_KEY, isGestaltOpen());
        edit.putString(MASSAGE_GLOBALS_KEY, getMassage());
        edit.apply();
    }
}
