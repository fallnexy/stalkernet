package com.example.stalkernet;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;

import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.CONTAMINATION_2D_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.MAX_HEALTH_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.MAX_PROTECTION_STRENGTH;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PREFERENCE_NAME;

public class Globals {
    public static final String PREFERENCE_GLOBALS = "globals_preferences";
    public static final String GESTALT_GLOBALS_KEY = "gestalt_globals_preferences";
    public static final String MASSAGE_GLOBALS_KEY = "massage_globals_preferences";

    Context mContext;

    private double[] totalProtections;
    private double[] radProtectionIn;
    private double[] bioProtectionIn;
    private double[] psyProtectionIn;
    public ProgressBar radQuestBar, radArtBar, radSuitBar;
    public ProgressBar bioQuestBar, bioArtBar, bioSuitBar;
    public ProgressBar psyQuestBar, psyArtBar, psySuitBar;
    public ProgressBar healthBar, radBar, bioBar, psyBar;

    private boolean gestaltOpen = false;
    private String massage;
    public TextView tvCoordinate, tvMessage, tvGestaltOpen;

    public String Health, TotalProtectionRad, TotalProtectionBio, TotalProtectionPsy,
            strengthRad, strengthBio, strengthPsy,
            ProtectionRadArr, ProtectionBioArr, ProtectionPsyArr;

    public TextView healthText, healthTextUser, radText, bioText, psyText,
            radProtectionOut, bioProtectionOut, psyProtectionOut, radTotalProtection, bioTotalProtection,psyTotalProtection,
            radCapacityOut, bioCapacityOut, psyCapacityOut, MaxProtectionAvailable;
    public Location location = new Location("GPS");

    public boolean scienceQR = false, applyQR = false;          // не работает или работает? - вроде как обновляет вместе с обновлением координат


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

