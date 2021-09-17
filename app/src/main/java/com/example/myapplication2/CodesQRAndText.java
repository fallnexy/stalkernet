package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;


import com.example.myapplication2.fragments.QRTab;

import java.util.Calendar;

import androidx.fragment.app.Fragment;

/**
 * Created by fallnexy on 16.09.2021.
 */
/*
* по задумке в этом классе должны лежать qr и текстовые коды для QRTab и ChatTab, а то они стали дублироваться
* */
public class CodesQRAndText {

    public Fragment fragment;
    public TextView textView;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues = new ContentValues();
    Cursor cursor;

    private long firstTime, secondTime;

    public CodesQRAndText(Fragment fragment, TextView textView){
        this.fragment = fragment;
        this.textView = textView;
    }

    public void checkCode(String code){
        dbHelper = new DBHelper(fragment.requireActivity().getApplicationContext());
        Intent intent;
        int var1;
        label94: {

            intent = new Intent("Command");
            switch(code.hashCode()) {
                // полное воскрешение со сбросом всех параметров
                case 1025788929: // гагры
                case -1699798056: // nuyzi2sg7y3vq5
                    if (code.equals("гагры") || code.equals("nuyzi2sg7y3vq5f")) {
                        var1 = 0;
                        break label94;
                    }
                /*case 1456976519: // пси
                    if (code.equals("191000")) {
                        var3 = 1;
                        break label94;
                    }
                    break;
                case 1456976674:
                    if (code.equals("191050")) {
                        var3 = 2;
                        break label94;
                    }
                    break;
                case 1456977480:
                    if (code.equals("191100")) {
                        var3 = 3;
                        break label94;
                    }
                    break;
                case 1455129477: //рад
                    if (code.equals("171000")) {
                        var3 = 4;
                        break label94;
                    }
                    break;
                case 1455129632:
                    if (code.equals("171050")) {
                        var3 = 5;
                        break label94;
                    }
                    break;
                case 1455130438:
                    if (code.equals("171100")) {
                        var3 = 6;
                        break label94;
                    }
                    break;
                case 1456052998: // био
                    if (code.equals("181000")) {
                        var3 = 7;
                        break label94;
                    }
                    break;
                case 1456053153:
                    if (code.equals("181050")) {
                        var3 = 8;
                        break label94;
                    }
                    break;
                case 1456053959:
                    if (code.equals("181100")) {
                        var3 = 9;
                        break label94;
                    }
                    break;
                case -48468164:
                    if (code.equals("зона5звезд")) {
                        var3 = 10;
                        break label94;
                    }
                    break;
                case 1698598526:
                    if (code.equals("доставщик")) {
                        var3 = 11;
                        break label94;
                    }
                    break;
                case -1945318196:
                    if (code.equals("шпагат")) {
                        var3 = 12;
                        break label94;
                    }
                    break;
                case 1572782670:
                    if (code.equals("поперечный")) {
                        var3 = 13;
                        break label94;
                    }
                    break;
                case 702574009: // простое оживление
                    if (code.equals("приветбумеранг")) {
                        var3 = 14;
                        break label94;
                    }
                    break;
                case 1563300753:
                    if (code.equals("505050")) {
                        var3 = 15;
                        break label94;
                    }
                    break;
                case 2063766181: // монолит
                    if (code.equals("далматинец")) {
                        var3 = 16;
                        break label94;
                    }
                    break;
                case 1711354489: // режим бога
                    if (code.equals("азесмьцарь")) {
                        var3 = 17;
                        break label94;
                    }
                case 1730932747: //персональный выброс
                    if (code.equals("выброс")) {
                        var3 = 18;
                        break label94;
                    }
                    break;
                // новые коды
                case -1925203169: //гештальт защита
                    if (code.equals("всегдазакрыт")) {
                        var3 = 19;
                        break label94;
                    }
                case 1974805046: //гештальт защита снята
                    if (code.equals("теперьоткрыт")) {
                        var3 = 20;
                        break label94;
                    }
                case 113633: // умный код 1
                    if (code.equals("sc1")) {
                        var3 = 21;
                        break label94;
                    }
                case 113634: // умный код 2
                    if (code.equals("sc2")) {
                        var3 = 22;
                        break label94;
                    }
                case 697322052: //
                    if (code.equals("разреш1тип")) {
                        var3 = 23;
                        break label94;
                    }
                case 697351843: //
                    if (code.equals("разреш2тип")) {
                        var3 = 24;
                        break label94;
                    }
                case 697381634: //
                    if (code.equals("разреш3тип")) {
                        var3 = 25;
                        break label94;
                    }
                case -604537487: //
                    if (code.equals("выходигрока")) {
                        var3 = 26;
                        break label94;
                    }
                case -189541994: //
                    if (code.equals("снятьнеуяз")) {
                        var3 = 27;
                        break label94;
                    }
                case 272021583: //
                    if (code.equals("коссева")) {
                        textCodeSplitted[0] = "sc1";
                        textCodeSplitted[1] = "bio";
                        textCodeSplitted[2] = "suit";
                        textCodeSplitted[3] = "80";
                        var3 = 21;
                        break label94;
                    }
                case 271719333: //
                    if (code.equals("косзаря")) {
                        textCodeSplitted[0] = "sc1";
                        textCodeSplitted[1] = "rad";
                        textCodeSplitted[2] = "suit";
                        textCodeSplitted[3] = "80";
                        var3 = 21;
                        break label94;
                    }
                case -156863704: //
                    if (code.equals("косстраж")) {
                        textCodeSplitted[0] = "sc1";
                        textCodeSplitted[1] = "bio";
                        textCodeSplitted[2] = "suit";
                        textCodeSplitted[3] = "50";
                        var3 = 21;
                        break label94;
                    }
                case -430325800: //
                    if (code.equals("стражснять")) {
                        textCodeSplitted[0] = "sc1";
                        textCodeSplitted[1] = "bio";
                        textCodeSplitted[2] = "suit";
                        textCodeSplitted[3] = "0";
                        var3 = 21;
                        break label94;
                    }
                case 272098569: //
                    if (code.equals("косучен")) {
                        var3 = 28;
                        break label94;
                    }
                case 1045731098: //
                    if (code.equals("штраф")) {
                        var3 = 29;
                        break label94;
                    }
                case 1825756908: //
                    if (code.equals("явсемогущий-")) {
                        var3 = 27;
                        break label94;
                    }
                case 1825756906: //
                    if (code.equals("явсемогущий+")) {
                        var3 = 17;
                        break label94;
                    }
                case 425265276: //
                    if (code.equals("чекинвыброс")) {
                        var3 = 30;
                        break label94;
                    }
                case -1113320671: //
                    if (code.equals("чнзнает")) {
                        var3 = 31;
                        break label94;
                    }
                case -1792935999: //
                    if (code.equals("gloriamonolithhaereticorummors")) {
                        var3 = 32;
                        break label94;
                    }
                case -1759609022: //
                    if (code.equals("наукада")) {
                        var3 = 33;
                        break label94;
                    }
                case 2021447335: //
                    if (code.equals("наука-")) {
                        var3 = 34;
                        break label94;
                    }*/
            }

            var1 = -1;
        }

        switch(var1) {
            case 0:
                textView.setText(R.string.beginNewGame);

                database = dbHelper.getWritableDatabase();
                database.delete(DBHelper.TABLE_COOLDOWNS, null, null);
                dbHelper.close();

                intent.putExtra("Command", "ResetStats");
                fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            /*case 1:
                argsDialog.putString("type", "Psy");
                dialog.setArguments(argsDialog);
                dialog.show(requireActivity().getSupportFragmentManager(), "custom");
                intent.putExtra("Command", "SetPsyProtection00");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 2:
                intent.putExtra("Command", "SetPsyProtection50");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 3:
                intent.putExtra("Command", "SetPsyProtection100");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 4:
                intent.putExtra("Command", "SetRadProtection0");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 5:
                intent.putExtra("Command", "SetRadProtection50");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 6:
                intent.putExtra("Command", "SetRadProtection100");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 7:
                intent.putExtra("Command", "SetBioProtection0");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 8:
                intent.putExtra("Command", "SetBioProtection50");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 9:
                intent.putExtra("Command", "SetBioProtection100");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 10:
                intent.putExtra("Command", "SetDischargeImmunityTrue");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 11:
                intent.putExtra("Command", "SetDischargeImmunityFalse");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 12:
                intent.putExtra("Command", "SetMaxHealth100");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 13:
                intent.putExtra("Command", "SetMaxHealth200");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 14:
                intent.putExtra("Command", "MakeAlive");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 15:
                intent.putExtra("Command", "ComboResetProtections");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 16:
                intent.putExtra("Command", "Monolith");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 17:
                intent.putExtra("Command", "God");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 18:
                intent.putExtra("Command", "Discharge");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 19:
                intent.putExtra("Command", "SetGesProtection");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 20:
                intent.putExtra("Command", "SetGesProtectionOFF");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 21: //sc1
                //Log.d("wft", String.valueOf(textCodeSplitted));
                anomalyTypeChecker(textCodeSplitted[1]);
                intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 22: //sc2
                //Log.d("wft", String.valueOf(textCodeSplitted));
                intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 23:
                intent.putExtra("Command", "setOneProtAv");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 24:
                intent.putExtra("Command", "setTwoProtAv");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 25:
                intent.putExtra("Command", "setThreeProtAv");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 26:
                intent.putExtra("Command", "15minutesGod");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 27:
                intent.putExtra("Command", "noMoreGod");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 28:
                intent.putExtra("Command", "sc1, rad, suit, 80");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                intent.putExtra("Command", "sc1, bio, suit, 80");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 29:
                intent.putExtra("Command", "штраф");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 30:
                intent.putExtra("Command", "discharge10BD");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 31:
                intent.putExtra("Command", "dolgDischargeImmunity");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 32:
                txtView.setText("Если у тебя нет жетона Монолита, то ты становишься адептом Монолита. Выйди на связь с бойцами Монолита, чтобы получить задание. Задача по умолчанию: не допустить уничтожение Монолита.\nКоличество разрешенных защит увеличено на 1\n\nЕсли у тебя есть жетон, то с возвращением в семью, брат!");
                intent.putExtra("Command", "SetPsyProtection0");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 33:
                intent.putExtra("Command", "ScienceQR");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;
            case 34:
                intent.putExtra("Command", "ScienceQRoff");
                ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                break;*/
        }
        dbHelper.close();
    }

