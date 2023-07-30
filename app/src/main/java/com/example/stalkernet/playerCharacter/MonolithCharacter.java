package com.example.stalkernet.playerCharacter;
/*
* создано 22.02.2023
* applyProtection - увеличивают здоровье, если находятся в пси, от гештальта стандартный урон
*
* */

import android.content.Context;

import androidx.core.util.Pair;

import static com.example.stalkernet.anomaly.Anomaly.GESTALT;
import static com.example.stalkernet.anomaly.Anomaly.PSY;

public class MonolithCharacter extends PlayerCharacter{
    public static final int MONOLITH_CHARACTER = 2;

    public MonolithCharacter(Context context) {
        super(context);
    }

    @Override
    public int getProtectionsAvailable(){
        return 0;
    }

    @Override
    public void applyProtection(Pair<String,Double> damagePair){
        String type = damagePair.first;
        if (type.equals(PSY)){
            increaseHealth(10d);
        } else if (type.equals(GESTALT)){
            super.applyProtection(damagePair);
        }
    }

    @Override
    public void purification(){
        increaseHealth(-0.5);
        setLastTimeHitBy("");
    }

    @Override
    public void responseDischarge(){
        //Do nothing
    }
}
