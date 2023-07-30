package com.example.stalkernet.anomaly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.stalkernet.StatsService;

import androidx.core.util.Pair;

import static java.lang.Float.isInfinite;
import static java.lang.Float.isNaN;

public class MineAnomaly extends Anomaly{

    public static final long MINE_COUNT_DOWN = 4000;
    public static final double MINE_DAMAGE_PERCENT = -30d;
    public static final String MINE_EXPLOSION = "mine_explosion";
    public static final String MINE_ACTIVATION = "mine_active";
    public static final String MINE_DEACTIVATION = "mine_deactivation";

    public MineAnomaly(StatsService service, SQLiteDatabase database, Cursor cursor){
        this.service = service;
        this.database = database;
        this.cursor = cursor;
    }

    @Override
    public Pair<String, Double> getDamage(){
        float speed = service.myCurrentLocation.getSpeed();
        if (!isNaN(speed)  && !isInfinite(speed)) {
            if (speed < 0.3f){
                return new Pair<>(MINE, 5d);
            }   else if (speed < 0.5f){
                return new Pair<>(MINE, 15d);
            } else if (speed < 1f) {
                return new Pair<>(MINE, 25d);
            } else {
                return new Pair<>(MINE, 50d);
            }
        } else {
            return new Pair<>(MINE, 0d);
        }
    }

}
