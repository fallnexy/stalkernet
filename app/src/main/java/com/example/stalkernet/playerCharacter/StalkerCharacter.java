package com.example.stalkernet.playerCharacter;
/*
* Created by fallnexy on 20.02.23
* Создано
* Класс сталкера
* В отличии от дефолтного класса, у этого возможны защиты от двух видов аномалий
* */

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;

import static com.example.stalkernet.MainActivity.INTENT_MAIN;
import static com.example.stalkernet.MainActivity.INTENT_MAIN_PROTECTION;
import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;

public class StalkerCharacter extends PlayerCharacter{
    public static final int STALKER_CHARACTER = 1;

    public StalkerCharacter(Context context) {
        super(context);
    }

    @Override
    public int getProtectionsAvailable(){
        return 2;
    }

    @Override
    public void setProtection(String type, String subType, double value) {
        //super.setProtection(type, subType, value);
        //counter считает, сколько стоит защит от аномалий не устанавлеемого типа
        int counter = 0;
        StringBuilder output = new StringBuilder();
        for (String protectionType : new String[] {RAD, BIO, PSY}){
            if (!protectionType.equals(type) && getTotalProtection(getProtectionMap().get(protectionType)[1]) > 0d){
                counter++;
                output.append(protectionType).append("@");
            }
        }
        //Log.d("ну чече", "counter = " + counter);
        //выставляет защиту
        double strength = value == 0 ? 0: MAX_PROTECTION_STRENGTH[getSubProtectionMap().get(subType)];
        getProtectionMap().get(type)[0][getSubProtectionMap().get(subType)] = strength;
        getProtectionMap().get(type)[1][getSubProtectionMap().get(subType)] = value;
        // если защит слишком много, отправляет сообщение в mainActivity,
        // чтобы там выбрали какую защиту снять
        if (value > 0 && counter > 1) {
            Intent intent = new Intent(INTENT_MAIN);
            intent.putExtra(INTENT_MAIN_PROTECTION, output.toString());
            getContext().sendBroadcast(intent);
        }
    }
    /*
    * Вызывается в mainActivity и выбирает, какую защиту снять
    * отправляет результат в сервис
    * */
    public void nullifyProtectionDialog( String input){
        String[] inputSplit = input.split("@");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выбор защиты");
        builder.setMessage("Превышено количество доступных защит. Выберите, какую защиту оставить вместе с только что полученной:");
        builder.setCancelable(false);
        builder.setPositiveButton(inputSplit[0],((dialogInterface, which) -> {
            sendIntent(inputSplit[1]);
        }));
        builder.setNegativeButton(inputSplit[1], ((dialogInterface, which) -> {
            sendIntent(inputSplit[0]);
        }));
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    /*
    * вызывается внутри nullifyProtectionDialog
    * */
    //TODO поменять StRoulette на что нибудь приемлимое
    private void sendIntent(String output) {
        Intent intent = new Intent("StRoulette");
        intent.putExtra("protection", output);
        getContext().sendBroadcast(intent);
    }
    /*
    * вызывается в сервисе и снимает выбранную защиту
    * */
    public void nullifyThirdProtection(String type){
        Arrays.fill(getProtectionMap().get(type)[0], 0);
        Arrays.fill(getProtectionMap().get(type)[1], 0);
    }
}
