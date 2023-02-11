package com.example.myapplication2;
// необходимо задавать число аномалий и зон безопасности

//необходимо сохранять gesstatus

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class StatsService extends Service {
    // создает экземпляр персонажа игрока
    public PlayerCharacter playerCharacter;

    private static final int ID_SERVICE = 101;
    public int NUMBER_OF_ANOMALIES = 0; // задается в onCreate
    private static final int NUMBER_OF_GESTALT_ANOMALIES = 2;
    private static final int NUMBER_OF_SAVE_ZONES = 5;
    private boolean IS_ANOMALIES_AVAILABLE = true;
    public Anomaly[] anomalies;
    public SafeZone[] SafeZones;
    public EffectManager EM;
    public double Health = 2000.0d, MaxHealth = 2000.0d;
    public double Bio = 0.0d, Psy = 0.0d, Rad = 0.0d;
    public int MaxRad = 1000, MaxBio = 1000;

    public int MaxProtectionsAvailable = 1;

    public double[] ProtectionCapacity = {100000, 2000, 2000};

    public double[] RadProtectionArr = {0, 0, 0};
    public double[] BioProtectionArr = {0, 0, 0};
    public double[] PsyProtectionArr = {0, 0, 0};

    public double[] RadProtectionCapacityArr = {0, 0, 0};
    public double[] BioProtectionCapacityArr = {0, 0, 0};
    public double[] PsyProtectionCapacityArr = {0, 0, 0};

    public double[] MaxRadProtectionCapacityArr = {0, 0, 0};
    public double[] MaxBioProtectionCapacityArr = {0, 0, 0};
    public double[] MaxPsyProtectionCapacityArr = {0, 0, 0};

    HashMap<String, double[]> protectionMap = new HashMap<>();
    HashMap<String, double[]> protectionCapacityMap = new HashMap<>();
    HashMap<String, double[]> protectionCapacityMaxMap = new HashMap<>();

    int RadProtectionTot = 0, BioProtectionTot = 0, PsyProtectionTot = 0;

    public boolean DischargeImmunity = false;
    public Boolean IsDead = Boolean.FALSE;
    public Boolean IsDischarging = Boolean.FALSE;
    public Boolean IsInsideAnomaly = Boolean.FALSE;
    public Boolean IsInsideSafeZone = Boolean.TRUE;
    public Boolean IsUnlocked = Boolean.TRUE;
    public Date LastTimeChanged;
    public String LastTimeHitBy = "";
    private MyLocationCallback LocationCallback;
    private boolean LocationUpdatesStarted = false;
    public Location MyCurrentLocation = new Location("GPS");
    public String TypeAnomalyIn = "";
    public Boolean Vibrate = Boolean.TRUE;
    public Boolean Sound = Boolean.TRUE;
    public int ScienceQR = 0;// не работает

    public int gesStatus;
    public int[] gesLockoutList = {0, 0, 0, 0, 0, 0};
    public boolean GestaltProtection = false;
    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    private Calendar cal = Calendar.getInstance();
    private int Hour = cal.get(Calendar.HOUR_OF_DAY);
    private int Minutes = cal.get(12);
    private int dayInt = cal.get(5);

    private Random random = new Random();

    long dayFirst = 1662991200; //1662991200// с 12 сентября в 17:00
    long daySecond = 1663058160; //1663058160 // c 13 сентября в 11:36
    long dayThird = 1663158000; //1663158000 //c 14 сентября в 15:20
    long dayFourth = 1663230000; //1663230000 //c 15 сентября в 11:20


    public long[] coolDawn = new long[25];


    // защита от пси у монолита
    boolean MonolithOk = false;
    //новый монолит
    boolean isMonolith = false;

    public boolean fastRadPurification = false;

    long checkTime_in = 1620988200;  // 14 мая 13:30 // 1620988200
    long checkTime_out = 1621167600;  // 16 мая 15:20  // 1621167600

    private FusedLocationProviderClient mFusedLocationProvider;
    private PowerManager.WakeLock wl;
    
    public double TotalProtection(double[] protectionType){
        double i = 1;
        if (protectionType[1] > 47 || protectionType[2] > 47){
            if (protectionType[0] == 80) {
                i = 1.8;
            } else {
                i = 1.9;
            }
        }
        double suitPlusArt = (protectionType[0] + protectionType[1] * i) / (1 + (protectionType[0] * protectionType[1] * i) / 10000);
        return (suitPlusArt + protectionType[2] * i) / (1 + (suitPlusArt * protectionType[2] * i) / 10000);
    }

    // для умных команд вида sc1@rad@suit@20
    public String textCode;
    public String[] textCodeSplitted = new String[6];
    public void MakeSplit(String input){
        try {
            Pattern pattern = Pattern.compile(",\\s");
            String[] words = pattern.split(input);
            int i = 0;
            for(String word:words){
                textCodeSplitted[i] = word;
                i++;
            }
            textCode = textCodeSplitted[0];
        } catch (Exception e) {
            textCode = input;
        }
    }


    BroadcastReceiver broadcastReceiverQR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String var4 = intent.getStringExtra("StRoulette");
            // значение изменений увеличены для теста, кроме последних трех
            switch (var4){
                case "RadPlusOne":
                    anomalies[NUMBER_OF_ANOMALIES].Apply();
                    break;
                case "BioPlusOne":
                    anomalies[NUMBER_OF_ANOMALIES + 1].Apply();
                    break;
                case "PsyPlusOne":
                    anomalies[NUMBER_OF_ANOMALIES + 2].Apply();
                    break;
                case "HpPlusFive":
                    Health += 100;
                    if (Health > MaxHealth){
                        Health = MaxHealth;
                    }
                    break;
                case "HpPlusSeven":
                    Health += 200;
                    if (Health > MaxHealth){
                        Health = MaxHealth;
                    }
                    break;
                case "HpMinus25perCent":
                    Health -= 0.25 * Health;
                    if(Health < 1){
                        Health = 1;
                    }
                    break;
                case "HpMinus20perCent":
                    Health -= 0.2 * Health;
                    if(Health < 1){
                        Health = 1;
                    }
                    break;
                case "HpMinus10perCent":
                    Health -= 0.1 * Health;
                    if(Health < 1){
                        Health = 1;
                    }
                    break;
            }
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
       public void onReceive(android.content.Context context, Intent intent) {
//принимает команды command
           int var3;
           HashMap<String, String> protectionRewhriteMap = new HashMap<>();
           HashMap<String, Integer> protectionSuitRewhriteMap = new HashMap<>();
           protectionRewhriteMap.put("rad", "Rad");
           protectionRewhriteMap.put("bio", "Bio");
           protectionRewhriteMap.put("psy", "Psy");
           protectionSuitRewhriteMap.put("suit", 0);
           protectionSuitRewhriteMap.put("art", 1);
           protectionSuitRewhriteMap.put("quest", 2);
           label110: {
               String var4 = intent.getStringExtra("Command");
               MakeSplit(var4);
               Log.d("wtf_textCode", var4);
               if (textCode.equals("sc1") | textCode.equals("sc2") | textCode.equals("sc3") | textCode.equals("del")) {
                   var4 = textCode;
               }
               Log.d("wtf_textCode", var4);
               Toast.makeText(StatsService.this.getApplicationContext(), var4, Toast.LENGTH_LONG).show();
               ContentValues contentValues;
               int id;
               switch (var4){
                   case "isMonolith":
                       isMonolith = true;
                       DischargeImmunity = true;
                       var3 = -1;
                       break label110;
                   case "sc3":
                       int dRadius = 150 + 20 * Integer.parseInt(textCodeSplitted[4]);
                       int dPower = 1 + 2 * Integer.parseInt(textCodeSplitted[3]);
                       id = Integer.parseInt(textCodeSplitted[5]);

                       database = dbHelper.open();
                       contentValues = new ContentValues();

                       contentValues.put(DBHelper.KEY_LATITUDE_ANOMALY, textCodeSplitted[1]);
                       contentValues.put(DBHelper.KEY_LONGITUDE_ANOMALY, textCodeSplitted[2]);
                       contentValues.put(DBHelper.KEY_POWER, String.valueOf(dPower));
                       contentValues.put(DBHelper.KEY_RADIUS, String.valueOf(dRadius));
                       int slot = NUMBER_OF_ANOMALIES - 5 + id;
                       Log.d("аномалии", String.valueOf(slot));
                       database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID_ANOMALY + "=?", new String[]{String.valueOf(slot)});


                       dbHelper.close();

                       var3 = -1;
                       break label110;
                   case "del":
                       database = dbHelper.open();
                       id = Integer.parseInt(textCodeSplitted[5]);
                       contentValues = new ContentValues();
                       contentValues.put(DBHelper.KEY_LATITUDE_ANOMALY, 0);
                       contentValues.put(DBHelper.KEY_LONGITUDE_ANOMALY, 0);
                       database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID_ANOMALY + "=" + (NUMBER_OF_ANOMALIES - 5 + id), null);
                       dbHelper.close();
                       var3 = -1;
                       break label110;
                   case "injectorRad85":
                       Rad -= 0.85 * Rad;
                       var3 = -1;
                       break label110;
                   case "injectorBio85":
                       Bio -= 0.85 * Bio;
                       var3 = -1;
                       break label110;
                   case "injectorHP50":
                       playerCharacter.setHealth(playerCharacter.getHealth() + 0.5 * playerCharacter.getMaxHealth());
                       var3 = -1;
                       break label110;
               }
               switch(var4.hashCode()) {
                   case 305958064:
                       if (var4.equals("ResetStats")) {
                           var3 = 0;
                           break label110;
                       }
                       break;
                   case 1796409274:
                       if (var4.equals("SetDischargeImmunityTrue")) {
                           var3 = 10;
                           break label110;
                       }
                       break;
                   case -159331209:
                       if (var4.equals("SetDischargeImmunityFalse")) {
                           var3 = 11;
                           break label110;
                       }
                       break;
                   case 146202227:
                       if (var4.equals("SetMaxHealth100")) {
                           var3 = 12;
                           break label110;
                       }
                       break;
                   case 146203188:
                       if (var4.equals("SetMaxHealth200")) {
                           var3 = 13;
                           break label110;
                       }
                       break;
                   case -1052620961:
                       if (var4.equals("MakeAlive")) {
                           var3 = 14;
                           break label110;
                       }
                       break;
                   case -2120901255:
                       if (var4.equals("ComboResetProtections")) {
                           var3 = 15;
                           break label110;
                       }
                       break;
                   case -258857420:
                       if (var4.equals("Monolith")) {
                           var3 = 16;
                           break label110;
                       }
                       break;
                   case 71772:
                       if (var4.equals("God")) {
                           var3 = 17;
                           break label110;
                       }
                       break;
                   case -1756574876:
                       if (var4.equals("Unlock")) {
                           var3 = 18;
                           break label110;
                       }
                       break;
                   case 565354622:
                       if (var4.equals("Monolith2")) {
                           var3 = 19;
                           break label110;
                       }
                       break;
                   case 76321168:
                       if (var4.equals("OnVib")) {
                           var3 = 20;
                           break label110;
                       }
                       break;
                   case -219690867:
                       if (var4.equals("StopVib")) {
                           var3 = 21;
                           break label110;
                       }
                       break;
                   case 1875682466:
                       if (var4.equals("Discharge")) {
                           var3 = 22;
                           break label110;
                       }
                       break;
                       // новые коды далее
                   case -1555514523:
                       if (var4.equals("ScienceQR")) {
                           var3 = 23;
                           break label110;
                       }
                       break;
                   case -1930888214:
                       if (var4.equals("ScienceQRoff")) {
                           var3 = 24;
                           break label110;
                       }
                       break;
                   case 1704779201:
                       if (var4.equals("gestalt_closed")) {
                           var3 = 25;
                           break label110;
                       }
                       break;
                   case 1910275380:
                       if (var4.equals("gestalt_closed_2")) {
                           var3 = 26;
                           break label110;
                       }
                       break;
                   case 317294316:
                       if (var4.equals("SetGesProtection")) {
                           var3 = 27;
                           break label110;
                       }
                       break;
                   case -707972381:
                       if (var4.equals("SetGesProtectionOFF")) {
                           var3 = 28;
                           break label110;
                       }
                       break;
                   case 1831428070: // снять неуяз к аномалиям и выбросу
                       if (var4.equals("noMoreGod")) {
                           var3 = 29;
                           break label110;
                       }
                       break;
                   case -1151237055:
                       if (var4.equals("15minutesGod")) {
                           var3 = 30;
                           break label110;
                       }
                       break;
                   case -1428886463:
                       // этот и следующие 2 - шприцы от рад и био и хп
                       if (var4.equals("injectorRad")) {
                           var3 = 31;
                           break label110;
                       }
                       break;
                   case -1428901580:
                       if (var4.equals("injectorBio")) {
                           var3 = 32;
                           break label110;
                       }
                       break;
                   case 2032116540:
                       if (var4.equals("injectorHP")) {
                           var3 = 33;
                           break label110;
                       }
                       break;
                   case -1745372224:
                       if (var4.equals("nullifyRad")) {
                           var3 = 34;
                           break label110;
                       }
                       break;
                   case -1745387341:
                       if (var4.equals("nullifyBio")) {
                           var3 = 35;
                           break label110;
                       }
                       break;
                   case -1745373567:
                       if (var4.equals("nullifyPsy")) {
                           var3 = 36;
                           break label110;
                       }
                       break;
                   case 1144354095:
                       if (var4.equals("artCompass")) {
                           var3 = 37;
                           break label110;
                       }
                       break;
                   case 1045731098:
                       if (var4.equals("штраф")) {
                           var3 = 38;
                           break label110;
                       }
                       break;
                   /*case -1357835385:
                       if (var4.equals("clear4")) {
                           var3 = 39;
                           break label110;
                       }
                       break;*/
                   /*case -1107434950:
                       if (var4.equals("health75")) {
                           var3 = 40;
                           break label110;
                       }
                       break;
                   case 29249045:
                       if (var4.equals("health100")) {
                           var3 = 41;
                           break label110;
                       }
                       break;*/
                   case -1241360960: // этот и следующие 2 - количество разешенных защит
                       if (var4.equals("setOneProtAv")) {
                           var3 = 42;
                           break label110;
                       }
                       break;
                   case 1396794662:
                       if (var4.equals("setTwoProtAv")) {
                           var3 = 43;
                           break label110;
                       }
                       break;
                   case -1781101704:
                       if (var4.equals("setThreeProtAv")) {
                           var3 = 44;
                           break label110;
                       }
                       break;
                   case 1071529183:
                       if (var4.equals("bioProt10030")) {
                           var3 = 45;
                           break label110;
                       }
                       break;
                   case 1071529276:
                       if (var4.equals("bioProt10060")) {
                           var3 = 46;
                           break label110;
                       }
                       break;
                   case 1071529369:
                       if (var4.equals("bioProt10090")) {
                           var3 = 47;
                           break label110;
                       }
                       break;
                   case 123907793:
                       if (var4.equals("psyProt10030")) {
                           var3 = 48;
                           break label110;
                       }
                       break;
                   case 123907886:
                       if (var4.equals("psyProt10060")) {
                           var3 = 49;
                           break label110;
                       }
                       break;
                   case 123907979:
                       if (var4.equals("psyProt10090")) {
                           var3 = 50;
                           break label110;
                       }
                       break;
                   case -1975691119:
                       if (var4.equals("discharge10Sc")) {
                           var3 = 51;
                           break label110;
                       }
                       break;
                   case -1975691677: // защита от выброса на10 минут
                       if (var4.equals("discharge10BD")) {
                           var3 = 51;
                           break label110;
                       }
                       break;
                   case 1271685827:
                       if (var4.equals("discharge45")) {
                           var3 = 52;
                           break label110;
                       }
                       break;
                   case -16716590: // плюс жизни от болотного доктора
                       if (var4.equals("BDplus2Health")) {
                           var3 = 53;
                           break label110;
                       }
                       break;
                   case -1649172843:
                       if (var4.equals("BDplus5Health")) {
                           var3 = 54;
                           break label110;
                       }
                       break;
                   case 1381804599:
                       if (var4.equals("BDplus10Health")) {
                           var3 = 55;
                           break label110;
                       }
                       break;
                   case 1036792636:
                       if (var4.equals("BDplus45HealthRandom")) {
                           var3 = 56;
                           break label110;
                       }
                       break;
                   case -944954941:
                       if (var4.equals("BDminus5Health")) {
                           var3 = 57;
                           break label110;
                       }
                       break;
                   case 804709100:
                       if (var4.equals("BDminus10HealthRandom")) {
                           var3 = 58;
                           break label110;
                       }
                       break;
                   case 5747468:
                       if (var4.equals("BDminus21HealthRandom")) {
                           var3 = 59;
                           break label110;
                       }
                       break;
                   case 1323666026:
                       if (var4.equals("BDprotectionBio6025")) {
                           var3 = 60;
                           break label110;
                       }
                       break;
                   case 1323666057:
                       if (var4.equals("BDprotectionBio6035")) {
                           var3 = 61;
                           break label110;
                       }
                       break;
                   case -1895336201:
                       if (var4.equals("BDprotectionRad6025")) {
                           var3 = 62;
                           break label110;
                       }
                       break;
                   case -1895336170:
                       if (var4.equals("BDprotectionRad6035")) {
                           var3 = 63;
                           break label110;
                       }
                       break;
                   case 1159342392:
                       if (var4.equals("BDprotectionPsy6025")) {
                           var3 = 64;
                           break label110;
                       }
                       break;
                   case 735430818:
                       if (var4.equals("BDprotectionBio120")) {
                           var3 = 65;
                           break label110;
                       }
                       break;
                   case 1185781365:
                       if (var4.equals("BDprotectionRad120")) {
                           var3 = 66;
                           break label110;
                       }
                       break;
                   case 1145772052:
                       if (var4.equals("BDprotectionPsy120")) {
                           var3 = 67;
                           break label110;
                       }
                       break;
                   case -1167097637:
                       if (var4.equals("setRadOn80Percent")) {
                           var3 = 68;
                           break label110;
                       }
                       break;
                   case 1699558920:
                       if (var4.equals("setBioOn80Percent")) {
                           var3 = 69;
                           break label110;
                       }
                       break;
                   case -1658045336:
                       if (var4.equals("mechMinus60Rad")) {
                           var3 = 72;
                           break label110;
                       }
                       break;
                   case -1658060453:
                       if (var4.equals("mechMinus60Bio")) {
                           var3 = 73;
                           break label110;
                       }
                       break;
                   case -232827188:
                       if (var4.equals("mechPlus70Health")) {
                           var3 = 74;
                           break label110;
                       }
                       break;
                   case 1984920125:
                       if (var4.equals("setRad0")) {
                           var3 = 75;
                           break label110;
                       }
                       break;
                   case 1388454378:
                       if (var4.equals("setBio15")) {
                           var3 = 76;
                           break label110;
                       }
                       break;
                   case 1984451498:
                       if (var4.equals("setBio0")) {
                           var3 = 77;
                           break label110;
                       }
                       break;
                   case 1784296673:
                       if (var4.equals("minus15Rad")) {
                           var3 = 78;
                           break label110;
                       }
                       break;
                   case -1523616740:
                       if (var4.equals("plus10Rad")) {
                           var3 = 80;
                           break label110;
                       }
                       break;
                   case -1523631857:
                       if (var4.equals("plus10Bio")) {
                           var3 = 81;
                           break label110;
                       }
                       break;
                   case 1784281556:
                       if (var4.equals("minus15Bio")) {
                           var3 = 83;
                           break label110;
                       }
                       break;
                   case 113633: //sc1
                       if (var4.equals("sc1")) {
                           var3 = 85;
                           break label110;
                       }
                       break;
                   case 113634: //sc2
                       if (var4.equals("sc2")) {
                           var3 = 86;
                           break label110;
                       }
                       break;
                   case 1910275381:
                       if (var4.equals("gestalt_closed_3")) {
                           var3 = 87;
                           break label110;
                       }
                       break;
                   case 1910275382:
                       if (var4.equals("gestalt_closed_4")) {
                           var3 = 88;
                           break label110;
                       }
                       break;
                   case 283987183:
                       if (var4.equals("art_oasis")) {
                           var3 = 89;
                           break label110;
                       }
                       break;
                       //здесь и далее глеб
                   case -982210431:
                       if (var4.equals("radiation")) {
                           var3 = 90;
                           break label110;
                       }
                       break;
                   case -383752240:
                       if (var4.equals("radiation1")) {
                           var3 = 91;
                           break label110;
                       }
                       break;
                   case -383752239:
                       if (var4.equals("radiation2")) {
                           var3 = 92;
                           break label110;
                       }
                       break;
                   case -383752238:
                       if (var4.equals("radiation3")) {
                           var3 = 93;
                           break label110;
                       }
                       break;
                   case 74018586:
                       if (var4.equals("biohazard")) {
                           var3 = 94;
                           break label110;
                       }
                       break;
                   case -2000391081:
                       if (var4.equals("biohazard1")) {
                           var3 = 95;
                           break label110;
                       }
                       break;
                   case -2000391080:
                       if (var4.equals("biohazard2")) {
                           var3 = 96;
                           break label110;
                       }
                       break;
                   case -2000391079:
                       if (var4.equals("biohazard3")) {
                           var3 = 97;
                           break label110;
                       }
                       break;
                   case -1221262756:
                       if (var4.equals("health")) {
                           var3 = 98;
                           break label110;
                       }
                       break;
                   case 795560277:
                       if (var4.equals("health1")) {
                           var3 = 99;
                           break label110;
                       }
                       break;
                   case 795560278:
                       if (var4.equals("health2")) {
                           var3 = 100;
                           break label110;
                       }
                       break;
                   case 795560279:
                       if (var4.equals("health3")) {
                           var3 = 101;
                           break label110;
                       }
                       break;
               }

               var3 = -1;
           }

           Intent intent1;
           int j;
           switch(var3) {
               case 0:
                   playerCharacter.setHealth(playerCharacter.getMaxHealth());
                   Health = 2000.0D;
                   MaxHealth = 2000.0D;
                   Rad = 0.0D;
                   Bio = 0.0D;
                   Psy = 0.0D;
                   Arrays.fill(RadProtectionArr, 0);
                   Arrays.fill(BioProtectionArr, 0);
                   Arrays.fill(PsyProtectionArr, 0);
                   Arrays.fill(RadProtectionCapacityArr, 0);
                   Arrays.fill(BioProtectionCapacityArr, 0);
                   Arrays.fill(PsyProtectionCapacityArr, 0);
                   Arrays.fill(MaxRadProtectionCapacityArr, 0);
                   Arrays.fill(MaxBioProtectionCapacityArr, 0);
                   Arrays.fill(MaxPsyProtectionCapacityArr, 0);
                   ScienceQR = 0; // больше не ученый
                   GestaltProtection = false;
                   for (int i = 0; i < NUMBER_OF_GESTALT_ANOMALIES; i++){
                       anomalies[i].gesStatus = 1;
                   }   // 1 - гештальт закрыт
                   DischargeImmunity = false;
                   playerCharacter.setDead(false);
                   intent1 = new Intent("StatsService.HealthUpdate");
                   intent1.putExtra("Health", "2000");
                   sendBroadcast(intent1);
                   intent1 = new Intent("StatsService.Message");
                   intent1.putExtra("Message", "A");
                   sendBroadcast(intent1);
                   break;
               case 10: // вкл выкл иммунитет от выбросов
                   DischargeImmunity = !DischargeImmunity;
                   break;
               case 11:
                   DischargeImmunity = false;
                   break;
               case 12:
                   MaxHealth = 2000.0D;
                   intent1 = new Intent("StatsService.HealthUpdate");
                   intent1.putExtra("Health", "2000");
                   sendBroadcast(intent1);
                   break;
               case 13:
                   MaxHealth = 3000.0D;
                   intent1 = new Intent("StatsService.HealthUpdate");
                   intent1.putExtra("Health", "3000");
                   sendBroadcast(intent1);
                   break;
               case 14:  //MakeAlive
                   playerCharacter.setHealth(playerCharacter.getMaxHealth());
                   Health = MaxHealth;
                   Rad = 0.0D;
                   Bio = 0.0D;
                   Psy = 0.0D;
                   RadProtectionArr[1] = 0;
                   RadProtectionArr[2] = 0;
                   BioProtectionArr[1] = 0;
                   BioProtectionArr[2] = 0;
                   PsyProtectionArr[1] = 0;
                   PsyProtectionArr[2] = 0;
                   MaxRadProtectionCapacityArr[1] = 0;
                   MaxRadProtectionCapacityArr[2] = 0;
                   MaxBioProtectionCapacityArr[1] = 0;
                   MaxBioProtectionCapacityArr[2] = 0;
                   MaxPsyProtectionCapacityArr[1] = 0;
                   MaxPsyProtectionCapacityArr[2] = 0;
                   Arrays.fill(RadProtectionCapacityArr, 0);
                   Arrays.fill(BioProtectionCapacityArr, 0);
                   Arrays.fill(PsyProtectionCapacityArr, 0);
                   playerCharacter.setDead(false);
                   break;
               case 15: //ComboResetProtections
                   Arrays.fill(RadProtectionArr, 0);
                   Arrays.fill(BioProtectionArr, 0);
                   Arrays.fill(PsyProtectionArr, 0);
                   Arrays.fill(MaxRadProtectionCapacityArr, 0);
                   Arrays.fill(MaxBioProtectionCapacityArr, 0);
                   Arrays.fill(MaxPsyProtectionCapacityArr, 0);
                   Arrays.fill(RadProtectionCapacityArr, 0);
                   Arrays.fill(BioProtectionCapacityArr, 0);
                   Arrays.fill(PsyProtectionCapacityArr, 0);
                   break;
               case 16: //monolith
                   /*DischargeImmunity = true;
                   //RadTotalProtection = 50;
                   BioQuestProtection = 50;
                   PsyQuestProtection = 100;
                   GestaltProtection = true;*/
                   break;
               case 17: //god
                   DischargeImmunity = true;
                   IS_ANOMALIES_AVAILABLE = false;
                   break;
               case 18:
                   IsUnlocked = true;
                   break;
               case 19://Monolith2 - аномалия, которая лечит, но только монолит, точнее тех, у кого иммунитет к выбросам
                   if (DischargeImmunity) {
                       Health = 2000.0D;
                       Bio = 0.0D;
                       Rad = 0.0D;
                       Arrays.fill(RadProtectionCapacityArr, 0);
                       Arrays.fill(BioProtectionCapacityArr, 0);
                       Arrays.fill(PsyProtectionCapacityArr, 0);
                   }
                   break;
               case 20:
                   Vibrate = true;
                   break;
               case 21:
                   Vibrate = false;
                   break;
               case 22:
                   Discharge();
                   break;
                   //новые коды
               case 23:
                   ScienceQR = 1;
                   break;
               case 24:
                   ScienceQR = 0;
                   break;
               case 25:
                   int g = 0;

                   database = dbHelper.open();
                   ContentValues contentValues;
                   contentValues = new ContentValues();
                   contentValues.put(DBHelper.KEY_GESTALT_STATUS, "1");
                   database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID_ANOMALY + "=?", new String[]{String.valueOf(g + 1)});
                   database.close();

                   anomalies[g].gesStatus = 1;
                   gesLockoutList[g] = 1;
                   GestaltLockout(g);
                   break;
               case 26:
                   int g1 = 1;
                   anomalies[g1].gesStatus = 1;
                   gesLockoutList[g1] = 1;
                   GestaltLockout(g1);
                   break;
               case 27:
                   GestaltProtection = true;
                   break;
               case 28:
                   GestaltProtection = false;
                   break;
               case 29:
                   IS_ANOMALIES_AVAILABLE = true;
                   DischargeImmunity = false;
                   break;
               case 30:// 60 минутный режим бога, чтобы до базы дойти
                   boolean iaaTemp = IS_ANOMALIES_AVAILABLE;
                   boolean DI = DischargeImmunity;
                   IS_ANOMALIES_AVAILABLE = false;
                   DischargeImmunity = true;
                   Handler handler2 = new Handler();
                   handler2.postDelayed(() -> {
                       IS_ANOMALIES_AVAILABLE = iaaTemp;
                       DischargeImmunity = DI;
                       EM.PlaySound("Start", 1);
                   }, 3600000);
                   break;
               case 31: // этот и следующие 2 - шприцы от рад и био
                   setHealthBy_BD("Rad", -75, true, 9);
                   break;
               case 32:
                   setHealthBy_BD("Bio", -75, true, 9);
                   break;
               case 33:
                   setHealthBy_BD("Health", 40, false, 0);
                   break;
               case 34: // этот и два следующих - обнуление защит
                   Arrays.fill(RadProtectionArr, 0);
                   Arrays.fill(MaxRadProtectionCapacityArr, 0);
                   break;
               case 35:
                   Arrays.fill(BioProtectionArr, 0);
                   Arrays.fill(MaxBioProtectionCapacityArr, 0);
                   break;
               case 36:
                   Arrays.fill(PsyProtectionArr, 0);
                   Arrays.fill(MaxPsyProtectionCapacityArr, 0);
                   break;
               case 37:// артос компас
                    IS_COMPASS = true;
                   break;
               case 38:
                   playerCharacter.setHealth(0);
                   break;
               case 39: // временная защита от ЧН аномалий
                   break;
               /*case 40:
                   Health = 0.75 * MaxHealth;
                   break;
               case 41:
                   Health = MaxHealth;
                   break;*/
               case 42: // макс одна защита
                   MaxProtectionsAvailable = 1;
                   break;
               case 43: // макс две защиты
                   MaxProtectionsAvailable = 2;
                   break;
               case 44: // макс три защиты
                   MaxProtectionsAvailable = 3;
                   break;
               /*case 45: // био защита на 30минут
                   SetTemporaryAnomalyProtection("Bio", 99, 1, 1800000);
                   break;
               case 46: // био защита на 60инут
                   SetTemporaryAnomalyProtection("Bio", 99, 1, 3600000);
                   break;
               case 47: // био защита на 90минут
                   SetTemporaryAnomalyProtection("Bio", 99, 1, 5400000);
                   break;
               case 48: // пси защита на 30минут
                   SetTemporaryAnomalyProtection("Psy", 99, 1, 1800000);
                   break;
               case 49: // psy защита на 60минут
                   SetTemporaryAnomalyProtection("Psy", 99, 1, 3600000);
                   break;
               case 50: // psy защита на 90минут
                   SetTemporaryAnomalyProtection("Psy", 99, 1, 5400000);
                   break;*/
               case 51: // защита от выброса на 10 минут
                   boolean DischargeImmunityTemp = DischargeImmunity;
                   DischargeImmunity = true;
                   Handler handler12 = new Handler();
                   handler12.postDelayed(() -> DischargeImmunity = DischargeImmunityTemp, 600000);
                   break;
               case 52: // защита от выброса на 45 минут
                   boolean DischargeImmunityTemp_1 = DischargeImmunity;
                   DischargeImmunity = true;
                   Handler handler13 = new Handler();
                   handler13.postDelayed(() -> DischargeImmunity = DischargeImmunityTemp_1, 2700000);
                   break;
               /*case 53:  //коды болотного доктора на жизни
                   setHealthBy_BD(2, false, 0);
                   break;
               case 54:
                   setHealthBy_BD(5, false, 0);
                   break;
               case 55:
                   setHealthBy_BD(10, false, 0);
                   break;
               case 56:
                   setHealthBy_BD(45, true, 15);
                   break;
               case 57:
                   setHealthBy_BD(-5, false, 0);
                   break;
               case 58:
                   setHealthBy_BD(-10, true, 10);
                   break;
               case 59:
                   setHealthBy_BD(-21, true, 14);
                   break;*/
               /*case 60:
                   SetTemporaryAnomalyProtection("Bio", 60, 15, 1500000);
                   break;
               case 61:
                   SetTemporaryAnomalyProtection("Bio", 60, 15, 2100000);
                   break;
               case 62:
                   SetTemporaryAnomalyProtection("Rad", 60, 15, 1500000);
                   break;
               case 63:
                   SetTemporaryAnomalyProtection("Rad", 60, 15, 2100000);
                   break;
               case 64:
                   SetTemporaryAnomalyProtection("Psy", 60, 15, 1500000);
                   break;
               case 65:
                   SetTemporaryAnomalyProtection("Bio", 0, 1, 1200000);
                   break;
               case 66:
                   SetTemporaryAnomalyProtection("Rad", 0, 1, 1200000);
                   break;
               case 67:
                   SetTemporaryAnomalyProtection("Psy", 0, 1, 1200000);
                   break;*/
               case 68:
                   if (Rad < 0.8 * MaxRad){
                       Rad = 0.8 * MaxRad;
                   }
                   break;
               case 69:
                   if (Bio < 0.8 * MaxBio){
                       Bio = 0.8 * MaxBio;
                   }
                   break;
               case 72:
                   Rad -= Rad * (random.nextInt(30) + 61) / 100;
                   break;
               case 73:
                   Bio -= Bio * (random.nextInt(30) + 61) / 100;
                   break;
              /* case 74:
                   setHealthBy_BD (70, true, 10);
                   break;*/
               case 75:
                   Rad = 0;
                   break;
               case 76:
                   if (Bio > 0.15 * MaxBio) {
                       Bio = 0.15 * MaxBio;
                   }
                   break;
               case 77:
                   Bio = 0;
                   break;
               case 78:
                   Rad -= 0.15 * Rad;
                   break;
               case 80:
                   Rad += 0.1 * MaxRad;
                   if (Rad >= MaxRad){
                       playerCharacter.setDead(true);
                       playerCharacter.setHealth(0);
                   }
                   break;
               case 81:
                   Bio += 0.1 * MaxBio;
                   if (Bio >= MaxBio){
                       playerCharacter.setDead(true);
                       playerCharacter.setHealth(0);
                   }
                   break;
               case 83:
                   Bio -= 0.15 * Bio;
                   break;
               case 85: //sc1 код по типу sc1@rad@suit@80@
                   try {
                       ProtectionChanger(protectionRewhriteMap.get(textCodeSplitted[1]));
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                   if (textCodeSplitted[1].equals("rad")){
                       try {
                           if (!textCodeSplitted[2].equals("tot")) {
                               RadProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                               RadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               if (textCodeSplitted[3].equals("0")){
                                   MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               } else {
                                   MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
                               }
                           } else {
                               RadProtectionTot = Integer.parseInt(textCodeSplitted[3]);
                           }
                       } catch (NullPointerException e) {
                           e.printStackTrace();
                       }
                   }

                   if (textCodeSplitted[1].equals("bio")){
                       try {
                           if (!textCodeSplitted[2].equals("tot")) {
                               BioProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                               BioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               if (textCodeSplitted[3].equals("0")){
                                   MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               }else {
                                   MaxBioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
                               }
                           } else {
                               BioProtectionTot = Integer.parseInt(textCodeSplitted[3]);
                           }
                       } catch (NullPointerException e) {
                           e.printStackTrace();
                       }
                   }
                   if (textCodeSplitted[1].equals("psy")){
                       try {
                           if (!textCodeSplitted[2].equals("tot")) {
                               PsyProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                               BioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               if (textCodeSplitted[3].equals("0")){
                                   MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                               } else {
                                   MaxPsyProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
                               }
                           } else {
                               PsyProtectionTot = Integer.parseInt(textCodeSplitted[3]);
                           }
                       } catch (NullPointerException e) {
                           e.printStackTrace();
                       }
                   }
                   break;
               case 86: //sc2 код по типу sc2@hp@+1@
                   if (textCodeSplitted[1].equals("rad")){
                       try {
                           Rad += Double.parseDouble(textCodeSplitted[2]) * MaxRad / 100;
                           if (Rad < 0){
                               Rad = 0;
                           }
                           if (Rad >= MaxRad){
                               playerCharacter.setDead(true);
                               playerCharacter.setHealth(0);
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                   if (textCodeSplitted[1].equals("bio")){
                       try {
                           Bio += Double.parseDouble(textCodeSplitted[2]) * MaxBio / 100;
                           if (Bio < 0) {
                               Bio = 0;
                           }
                           if (Bio >= MaxBio){
                               playerCharacter.setDead(true);
                               playerCharacter.setHealth(0);
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                   if (textCodeSplitted[1].equals("hp")){
                       try {
                           Health += Double.parseDouble(textCodeSplitted[2]) * MaxHealth / 100;
                           if (Health > MaxHealth){
                               Health = MaxHealth;
                           }
                           if (Health <= 0){
                               playerCharacter.setDead(true);
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                   break;
               case 87:
                   int g2 = 4;
                   anomalies[g2].gesStatus = 1;
                   gesLockoutList[g2] = 1;
                   GestaltLockout(g2);
                   break;
               case 88:
                   int g3 = 5;
                   anomalies[g3].gesStatus = 1;
                   gesLockoutList[g3] = 1;
                   GestaltLockout(g3);
                   break;
               case 89:
                   fastRadPurification = true;
                   break;
               case 90: //Здесь и далее глеб
                   setHealthBy_BD("Rad", -65, true, 9);
                   break;
               case 91:
                   setHealthBy_BD("Rad", -75, true, 9);
                   break;
               case 92:
                   setHealthBy_BD("Rad", -85, true, 9);
                   break;
               case 93:
                   Rad = 0;
                   break;
               case 94:
                   setHealthBy_BD("Bio", -65, true, 9);
                   break;
               case 95:
                   setHealthBy_BD("Bio", -75, true, 9);
                   break;
               case 96:
                   setHealthBy_BD("Bio", -85, true, 9);
                   break;
               case 97:
                   Bio = 0;
                   break;
               case 98:
                   setHealthBy_BD("Health", 60, true, 9);
                   break;
               case 99:
                   setHealthBy_BD("Health", 70, true, 9);
                   break;
               case 100:
                   setHealthBy_BD("Health", 80, true, 9);
                   break;
               case 101:
                   Health = MaxHealth;
                   break;
           }

       }
    };

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
// для qr болотного доктора
    private void setHealthBy_BD (String type, int quantity, boolean isRandom, int rangeOfRandom){
        switch (type){
            case "Health":
                if(isRandom){
                    Health = Health + (quantity + Math.signum(quantity) * (random.nextInt(rangeOfRandom) + 1)) * MaxHealth  / 100;
                } else {
                    Health = Health + quantity * MaxHealth / 100;
                }
                if(Health > MaxHealth){
                    Health = MaxHealth;
                }
                break;
            case "Rad":
                if(isRandom){
                    Rad += (quantity + Math.signum(quantity) * (random.nextInt(rangeOfRandom) + 1)) * Rad  / 100;
                } else {
                    Rad += quantity * Rad / 100d;
                }
                break;
            case "Bio":
                if(isRandom){
                    Bio += (quantity + Math.signum(quantity) * (random.nextInt(rangeOfRandom) + 1)) * Bio  / 100;
                } else {
                    Bio += quantity * Bio / 100d;
                }
                break;
        }

    }

    //артос компас
    public boolean IS_COMPASS = false;
    public void artCompass(){
        if (IS_COMPASS){
            IS_ANOMALIES_AVAILABLE = false;
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                IS_COMPASS = false;
                IS_ANOMALIES_AVAILABLE = true;
            }, 900000);

        }
    }

    // установка количества возможных защиты от аномалий
    private void ProtectionChanger(String protectionType){
        if (MaxProtectionsAvailable == 1) {
            for (String protType : new String[]{"Rad", "Bio", "Psy"}){
                if (!protectionType.equals(protType)){
                    Arrays.fill(protectionMap.get(protType), 0);
                    Arrays.fill(protectionCapacityMap.get(protType), 0);
                    Arrays.fill(protectionCapacityMaxMap.get(protType), 0);
                }
            }
        }
    }

    private WifiManager wifiManager;
    private List<ScanResult> wifiList;

    public void detectWifi(){
        this.wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.wifiManager.startScan();
        this.wifiList = this.wifiManager.getScanResults();

        //Log.d("TAGg", wifiList.toString());
        try {
            for (int i = 0; i<wifiList.size(); i++){
                String item = wifiList.get(i).toString();
                String[] vector_item = item.split(",");
                String item_essid = vector_item[0];
                String ssid = item_essid.split(": ")[1];
               // Log.d("TAGgg", ssid);
                if (ssid.equals("control")){
                    IsInsideAnomaly = Boolean.TRUE;
                    anomalies[NUMBER_OF_ANOMALIES + 2].Apply();
                }
                if (ssid.equals("chimera")){
                    IsInsideAnomaly = Boolean.TRUE;
                    anomalies[NUMBER_OF_ANOMALIES + 1].Apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCreate() {
        super.onCreate();
        this.wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "STALKERNET:My_Partial_Wake_Lock");
        this.wl.acquire(10*60*1000L /*10 minutes*/);   //timeout заставила студия поставить, не знаю как это работает
        this.EM = new EffectManager(this);
        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.create_db();

        playerCharacter = new PlayerCharacter(this);

        NUMBER_OF_ANOMALIES = getNumberOfAnomalies();

        GetAnomalies();

        CreateSafeZones();

        LoadStats();
        playerCharacter.loadStats(getApplicationContext());



        this.mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        //startForeground(101, new Builder(this, Build.VERSION.SDK_INT >= 26 ? createNotificationChannel((NotificationManager) getSystemService("notification")) : "").setOngoing(true).setSmallIcon(R.drawable.ic_launcher_background).setPriority(1).setCategory("service").setContentTitle("StatsService").setContentText("Stats are being updated.").build());
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    IMPORTANCE_HIGH);
            channel.setImportance(IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        //MaxRadProtectionCapacity = 1000;

        protectionMap.put("Rad", RadProtectionArr);
        protectionMap.put("Bio", BioProtectionArr);
        protectionMap.put("Psy", PsyProtectionArr);
        protectionCapacityMaxMap.put("Rad", MaxRadProtectionCapacityArr);
        protectionCapacityMaxMap.put("Bio", MaxBioProtectionCapacityArr);
        protectionCapacityMaxMap.put("Psy", MaxPsyProtectionCapacityArr);
        protectionCapacityMap.put("Rad", RadProtectionCapacityArr);
        protectionCapacityMap.put("Bio", BioProtectionCapacityArr);
        protectionCapacityMap.put("Psy", PsyProtectionCapacityArr);
    }
    //всё работает так как функцию никто не вызывает
    @RequiresApi(26)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String str = "101";
        NotificationChannel notificationChannel = new NotificationChannel(str, "StatsService", IMPORTANCE_HIGH);
        notificationChannel.setImportance(IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(0);
        notificationManager.createNotificationChannel(notificationChannel);
        return str;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        Toast.makeText(this, "Service has been started.", Toast.LENGTH_SHORT).show();
        CheckPermissions();
        registerReceiver(this.broadcastReceiver, new IntentFilter("Command"));
        registerReceiver(this.broadcastReceiverQR, new IntentFilter("StRoulette"));
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        unregisterReceiver(this.broadcastReceiverQR);
        SaveStats();
        playerCharacter.saveStats(getApplicationContext());
        this.wl.release();
    }

    //private int Course_Location_RequestCode = 1;
    private void CheckPermissions() {
        while (!this.LocationUpdatesStarted) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                //if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COURSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                    LocationRequest create = LocationRequest.create();
                    create.setPriority(100).setInterval(1000).setFastestInterval(1000);
                    this.LocationCallback = new MyLocationCallback(this.MyCurrentLocation, this);
                    this.mFusedLocationProvider.requestLocationUpdates(create, this.LocationCallback, null);
                    this.LocationUpdatesStarted = true;
                    Toast.makeText(this, "Location updates have been started.", Toast.LENGTH_SHORT).show();
                //}
            }
        }
    }

    public int getNumberOfAnomalies(){
        int numberOfAnomalies = 0;
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"COUNT(_id)"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            numberOfAnomalies = cursor.getInt(0);
        }
        cursor.close();
        dbHelper.close();
        return numberOfAnomalies;
    }

    // гештальт аномалию 20 минут нельзя снова открыть
    private void GestaltLockout(final int gesIndex){
       Handler handler = new Handler();
       handler.postDelayed(() -> gesLockoutList[gesIndex] = 0, 1200000);
    }

    // список аномалий
    // d - сила
    // d2 - радиус
    // гештальт должен идти первым
    // 0 - не гештальт, 1 - закрыто, 2 - открыто
    // вызывается в onCreate()
    /*
    * Вызывается в class MyLocationCallback и создает список аномалий, которые берет из БД, а также
    * добавляет 3 аномалии для QR рулетки
    */
    public void GetAnomalies() {
        gesStatus = 1;
        /*
        * к NUMBER_OF_ANOMALIES  добавляем 3 в счет аномалий у сталкерской рулетки,
        * которые не учитываются в CheckAnomalies()
        */
        Anomaly[] anomalyArr = new Anomaly[NUMBER_OF_ANOMALIES + 3];  // +3 аномалии для сталкерской рулетки
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_ANOMALY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID_ANOMALY);
            int polygon_type = cursor.getColumnIndex(DBHelper.KEY_POLYGON_TYPE);
            int type = cursor.getColumnIndex(DBHelper.KEY_TYPE);
            int radius = cursor.getColumnIndex(DBHelper.KEY_RADIUS);
            int power = cursor.getColumnIndex(DBHelper.KEY_POWER);
            int minPower = cursor.getColumnIndex(DBHelper.KEY_MIN_POWER);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE_ANOMALY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE_ANOMALY);
           // int statService = cursor.getColumnIndex(DBHelper.KEY_STATSERVICE);  - не подумал, что у БД нет типа statService
            int gestaltStatus = cursor.getColumnIndex(DBHelper.KEY_GESTALT_STATUS);
            int boolShow = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOWABLE);
            do {
                if (!cursor.getString(type).equals("")) {
                    anomalyArr[cursor.getInt(idIndex) - 1] = new Anomaly(cursor.getString(polygon_type), cursor.getString(type), cursor.getDouble(power), cursor.getDouble(radius), new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), this,cursor.getInt(gestaltStatus),cursor.getString(boolShow));
                    anomalyArr[cursor.getInt(idIndex) - 1].minstrenght = cursor.getDouble(minPower);
                } else{
                    anomalyArr[cursor.getInt(idIndex) - 1] = new MonolithAnomaly(cursor.getString(polygon_type), cursor.getString(type), cursor.getDouble(power), cursor.getDouble(radius), new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), this,cursor.getInt(gestaltStatus),cursor.getString(boolShow));

                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();

        anomalyArr[NUMBER_OF_ANOMALIES] = new Anomaly("QR", "Rad", 1d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[NUMBER_OF_ANOMALIES + 1] = new Anomaly("QR", "Bio", 2d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[NUMBER_OF_ANOMALIES + 2] = new Anomaly("QR", "Psy", 1d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalies = anomalyArr;
    }

    public void GetTime() {
        this.cal = Calendar.getInstance();
        this.dayInt = this.cal.get(5);
        this.Hour = this.cal.get(Calendar.HOUR_OF_DAY);
        this.Minutes = this.cal.get(12);
    }

    public LatLng moving_anomalies(LatLng start_LatLng, LatLng finish_latLng){
        double dLat = (start_LatLng.latitude - finish_latLng.latitude) / 60;
        double dLng = (start_LatLng.longitude - finish_latLng.longitude) / 60;
        return new LatLng(start_LatLng.latitude - (dLat * (double) Minutes), start_LatLng.longitude - (dLng * (double) Minutes));
    }
    public void getMovingAnomalies(){
/*
        anomalies[26].center = moving_anomalies(new LatLng(64.35367638665042d, 40.72390097232614d), new LatLng(64.35447294898991d, 40.72723301461363d));
        anomalies[31].center = moving_anomalies(new LatLng(64.35320380110174d, 40.731077622120544d), new LatLng(64.35398513048334d, 40.73641791866202d));
        anomalies[34].center = moving_anomalies(new LatLng(64.35623672541492d, 40.737128289701104d), new LatLng(64.35676588233207d, 40.73945088292754d));
        anomalies[39].center = moving_anomalies(new LatLng(64.35289574876842d, 40.717908062517054d), new LatLng(64.3539419138342d, 40.721960267940204d));
        anomalies[42].center = moving_anomalies(new LatLng(64.35396930076678d, 40.73175313604288d), new LatLng(64.35490024649128d, 40.73339218798626d));
        anomalies[60].center = moving_anomalies(new LatLng(64.35222610146626d, 40.72358407989295d), new LatLng(64.35320565629961d, 40.7270548724309d));
        anomalies[69].center = moving_anomalies(new LatLng(64.35323883585698d, 40.73111057506516d), new LatLng(64.35411721533774d, 40.7376819375967d));
        anomalies[70].center = moving_anomalies(new LatLng(64.35278372651375d, 40.728019600236955d), new LatLng(64.35352818391922d, 40.72436792972977d));
        anomalies[71].center = moving_anomalies(new LatLng(64.35108577387157d, 40.73010424116759d), new LatLng(64.35113415381451d, 40.7270970155379d));
        anomalies[97].center = moving_anomalies(new LatLng(64.35563755498839d, 40.722948500302955d), new LatLng(64.35614047773963d, 40.72465317082411d));
        anomalies[108].center = moving_anomalies(new LatLng(64.35392743705654d, 40.72682310973268d), new LatLng(64.35447674255012d, 40.72307019618953d));
*/
    }

    public void CheckPsyForMonolith(){
        if (isMonolith && IS_ANOMALIES_AVAILABLE){
            double d = playerCharacter.getHealth() - 0.5;
            playerCharacter.setHealth(d);
        }
    }


    // применяет аномалии
    // вызывается в MyLocationCallback()
    public void CheckAnomalies() {
        Anomaly anomaly;
        if (IS_ANOMALIES_AVAILABLE && !isInSuperSaveZone) {
            long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
            // постоянные аномалии
            if (timeInSeconds > dayFirst) { // 6 сентября в 17:00
                int i1;
                if (MonolithOk){
                    i1 = 7;
                } else{
                    i1 = 0;
                }
                for (int i = i1; i < 18; i++) {//18
                    anomalies[i].Apply();
                }

            }
            // с 12 сентября в 17:00
            CheckAnomaliesRegular(dayFirst, daySecond, 18, 31);
            // c 13 сентября в 11:36
            CheckAnomaliesRegular(daySecond, dayThird, 18, 38);
            // c 14 сентября в 15:20
            CheckAnomaliesRegular(dayThird, dayFourth, 38, 58);
            // c 15 сентября в 11:20
            CheckAnomaliesRegular(dayFourth, (dayFourth + dayFourth), 58, 72);
            // аномалии монолита
            CheckAnomaliesRegular(dayFirst, (dayFourth + dayFourth), 72, 77);

        }

    }

    public void CheckAnomaliesRegular(long timeStart, long timeFinish, int anomalyStart, int anomalyFinish){
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        if(timeInSeconds > timeStart  && timeInSeconds < timeFinish){
            for (int i = anomalyStart; i < anomalyFinish; i++) {
                anomalies[i].Apply();
            }
        }
    }

   // public int anomalyIndex;
    public void CheckIfInAnyAnomalyRegular(long timeStart, long timeFinish, int anomalyStart, int anomalyFinish){
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        if (timeInSeconds > timeStart  && timeInSeconds < timeFinish){
            ContentValues contentValues;
            for (int i = anomalyStart; i < anomalyFinish; i++) {
                if (anomalies[i].IsInside) {
                    if (anomalies[i].toShow) {
                        database = dbHelper.open();
                        contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "true");
                        database.update(DBHelper.TABLE_ANOMALY, contentValues,  DBHelper.KEY_ID_ANOMALY + "=" + (i+1), null);
                    }
                    IsInsideAnomaly = Boolean.TRUE;
                    break;
                }
            }
        }
    }

    // вызывается в MyLocationCallback()
    public void CheckIfInAnyAnomaly() {
        this.IsInsideAnomaly = Boolean.FALSE;
        database = dbHelper.open();
        ContentValues contentValues;
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"_id", "bool_show_on_map"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "false");
                database.update(DBHelper.TABLE_ANOMALY, contentValues, null, null);
            } while (cursor.moveToNext());
        }

        detectWifi();

        if (IS_ANOMALIES_AVAILABLE && !isInSuperSaveZone) {

            int i1;
            if (MonolithOk){
                i1 = 7;
            }else{
                i1 = 0;
            }
            for (int i = i1 ; i < 18; i++) {//18
                if (anomalies[i].IsInside) {
                    if (anomalies[i].toShow) {
                        contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "true");
                        database = dbHelper.open();
                        database.update(DBHelper.TABLE_ANOMALY, contentValues,  DBHelper.KEY_ID_ANOMALY + "=" + (i+1), null);
                    }

                    IsInsideAnomaly = Boolean.TRUE;
                    if (!GestaltProtection) {                                                 //проверка на защиту от открытия гештальта
                        if (i < NUMBER_OF_GESTALT_ANOMALIES && anomalies[i].gesStatus == 1){  //если конкретный гештальт закрыт
                            if (gesLockoutList[i] != 1) {                                     // проверяет можно ли конкретный гештальт открыть
                                database = dbHelper.getWritableDatabase();
                                anomalies[i].gesStatus = 2;                                   // открываем гештальт
                                /*ЕСЛИ ГЕШТАЛЬТ ОТКРЫВАЕТСЯ, ТО СТАВИТ ЕГО КООРДИНАТУ НА КАРТУ*/
                                contentValues = new ContentValues();
                                contentValues.put(DBHelper.KEY_NAME, "!!!GESTALT!!!");
                                contentValues.put(DBHelper.KEY_ICON, "icon");
                                contentValues.put(DBHelper.KEY_LATITUDE, Double.toString(anomalies[i].center.latitude));
                                contentValues.put(DBHelper.KEY_LONGITUDE, Double.toString(anomalies[i].center.longitude));
                                contentValues.put(DBHelper.KEY_COMMENT, "Обнаружен Гештальт");
                                database.insert(DBHelper.TABLE_MARKERS, null, contentValues);

                                database = dbHelper.open();
                                contentValues = new ContentValues();
                                contentValues.put(DBHelper.KEY_GESTALT_STATUS, "2");
                                database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID_ANOMALY + "=?", new String[]{String.valueOf(i+1)});
                            }
                        }
                    }
                }
            }
            // с 6 сентября в 17:00
            CheckIfInAnyAnomalyRegular(dayFirst, daySecond, 18, 31);
            // c 7 сентября в 17:23
            CheckIfInAnyAnomalyRegular(daySecond, dayThird, 18, 38);
            // c 8 сентября в 10:47
            CheckIfInAnyAnomalyRegular(dayThird, dayFourth, 38, 58);
            // c 9 сентября в 13:37
            CheckIfInAnyAnomalyRegular(dayFourth, (dayFourth + dayFourth), 58, 72);
            // аномалии монолита
            CheckIfInAnyAnomalyRegular(dayFirst, (dayFourth + dayFourth), 72, 77);

        }
        /*
        рад выводится само со временем
        пси выводится сразу
        био само не выводится
         */
        if (!IsInsideAnomaly) {
            if (fastRadPurification) {
                if (Rad > 0) {
                    Rad -= 0.3; // выводится за час 20
                }
            } else {
                if (Rad > 0) {
                    Rad -= 0.2; // должно выводиться за ...
                }
            }
            Psy = 0.0d;
            EM.StopActions();
        }
        cursor.close();
        dbHelper.close();
    }



    SuperSaveZone[] superSaveZones = new SuperSaveZone[4];
    public void Create_super_save_zones(){
        superSaveZones[0] = new SuperSaveZone(checkTime_in, 0, 180, 15d, "stalkers_in");
        superSaveZones[1] = new SuperSaveZone(checkTime_in, 0, 180, 15d, "military_in");
        superSaveZones[2] = new SuperSaveZone(checkTime_in, 0, 180, 15d, "stalkers_out");
/*        superSaveZones[3] = new SuperSaveZone(checkTime_in + 90, 1, 180, 20d, "stalkers_out");
        superSaveZones[4] = new SuperSaveZone(checkTime_out, 0, 180, 30d, "stalkers_out");
        superSaveZones[5] = new SuperSaveZone(checkTime_out + 90, 1, 180, 30d, "stalkers_out");
        superSaveZones[6] = new SuperSaveZone(checkTime_out, 0, 180, 20d, "green_out");
        superSaveZones[7] = new SuperSaveZone(checkTime_out + 90, 1, 180, 20d, "green_out");*/
    }



    public boolean isInSuperSaveZone = false;
    public void Super_save_zone_check(){
        isInSuperSaveZone = false;
        if (((Calendar.getInstance().getTimeInMillis() / 1000) >= dayFirst) /*&& ((Calendar.getInstance().getTimeInMillis() / 1000) <= (checkTime_in + 3600))*/){
/*            if (Hour >= 20 || Hour <= 4) {
                for (LatLng latLng : nightZones){
                    Location location = new Location("GPS");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    if (location.distanceTo(MyCurrentLocation) <= 17){
                        isInSuperSaveZone = true;
                    }

                }
            }*/
        }
    }


    public void CreateSafeZones() {
        SafeZone[] safeZoneArr = new SafeZone[NUMBER_OF_SAVE_ZONES];
        safeZoneArr[0] = new SafeZone("Circle", 50.0d, new LatLng(64.351080d, 40.736224d), this); // Свобода
        safeZoneArr[1] = new SafeZone("Circle", 100.0d, new LatLng(64.357220d, 40.721517d), this); // денисовичи
        safeZoneArr[2] = new SafeZone("Circle", 40.0d, new LatLng(64.351663d, 40.727578d), this); // гараж
        safeZoneArr[3] = new SafeZone("Circle", 40.0d, new LatLng(64.349906d, 40.725957d), this); // у озера
        safeZoneArr[4] = new SafeZone("Circle", 40.0d, new LatLng(64.358117d, 40.722426d), this); // денисовичи еще раз
        this.SafeZones = safeZoneArr;
    }


    public void CheckIfInAnySafezone() {
        int i = 0;
        this.IsInsideSafeZone = Boolean.FALSE;
        while (i < NUMBER_OF_SAVE_ZONES) {
            this.SafeZones[i].Apply();
            if (this.SafeZones[i].IsInside) {
                this.IsInsideSafeZone = Boolean.TRUE;
            }
            i++;
        }
    }

    public void Discharge() {
        this.EM.PlayBuzzer();
        Toast.makeText(getApplicationContext(), "Близиться выброс, не спеша...", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            CheckIfInAnySafezone();
            EM.PlayBuzzer();
            if (!(IsInsideSafeZone || DischargeImmunity)) {
                LastTimeHitBy = "Dis";
                playerCharacter.setDead(true);
                Health = 0.0d;
                Intent intent = new Intent("StatsService.Message");
                intent.putExtra("Message", "H");
                sendBroadcast(intent);
            }
            Toast.makeText(getApplicationContext(), "Выброс Окончен!!", Toast.LENGTH_SHORT).show();
            IsDischarging = Boolean.FALSE;
        }, 600000);
    }

    public void checkQuest(){
        //
        //проверка кредо на легенду зоны
        //
        database = dbHelper.open();
        ContentValues cv;

        String creedString = "SELECT access_status FROM milestone WHERE access_status =?";
        Cursor cursor = database.rawQuery(creedString, new String[]{"true"});
        if (cursor.getCount() > 8) {
            cv = new ContentValues();
            cv.put(DBHelper.KEY_STATUS__CREED_BRANCH, "true");
            database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "= ?", new String[]{"15"});
            if (RadProtectionTot < 15){
                RadProtectionTot = 15;
            }
            if (BioProtectionTot < 15){
                BioProtectionTot = 15;
            }
            if (PsyProtectionTot < 15){
                PsyProtectionTot = 15;
            }
        }
        cursor.close();

        database.close();
    }
    /*
    * Проверяет находится ли игрок в радиусе 30 метров, если находится, то ставит локации
    * access_status значению true
    * */
    public void checkLocality(){
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_LOCALITY, new String[]{"_id", "latitude", "longitude", "access_status", "access_key"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__LOCALITY);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__LOCALITY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__LOCALITY);
            int accessStatusIndex = cursor.getColumnIndex(DBHelper.KEY_ACCESS_STATUS__LOCALITY);
            int accessKeyIndex = cursor.getColumnIndex(DBHelper.KEY_ACCESS_KEY__LOCALITY);

            do {
                if (cursor.getString(accessStatusIndex).equals("false")) {
                    Location location = new Location("");
                    location.setLatitude(cursor.getDouble(latIndex));
                    location.setLongitude(cursor.getDouble(lonIndex));
                    double distanceToLocality = location.distanceTo(MyCurrentLocation);
                    if (distanceToLocality < 30){
                        ContentValues contentValues = new ContentValues();
                        database.beginTransaction();
                        try {
                            contentValues.put(DBHelper.KEY_ACCESS_STATUS__LOCALITY, "true");
                            database.update(DBHelper.TABLE_LOCALITY, contentValues,DBHelper.KEY_ID__LOCALITY + "=" + cursor.getInt(idIndex),null);
                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }


    // Нужно чтобы загружать из памяти массивы, которые из double были переведены в string
    public double[] StringArrToDoubleArr (String stringArr){
        return Arrays.stream(Objects.requireNonNull(defaultSharedPreferences.getString(stringArr, "0, 0, 0")).split(", ")).mapToDouble(Double::parseDouble).toArray();
    }

    SharedPreferences defaultSharedPreferences;
    public void LoadStats() {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MaxHealth = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("MaxHealth", "2000")));
        this.Health = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Health", "2000")));
        this.Rad = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Rad", "0")));
        this.Bio = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Bio", "0")));
        this.Psy = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Psy", "0")));
        RadProtectionArr = StringArrToDoubleArr("RadProtectionArr");
        BioProtectionArr = StringArrToDoubleArr("BioProtectionArr");
        PsyProtectionArr = StringArrToDoubleArr("PsyProtectionArr");
        RadProtectionCapacityArr = StringArrToDoubleArr("RadProtectionCapacityArr");
        BioProtectionCapacityArr = StringArrToDoubleArr("BioProtectionCapacityArr");
        PsyProtectionCapacityArr = StringArrToDoubleArr("PsyProtectionCapacityArr");
        MaxRadProtectionCapacityArr = StringArrToDoubleArr("MaxRadProtectionCapacityArr");
        MaxBioProtectionCapacityArr = StringArrToDoubleArr("MaxBioProtectionCapacityArr");
        MaxPsyProtectionCapacityArr = StringArrToDoubleArr("MaxPsyProtectionCapacityArr");
        this.GestaltProtection = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("GesProtection", "false")));
        this.anomalies[0].gesStatus = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("GesStatus", "1")));
        this.ScienceQR = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("ScienceQR", "0")));
        this.DischargeImmunity = Boolean.parseBoolean(defaultSharedPreferences.getString("DischargeImmunity", "false"));
        this.IsUnlocked = Boolean.parseBoolean(defaultSharedPreferences.getString("Lock", "true"));
        this.IS_ANOMALIES_AVAILABLE = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("IS_ANOMALIES_AVAILABLE", "true")));
        this.MaxProtectionsAvailable = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("MaxProtectionsAvailable", "1")));
        this.fastRadPurification = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("fastRadPurification", "false")));
        this.MonolithOk = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("MonolithOk", "false")));
        this.isMonolith = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("isMonolith", "false")));
        this.RadProtectionTot = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("RadProtectionTot", "0")));
        this.BioProtectionTot = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("BioProtectionTot", "0")));
        this.PsyProtectionTot = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("PsyProtectionTot", "0")));
    }

    public void SaveStats() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("MaxHealth", Double.toString(this.MaxHealth));
        edit.putString("Health", Double.toString(this.Health));
        edit.putString("Rad", Double.toString(this.Rad));
        edit.putString("Bio", Double.toString(this.Bio));
        edit.putString("Psy", Double.toString(this.Psy));
        edit.putString("RadProtectionArr", Arrays.toString(RadProtectionArr).replaceAll("[\\[\\]]", ""));
        edit.putString("BioProtectionArr", Arrays.toString(BioProtectionArr).replaceAll("[\\[\\]]", ""));
        edit.putString("PsyProtectionArr", Arrays.toString(PsyProtectionArr).replaceAll("[\\[\\]]", ""));
        edit.putString("RadProtectionCapacityArr", Arrays.toString(RadProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("BioProtectionCapacityArr", Arrays.toString(BioProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("PsyProtectionCapacityArr", Arrays.toString(PsyProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("MaxRadProtectionCapacityArr", Arrays.toString(MaxRadProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("MaxBioProtectionCapacityArr", Arrays.toString(MaxBioProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("MaxPsyProtectionCapacityArr", Arrays.toString(MaxPsyProtectionCapacityArr).replaceAll("[\\[\\]]", ""));
        edit.putString("GesProtection", Boolean.toString(this.GestaltProtection));
        edit.putString("GesStatus", Integer.toString(this.anomalies[0].gesStatus));
        edit.putString("ScienceQR", Integer.toString(this.ScienceQR));
        edit.putString("DischargeImmunity", Boolean.toString(this.DischargeImmunity));
        edit.putString("Lock", Boolean.toString(this.IsUnlocked));
        edit.putString("IS_ANOMALIES_AVAILABLE", Boolean.toString(this.IS_ANOMALIES_AVAILABLE));
        edit.putString("MaxProtectionsAvailable", Integer.toString(MaxProtectionsAvailable));
        edit.putString("fastRadPurification", Boolean.toString(this.fastRadPurification));
        edit.putString("MonolithOk", Boolean.toString(this.MonolithOk));
        edit.putString("isMonolith", Boolean.toString(this.isMonolith));
        edit.putString("RadProtectionTot", Integer.toString(this.RadProtectionTot));
        edit.putString("BioProtectionTot", Integer.toString(this.BioProtectionTot));
        edit.putString("PsyProtectionTot", Integer.toString(this.PsyProtectionTot));
        edit.apply();
    }
}
