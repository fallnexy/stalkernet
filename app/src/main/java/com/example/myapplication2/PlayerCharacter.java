package com.example.myapplication2;
/*
* создано 10.02.23
* Тут должно быть всякие особенности персонажей
* */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Arrays;

public class PlayerCharacter {
    private static final String PREFERENCE_NAME = "player_character_preferences";
    private static final String NAME_KEY = "name_key";
    private static final String FACTION_KEY = "faction_key";
    private static final String FACTION_POSITION_KEY = "faction_position_key";
    private static final String LAST_TIME_HIT_BY_KEY = "last_time_hit_by_key";
    private static final String MAX_HEALTH_KEY = "max_health_key";
    private static final String HEALTH_KEY = "health_key";
    private static final String DEAD_KEY = "dead_key";

    // значения, которые может принимать lastTimeHitBy
    private static final String[] ALLOWED_VALUES = {"Rad", "Bio", "Psy", "Dis", "Ges", ""};

    private String name;
    private String faction;
    private String factionPosition;
    private String lastTimeHitBy;
    private double maxHealth;
    private double health;
    private boolean dead;
    private Context context;


    public PlayerCharacter(Context context) {
        this.context = context;
        this.name = "Иван";
        this.faction = "Вольный сталкер";
        this.factionPosition = "рядовой";
        this.lastTimeHitBy = "";
        this.maxHealth = 2000;
        this.health = 2000;
        this.dead = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getFactionPosition() {
        return factionPosition;
    }

    public void setFactionPosition(String factionPosition) {
        this.factionPosition = factionPosition;
    }

    public String getLastTimeHitBy() {
        return lastTimeHitBy;
    }

    public void setLastTimeHitBy(String lastTimeHitBy) {
        if (Arrays.asList(ALLOWED_VALUES).contains(lastTimeHitBy)) {
            this.lastTimeHitBy = lastTimeHitBy;
        } else {
            throw new IllegalArgumentException("Invalid value for LastTimeHitBy. Only 'Rad', 'Bio', 'Psy', 'Dis', 'Ges', or '' are allowed.");
        }
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (health > 0.0d && health <= maxHealth) {
            this.health = health;
        } else if (health > maxHealth) {
            this.health = maxHealth;
        } else {
            setDead(true);
            this.health = health;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        if (!dead) {
            this.dead = false;
        } else {
            this.dead = true;
            switch (lastTimeHitBy) {
                case "Rad":
                    sendDeathInfo("Вы умерли от Радиации", "H");
                    break;
                case "Bio":
                    sendDeathInfo("Вы умерли от Биозаражения", "H");
                    break;
                case "Psy":
                    sendDeathInfo("Вы умерли от Пси воздействия", "P");
                    break;
                case "Dis":
                    sendDeathInfo("Вы умерли от выброса", "H");
                    break;
                case "":
                    sendDeathInfo("Вы умерли?", "H");
                    break;
            }
        }
    }

    private void sendDeathInfo(String causeOfDeath, String letter) {
        Toast.makeText(context, causeOfDeath, Toast.LENGTH_LONG).show();
        Intent intent = new Intent("StatsService.Message");
        intent.putExtra("Message", letter);
        context.sendBroadcast(intent);
    }


    public void loadStats(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        name = sharedPreferences.getString(NAME_KEY, "Иван");
        faction = sharedPreferences.getString(FACTION_KEY, "Вольный сталкер");
        factionPosition = sharedPreferences.getString(FACTION_POSITION_KEY, "рядовой");
        lastTimeHitBy = sharedPreferences.getString(LAST_TIME_HIT_BY_KEY, "");
        maxHealth = sharedPreferences.getFloat(MAX_HEALTH_KEY, 2000);
        health = sharedPreferences.getFloat(HEALTH_KEY, 2000);
        dead = sharedPreferences.getBoolean(DEAD_KEY, false);
    }

    public void saveStats(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_KEY, name);
        editor.putString(FACTION_KEY, faction);
        editor.putString(FACTION_POSITION_KEY, factionPosition);
        editor.putString(LAST_TIME_HIT_BY_KEY, lastTimeHitBy);
        editor.putFloat(MAX_HEALTH_KEY, (float) maxHealth);
        editor.putFloat(HEALTH_KEY, (float) health);
        editor.putBoolean(DEAD_KEY, dead);
        editor.apply();
    }
}