    public void checkCode(String code, boolean scienceQR){
        dbHelper = new DBHelper(fragment.requireActivity().getApplicationContext());
        Intent intent;
        int var2;
        label94:{
            intent = new Intent("Command");
            switch (code.hashCode()) {
                // шприца на минус рад био и плюс хп
                case 227556695:
                    if (code.equals("mpjvqlzkws")) {
                        var2 = 0;
                        break label94;
                    }
                case 1134924355:
                    if (code.equals("xrjoqykant")) {
                        var2 = 1;
                        break label94;
                    }
                case 1393505176:
                    if (code.equals("pjiscyunaf")) {
                        var2 = 2;
                        break label94;
                    }
            }

            var2 = -1;
        }
        switch(var2) {
            case 0:
                textAndCoolDown(intent, 90000, R.string.injectorRad, R.string.injectorRadSc, R.string.injectorRadDawn, "injectorRad", code, scienceQR);
                break;
            case 1:
                textAndCoolDown(intent, 90000, R.string.injectorBio, R.string.injectorBioSc, R.string.injectorBioDawn, "injectorBio", code, scienceQR);
                break;
            case 2:
                textAndCoolDown(intent, 90000, R.string.injectorHP, R.string.injectorHPsc, R.string.injectorHPdawn, "injectorHP", code, scienceQR);
                break;
        }
        dbHelper.close();
    }

