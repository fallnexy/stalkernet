package com.example.myapplication2.anomaly;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication2.StatsService;

import androidx.core.util.Pair;

public class OasisAnomaly extends Anomaly{

    Context context;

    public OasisAnomaly(StatsService service) {
        super(service);
        context = service.getApplicationContext();
    }

    public Pair<String, Double> getDamage(){
        Intent intent = new Intent("StatsService.Message");
        intent.putExtra("Message", "O");
        context.sendBroadcast(intent);
        return new Pair<>(OASIS, -0.5);
    }
}
