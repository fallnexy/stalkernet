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
import android.graphics.Color;
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

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

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
    private static final int ID_SERVICE = 101;
    public int NUMBER_OF_ANOMALIES = 0; // задается в onCreate
    private static final int NUMBER_OF_GESTALT_ANOMALIES = 0;
    private static final int NUMBER_OF_SAVE_ZONES = 5;
    public static final int SUIT_PROTECTION = 80; //ЗАЩИТА ОТ КОСТЮМА
    public static final int SUIT_PROTECTION_50 = 50; //ЗАЩИТА ОТ КОСТЮМА
    public static final int SUIT_PROTECTION_25 = 25; //ЗАЩИТА ОТ КОСТЮМА
    public static final double SUIT_PROTECTION_CAPACITY = 100000; //ЗАЩИТА ОТ КОСТЮМА
    public static final double ART_PROTECTION_CAPACITY = 2000; //ЗАЩИТА ОТ КОСТЮМА
    public static final double QUEST_PROTECTION_CAPACITY = 2000; //ЗАЩИТА ОТ КОСТЮМА
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

    long dayFirst = 1630936800;
    long daySecond = 1631024580;
    long dayThird = 1631087220;
    long dayFourth = 1631183820;

    public long[] coolDawn = new long[25];



    // защита от аномалий у чистого неба
    public boolean ClearSkyAnomalyProtection = false;
    // защита от выброса у свободы
    public boolean FreedomDischargeImmunity = false;
    // защита от пси у монолита
    boolean MonolithOk = false;

    public boolean fastRadPurification = false;

    public LatLng latLngAnomaly;
    public Double radiusAnomaly;

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
    public String[] textCodeSplitted = new String[4];
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
               if (textCode.equals("sc1") | textCode.equals("sc2")) {
                   var4 = textCode;
               }
               Log.d("wtf_textCode", var4);
               Toast.makeText(StatsService.this.getApplicationContext(), var4, Toast.LENGTH_LONG).show();
               switch(var4.hashCode()) {
                   case 305958064:
                       if (var4.equals("ResetStats")) {
                           var3 = 0;
                           break label110;
                       }
                       break;
                   case 1952950435:
                       if (var4.equals("SetPsyProtection0")) {
                           var3 = 1;
                           break label110;
                       }
                       break;
                   case 411921544:
                       if (var4.equals("SetPsyProtection50")) {
                           var3 = 2;
                           break label110;
                       }
                       break;
                   case -115337820:
                       if (var4.equals("SetPsyProtection100")) {
                           var3 = 3;
                           break label110;
                       }
                       break;
                   case -782617084:
                       if (var4.equals("SetRadProtection0")) {
                           var3 = 4;
                           break label110;
                       }
                       break;
                   case 1508674375:
                       if (var4.equals("SetRadProtection50")) {
                           var3 = 5;
                           break label110;
                       }
                       break;
                   case -475738427:
                       if (var4.equals("SetRadProtection100")) {
                           var3 = 6;
                           break label110;
                       }
                       break;
                   case 2084039473:
                       if (var4.equals("SetBioProtection0")) {
                           var3 = 7;
                           break label110;
                       }
                       break;
                   case 180714426:
                       if (var4.equals("SetBioProtection50")) {
                           var3 = 8;
                           break label110;
                       }
                       break;
                   case 1307176114:
                       if (var4.equals("SetBioProtection100")) {
                           var3 = 9;
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
                   case -1357835385:
                       if (var4.equals("clear4")) {
                           var3 = 39;
                           break label110;
                       }
                       break;
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
                   case -1449685624: // этот и следующий - локальные защиты от выбросов
                       if (var4.equals("dolgDischargeImmunity")) {
                           var3 = 70;
                           break label110;
                       }
                       break;
                   case 1259972122: // сентябрь21 - чистое небо
                       if (var4.equals("naemnikiDischargeImmunity")) {
                           var3 = 71;
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
                   case 1265750414:
                       if (var4.equals("ifLess50healthSet70RadProt")) {
                           var3 = 79;
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
                   case 189785345:
                       if (var4.equals("ifLess50healthSet70BioProt")) {
                           var3 = 82;
                           break label110;
                       }
                       break;
                   case 1784281556:
                       if (var4.equals("minus15Bio")) {
                           var3 = 83;
                           break label110;
                       }
                       break;
                   case -1576308282:
                       if (var4.equals("ifLess50healthPlus25Health")) {
                           var3 = 84;
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
                   IsDead = false;
                   intent1 = new Intent("StatsService.HealthUpdate");
                   intent1.putExtra("Health", "2000");
                   sendBroadcast(intent1);
                   intent1 = new Intent("StatsService.Message");
                   intent1.putExtra("Message", "A");
                   sendBroadcast(intent1);
                   break;
               case 1: //191000 монолит на 2021
                    if (DischargeImmunity){
                        MonolithOk = true;
                        MaxProtectionsAvailable = 3;
                        RadProtectionArr[0] = 50;
                        RadProtectionCapacityArr[0] = 0;
                        MaxRadProtectionCapacityArr[0] = SUIT_PROTECTION_CAPACITY;
                        BioProtectionArr[0] = 50;
                        BioProtectionCapacityArr[0] = 0;
                        MaxBioProtectionCapacityArr[0] = SUIT_PROTECTION_CAPACITY;
                        PsyProtectionArr[0] = 50;
                        PsyProtectionCapacityArr[0] = 0;
                        MaxPsyProtectionCapacityArr[0] = SUIT_PROTECTION_CAPACITY;
                    } else {
                        if (MaxProtectionsAvailable < 3){
                            MaxProtectionsAvailable++;
                        }
                    }
                   break;
               case 2: //191050
                   /*ProtectionChanger("Psy");
                   j = 1;
                   PsyProtectionCapacityArr[j] = 0;
                   PsyProtectionArr[j] = 50;
                   MaxPsyProtectionCapacityArr[j] = 100;
                   break;
               case 3:
                   PsyQuestProtection = 100;*/
                   break;
               case 4: //171000
                   /*ProtectionChanger("Rad");
                   Arrays.fill(RadProtectionCapacityArr, 0);
                   RadProtectionArr[0] = 30;
                   RadProtectionArr[1] = 40;
                   RadProtectionArr[2] = 50;

                   MaxRadProtectionCapacityArr[0] = 300;
                   MaxRadProtectionCapacityArr[1] = 300;
                   MaxRadProtectionCapacityArr[2] = 300;*/
                   break;
               case 5: //171050
                   //RadTotalProtection = 90;
                   //MaxRadProtectionCapacityArr[2] = 50000;
                   break;
               case 6: // используется не только цифровым кодом, но и qr
                   //RadTotalProtection = 99;
                   //MaxRadProtectionCapacityArr[2] = 50000;
                   break;
               case 7: //181000
                   /*ProtectionChanger("Bio");
                   j = 0;
                   BioProtectionCapacityArr[j] = 0;
                   BioProtectionArr[j] = 80;
                   MaxBioProtectionCapacityArr[j] = 100;*/
                   break;
               case 8:
                   //BioQuestProtection = 50;
                   break;
               case 9: // используется не только цифровым кодом, но и qr
                   /*if (BioProtectionChangeability) {
                       BioQuestProtection = 90;
                   }*/
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
                   intent1.putExtra("Health", "200");
                   sendBroadcast(intent1);
                   break;
               case 13:
                   MaxHealth = 3000.0D;
                   intent1 = new Intent("StatsService.HealthUpdate");
                   intent1.putExtra("Health", "300");
                   sendBroadcast(intent1);
                   break;
               case 14:  //MakeAlive
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
                   IsDead = false;
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
                   anomalies[g].gesStatus = 1;
                   gesLockoutList[g] = 1;
                   GestaltLockout(g);
                   break;
               case 26:
                   int g1 = 4;
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
                   setHealth(0);
                   break;
               case 39: // временная защита от ЧН аномалий
                   boolean clearTemp = ClearSkyAnomalyProtection;
                   ClearSkyAnomalyProtection = true;
                   Handler handler = new Handler();
                   handler.postDelayed(() -> ClearSkyAnomalyProtection = clearTemp, 14400000);
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
               case 70: // этот защита от аномалий
                   ClearSkyAnomalyProtection = true;
                   break;
               case 71:// этот  локальные защиты от выбросов
                   FreedomDischargeImmunity = true;
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
               case 79:
                   /*if (Health >= MaxHealth * 0.5){
                       Health -= 0.15 * Health;
                   }else {
                       SetTemporaryAnomalyProtection ("Rad", 69, 1, 1200000);
                   }*/
                   break;
               case 80:
                   Rad += 0.1 * MaxRad;
                   if (Rad >= MaxRad){
                       setDead(Boolean.TRUE);
                       setHealth(0);
                   }
                   break;
               case 81:
                   Bio += 0.1 * MaxBio;
                   if (Bio >= MaxBio){
                       setDead(Boolean.TRUE);
                       setHealth(0);
                   }
                   break;
               case 82:
                   /*if (Health >= MaxHealth * 0.5){
                       Health -= 0.15 * Health;
                   }else {
                       SetTemporaryAnomalyProtection ("Bio", 69, 1, 1200000);
                   }*/
                   break;
               case 83:
                   Bio -= 0.15 * Bio;
                   break;
               case 84:
                   if (Health >= MaxHealth * 0.5){
                       Health -= 0.15 * Health;
                   }else {
                       Health += 0.25 * MaxHealth;
                       if (Health >= MaxHealth){
                           Health = MaxHealth;
                       }
                   }
                   break;
               case 85: //sc1 код по типу sc1@rad@suit@80@
                   try {
                       ProtectionChanger(protectionRewhriteMap.get(textCodeSplitted[1]));
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   if (textCodeSplitted[1].equals("rad")){
                       try {
                           RadProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                           RadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           if (textCodeSplitted[3].equals("0")){
                               MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           } else {
                               MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
                           }
                       } catch (NullPointerException e) {
                           e.printStackTrace();
                       }
                   }
                   if (textCodeSplitted[1].equals("bio")){
                       try {
                           BioProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                           BioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           if (textCodeSplitted[3].equals("0")){
                               MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           }else {
                               MaxBioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
                           }
                       } catch (NullPointerException e) {
                           e.printStackTrace();
                       }
                   }
                   if (textCodeSplitted[1].equals("psy")){
                       try {
                           PsyProtectionArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = Double.parseDouble(textCodeSplitted[3]);
                           BioProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           if (textCodeSplitted[3].equals("0")){
                               MaxRadProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = 0;
                           } else {
                               MaxPsyProtectionCapacityArr[protectionSuitRewhriteMap.get(textCodeSplitted[2])] = ProtectionCapacity[protectionSuitRewhriteMap.get(textCodeSplitted[2])];
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
                               setDead(true);
                               setHealth(0);
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
                               setDead(true);
                               setHealth(0);
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
                               setDead(true);
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                   break;
               case 87:
                   int g2 = 2;
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
            Location locationCompass = MyCurrentLocation;
            double distanceToCompass = locationCompass.distanceTo(MyCurrentLocation);
            IS_ANOMALIES_AVAILABLE = (distanceToCompass > 20);
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

    public void setProtectionCapacity(double c){

    }

    public void setHealth(double d) {
        if (d > 0.0d) {
            Health = d;
            return;
        }
        Health = d;
        setDead(Boolean.TRUE);
    }

    public void setDead(Boolean var1) {
        byte var2 = 0;
        var1 = false;
        if (var1) {
            this.IsDead = var1;
        } else {
            label39: {
                this.IsDead = var1;
                String var4 = this.LastTimeHitBy;
                int var3 = var4.hashCode();
                if (var3 != 66792) {
                    if (var3 != 68718) {
                        if (var3 != 80566) {
                            if (var3 == 81909 && var4.equals("Rad")) {
                                break label39;
                            }
                        } else if (var4.equals("Psy")) {
                            var2 = 1;
                            break label39;
                        }
                    } else if (var4.equals("Dis")) {
                        var2 = 3;
                        break label39;
                    }
                } else if (var4.equals("Bio")) {
                    var2 = 2;
                    break label39;
                }

                var2 = -1;
            }

            Intent var5;
            switch(var2) {
                case 0:
                    Toast.makeText(this.getApplicationContext(), "Вы умерли от Радиации", Toast.LENGTH_LONG).show();
                    var5 = new Intent("StatsService.Message");
                    var5.putExtra("Message", "H");
                    this.sendBroadcast(var5);
                    break;
                case 1:
                    Toast.makeText(this.getApplicationContext(), "Вы умерли от Пси", Toast.LENGTH_LONG).show();
                    var5 = new Intent("StatsService.Message");
                    var5.putExtra("Message", "P");
                    this.sendBroadcast(var5);
                    break;
                case 2:
                    Toast.makeText(this.getApplicationContext(), "Вы умерли от Био", Toast.LENGTH_LONG).show();
                    var5 = new Intent("StatsService.Message");
                    var5.putExtra("Message", "H");
                    this.sendBroadcast(var5);
                    break;
                case 3:
                    Toast.makeText(this.getApplicationContext(), "Вы умерли от Выброса°", Toast.LENGTH_LONG).show();
                    var5 = new Intent("StatsService.Message");
                    var5.putExtra("Message", "H");
                    this.sendBroadcast(var5);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCreate() {
        super.onCreate();
        latLngAnomaly = new LatLng(0, 0);
        this.wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "STALKERNET:My_Partial_Wake_Lock");
        this.wl.acquire(10*60*1000L /*10 minutes*/);   //timeout заставила студия поставить, не знаю как это работает
        this.EM = new EffectManager(this);
        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.create_db();
        NUMBER_OF_ANOMALIES = getNumberOfAnomalies();
        GetAnomalies();
        CreateSafeZones();
        LoadStats();
        //Create_super_save_zones();
        create_nightZones();
        create_stalkers_zones_in();
        create_constantZones();
        this.mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        //startForeground(101, new Builder(this, Build.VERSION.SDK_INT >= 26 ? createNotificationChannel((NotificationManager) getSystemService("notification")) : "").setOngoing(true).setSmallIcon(R.drawable.ic_launcher_background).setPriority(1).setCategory("service").setContentTitle("StatsService").setContentText("Stats are being updated.").build());
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    IMPORTANCE_HIGH);

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

    private int getNumberOfAnomalies(){
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

    // гештальт аномалию 180 сек. нельзя снова открыть
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
    private void GetAnomalies() {
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
                    anomalyArr[cursor.getInt(idIndex)-1] = new Anomaly(cursor.getString(polygon_type), cursor.getString(type), cursor.getDouble(power), cursor.getDouble(radius), new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), this,cursor.getInt(gestaltStatus),cursor.getString(boolShow));
                    anomalyArr[cursor.getInt(idIndex)-1].minstrenght = cursor.getDouble(minPower);
                } else{
                    anomalyArr[cursor.getInt(idIndex)-1] = new MonolithAnomaly(cursor.getString(polygon_type), cursor.getString(type), cursor.getDouble(power), cursor.getDouble(radius), new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), this,cursor.getInt(gestaltStatus),cursor.getString(boolShow));

                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();

        /*// чистое небо 2021
        anomalyArr[0] = new Anomaly("Circle", "ClS", 10d, 65.0d, new LatLng(64.350906d, 40.719229d), this, 0, "true"); // особая аномаль чистого неба
        anomalyArr[0].minstrenght = 10;
        // пси пустошь 2021
        anomalyArr[1] = new Anomaly("Circle", "Psy", 1.0d, 50d, new LatLng(64.5736076d, 40.516662d), this, 0, "true"); //адмиралтейская
        anomalyArr[1].minstrenght = 1;
        anomalyArr[2] = new Anomaly("Circle", "Bio", 1.0d, 90d, new LatLng(64.573608d, 40.516663d), this, 0, "true"); // кпп майдан
        anomalyArr[2].minstrenght = 1;*/
        /*anomalyArr[3] = new Anomaly("Circle", "Psy", 50000d, 90d, new LatLng(64.352655d, 40.742348d), this, 0, true); // 65-42-3
        anomalyArr[3].minstrenght = 10000;
        anomalyArr[4] = new Anomaly("Circle", "Psy", 50000d, 90d, new LatLng(64.355109d, 40.739336d), this, 0, true); // 66-43-8
        anomalyArr[4].minstrenght = 10000;
        anomalyArr[5] = new Anomaly("Circle", "Psy", 50000d, 50.0d, new LatLng(64.356078d, 40.741000d), this, 0, true); // 65-41-3
        anomalyArr[5].minstrenght = 10000;
        anomalyArr[6] = new Anomaly("Circle", "Psy", 50000d, 50.0d, new LatLng(64.354222d, 40.738711d), this, 0, true); // 69-44-1
        anomalyArr[6].minstrenght = 10000;
        anomalyArr[7] = new MonolithAnomaly("Circle", "", 0.0d,  30.0d, new LatLng(64.357216d, 40.721512d), this, 0, false); // 72-44-4
        anomalyArr[8] = new MonolithAnomaly("Circle", "", 0.0d,  30.0d, new LatLng(64.353651d, 40.743639d), this, 0, false); // 72-44-4
        // постояные 2021
        anomalyArr[9] = new Anomaly("Circle", "Psy", 20d, 10.0d, new LatLng(64.353083d, 40.734060d), this, 0, true);// 66-39
        anomalyArr[10] = new Anomaly("Circle", "Psy", 20d, 10.0d, new LatLng(64.352305d, 40.733189d), this, 0, true);  // 67-42
        anomalyArr[11] = new Anomaly("Circle", "Psy", 20d, 10.0d, new LatLng(64.352340d, 40.735277d), this, 0, true);  // 69-38-4
        // с 6 сентября
        anomalyArr[12] = new Anomaly("Circle", "Rad", 0.3d, 40.0d, new LatLng(64.356195d, 40.737017d), this, 0, false); //64-37-9
        anomalyArr[13] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.356053d, 40.736761d), this, 0, true); // in 64-37-9
        anomalyArr[14] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.356148d, 40.737472d), this, 0, true); // in 64-37-9
        anomalyArr[15] = new Anomaly("Circle", "Rad", 0.3d, 20.0d, new LatLng(64.354073d, 40.729209d), this, 0, false); //65-34-4
        anomalyArr[16] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.354114d, 40.729009d), this, 0, true); // in 65-34-4
        anomalyArr[17] = new Anomaly("Circle", "Rad", 0.3d, 35.0d, new LatLng(64.355738d, 40.725383d), this, 0, false);  //64-35-8
        anomalyArr[18] = new Anomaly("Circle", "Rad", 25.0d, 10.0d, new LatLng(64.355755d, 40.725650d), this, 0, true); // in 64-35-8
        anomalyArr[19] = new Anomaly("Circle", "Bio", 0.3d, 70.0d, new LatLng(64.35441610258081d, 40.720545362547554d), this, 0, false); // 65-33
        anomalyArr[20] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35488981566387d, 40.720510796852864d), this, 0, true);  //in 65-33
        anomalyArr[21] = new Anomaly("Circle", "Bio", 30.0d, 15.0d, new LatLng(64.35455141304958d, 40.72072013505793d), this, 0, true);  //in 65-33
        anomalyArr[22] = new Anomaly("Circle", "Bio", 10.0d, 20.0d, new LatLng(64.35425529146416d, 40.720171258543d), this, 0, true);  //in 65-33
        anomalyArr[23] = new Anomaly("Circle", "Bio", 0.3d, 40.0d, new LatLng(64.353853407859d, 40.72373129780839d), this, 0, false);  //65-34-8
        anomalyArr[24] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.35395682176382d, 40.724106707510835d), this, 0, true);  //in 65-33
        anomalyArr[25] = new Anomaly("Circle", "Bio", 15.0d, 10.0d, new LatLng(64.35384326298667d, 40.723357047115236d), this, 0, true);  //in 65-33
        anomalyArr[26] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.35367638665042d, 40.72390097232614d), this, 0, true);  //бродячая
        anomalyArr[27] = new Anomaly("Circle", "Bio", 0.3d, 40.0d, new LatLng(64.35447294898991d, 40.72723301461363d), this, 0, false);  //65-35
        anomalyArr[28] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35449758103714d, 40.726882196361224d), this, 0, true);  //in 65-35
        anomalyArr[29] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.35433031976842d, 40.727510207809345d), this, 0, true);  //in 65-33
        anomalyArr[30] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.35292560012574d, 40.728554496046826d), this, 0, false);  //66-35
        anomalyArr[31] = new Anomaly("Circle", "Rad", 20.0d, 15.0d, new LatLng(64.35320380110174d, 40.731077622120544d), this, 0, true);  //бродячая
        anomalyArr[32] = new Anomaly("Circle", "Rad", 0.3d, 30.0d, new LatLng(64.35182301142662d, 40.73314889141106d), this, 0, false);  //66-36
        anomalyArr[33] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.35192608419467d, 40.73324431693408d), this, 0, true);  //in 66-36
        // 7 september
        anomalyArr[34] = new Anomaly("Circle", "Rad", 20.0d, 15.0d, new LatLng(64.35623672541492d, 40.737128289701104d), this, 0, true);  //walking
        anomalyArr[35] = new Anomaly("Circle", "Bio", 0.3d, 90.0d, new LatLng(64.35267682519967d, 40.717356305038564d), this, 0, false);  //
        anomalyArr[36] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.35248742217176d, 40.717968083745006d), this, 0, true);  //
        anomalyArr[37] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.35247246224212d, 40.71681505037958d), this, 0, true);  //
        anomalyArr[38] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.35285089804125d, 40.71695265512005d), this, 0, true);  //
        anomalyArr[39] = new Anomaly("Circle", "Bio", 50.0d, 15.0d, new LatLng(64.35289574876842d, 40.717908062517054d), this, 0, true);  // walking
        anomalyArr[40] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.35396930076678d, 40.73175313604288d), this, 0, false);  //
        anomalyArr[41] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.35490024649128d, 40.73339218798626d), this, 0, false);  //
        anomalyArr[42] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35490024649128d, 40.73339218798626d), this, 0, true);  //walking
        anomalyArr[43] = new Anomaly("Circle", "Bio", 0.3d, 50.0d, new LatLng(64.35087133466631d, 40.73089835104761d), this, 0, false);  //
        anomalyArr[44] = new Anomaly("Circle", "Bio", 30.0d, 10.0d, new LatLng(64.35085395411117d, 40.731653106859845d), this, 0, true);  //
        anomalyArr[45] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35089663069634d, 40.730492624952184d), this, 0, true);  //
        // 8 september
        anomalyArr[46] = new Anomaly("Circle", "Bio", 0.3d, 50.0d, new LatLng(64.35520185416888d, 40.71830669874832d), this, 0, false);  //
        anomalyArr[47] = new Anomaly("Circle", "Bio", 0.3d, 15.0d, new LatLng(64.35502114565935d, 40.72216400684764d), this, 0, false);  //
        anomalyArr[48] = new Anomaly("Circle", "Bio", 0.3d, 35.0d, new LatLng(64.35517409604017d, 40.72371844396568d), this, 0, false);  //
        anomalyArr[49] = new Anomaly("Circle", "Bio", 0.3d, 15.0d, new LatLng(64.35477016330874d, 40.725625143377364d), this, 0, false);  //
        anomalyArr[50] = new Anomaly("Circle", "Bio", 0.3d, 25.0d, new LatLng(64.35436057188933d, 40.727528543378405d), this, 0, false);  //
        anomalyArr[51] = new Anomaly("Circle", "Bio", 0.3d, 35.0d, new LatLng(64.3560712892297d, 40.728562345880114d), this, 0, false);  //
        anomalyArr[52] = new Anomaly("Circle", "Bio", 30.0d, 15.0d, new LatLng(64.35597386833422d, 40.72846537301083d), this, 0, true);  //
        anomalyArr[53] = new Anomaly("Circle", "Rad", 0.3d, 65.0d, new LatLng(64.35443493285447d, 40.72570334131888d), this, 0, false);  //
        anomalyArr[54] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.35423985960506d, 40.725333100380695d), this, 0, true);  //
        anomalyArr[55] = new Anomaly("Circle", "Rad", 0.3d, 70.0d, new LatLng(64.35595853414726d, 40.725026029581535d), this, 0, false);  //
        anomalyArr[56] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.356137960433d, 40.724578310765345d), this, 0, true);  //
        anomalyArr[57] = new Anomaly("Circle", "Rad", 0.3d, 40.0d, new LatLng(64.35299097571637d, 40.72437621212613d), this, 0, false);  //
        anomalyArr[58] = new Anomaly("Circle", "Rad", 10.0d, 15.0d, new LatLng(64.35304555634212d, 40.72396918133599d), this, 0, true);  //
        anomalyArr[59] = new Anomaly("Circle", "Rad", 25.0d, 15.0d, new LatLng(64.35222610146626d, 40.72358407989295d), this, 0, true);  //walking
        anomalyArr[60] = new Anomaly("Circle", "Rad", 0.3d, 70.0d, new LatLng(64.35100261846891d, 40.72517651843651d), this, 0, false);  //
        anomalyArr[61] = new Anomaly("Circle", "Rad", 20.0d, 15.0d, new LatLng(64.35065790903623d, 40.72526482745829d), this, 0, true);  //
        anomalyArr[62] = new Anomaly("Circle", "Rad", 30.0d, 15.0d, new LatLng(64.35108671536487d, 40.72557717571212d), this, 0, true);  //
        anomalyArr[63] = new Anomaly("Circle", "Rad", 40.0d, 10.0d, new LatLng(64.35119817646464d, 40.724595397862686d), this, 0, true);  //
        anomalyArr[64] = new Anomaly("Circle", "Rad", 0.3d, 35.0d, new LatLng(64.3529490708463d, 40.73389950191273d), this, 0, false);  //
        anomalyArr[65] = new Anomaly("Circle", "Rad", 10.0d, 5.0d, new LatLng(64.35291473113561d, 40.734527435257796d), this, 0, true);  //
        anomalyArr[66] = new Anomaly("Circle", "Rad", 30.0d, 5.0d, new LatLng(64.35276287997047d, 40.73356693066024d), this, 0, true);  //
        anomalyArr[67] = new Anomaly("Circle", "Rad", 20.0d, 5.0d, new LatLng(64.35310069323374d, 40.73361043208629d), this, 0, true);  //
        anomalyArr[68] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.35400045474597d, 40.72952355709435d), this, 0, false);  //
        anomalyArr[69] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.35323883585698d, 40.73111057506516d), this, 0, true);  //walking
        anomalyArr[70] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.35278372651375d, 40.728019600236955d), this, 0, true);  //walking
        anomalyArr[71] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.35108577387157d, 40.73010424116759d), this, 0, true);  //
        anomalyArr[72] = new Anomaly("Circle", "Bio", 0.3d, 45.0d, new LatLng(64.35082441377548d, 40.730144189669254d), this, 0, false);  //
        anomalyArr[73] = new Anomaly("Circle", "Bio", 20.0d, 5.0d, new LatLng(64.35099277816369d, 40.73047791910535d), this, 0, true);  //
        anomalyArr[74] = new Anomaly("Circle", "Bio", 15.0d, 10.0d, new LatLng(64.35064836311987d, 40.729697237730285d), this, 0, true);  //
        anomalyArr[75] = new Anomaly("Circle", "Bio", 0.3d, 70.0d, new LatLng(64.3505003251989d, 40.73922686433046d), this, 0, false);  //
        anomalyArr[76] = new Anomaly("Circle", "Bio", 50.0d, 15.0d, new LatLng(64.35077368297742d, 40.73883501767552d), this, 0, true);  //
        anomalyArr[77] = new Anomaly("Circle", "Bio", 10.0d, 5.0d, new LatLng(64.3503292179291d, 40.73893356178716d), this, 0, true);  //
        //9 september
        anomalyArr[78] = new Anomaly("Circle", "Bio", 0.3d, 50.0d, new LatLng(64.35015499058346d, 40.73124464936575d), this, 0, false);  //
        anomalyArr[79] = new Anomaly("Circle", "Bio", 0.3d, 60.0d, new LatLng(64.35038193850691d, 40.739418472054716d), this, 0, false);  //
        anomalyArr[80] = new Anomaly("Circle", "Bio", 0.3d, 60.0d, new LatLng(64.35216722962195d, 40.73807295429279d), this, 0, false);  //
        anomalyArr[81] = new Anomaly("Circle", "Bio", 30.0d, 15.0d, new LatLng(64.35195100580208d, 40.737319620613555d), this, 0, true);  //
        anomalyArr[82] = new Anomaly("Circle", "Bio", 0.3d, 35.0d, new LatLng(64.35200950328962d, 40.73315607731875d), this, 0, false);  //
        anomalyArr[83] = new Anomaly("Circle", "Bio", 0.3d, 35.0d, new LatLng(64.35196972598118d, 40.725731833633354d), this, 0, false);  //
        anomalyArr[84] = new Anomaly("Circle", "Bio", 0.3d, 80.0d, new LatLng(64.35290967022628d, 40.723924335732164d), this, 0, false);  //
        anomalyArr[85] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.35263065606567d, 40.725084047409524d), this, 0, true);  //
        anomalyArr[86] = new Anomaly("Circle", "Bio", 10.0d, 5.0d, new LatLng(64.35280182419676d, 40.72434192111655d), this, 0, true);  //
        anomalyArr[87] = new Anomaly("Circle", "Bio", 25.0d, 10.0d, new LatLng(64.35266549960997, 40.72334777871776d), this, 0, true);  //
        anomalyArr[88] = new Anomaly("Circle", "Bio", 15.0d, 10.0d, new LatLng(64.35318405230508d, 40.72356234187144d), this, 0, true);  //
        anomalyArr[89] = new Anomaly("Circle", "Bio", 0.3d, 70.0d, new LatLng(64.35429318087041d, 40.735303403897525d), this, 0, false);  //
        anomalyArr[90] = new Anomaly("Circle", "Bio", 50.0d, 10.0d, new LatLng(64.35373614663098d, 40.73573880968889d), this, 0, true);  //
        anomalyArr[91] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.35425514956867d, 40.73629037396034d), this, 0, true);  //
        anomalyArr[92] = new Anomaly("Circle", "Bio", 15.0d, 5.0d, new LatLng(64.35422056128651d, 40.73544968240225d), this, 0, true);  //
        anomalyArr[93] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35438978132431d, 40.734596016788544d), this, 0, true);  //
        anomalyArr[94] = new Anomaly("Circle", "Bio", 20.0d, 10.0d, new LatLng(64.35465789466718d, 40.735591741868134d), this, 0, true);  //
        anomalyArr[95] = new Anomaly("Circle", "Bio", 0.3d, 20.0d, new LatLng(64.35563089619237d, 40.73456604381825d), this, 0, false);  //
        anomalyArr[96] = new Anomaly("Circle", "Bio", 0.3d, 40.0d, new LatLng(64.35550764560742d, 40.726272603384444d), this, 0, false);  //
        anomalyArr[97] = new Anomaly("Circle", "Rad", 25.0d, 10.0d, new LatLng(64.35563755498839d, 40.722948500302955d), this, 0, true);  //
        anomalyArr[98] = new Anomaly("Circle", "Rad", 0.3d, 80.0d, new LatLng(64.35416547504563d, 40.719762461083505d), this, 0, false);  //
        anomalyArr[99] = new Anomaly("Circle", "Rad", 10.0d, 5.0d, new LatLng(64.35422415016498d, 40.72080291185718d), this, 0, true);  //
        anomalyArr[100] = new Anomaly("Circle", "Rad", 20.0d, 15.0d, new LatLng(64.35385142701769d, 40.71898062666323d), this, 0, true);  //
        anomalyArr[101] = new Anomaly("Circle", "Rad", 15.0d, 10.0d, new LatLng(64.35386625330369d, 40.71995569454652d), this, 0, true);  //
        anomalyArr[102] = new Anomaly("Circle", "Rad", 20.0d, 5.0d, new LatLng(64.35419487158974d, 40.719660473659744d), this, 0, true);  //
        anomalyArr[103] = new Anomaly("Circle", "Rad", 0.3d, 30.0d, new LatLng(64.3544538944255d, 40.723186400732935d), this, 0, false);  //
        anomalyArr[104] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.35447830720575d, 40.723423340694616d), this, 0, true);  //
        anomalyArr[105] = new Anomaly("Circle", "Rad", 0.3d, 50.0d, new LatLng(64.35383900933293d, 40.72746875692112d), this, 0, false);  //
        anomalyArr[106] = new Anomaly("Circle", "Rad", 70.0d, 10.0d, new LatLng(64.35374207549692d, 40.72686678795913d), this, 0, true);  //
        anomalyArr[107] = new Anomaly("Circle", "Rad", 70.0d, 15.0d, new LatLng(64.35380966062684d, 40.72800631707114d), this, 0, true);  //
        anomalyArr[108] = new Anomaly("Circle", "Rad", 25.0d, 10.0d, new LatLng(64.35392743705654d, 40.72682310973268d), this, 0, true);  //
        */
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
        //Log.d("minutes", String.valueOf(new LatLng(start_LatLng.latitude + (dLat * (double) Minutes), start_LatLng.longitude + (dLng * (double) Minutes))));
        //return start_LatLng;
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

    // применяет аномалии
    // вызывается в MyLocationCallback()
    public void CheckAnomalies() {
        Anomaly anomaly;
        if (IS_ANOMALIES_AVAILABLE && !isInSuperSaveZone) {
            long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
            // постоянные аномалии
            if (timeInSeconds > dayFirst) { // 6 сентября в 17:00
                int ClSky;
                if (ClearSkyAnomalyProtection){
                     ClSky = 1;
                } else if (MonolithOk){
                    ClSky = 7;
                } else{
                    ClSky = 0;
                }
                for (int i = ClSky; i < NUMBER_OF_ANOMALIES; i++) {
                    anomalies[i].Apply();
                }

            }
            // с 6 сентября в 17:00
/*            CheckAnomaliesRegular(dayFirst, daySecond, 12, 34);
            // c 7 сентября в 17:23
            CheckAnomaliesRegular(daySecond, dayThird, 12, 46);
            // c 8 сентября в 10:47
            CheckAnomaliesRegular(dayThird, dayFourth, 46, 78);
            // c 9 сентября в 13:37
            CheckAnomaliesRegular(dayFourth, (dayFourth + dayFourth), 78, 109);*/
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
                        //Log.d("аномалия_inside", String.valueOf(i));
                        contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "true");
                        cursor.moveToPosition(i +1);
                        database.update(DBHelper.TABLE_ANOMALY, contentValues,  DBHelper.KEY_ID_ANOMALY + "=" + (cursor.getPosition()-1), null);
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
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{ "bool_show_on_map"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "false");
                database.update(DBHelper.TABLE_ANOMALY, contentValues, null, null);
            } while (cursor.moveToNext());
        }

        detectWifi();

        if (IS_ANOMALIES_AVAILABLE && !isInSuperSaveZone) {

            int ClSky;
            if (ClearSkyAnomalyProtection){
                ClSky = 1;
            } else if (MonolithOk){
                ClSky = 7;
            }else{
                ClSky = 0;
            }
            for (int i = ClSky ; i < NUMBER_OF_ANOMALIES; i++) {
                if (anomalies[i].IsInside) {
                    if (anomalies[i].toShow) {
                        Log.d("аномалия_inside", String.valueOf(i));
                        contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP, "true");
                        cursor.moveToPosition(i+1);
                        database.update(DBHelper.TABLE_ANOMALY, contentValues,  DBHelper.KEY_ID_ANOMALY + "=" + (cursor.getPosition()), null);
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

                            }
                        }
                    }
                }
            }
            // с 6 сентября в 17:00
/*            CheckIfInAnyAnomalyRegular(dayFirst, daySecond, 12, 34);
            // c 7 сентября в 17:23
            CheckIfInAnyAnomalyRegular(daySecond, dayThird, 12, 46);
            // c 8 сентября в 10:47
            CheckIfInAnyAnomalyRegular(dayThird, dayFourth, 46, 78);
            // c 9 сентября в 13:37
            CheckIfInAnyAnomalyRegular(dayFourth, (dayFourth + dayFourth), 78, 109);*/
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



    LatLng[] nightZones = new LatLng[12];
    public void create_nightZones(){
        LatLng[] latLngs = new LatLng[12];
        latLngs[0] = new LatLng(64.35635328466024d, 40.740526559484145d);// ночные
        latLngs[1] = new LatLng(64.35648749695677d, 40.74024632672407d);//
        latLngs[2] = new LatLng(64.35662201786853d, 40.7399589942588d);//
        latLngs[3] = new LatLng(64.3567484782622d, 40.73967596773043d);// ночные
        latLngs[4] = new LatLng(64.35190461794156d, 40.740989107818386d);//ночные
        latLngs[5] = new LatLng(64.35186262065666d, 40.74058758789669d);// ночные
        latLngs[6] = new LatLng(64.3517975167356d, 40.74019942527292d);// ночные
        latLngs[7] = new LatLng(64.3548623948148d, 40.73747803747212d);// ночные
        latLngs[8] = new LatLng(64.35479771816406d, 40.73709830414507d);// ночные
        latLngs[9] = new LatLng(64.35279819399817d, 40.73884715079873d);//ночная
        latLngs[10] = new LatLng(64.35597785931614d, 40.73796221719635d);
        latLngs[11] = new LatLng(64.35606751644602d, 40.73759465692494d);
        nightZones = latLngs;
    }
    LatLng[] constantZones = new LatLng[2];
    public void create_constantZones(){
        LatLng[] latLngs = new LatLng[2];
        latLngs[0] = new LatLng(64.352410d, 40.739851d);//
        latLngs[1] = new LatLng(64.352406d, 40.739431d);//
        constantZones = latLngs;
    }
    LatLng[] stalkers_save_zones_in = new LatLng[96];
    public void create_stalkers_zones_in(){
        LatLng[] latLngs = new LatLng[96];
        latLngs[0] = new LatLng(64.354129d, 40.743913d);// кпп и далее к выходу
        latLngs[1] = new LatLng(64.354235d, 40.744258d);
        latLngs[2] = new LatLng(64.35432521220459d, 40.74460558362928d);
        latLngs[3] = new LatLng(64.35445293046173d, 40.744923831508146d);
        latLngs[4] = new LatLng(64.3545824026996d, 40.745214162825555d);
        latLngs[5] = new LatLng(64.35472209740922d, 40.74546291301759d);
        latLngs[6] = new LatLng(64.35489346502892d, 40.745582845300504d);
        latLngs[7] = new LatLng(64.35500782797686d, 40.74526722512475d);
        latLngs[8] = new LatLng(64.35507067122896d, 40.74488372241825d);
        latLngs[9] = new LatLng(64.35513902594106d, 40.74451641974487d);
        latLngs[10] = new LatLng(64.3552087889606d, 40.74412309924951d);
        latLngs[11] = new LatLng(64.3552827954611d, 40.743742309081476d);
        latLngs[12] = new LatLng(64.35534417931994d, 40.74335453065853d);
        latLngs[13] = new LatLng(64.35541176405508d, 40.74296775537176d);
        latLngs[14] = new LatLng(64.35555348581504d, 40.742687429518696d);
        latLngs[15] = new LatLng(64.35563584135113d, 40.742319967224745d);
        latLngs[16] = new LatLng(64.3557006595403d, 40.74194897608368d);
        latLngs[17] = new LatLng(64.3558133075042d, 40.74161370149263d);
        latLngs[18] = new LatLng(64.35595448110925d, 40.74133608438898d);
        latLngs[19] = new LatLng(64.35609017983464d, 40.74107015979084d);
        latLngs[20] = new LatLng(64.35624275507247d, 40.74082160943004d);

        latLngs[21] = new LatLng(64.35295356108362d, 40.7434670952109d); //толчек у монолита
        latLngs[22] = new LatLng(64.35280265443103d, 40.74370276887028d);
        latLngs[23] = new LatLng(64.3526529104659d, 40.74394533079032d);
        latLngs[24] = new LatLng(64.35247292472532d, 40.74397037100172d);
        latLngs[25] = new LatLng(64.35234879448167d, 40.74365386149042d);
        latLngs[26] = new LatLng(64.35225051500136d, 40.7433202479478d);
        latLngs[27] = new LatLng(64.3521427267776d, 40.74298846111945d);
        latLngs[28] = new LatLng(64.35208781625262d, 40.74256105302851d);
        latLngs[29] = new LatLng(64.35206614369244d, 40.742176031451784d);
        latLngs[30] = new LatLng(64.35203133624847d, 40.74175692849512d);
        latLngs[31] = new LatLng(64.3519613993229d, 40.741380966909865d);

        latLngs[32] = new LatLng(64.35276759841277d, 40.74260360547909d); // путь военных
        latLngs[33] = new LatLng(64.35269821146606d, 40.742199371531434d);
        latLngs[34] = new LatLng(64.35259972068465d, 40.741861999009714d);
        latLngs[35] = new LatLng(64.35251103312895d, 40.741499425573956d);
        latLngs[36] = new LatLng(64.35247640158082d, 40.74108919460003d);
        latLngs[37] = new LatLng(64.35243869370726d, 40.74068559251896d);
        latLngs[38] = new LatLng(64.35242336435232d, 40.740270397903735d);

        latLngs[39] = new LatLng(64.35317903326629d, 40.74211621986506d); // самый жирный
        latLngs[40] = new LatLng(64.3530676855036d, 40.74177850748372d);
        latLngs[41] = new LatLng(64.35307759043815d, 40.74136361752823d);
        latLngs[42] = new LatLng(64.35311559862588d, 40.740947540217554d);
        latLngs[43] = new LatLng(64.35320833036639d, 40.7406033580693d);
        latLngs[44] = new LatLng(64.3533427514016d, 40.74088981008373d);
        latLngs[45] = new LatLng(64.3534273880053d, 40.74125741173395d);
        latLngs[46] = new LatLng(64.3535272506385d, 40.7416104341624d);
        latLngs[47] = new LatLng(64.35369931415698d, 40.74167856221654d);
        latLngs[48] = new LatLng(64.35385106807306d, 40.741420371104475d);
        latLngs[49] = new LatLng(64.35399244102797d, 40.74117024644733d);
        latLngs[50] = new LatLng(64.35399947701931d, 40.740757545914946d);
        latLngs[51] = new LatLng(64.35413600828822d, 40.74047520189392d);
        latLngs[52] = new LatLng(64.35431770759529d, 40.74047530249902d);
        latLngs[53] = new LatLng(64.35448818768883d, 40.74060662377004d);
        latLngs[54] = new LatLng(64.3546205550841d, 40.740366451897d);
        latLngs[55] = new LatLng(64.35463945956784d, 40.73993880157785d);
        latLngs[56] = new LatLng(64.35465628840566d, 40.739523336212606d);
        latLngs[57] = new LatLng(64.35468482593951d, 40.73910560830513d);
        latLngs[58] = new LatLng(64.35483549689444d, 40.738899002714625d);
        latLngs[59] = new LatLng(64.354993892013d, 40.73867466493646d);
        latLngs[60] = new LatLng(64.35498453249937d, 40.7382720979003d);
        latLngs[61] = new LatLng(64.35492709824331d, 40.7378803923697d);

        latLngs[62] = new LatLng(64.35289419918085d, 40.74006914469832d);//самая короткая
        latLngs[63] = new LatLng(64.35282882522654d, 40.73965389027957d);
        latLngs[64] = new LatLng(64.35281241378898d, 40.739259701118684d);

        latLngs[65] = new LatLng(64.35327464552576d, 40.74278657071493d); // самая важная
        latLngs[66] = new LatLng(64.35338909350193d, 40.74310977309313d);
        latLngs[67] = new LatLng(64.35339042102528d, 40.742703076129935d);
        latLngs[68] = new LatLng(64.35357354817818d, 40.74276856634414d);
        latLngs[69] = new LatLng(64.35374926753758d, 40.74276936833783d);
        latLngs[70] = new LatLng(64.35392842206795d, 40.74275131202157d);
        latLngs[71] = new LatLng(64.35406736165523d, 40.74247493169485d);
        latLngs[72] = new LatLng(64.35424611985239d, 40.7425261775552d);
        latLngs[73] = new LatLng(64.35442159914702d, 40.74253051689229d);
        latLngs[74] = new LatLng(64.35459589159377d, 40.74244715268726d);
        latLngs[75] = new LatLng(64.35473228695206d, 40.7421457820547d);
        latLngs[76] = new LatLng(64.35474349130439d, 40.74172442186285d);
        latLngs[77] = new LatLng(64.35474117057692d, 40.741743502867166);
        latLngs[78] = new LatLng(64.35480112579775d, 40.741346431397666d);
        latLngs[79] = new LatLng(64.35492614666789d, 40.741040636954516d);
        latLngs[80] = new LatLng(64.3550648535183d, 40.740787868746104d);
        latLngs[81] = new LatLng(64.35523643165064d, 40.74092887045961d);
        latLngs[82] = new LatLng(64.35535259212864d, 40.740618210093004d);
        latLngs[83] = new LatLng(64.35538887008047d, 40.740199498863824d);
        latLngs[84] = new LatLng(64.35546413114129d, 40.73981468710406d);
        latLngs[85] = new LatLng(64.35556608591283d, 40.739482952991544d);
        latLngs[86] = new LatLng(64.35566787403617d, 40.739141199432446d);
        latLngs[87] = new LatLng(64.35562164086585d, 40.73873399434004d);
        latLngs[88] = new LatLng(64.35566212114979d, 40.7383278893475d);
        latLngs[89] = new LatLng(64.3558329544728d, 40.738182241545026d);
        latLngs[90] = new LatLng(64.35471398517412d, 40.74072987952912d);//отворотки
        latLngs[91] = new LatLng(64.3540625268518d, 40.74008438658268d);//отворотки
        latLngs[92] = new LatLng(64.35388053787214d, 40.74008035258441d);//отворотки
        latLngs[93] = new LatLng(64.35511363345678d, 40.7435522554439d);//отворотки
        latLngs[94] = new LatLng(64.35499368143708d, 40.74324220074001d);//отворотки
        latLngs[95] = new LatLng(64.35366985130402d, 40.74305883740328d);//
        stalkers_save_zones_in = latLngs;
    }

    public boolean isInSuperSaveZone = false;
    public void Super_save_zone_check(){
        isInSuperSaveZone = false;
        if (((Calendar.getInstance().getTimeInMillis() / 1000) >= dayFirst) /*&& ((Calendar.getInstance().getTimeInMillis() / 1000) <= (checkTime_in + 3600))*/){
            if (Hour >= 20 || Hour <= 4) {
                for (LatLng latLng : nightZones){
                    Location location = new Location("GPS");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    if (location.distanceTo(MyCurrentLocation) <= 17){
                        isInSuperSaveZone = true;
                    }

                }
            }
            for (LatLng latLng : stalkers_save_zones_in){
                Location location = new Location("GPS");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                if (location.distanceTo(MyCurrentLocation) <= 17){
                    isInSuperSaveZone = true;
                }

            }
            for (LatLng latLng : constantZones){
                Location location = new Location("GPS");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                if (location.distanceTo(MyCurrentLocation) <= 17){
                    isInSuperSaveZone = true;
                }

            }
        }
    }


    public void CreateSafeZones() {
        SafeZone[] safeZoneArr = new SafeZone[NUMBER_OF_SAVE_ZONES];
        safeZoneArr[0] = new SafeZone("Circle", 50.0d, new LatLng(64.351080d, 40.736224d), this); // Свобода
        safeZoneArr[1] = new SafeZone("Circle", 80.0d, new LatLng(64.357220d, 40.721517d), this); // денисовичи
        safeZoneArr[2] = new SafeZone("Circle", 40.0d, new LatLng(64.351663d, 40.727578d), this); // гараж
        safeZoneArr[3] = new SafeZone("Circle", 40.0d, new LatLng(64.349906d, 40.725957d), this); // у озера
        safeZoneArr[4] = new SafeZone("Circle", 40.0d, new LatLng(64.350906d, 40.719229d), this); // чн
        this.SafeZones = safeZoneArr;
    }


    public void CheckIfInAnySafezone() {
        int i = 0;
        if (!FreedomDischargeImmunity){
            i = 1;
        }
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
        Toast.makeText(getApplicationContext(), "Близиться выброс.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            CheckIfInAnySafezone();
            EM.PlayBuzzer();
            if (!(IsInsideSafeZone || DischargeImmunity)) {
                LastTimeHitBy = "Dis";
                setDead(Boolean.TRUE);
                Health = 0.0d;
                Intent intent = new Intent("StatsService.Message");
                intent.putExtra("Message", "H");
                sendBroadcast(intent);
            }
            Toast.makeText(getApplicationContext(), "Выброс Окончен!!", Toast.LENGTH_SHORT).show();
            IsDischarging = Boolean.FALSE;
        }, 60000);
    }

    // Нужно чтобы загружать из памяти массивы, которые из double были переведены в string
    public double[] StringArrToDoubleArr (String stringArr){
        return Arrays.stream(Objects.requireNonNull(defaultSharedPreferences.getString(stringArr, "0, 0, 0")).split(", ")).mapToDouble(Double::parseDouble).toArray();
    }

    SharedPreferences defaultSharedPreferences;
    public void LoadStats() {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.MaxHealth = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("MaxHealth", "2000")));
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
        this.ClearSkyAnomalyProtection = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("DolgDischargeImmunity", "false")));
        this.FreedomDischargeImmunity = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("NaemnikiDischargeImmunity", "false")));
        this.fastRadPurification = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("fastRadPurification", "false")));
        this.MonolithOk = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("MonolithOk", "false")));
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
        edit.putString("DolgDischargeImmunity", Boolean.toString(this.ClearSkyAnomalyProtection));
        edit.putString("NaemnikiDischargeImmunity", Boolean.toString(this.FreedomDischargeImmunity));
        edit.putString("fastRadPurification", Boolean.toString(this.fastRadPurification));
        edit.putString("MonolithOk", Boolean.toString(this.MonolithOk));
        edit.apply();
    }
}
