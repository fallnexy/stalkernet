package com.example.stalkernet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.example.stalkernet.StatsService.INTENT_SERVICE;
import static com.example.stalkernet.StatsService.INTENT_SERVICE_QR;
import static com.example.stalkernet.StatsService.INTENT_SERVICE_USER_ID;
import static com.example.stalkernet.StatsService.LOG_CHE;
import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.ARTEFACT;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.QUEST;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.SUIT;

/*
 * Created by fallnexy on 16.09.2021.
 */
/*
* по задумке в этом классе должны лежать qr и текстовые коды для QRTab и ChatTab, а то они стали дублироваться
* */
public class CodesQRAndText {
    // общие переменные
    Globals globals;
    public Fragment fragment;
    public TextView textView;
    // переменные, необходимые для кулдаунов
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues = new ContentValues();
    Cursor cursor;
    // переменные, необходимые для sc1 и sc2
    String textCode;
    String[] textCodeSplitted = new String[10];

    public CodesQRAndText(Fragment fragment, TextView textView, Globals globals){
        this.fragment = fragment;
        this.textView = textView;
        this.globals = globals;
    }

    public void checkCode(String code, boolean scienceQR, boolean applyQR){
        dbHelper = new DBHelper(fragment.requireActivity().getApplicationContext());
        Intent intent = new Intent("Command");
        Intent intentNew = new Intent(INTENT_SERVICE);
        int var1;

        MakeSplit(code);

        /*
        * оределение юзера-игрока
        * */
        if (textCode.equals("sc4")){
            setUser(intentNew, textCodeSplitted);
        }

        if (textCode.equals("sc1") | textCode.equals("sc2") | textCode.equals("sc5")) {
            code = textCode;
        }

        //
        // ввод кода на добавление аномалии
        //
        if (textCode.equals("sc3") | textCode.equals("del")){
            code = textCode;
        }

        label94: {

            //intent = new Intent("Command");
            switch (code){
                case "мины":
                    intent.putExtra("Command", "mines");
                    fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
                    var1 = -1;
                    break label94;
                case "во славу монолита":
                    setUser(intentNew, new String[]{"sc4", "user", "96"});
                    var1 = -1;
                    break label94;
                case "пятнистый":
                    intent.putExtra("Command", "isMonolith");
                    fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
                    var1 = -1;
                    break label94;
                case "sc3": //sc3 и del
                case "del":
                    textView.setText(makeSCText(textCodeSplitted));
                    intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                    fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
                    Log.d("аномалии", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                    var1 = -1;
                    break label94;
                case "lcycuibllm":
                    textAndCoolDown(intent, 900000, R.string.rad_umenshit, R.string.empty, R.string.injectorRadDawn, "injectorRad85", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "cyavhqijyf":
                    textAndCoolDown(intent, 960000, R.string.bio_umenshit, R.string.empty, R.string.injectorRadDawn, "injectorBio85", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "frpugjpqxu":
                    textAndCoolDown(intent, 1020000, R.string.hp_uvelishit, R.string.empty, R.string.injectorRadDawn, "injectorHP50", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "приветбумеранг":
                case "жив":
                case "nuyzi2sg7y3vq5f":
                    database = dbHelper.getWritableDatabase();
                    database.delete(DBHelper.TABLE_COOLDOWNS, null, null);
                    dbHelper.close();
                    simpleSendMessageAndText(intent, R.string.beginNewGame, "ResetStats");
                    var1 = -1;
                    break label94;
                case "гагры":
                    simpleSendMessageAndText(intent, R.string.beginNewGame, "MakeAlive");
                    var1 = -1;
                    break label94;
                case "mpjvqlzkws":
                    textAndCoolDown(intent, 90000, R.string.injectorRad, R.string.injectorRadSc, R.string.injectorRadDawn, "injectorRad", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "xrjoqykant":
                    textAndCoolDown(intent, 90000, R.string.injectorBio, R.string.injectorBioSc, R.string.injectorBioDawn, "injectorBio", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "pjiscyunaf":
                    textAndCoolDown(intent, 90000, R.string.injectorHP, R.string.injectorHPsc, R.string.injectorHPdawn, "injectorHP", code, scienceQR);
                    var1 = -1;
                    break label94;
                case "sc1":
                case "sc2":
                    textView.setText(makeSCText(textCodeSplitted));
                    intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                    fragment.requireActivity().getApplicationContext().sendBroadcast(intent);

                    var1 = -1;
                    break label94;
                case "sc5":
                    if (!applyQR && !scienceQR) {
                        textView.setText(makeSCText(textCodeSplitted));
                        intentNew.putExtra(INTENT_SERVICE_QR, Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                        fragment.requireActivity().getApplicationContext().sendBroadcast(intentNew);
                    }
                    var1 = -1;
                    break label94;
                case "зона5звезд":
                    simpleSendMessageAndText(intent, R.string.setDischargeImmunityTrue, "SetDischargeImmunityTrue");
                    var1 = -1;
                    break label94;
                case "доставщик":
                    simpleSendMessageAndText(intent, R.string.setDischargeImmunityFalse, "SetDischargeImmunityFalse");
                    var1 = -1;
                    break label94;
                case "505050":
                    simpleSendMessageAndText(intent, R.string.resetAllProtections, "ComboResetProtections");
                    var1 = -1;
                    break label94;
                case "выходигрока":
                    simpleSendMessageAndText(intent, R.string.setNewBeginImmunity, "15minutesGod");
                    var1 = -1;
                    break label94;
                case "снятьнеуяз":
                    simpleSendMessageAndText(intent, R.string.dropNewBeginImmunity, "noMoreGod");
                    var1 = -1;
                    break label94;
                case "всегдазакрыт":
                    simpleSendMessageAndText(intent, R.string.ges_available_off, "SetGesProtection");
                    var1 = -1;
                    break label94;
                case "теперьоткрыт":
                    simpleSendMessageAndText(intent, R.string.ges_available_on, "SetGesProtectionOFF");
                    var1 = -1;
                    break label94;
            }

            switch(code.hashCode()) {
                /*
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



            /*
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
            case 31:
                intent.putExtra("Command", "dolgDischargeImmunity");
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

    // простая функция, которая передает текст в textView и отправляет intent
    private void simpleSendMessageAndText(Intent intent, int text, String command){
        textView.setText(text);
        intent.putExtra("Command", command);
        fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
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
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID_CD);
        id = cursor.getInt(idIndex);

        cursor = database.query(DBHelper.TABLE_COOLDOWNS, null, null, null, null, null, null);
        cursor.moveToPosition(id-1);

        if (scienceQR){
            textView.setText(science);
        } else {
            long firstTime = Calendar.getInstance().getTimeInMillis();
            int timeIndex = cursor.getColumnIndex(DBHelper.KEY_TIME_CD);
            if (firstTime - cursor.getLong(timeIndex) > coolDawnMillis){
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

    // взято из ChatTab необходимо для sc1 и sc2
    private void MakeSplit(String input){
        try {
            Pattern pattern = Pattern.compile("[@]");
            String[] words = pattern.split(input);
            int i = 0;
            for(String word:words){
                textCodeSplitted[i] = word;
                i++;
            }
            if (textCodeSplitted[0].equals("sc1")) {
                if (textCodeSplitted[2].equals(SUIT) && Double.parseDouble(textCodeSplitted[3]) > 80){
                    textCodeSplitted[3] = "80";
                } else if (!textCodeSplitted[2].equals(SUIT) && !textCodeSplitted[2].equals("tot") && Double.parseDouble(textCodeSplitted[3]) > 49.95){
                    textCodeSplitted[3] = "49.95";
                }
                if (Double.parseDouble(textCodeSplitted[3]) < 0){
                    textCodeSplitted[3] = "0";
                }
                if (textCodeSplitted[2].equals("tot") && Double.parseDouble(textCodeSplitted[3]) > 100){
                    textCodeSplitted[3] = "100";
                }
            }
            if (textCodeSplitted[0].equals("sc3")){
                if (Integer.parseInt(textCodeSplitted[3]) > 5){
                    textCodeSplitted[3] = "5";
                }
                if (Integer.parseInt(textCodeSplitted[4]) > 5){
                    textCodeSplitted[3] = "5";
                }
                if ((Integer.parseInt(textCodeSplitted[3]) + Integer.parseInt(textCodeSplitted[4])) > 6){
                    textCodeSplitted[4] = String.valueOf(6 - Integer.parseInt(textCodeSplitted[3]));
                }
                if (Integer.parseInt(textCodeSplitted[5]) > 5){
                    textCodeSplitted[3] = "5";
                }
            }
            textCode = textCodeSplitted[0];
        } catch (Exception e) {
            textCode = input;
        }
    }

    private String makeSCText(@NonNull String[] text){
        String textFinal = "";
        if (text[0].equals("sc1")){
            textFinal = "Установлено: уровень защиты от ";
            switch (text[1]){
                case RAD:
                    textFinal += "рад - ";
                    break;
                case BIO:
                    textFinal += "био -  ";
                    break;
                case PSY:
                    textFinal += "пси - ";
                    break;
                default:
                    textFinal += "ничего - ";
            }
            switch (text[2]){
                case SUIT:
                    textFinal += "костюм: ";
                    break;
                case ARTEFACT:
                    textFinal += "артефакт: ";
                    break;
                case QUEST:
                    textFinal += "квест: ";
                    break;
                default:
                    textFinal += "ничто: ";
            }
            textFinal += text[3] + "%";
        } else if (text[0].equals("sc2")){
            textFinal = "Установлено: ";
            switch (text[1]){
                case RAD:
                    textFinal += "степень радиационного заражения изменена на ";
                    break;
                case BIO:
                    textFinal += "степень биологического заражения изменена на ";
                    break;
                case "hp":
                    textFinal += "жизненные показатели пользователя изменены на ";
                    break;
                default:
                    textFinal += "ничего не изменено ";
            }
            textFinal += text[2] + "%";

        } else  if (text[0].equals("sc3")){
            textFinal = "Установлена аномалия в слот № " + text[5] + " с силой " + text[3] + " и радиусом " + (150 + 20 * Integer.parseInt(text[4]) + "м");
        } else if (text[0].equals("del")){
            textFinal = "Удалена аномалия из слота № " + text[5];
        } else if (text[0].equals("sc5")){
            textFinal = "улучшение возможности применения артефакта";
            Log.d(LOG_CHE, "текст = " + textFinal);
        }

        return textFinal;
    }

    /*
    * определение юзера-игрока
    * */
    private void setUser(Intent intent, @NonNull String[] text){
        String toastText = "";
        switch (text[1]){
            case "user":
                toastText = "Выполнен вход пользователя №" + text[2];
                textView.setText(toastText);

                intent.putExtra(INTENT_SERVICE_USER_ID, text[2]);
                break;
            case "qr":
                if (text[2].equals("sci")){
                    if (text[3].equals("on")){
                        toastText = "Установлено соединение с сервером ВНИИ ЧЗО \\\"Salus\\\".";
                    } else if (text[3].equals("off")){
                        toastText = "Cоединение с сервером ВНИИ ЧЗО \\\"Salus\\\" разорвано.";
                    } else{
                        toastText = "ERROR";
                    }

                } else if (text[2].equals("app")){
                    if (text[3].equals("on")){
                        toastText = "Апгрейд установлен: можно применять артефакты.";
                    } else if (text[3].equals("off")){
                        toastText = "Произошел откат апгрейда: применение артефактов невозможно";
                    } else{
                        toastText = "ERROR";
                    }

                }
                intent.putExtra(INTENT_SERVICE_QR, Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                break;
        }

        textView.setText(toastText);
        fragment.requireActivity().getApplicationContext().sendBroadcast(intent);
    }
}