    // взято из QRTab, используется для кодов с кулдаунами
    private void textAndCoolDown(Intent intent, int coolDawnMillis, int nonScience, int science,
                                 int txtCoolDawn, String command, String code, boolean scienceQR){

        database = dbHelper.getWritableDatabase();

        int id;
        cursor = database.rawQuery("select * from " + DBHelper.TABLE_COOLDOWNS + " where " + DBHelper.KEY_NAME_CD + "=?", new String[]{code});
        if (cursor.getCount() == 0){
            contentValues.put(DBHelper.KEY_NAME_CD, code);
            contentValues.put(DBHelper.KEY_TIME_CD, "0");
            database.insert(DBHelper.TABLE_COOLDOWNS, null, contentValues);
            cursor = database.rawQuery("select * from " + DBHelper.TABLE_COOLDOWNS + " where " + DBHelper.KEY_NAME_CD + "=?", new String[]{code});
        }
        cursor.moveToFirst();
        id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID_CD));

        cursor = database.query(DBHelper.TABLE_COOLDOWNS, null, null, null, null, null, null);
        cursor.moveToPosition(id-1);

        if (scienceQR){
            textView.setText(science);
        } else {
            firstTime = Calendar.getInstance().getTimeInMillis();
            if (firstTime - cursor.getLong(cursor.getColumnIndex(DBHelper.KEY_TIME_CD)) > coolDawnMillis){
                textView.setText(nonScience);
                intent.putExtra("Command", command);
                fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
                contentValues.put(DBHelper.KEY_TIME_CD, String.valueOf(firstTime));
                contentValues.put(DBHelper.KEY_NAME_CD, code);
                database.update(DBHelper.TABLE_COOLDOWNS, contentValues, DBHelper.KEY_NAME_CD + "= ?", new String[] {String.valueOf(code)});
            } else {
                textView.setText(txtCoolDawn);
            }
        }

        dbHelper.close();
        cursor.close();
    }



}
