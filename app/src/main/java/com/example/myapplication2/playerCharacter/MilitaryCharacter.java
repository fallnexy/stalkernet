package com.example.myapplication2.playerCharacter;

import android.content.Context;

import androidx.core.util.Pair;

import static com.example.myapplication2.anomaly.Anomaly.MINE;

/*
* Создано 28.02.2023
* военные автоматически проходят проверку на минном поле
* */
public class MilitaryCharacter extends PlayerCharacter{

    public MilitaryCharacter(Context context) {
        super(context);
    }

    @Override
    public void applyProtection(Pair<String,Double> damagePair){
        if (damagePair.first.equals(MINE)){
            resolveMine(damagePair.second, "true");
        } else {
          super.applyProtection(damagePair);
        }
    }
}
