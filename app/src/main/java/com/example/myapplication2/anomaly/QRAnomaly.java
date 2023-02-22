package com.example.myapplication2.anomaly;

import androidx.core.util.Pair;
/*
* создан 16.02.23
* подразумевается использование для сталкерской рулетки
* то есть когда сканируют qr код
* в QRTab связанный с этим метод - stalkerRoulette()
* */
public class QRAnomaly extends Anomaly{

    private double damage = 0;
    private String type = "";

    public QRAnomaly(){
        super();
    }

    /*
    * как бы перепианный метод, но без таковой надписи, потому что
    * в родительском классе у этого метода другие входяшие параметры
    * */
    public Pair<String, Double> getDamage(String type) {
        this.type = type;
        damage = 50d;
        return new Pair<>(this.type, damage);
    }
    /*
     * метод переписан, чтобы возращать в паре тип, какой пришел, и damage 50
     * */
    @Override
    public double getPower() {
        return damage;
    }

    @Override
    public String getType() {
        return type;
    }

    /*
    * этот метод и все осатльные переписаны, чтобы ничего не делать
    * */
    @Override
    public Pair<Boolean, String> isInside(int day) {
        // Do nothing
        return new Pair<>(false, "");
    }

    @Override
    public void setShowable(int i) {
        // Do nothing
    }

    @Override
    public Pair<String, Double> getDamage() {
        // Do nothing
        return new Pair<>("", 0d);
    }
}