    /*
    * для здоровья
    * updateHealth обновляет в юзере
    * updateBar обновляет на карте
    * */
    private void updateHealth(int maxHealth, String strHealth, TextView tvRegularFraction){
        float health;
        try {
            health = Float.parseFloat(strHealth);
        } catch (Exception unused) {
            health = 0;
        }
        String regularFraction = String.format(Locale.US,"Состояние: %.0f", health) + " / " + maxHealth;
        tvRegularFraction.setText(regularFraction);
    }
    private void updateBar(@NonNull ProgressBar barName, int maxHealth, String strHealth, TextView perCent){
        float health;
        barName.setMax(maxHealth);
        try {
            health = Float.parseFloat(strHealth);
        } catch (Exception unused) {
            health = 0;
        }
        barName.setProgress((int) health);
        String percent = String.format(Locale.US,"%.0f", health) + " / " +maxHealth;
        perCent.setText(percent);
    }
    // для всего остального
    private void updateBar(double[] protectionValues, TextView perCentCapacity, TextView perCentProtection, double totalProtection, TextView tvTotalProtection){
        String percentCapacity =
                String.format(Locale.US,"%.1f\n", 100 * protectionValues[2] / MAX_PROTECTION_STRENGTH[2]) +
                String.format(Locale.US,"%.1f\n", 100 * protectionValues[1] / MAX_PROTECTION_STRENGTH[1]) +
                String.format(Locale.US,"%.1f\n", 100 * protectionValues[0] / MAX_PROTECTION_STRENGTH[0]);

        perCentCapacity.setText(percentCapacity);

        String percentProtection = String.format(Locale.US,"%.2f\n%.2f\n%.2f", protectionValues[5], protectionValues[4], protectionValues[3]);
        perCentProtection.setText(percentProtection);

        String str = String.format(Locale.US,"Итоговая защита, %%: %.2f", totalProtection);
        tvTotalProtection.setText(str);
    }
    /*
    * методы добавленные после начала переделки
    * */
    private void setTvMessages(){
        tvMessage.setText(massage);
    }
    private void setTvGestaltOpen(){
        int visibility = gestaltOpen ? View.VISIBLE : View.INVISIBLE;
        tvGestaltOpen.setVisibility(visibility);
    }
    /*
    * заполняет progressBar и textView для rad bio psy
    * */
    public void setContamination(int[] contamination){
        setContaminationType(radBar, radText, maxRad, contamination[0]);
        setContaminationType(bioBar, bioText, maxBio, contamination[1]);
        setContaminationType(psyBar, psyText, maxPsy, contamination[2]);
    }
    private void setContaminationType(ProgressBar pbContamination, TextView tvContamination, int max, int current){
        pbContamination.setMax(max);
        pbContamination.setProgress(current);
        String text = current + " / " + max;
        tvContamination.setText(text);
    }
    /*
    * выставляет total protections
    * */
    public void setTotalProtections(double[] totalProtections){
        this.totalProtections = Arrays.copyOf(totalProtections, totalProtections.length);
    }
    /*
    * выставляет длинный массив защит
    * */
    public void setProtections(String type, double[] protections){
        switch (type){
            case RAD:
                this.radProtectionIn = Arrays.copyOf(protections, protections.length);
                break;
            case BIO:
                this.bioProtectionIn = Arrays.copyOf(protections, protections.length);
                break;
            case PSY:
                this.psyProtectionIn = Arrays.copyOf(protections, protections.length);
                break;
        }

    }
    /*
    * заполняет progressBars защит
    * */
    public void setProtectionBars(ProgressBar questBars, ProgressBar artBars,ProgressBar suitBars, double[] protections){
        setBar(questBars, protections[0], 0);
        setBar(artBars, protections[1], 1);
        setBar(suitBars, protections[2], 2);
    }
    private void setBar(ProgressBar bar, double value, int i){
        bar.setMax(MAX_PROTECTION_STRENGTH[i]);
        bar.setProgress((int) value);
        if ( value > 0 && bar.getVisibility() == View.GONE) {
            bar.setVisibility(View.VISIBLE);
        } else if (value <= 0 && bar.getVisibility() == View.VISIBLE) {
            bar.setVisibility(View.GONE);
        }
    }
    /*
    * координаты
    * */
    private void setCoordinate(){
        String coordinate = String.format(Locale.US,"%.6f", location.getLatitude()) +
                            " - " +
                            String.format(Locale.US,"%.6f", location.getLongitude());
        tvCoordinate.setText(coordinate);
    }

    //эта штука вызывается в MainActivity и обновяет статы, которые есть в UserChildFragment
    public void updateStats() {
        loadSome(mContext);
        updateHealth(maxHealth, Health, healthTextUser);
        updateBar(healthBar,maxHealth, Health, healthText);
        updateBar(radProtectionIn, radCapacityOut, radProtectionOut, totalProtections[0], radTotalProtection);
        updateBar(bioProtectionIn, bioCapacityOut, bioProtectionOut, totalProtections[1], bioTotalProtection);
        updateBar(psyProtectionIn, psyCapacityOut, psyProtectionOut, totalProtections[2], psyTotalProtection);
        setProtectionBars(radQuestBar, radArtBar, radSuitBar, radProtectionIn);
        setProtectionBars(bioQuestBar, bioArtBar, bioSuitBar, bioProtectionIn);
        setProtectionBars(psyQuestBar, psyArtBar, psySuitBar, psyProtectionIn);
        setTvGestaltOpen();
        setTvMessages();
        setCoordinate();
    }

    private int maxHealth;
    private int maxRad, maxBio, maxPsy;

    private void loadSome(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        maxHealth = sharedPreferences.getInt(MAX_HEALTH_KEY, 2000);
        double[] maxRadBioPsy = Arrays.stream(Objects.requireNonNull(sharedPreferences.getString(CONTAMINATION_2D_KEY, "0, 0, 0, 1000, 1000, 1000")).split(", ")).mapToDouble(Double::parseDouble).toArray();
        maxRad = (int) maxRadBioPsy[3];
        maxBio = (int) maxRadBioPsy[4];
        maxPsy = (int) maxRadBioPsy[5];
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
