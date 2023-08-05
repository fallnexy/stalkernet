package com.example.stalkernet;

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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.stalkernet.anomaly.Anomaly;
import com.example.stalkernet.anomaly.GestaltAnomaly;
import com.example.stalkernet.anomaly.MineAnomaly;
import com.example.stalkernet.anomaly.OasisAnomaly;
import com.example.stalkernet.anomaly.QRAnomaly;
import com.example.stalkernet.anomaly.WifiAnomaly;
import com.example.stalkernet.playerCharacter.MilitaryCharacter;
import com.example.stalkernet.playerCharacter.MonolithCharacter;
import com.example.stalkernet.playerCharacter.PlayerCharacter;
import com.example.stalkernet.playerCharacter.StalkerCharacter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.GESTALT;
import static com.example.stalkernet.anomaly.Anomaly.MINE;
import static com.example.stalkernet.anomaly.Anomaly.OASIS;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;
import static com.example.stalkernet.anomaly.MineAnomaly.MINE_ACTIVATION;
import static com.example.stalkernet.anomaly.MineAnomaly.MINE_DAMAGE_PERCENT;
import static com.example.stalkernet.anomaly.MineAnomaly.MINE_DEACTIVATION;
import static com.example.stalkernet.anomaly.MineAnomaly.MINE_EXPLOSION;
import static com.example.stalkernet.anomaly.WifiAnomaly.CHIMERA_WIFI;
import static com.example.stalkernet.anomaly.WifiAnomaly.CONTROL_WIFI;
import static com.example.stalkernet.fragments.MapOSMTab.INTENT_MAP;
import static com.example.stalkernet.fragments.MapOSMTab.INTENT_MAP_UPDATE;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.BIO_PROTECTION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.CONTAMINATION_2D_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.DEAD_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.FACTION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.FACTION_POSITION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.GESTALT_PROTECTION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.HEALTH_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.LAST_TIME_HIT_BY_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.MAX_HEALTH_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.NAME_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PREFERENCE_NAME;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PROTECTIONS_AVAILABLE_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PSY_PROTECTION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.RAD_PROTECTION_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.USER_ID_KEY;
import static java.util.Objects.isNull;

public class StatsService extends Service {
    public static final String LOG_CHE = "ну чече";
    public static final String CHANNEL_ID = "my_channel_01";
    public static final String INTENT_SERVICE = "StRoulette";
    public static final String INTENT_SERVICE_PROTECTION = "protection";
    public static final String INTENT_SERVICE_USER_ID = "user_id";
    public static final String INTENT_SERVICE_MINE = "mine_anomaly";
    public static final String INTENT_SERVICE_SOUND = "effect_manager";
    public static final String INTENT_SERVICE_TEST_COORDINATE = "drag_marker";
    public static final String INTENT_SERVICE_VIBRATION = "vibration";
    public static final String INTENT_SERVICE_MAX_DRIFT = "max_drift";
    public static final String INTENT_SERVICE_DRIFT_CORRECTION = "drift_correction";
    public static final String SEND_CONTAMINATION = "send_contamination";
    public static final String SEND_TOTAL_PROTECTIONS = "send_total_protections";
    public static final String SEND_RAD_PROTECTIONS = "send_rad_protections";
    public static final String SEND_BIO_PROTECTIONS = "send_bio_protections";
    public static final String SEND_PSY_PROTECTIONS = "send_psy_protections";
    public static final String CURRENT_KEY = "current_key";
    public static final String MAX_DRIFT_KEY = "max_drift_key";
    public static final String DRIFT_CORRECTION_KEY = "drift_correction_key";

    public static final double MAX_CONTAMINATION = 1000;
    public static final double MAX_CONTAMINATION_LEGEND = 1500;
    public static final String GESTALT_ID = "1";
    // создает экземпляр персонажа игрока и аномалии
    private int current;
    public PlayerCharacter[] playerCharacter;
    public Anomaly[] anomaly;
    public Quest quest;
    public Tool tools;
    public QRAnomaly qrAnomaly;
    public WifiAnomaly wifiAnomaly;
    public Discharge discharge;
    public EffectManager effectManager;

    // нужные переменные

    int user_id = 1;
    private boolean isInsideAnomaly = false;
    private HashMap<String, Integer> anomalyMap = new HashMap<>();
    private HashMap<Integer, Integer> factionMap = new HashMap<>();

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    private MyLocationCallback LocationCallback;
    private boolean LocationUpdatesStarted = false;
    public Location myLocation = new Location("GPS");
    public Location myCurrentLocation;

    private int maxDrift;
    private int driftCorrection;
    public boolean scienceQR;
    //нужность переменных неизвестна
    //private static final int ID_SERVICE = 101;
    public int NUMBER_OF_ANOMALIES = 0; // задается в onCreate
    private boolean IS_ANOMALIES_AVAILABLE = true;


    public double Health = 2000.0d, MaxHealth = 2000.0d;
    public double Bio = 0.0d, Rad = 0.0d;
    public int MaxRad = 1000, MaxBio = 1000;



    public double[] RadProtectionArr = {0, 0, 0};
    public double[] BioProtectionArr = {0, 0, 0};
    public double[] PsyProtectionArr = {0, 0, 0};

    public double[] RadProtectionCapacityArr = {0, 0, 0};
    public double[] BioProtectionCapacityArr = {0, 0, 0};
    public double[] PsyProtectionCapacityArr = {0, 0, 0};


    public boolean DischargeImmunity = false;

    public boolean IsUnlocked = true;

    public boolean vibrate = true;
    public boolean Sound = true;


    private Calendar cal = Calendar.getInstance();
    private int Hour = cal.get(Calendar.HOUR_OF_DAY);
    private int Minutes = cal.get(12);
    private int dayInt = cal.get(5);

    private Random random = new Random();

    //новый монолит
    boolean isMonolith = false;

    private FusedLocationProviderClient mFusedLocationProvider;
    private PowerManager.WakeLock wl;

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
            if (var4 != null) {
                switch (var4){
                    case "RadPlusOne":
                        playerCharacter[current].applyProtection(qrAnomaly.getDamage(RAD));
                        break;
                    case "BioPlusOne":
                        playerCharacter[current].applyProtection(qrAnomaly.getDamage(BIO));
                        break;
                    case "PsyPlusOne":
                        playerCharacter[current].applyProtection(qrAnomaly.getDamage(PSY));
                        break;
                    case "HpPlusFive":
                        playerCharacter[current].setHealth(100);
                        break;
                    case "HpPlusSeven":
                        playerCharacter[current].setHealth(200);
                        break;
                    case "HpMinus25perCent":
                        playerCharacter[current].increaseHealthPercent(-15);
                        break;
                    case "HpMinus20perCent":
                        playerCharacter[current].increaseHealthPercent(-10);
                        break;
                    case "HpMinus10perCent":
                        playerCharacter[current].increaseHealthPercent(-5);
                        break;
                }
            }
            String type = intent.getStringExtra(INTENT_SERVICE_PROTECTION);
            if (type != null){
                ((StalkerCharacter) playerCharacter[1]).nullifyThirdProtection(type);
            }
            String mine = intent.getStringExtra(INTENT_SERVICE_MINE);
            if (!isNull(mine)){
                playerCharacter[current].setMineAvailable(true);
                effectManager.stopSound();
                if (mine.equals("true")){
                    effectManager.mineDisActivated();
                } else {
                    playerCharacter[current].increaseHealthPercent(MINE_DAMAGE_PERCENT);
                    effectManager.mineExplosion();
                }

            }
            String sound = intent.getStringExtra(INTENT_SERVICE_SOUND);
            if (sound != null) {
                switch (sound){
                    case MINE_ACTIVATION:
                        effectManager.mineActivated();
                        break;
                    case MINE_EXPLOSION:
                        effectManager.stopSound();
                        effectManager.mineExplosion();
                        break;
                    case MINE_DEACTIVATION:
                        effectManager.stopSound();
                        effectManager.mineDisActivated();
                }
            }
            String input = intent.getStringExtra(INTENT_SERVICE_TEST_COORDINATE);
            if (input != null){
                String[] testCoordinate = input.split(",");
                myCurrentLocation.setLatitude(Double.parseDouble(testCoordinate[0]));
                myCurrentLocation.setLongitude(Double.parseDouble(testCoordinate[1]));
            }
            input = intent.getStringExtra(INTENT_SERVICE_VIBRATION);
            if (input != null){
                vibrate = !vibrate;
            }
            input = intent.getStringExtra(INTENT_SERVICE_USER_ID);
            if (input != null){
                user_id = Integer.parseInt(input);

                cursor = database.query(DBHelper.TABLE_USER, new String[] {DBHelper.KEY_ID__USER, DBHelper.KEY_FACTION_ID__USER}, null, null, null, null, null);
                cursor.moveToPosition(user_id - 1);
                int factionIndex = cursor.getColumnIndex(DBHelper.KEY_FACTION_ID__USER);
                current = factionMap.get(cursor.getInt(factionIndex));
                cursor.close();


                saveStats();
                loadStats();
                quest.isQuestAvailable(input);
            }
            int intInput = intent.getIntExtra(INTENT_SERVICE_MAX_DRIFT, -1);
            if (intInput > 0){
                setMaxDrift(intInput);
            }
            intInput = intent.getIntExtra(INTENT_SERVICE_DRIFT_CORRECTION, -1);
            if (intInput > 0){
                setDriftCorrection(intInput);
            }
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
       public void onReceive(android.content.Context context, Intent intent) {
//принимает команды command
           int shit;
           HashMap<String, Integer> protectionSuitRewhriteMap = new HashMap<>();
           protectionSuitRewhriteMap.put("suit", 2);
           protectionSuitRewhriteMap.put("art", 1);
           protectionSuitRewhriteMap.put("quest", 0);
           label110: {
               String fromCommand = intent.getStringExtra("Command");
               MakeSplit(fromCommand);
               //Log.d("wtf_textCode", fromCommand);
               if (textCode.equals("sc1") | textCode.equals("sc2") | textCode.equals("sc3") | textCode.equals("del")) {
                   fromCommand = textCode;
               }
               //Log.d("wtf_textCode", fromCommand);
               Toast.makeText(StatsService.this.getApplicationContext(), fromCommand, Toast.LENGTH_LONG).show();
               ContentValues contentValues;
               int id;
               switch (fromCommand){
                   case "isMonolith":
                       isMonolith = true;
                       DischargeImmunity = true;
                       shit = -1;
                       break label110;
                   case "sc1":  //sc1 код по типу sc1@rad@suit@80@
                                //                 0   1   2   3
                       //try {
                           playerCharacter[current].setProtection(textCodeSplitted[1],textCodeSplitted[2],Double.parseDouble(textCodeSplitted[3]));
                       //} catch (Exception e) {
                       //    e.printStackTrace();
                       //}
                       shit = -1;
                       break label110;
                   case "sc2": // sc2@rad@25
                       //          0   1   2
                       // TODO переделать, чтобы было без if
                       if (textCodeSplitted[1].equals("rad") || textCodeSplitted[1].equals("bio")){
                           try {
                               playerCharacter[current].increaseContaminationUnitByPercent(textCodeSplitted[1], Double.parseDouble(textCodeSplitted[2]));
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                       if (textCodeSplitted[1].equals("hp")){
                           try {
                               playerCharacter[current].setHealth(playerCharacter[current].getHealth() + Float.parseFloat(textCodeSplitted[2]) * playerCharacter[current].getMaxHealth() / 100);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                       shit = -1;
                       break label110;
                   case "sc3": //TODO этот код и следующий связан с монолитом - переделать нафиг
                       int dRadius = 150 + 20 * Integer.parseInt(textCodeSplitted[4]);
                       int dPower = 1 + 2 * Integer.parseInt(textCodeSplitted[3]);
                       id = Integer.parseInt(textCodeSplitted[5]);

                       contentValues = new ContentValues();

                       contentValues.put(DBHelper.KEY_LATITUDE__ANOMALY, textCodeSplitted[1]);
                       contentValues.put(DBHelper.KEY_LONGITUDE__ANOMALY, textCodeSplitted[2]);
                       contentValues.put(DBHelper.KEY_POWER__ANOMALY, String.valueOf(dPower));
                       contentValues.put(DBHelper.KEY_RADIUS__ANOMALY, String.valueOf(dRadius));
                       int slot = NUMBER_OF_ANOMALIES - 5 + id;
                       Log.d("аномалии", String.valueOf(slot));
                       database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=?", new String[]{String.valueOf(slot)});

                       shit = -1;
                       break label110;
                   case "del":
                       id = Integer.parseInt(textCodeSplitted[5]);
                       contentValues = new ContentValues();
                       contentValues.put(DBHelper.KEY_LATITUDE__ANOMALY, 0);
                       contentValues.put(DBHelper.KEY_LONGITUDE__ANOMALY, 0);
                       database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=" + (NUMBER_OF_ANOMALIES - 5 + id), null);

                       shit = -1;
                       break label110;
                   case "mines":

                       contentValues = new ContentValues();

                       contentValues.put(DBHelper.KEY_LATITUDE__ANOMALY, myCurrentLocation.getLatitude());
                       contentValues.put(DBHelper.KEY_LONGITUDE__ANOMALY, myCurrentLocation.getLongitude());
                       contentValues.put(DBHelper.KEY_POWER__ANOMALY, 0);
                       contentValues.put(DBHelper.KEY_RADIUS__ANOMALY, 30);
                       contentValues.put(DBHelper.KEY_TYPE__ANOMALY, "min");
                       int slotMine = anomaly[0].getAnomalyInfo() - 5 + 4;
                       Log.d("аномалии", String.valueOf(slotMine));
                       database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=?", new String[]{String.valueOf(slotMine)});

                       Toast.makeText(getApplicationContext(),"минное поле активировано", Toast.LENGTH_LONG).show();
                       shit = -1;
                       break label110;
                   case "injectorRad85":
                       Rad -= 0.85 * Rad;
                       shit = -1;
                       break label110;
                   case "injectorBio85":
                       Bio -= 0.85 * Bio;
                       shit = -1;
                       break label110;
                   case "injectorHP50":
                       playerCharacter[current].setHealth(playerCharacter[current].getHealth() + 0.5 * playerCharacter[current].getMaxHealth());
                       shit = -1;
                       break label110;
                   case "gestalt_closed": //TODO добавить в систему смарт кодов так, чтобы гештальт ид из полученного сообщения добывался
                       ((GestaltAnomaly) anomaly[anomalyMap.get(GESTALT)]).setProtected(GESTALT_ID);
                       shit = -1;
                       break label110;
               }
               switch(fromCommand.hashCode()) {
                   case 305958064:
                       if (fromCommand.equals("ResetStats")) {
                           shit = 0;
                           break label110;
                       }
                       break;
                   case 1796409274:
                       if (fromCommand.equals("SetDischargeImmunityTrue")) {
                           shit = 10;
                           break label110;
                       }
                       break;
                   case -159331209:
                       if (fromCommand.equals("SetDischargeImmunityFalse")) {
                           shit = 11;
                           break label110;
                       }
                       break;
                   case 146202227:
                       if (fromCommand.equals("SetMaxHealth100")) {
                           shit = 12;
                           break label110;
                       }
                       break;
                   case 146203188:
                       if (fromCommand.equals("SetMaxHealth200")) {
                           shit = 13;
                           break label110;
                       }
                       break;
                   case -1052620961:
                       if (fromCommand.equals("MakeAMakeAlive")) {
                           shit = 14;
                           break label110;
                       }
                       break;
                   case -2120901255:
                       if (fromCommand.equals("ComboResetProtections")) {
                           shit = 15;
                           break label110;
                       }
                       break;
                   case -258857420:
                       if (fromCommand.equals("Monolith")) {
                           shit = 16;
                           break label110;
                       }
                       break;
                   case 71772:
                       if (fromCommand.equals("God")) {
                           shit = 17;
                           break label110;
                       }
                       break;
                   case -1756574876:
                       if (fromCommand.equals("Unlock")) {
                           shit = 18;
                           break label110;
                       }
                       break;
                   case 565354622:
                       if (fromCommand.equals("Monolith2")) {
                           shit = 19;
                           break label110;
                       }
                       break;
                   case 1875682466:
                       if (fromCommand.equals("Discharge")) {
                           shit = 22;
                           break label110;
                       }
                       break;
                       // новые коды далее
                   case -1555514523:
                       if (fromCommand.equals("ScienceQR")) {
                           shit = 23;
                           break label110;
                       }
                       break;
                   case -1930888214:
                       if (fromCommand.equals("ScienceQRoff")) {
                           shit = 24;
                           break label110;
                       }
                       break;
                   case 317294316:
                       if (fromCommand.equals("SetGesProtection")) {
                           shit = 27;
                           break label110;
                       }
                       break;
                   case -707972381:
                       if (fromCommand.equals("SetGesProtectionOFF")) {
                           shit = 28;
                           break label110;
                       }
                       break;
                   case 1831428070: // снять неуяз к аномалиям и выбросу
                       if (fromCommand.equals("noMoreGod")) {
                           shit = 29;
                           break label110;
                       }
                       break;
                   case -1151237055:
                       if (fromCommand.equals("15minutesGod")) {
                           shit = 30;
                           break label110;
                       }
                       break;
                   case -1428886463:
                       // этот и следующие 2 - шприцы от рад и био и хп
                       if (fromCommand.equals("injectorrad")) {
                           shit = 31;
                           break label110;
                       }
                       break;
                   case -1428901580:
                       if (fromCommand.equals("injectorBio")) {
                           shit = 32;
                           break label110;
                       }
                       break;
                   case 2032116540:
                       if (fromCommand.equals("injectorHP")) {
                           shit = 33;
                           break label110;
                       }
                       break;
                   case -1745372224:
                       if (fromCommand.equals("nullifyRad")) {
                           shit = 34;
                           break label110;
                       }
                       break;
                   case -1745387341:
                       if (fromCommand.equals("nullifyBio")) {
                           shit = 35;
                           break label110;
                       }
                       break;
                   case -1745373567:
                       if (fromCommand.equals("nullifyPsy")) {
                           shit = 36;
                           break label110;
                       }
                       break;
                   case 1144354095:
                       if (fromCommand.equals("artCompass")) {
                           shit = 37;
                           break label110;
                       }
                       break;
                   case 1045731098:
                       if (fromCommand.equals("штраф")) {
                           shit = 38;
                           break label110;
                       }
                       break;
                   case -1975691119:
                       if (fromCommand.equals("discharge10Sc")) {
                           shit = 51;
                           break label110;
                       }
                       break;
                   case 1271685827:
                       if (fromCommand.equals("discharge45")) {
                           shit = 52;
                           break label110;
                       }
                       break;
                   case -16716590: // плюс жизни от болотного доктора
                       if (fromCommand.equals("BDplus2Health")) {
                           shit = 53;
                           break label110;
                       }
                       break;
                   case -1649172843:
                       if (fromCommand.equals("BDplus5Health")) {
                           shit = 54;
                           break label110;
                       }
                       break;
                   case 1381804599:
                       if (fromCommand.equals("BDplus10Health")) {
                           shit = 55;
                           break label110;
                       }
                       break;
                   case 1036792636:
                       if (fromCommand.equals("BDplus45HealthRandom")) {
                           shit = 56;
                           break label110;
                       }
                       break;
                   case -944954941:
                       if (fromCommand.equals("BDminus5Health")) {
                           shit = 57;
                           break label110;
                       }
                       break;
                   case 804709100:
                       if (fromCommand.equals("BDminus10HealthRandom")) {
                           shit = 58;
                           break label110;
                       }
                       break;
                   case 5747468:
                       if (fromCommand.equals("BDminus21HealthRandom")) {
                           shit = 59;
                           break label110;
                       }
                       break;
                   case 1323666026:
                       if (fromCommand.equals("BDprotectionBio6025")) {
                           shit = 60;
                           break label110;
                       }
                       break;
                   case 1323666057:
                       if (fromCommand.equals("BDprotectionBio6035")) {
                           shit = 61;
                           break label110;
                       }
                       break;
                   case -1895336201:
                       if (fromCommand.equals("BDprotectionRad6025")) {
                           shit = 62;
                           break label110;
                       }
                       break;
                   case -1895336170:
                       if (fromCommand.equals("BDprotectionRad6035")) {
                           shit = 63;
                           break label110;
                       }
                       break;
                   case 1159342392:
                       if (fromCommand.equals("BDprotectionPsy6025")) {
                           shit = 64;
                           break label110;
                       }
                       break;
                   case 735430818:
                       if (fromCommand.equals("BDprotectionBio120")) {
                           shit = 65;
                           break label110;
                       }
                       break;
                   case 1185781365:
                       if (fromCommand.equals("BDprotectionRad120")) {
                           shit = 66;
                           break label110;
                       }
                       break;
                   case 1145772052:
                       if (fromCommand.equals("BDprotectionPsy120")) {
                           shit = 67;
                           break label110;
                       }
                       break;
                   case -1167097637:
                       if (fromCommand.equals("setRadOn80Percent")) {
                           shit = 68;
                           break label110;
                       }
                       break;
                   case 1699558920:
                       if (fromCommand.equals("setBioOn80Percent")) {
                           shit = 69;
                           break label110;
                       }
                       break;
                   case -1658045336:
                       if (fromCommand.equals("mechMinus60Rad")) {
                           shit = 72;
                           break label110;
                       }
                       break;
                   case -1658060453:
                       if (fromCommand.equals("mechMinus60Bio")) {
                           shit = 73;
                           break label110;
                       }
                       break;
                   case -232827188:
                       if (fromCommand.equals("mechPlus70Health")) {
                           shit = 74;
                           break label110;
                       }
                       break;
                   case 1984920125:
                       if (fromCommand.equals("setRad0")) {
                           shit = 75;
                           break label110;
                       }
                       break;
                   case 1388454378:
                       if (fromCommand.equals("setBio15")) {
                           shit = 76;
                           break label110;
                       }
                       break;
                   case 1984451498:
                       if (fromCommand.equals("setBio0")) {
                           shit = 77;
                           break label110;
                       }
                       break;
                   case 1784296673:
                       if (fromCommand.equals("minus15Rad")) {
                           shit = 78;
                           break label110;
                       }
                       break;
                   case -1523616740:
                       if (fromCommand.equals("plus10Rad")) {
                           shit = 80;
                           break label110;
                       }
                       break;
                   case -1523631857:
                       if (fromCommand.equals("plus10Bio")) {
                           shit = 81;
                           break label110;
                       }
                       break;
                   case 1784281556:
                       if (fromCommand.equals("minus15Bio")) {
                           shit = 83;
                           break label110;
                       }
                       break;
                   case 1910275381:
                       //здесь и далее глеб
                   case -982210431:
                       if (fromCommand.equals("radiation")) {
                           shit = 90;
                           break label110;
                       }
                       break;
                   case -383752240:
                       if (fromCommand.equals("radiation1")) {
                           shit = 91;
                           break label110;
                       }
                       break;
                   case -383752239:
                       if (fromCommand.equals("radiation2")) {
                           shit = 92;
                           break label110;
                       }
                       break;
                   case -383752238:
                       if (fromCommand.equals("radiation3")) {
                           shit = 93;
                           break label110;
                       }
                       break;
                   case 74018586:
                       if (fromCommand.equals("biohazard")) {
                           shit = 94;
                           break label110;
                       }
                       break;
                   case -2000391081:
                       if (fromCommand.equals("biohazard1")) {
                           shit = 95;
                           break label110;
                       }
                       break;
                   case -2000391080:
                       if (fromCommand.equals("biohazard2")) {
                           shit = 96;
                           break label110;
                       }
                       break;
                   case -2000391079:
                       if (fromCommand.equals("biohazard3")) {
                           shit = 97;
                           break label110;
                       }
                       break;
                   case -1221262756:
                       if (fromCommand.equals("health")) {
                           shit = 98;
                           break label110;
                       }
                       break;
                   case 795560277:
                       if (fromCommand.equals("health1")) {
                           shit = 99;
                           break label110;
                       }
                       break;
                   case 795560278:
                       if (fromCommand.equals("health2")) {
                           shit = 100;
                           break label110;
                       }
                       break;
                   case 795560279:
                       if (fromCommand.equals("health3")) {
                           shit = 101;
                           break label110;
                       }
                       break;
               }

               shit = -1;
           }

           Intent intent1;
           int j;
           switch(shit) {
               case 0:
                   playerCharacter[current].setDead(false);
                   playerCharacter[current].setHealth(playerCharacter[current].getMaxHealth());
                   playerCharacter[current].setMaxHealth(2000);
                   playerCharacter[current].setContamination(new double[]{0, 0, 0});
                   playerCharacter[current].setRadProtection(new double[][]{{0, 0, 0}, {0, 0, 0}});
                   playerCharacter[current].setMineAvailable(true);
                   ((GestaltAnomaly) anomaly[anomalyMap.get(GESTALT)]).setProtected(GESTALT_ID);

                   scienceQR = false; // больше не ученый
                   DischargeImmunity = false;

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
                   playerCharacter[current].setMaxHealth(2000);
                   break;
               case 13:
                   playerCharacter[current].setMaxHealth(3000);
                   break;
               case 14:  //MakeAlive
                   playerCharacter[current].setDead(false);
                   playerCharacter[current].setHealth(playerCharacter[current].getMaxHealth());
                   playerCharacter[current].setContamination(new double[]{0, 0, 0});

                   break;
               case 15: //ComboResetProtections
                   Arrays.fill(RadProtectionArr, 0);
                   Arrays.fill(BioProtectionArr, 0);
                   Arrays.fill(PsyProtectionArr, 0);
                   Arrays.fill(RadProtectionCapacityArr, 0);
                   Arrays.fill(BioProtectionCapacityArr, 0);
                   Arrays.fill(PsyProtectionCapacityArr, 0);
                   break;
               case 17: //god
                   DischargeImmunity = true;
                   IS_ANOMALIES_AVAILABLE = false;
                   break;
               case 18:
                   IsUnlocked = true;
                   break;
               case 22:
                   playerCharacter[current].responseDischarge();
                   break;
                   //новые коды
               case 23:
                   tools.setScienceQR(user_id, true);
                   scienceQR = true;
                   break;
               case 24:
                   tools.setScienceQR(user_id, false);
                   scienceQR = false;
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
                       effectManager.PlaySound("Start", 1);
                   }, 3600000);
                   break;
               case 31: // этот и следующие 2 - шприцы от рад и био
                   setGleb_Mech("rad", -75, true, 9);
                   break;
               case 32:
                   setGleb_Mech("bio", -75, true, 9);
                   break;
               case 33:
                   setGleb_Mech("Health", 40, false, 0);
                   break;
               case 34: // этот и два следующих - обнуление защит
                   Arrays.fill(RadProtectionArr, 0);
                   break;
               case 35:
                   Arrays.fill(BioProtectionArr, 0);
                   break;
               case 36:
                   Arrays.fill(PsyProtectionArr, 0);
                   break;
               case 37:// артос компас
                    IS_COMPASS = true;
                   break;
               case 38:
                   playerCharacter[current].setHealth(0);
                   break;
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
                       playerCharacter[current].setHealth(0);
                   }
                   break;
               case 81:
                   Bio += 0.1 * MaxBio;
                   if (Bio >= MaxBio){
                       playerCharacter[current].setHealth(0);
                   }
                   break;
               case 83:
                   Bio -= 0.15 * Bio;
                   break;
               case 90: //Здесь и далее глеб
                   setGleb_Mech("rad", -65, true, 9);
                   break;
               case 91:
                   setGleb_Mech("rad", -75, true, 9);
                   break;
               case 92:
                   setGleb_Mech("rad", -85, true, 9);
                   break;
               case 93:
                   Rad = 0;
                   break;
               case 94:
                   setGleb_Mech("bio", -65, true, 9);
                   break;
               case 95:
                   setGleb_Mech("bio", -75, true, 9);
                   break;
               case 96:
                   setGleb_Mech("bio", -85, true, 9);
                   break;
               case 97:
                   Bio = 0;
                   break;
               case 98:
                   setGleb_Mech("Health", 60, true, 9);
                   break;
               case 99:
                   setGleb_Mech("Health", 70, true, 9);
                   break;
               case 100:
                   setGleb_Mech("Health", 80, true, 9);
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
    private void setGleb_Mech(String type, int quantity, boolean isRandom, int rangeOfRandom){
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
            case "rad":
                if(isRandom){
                    Rad += (quantity + Math.signum(quantity) * (random.nextInt(rangeOfRandom) + 1)) * Rad  / 100;
                } else {
                    Rad += quantity * Rad / 100d;
                }
                break;
            case "bio":
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

    public void onCreate() {
        super.onCreate();
        this.wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "STALKERNET:My_Partial_Wake_Lock");
        this.wl.acquire(10*60*1000L /*10 minutes*/);   //timeout заставила студия поставить, не знаю как это работает
        AndroidThreeTen.init(this);
        //здесь база данных открывается, а в onDestroy закрывается вместе с курсором
        //TODO надо в остальных местах убрать открытие и закрытие базы данных
        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.create_db();
        database = dbHelper.open();

        effectManager = new EffectManager(this);
        playerCharacter = new PlayerCharacter[4];
        playerCharacter[0] = new PlayerCharacter(this);
        playerCharacter[1] = new StalkerCharacter(this);
        playerCharacter[2] = new MonolithCharacter(this);
        playerCharacter[3] = new MilitaryCharacter(this);
        qrAnomaly = new QRAnomaly();
        wifiAnomaly = new WifiAnomaly(this);
        anomaly = new Anomaly[4];
        anomaly[0] = new Anomaly(this, database, cursor);
        anomaly[1] = new GestaltAnomaly(this, database, cursor);
        anomaly[2] = new OasisAnomaly(this);
        anomaly[3] = new MineAnomaly(this,database,cursor);

        quest = new Quest(database, cursor);
        tools = new Tool(database, cursor);
        scienceQR = tools.getScienceQR(user_id);

        discharge = new Discharge(this, database, cursor);

        loadStats();

        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel stalker",
                    IMPORTANCE_HIGH);
            channel.setImportance(IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        Toast.makeText(this, "Service has been started.", Toast.LENGTH_SHORT).show();
        checkPermissions();
        registerReceiver(this.broadcastReceiver, new IntentFilter("Command"));
        registerReceiver(this.broadcastReceiverQR, new IntentFilter(INTENT_SERVICE));
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        unregisterReceiver(this.broadcastReceiverQR);
        saveStats();
        this.wl.release();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
    private void checkPermissions() {
        while (!LocationUpdatesStarted) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                LocationRequest request = LocationRequest.create();
                request.setPriority(100).setInterval(1000).setFastestInterval(1000);
                LocationCallback = new MyLocationCallback(myLocation, this);
                mFusedLocationProvider.requestLocationUpdates(request, LocationCallback, null);
                LocationUpdatesStarted = true;
                Toast.makeText(this, "Location updates have been started.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
     * сеттеры и геттеры для максимально допустимого смещения координаты и ее коррекции
     * */
    public int getMaxDrift() {
        return maxDrift;
    }

    public void setMaxDrift(int maxDrift) {
        this.maxDrift = maxDrift;
        if (maxDrift < getDriftCorrection()){
            setDriftCorrection(maxDrift);
        }
    }

    public int getDriftCorrection() {
        return driftCorrection;
    }

    public void setDriftCorrection(int driftCorrection) {
        this.driftCorrection = Math.min(driftCorrection, getMaxDrift());
    }

    /*
    * получает координаты из MyLocаationCallback
    * */
    public void updateLocation(Location location) {

        myCurrentLocation = location;
        //Toast.makeText(this, "speed = " + myCurrentLocation.getSpeed(), Toast.LENGTH_SHORT).show();
        //Log.d(LOG_CHE_CHE, "service speed " + myCurrentLocation.getSpeed());
        if (!playerCharacter[current].isDead() && IsUnlocked) {
            applyDischarge();
            //artCompass(); // артос компас, который дает неуязвимость на 15 нимут
            //getMovingAnomalies();
            applyAnomalies();
            checkLocality();
            checkQuest();


        }
        setOutPutString();
        saveStats();
    }
    public void setOutPutString(){
        Intent intent = new Intent("StatsService.Update");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(playerCharacter[current].getHealth()); //0
        stringBuilder.append(":");
        stringBuilder.append(myCurrentLocation.getLatitude()); //4
        stringBuilder.append(":");
        stringBuilder.append(myCurrentLocation.getLongitude());//6
        stringBuilder.append(":");
        stringBuilder.append(scienceQR);  //qr ученого//6
        stringBuilder.append(":");
        String stringBuilder2 = stringBuilder.toString();

        intent.putExtra("Stats", stringBuilder2);
        intent.putExtra(SEND_CONTAMINATION, playerCharacter[current].getIntContamination(true));
        intent.putExtra(SEND_TOTAL_PROTECTIONS, playerCharacter[current].getStringTotalProtections());
        intent.putExtra(SEND_RAD_PROTECTIONS, Arrays.stream(playerCharacter[current].getRadProtection()).flatMapToDouble(Arrays::stream).toArray());
        intent.putExtra(SEND_BIO_PROTECTIONS, Arrays.stream(playerCharacter[current].getBioProtection()).flatMapToDouble(Arrays::stream).toArray());
        intent.putExtra(SEND_PSY_PROTECTIONS, Arrays.stream(playerCharacter[current].getPsyProtection()).flatMapToDouble(Arrays::stream).toArray());



        sendBroadcast(intent);
        Intent intent1 = new Intent(INTENT_MAP);
        intent1.putExtra(INTENT_MAP_UPDATE, "Draw");
        sendBroadcast(intent1);
    }

    /*
    * карта группировка - character
    * */
    private void setFactionMap(){
        factionMap.put(1, 1);
        factionMap.put(2, 0);
        factionMap.put(3, 0);
        factionMap.put(4, 0);
        factionMap.put(5, 3);
        factionMap.put(6, 2);
        factionMap.put(7, 0);
        factionMap.put(8, 0);
        factionMap.put(9, 0);
        factionMap.put(10, 0);
        factionMap.put(11, 0);
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

    /*
    * заполняет карту для определения гештальт аномалия или нет
    * */
    private void setAnomalyMap(){
        anomalyMap.put(RAD, 0);
        anomalyMap.put(BIO, 0);
        anomalyMap.put(PSY, 0);
        anomalyMap.put(GESTALT, 1);
        anomalyMap.put(OASIS, 2);
        anomalyMap.put(MINE, 3);
    }
    /*
    * возвращает номер игрового для в зависимости от даты
    * нужно, чтобы аномалии работали в тот день, в который должны
    * */
    // TODO должна возвращать номер игрового для в зависимости от даты но пока что возвращает 1
    private int getCurrentDay(){
        return 1;
    }

    /*
    * вызывается в setOutPutString
    * применяет аномалии
    * */
    public void applyAnomalies() {
        if (IS_ANOMALIES_AVAILABLE) {
            // перед началом проверки ставит, что мол не находится внутри никаких аномалий
            isInsideAnomaly = false;
            // не только считает количество аномалий, но и записывает в курсор таблицу аномалий
            int anomalyCount = anomaly[0].getAnomalyInfo();
            // цикл, в котором проверяется каждая строка из базы данных
            for (int i = 0; i < anomalyCount; i++) {
                // проверяет находится ли игрок внутри аномалии i. TODO inside учитывает день игры, не повлияет ли это на гештальт?
                Pair<Boolean, String> anomalyPair = anomaly[0].isInside(getCurrentDay());


                boolean inside = anomalyPair.first;
                String type = anomalyPair.second;
                // если аномалия - гештальт, то передает в класс гештальт свойства этого гештальта
                // а также проверяет статус гештальта: open, close, protected
                if (type.equals(GESTALT)){
                    ((GestaltAnomaly) anomaly[anomalyMap.get(GESTALT)]).isProtected(anomaly[0].getGestaltDamageInfo());
                }
                // проверяет находится ли игрок внутри обычной аномалии или снаружи гештальта
                // если так, то наносит урон, играет звук и запсывает в таблицу, что надо показывать аномалию на карте
                if (inside){
                    isInsideAnomaly = true;
                    int anomalySubClass = anomalyMap.get(type);
                    playerCharacter[current].applyProtection(anomaly[anomalySubClass].getDamage());
                    //TODO передвинуть звуки в свой метод
                    effectManager.StopActions();
                    effectManager.PlaySound(type, anomaly[0].getPower());
                    if (vibrate) {
                        effectManager.VibrateInPattern();
                    }
                }
            }
            // вай фай аномалии - контролер или химера
            if (wifiAnomaly.getWiFiScan()){
                playerCharacter[current].applyProtection(wifiAnomaly.getDamage(CONTROL_WIFI));
                playerCharacter[current].applyProtection(wifiAnomaly.getDamage(CHIMERA_WIFI));
                isInsideAnomaly = true;

                if (wifiAnomaly.makeSound()) {
                    effectManager.StopActions();
                    effectManager.PlaySound(wifiAnomaly.getType(), wifiAnomaly.getPower());
                    if (vibrate) {
                        effectManager.VibrateInPattern();
                    }
                }
            }

            if (!isInsideAnomaly){
                playerCharacter[current].purification();
            }
        }
    }
    /*
    * вызывается в MyLocationCallback()
    * за 10 минут до выброса происходит варнинг
    * а во время выброса приоверяет, находится ли в безопасном месте игрок
    * если не находится то вызывает метод персонажа, ответственный за реакцию на выброс
    * */
    public void applyDischarge(){
        discharge.checkDischargeTime();
        if (discharge.isWarning()){
            Toast.makeText(this, "Близится выброс, необходимо укрытие", Toast.LENGTH_LONG).show();
            effectManager.PlayBuzzer();
            discharge.setWarning(false);
        }
        if (discharge.isDischarging()){
            Toast.makeText(getApplicationContext(), "!!!Выброс!!!", Toast.LENGTH_LONG).show();
            effectManager.PlayBuzzer();
            if (!discharge.isInsideSafeZone()){
                playerCharacter[current].responseDischarge();
            }
            discharge.setDischarging(false);
        }
    }

    public void checkQuest(){
        //
        //проверка кредо на легенду зоны
        //
        ContentValues cv;

        String creedString = "SELECT access_status FROM milestone WHERE access_status =?";
        cursor = database.rawQuery(creedString, new String[]{"true"});
        if (cursor.getCount() > 8) {
            cv = new ContentValues();
            cv.put(DBHelper.KEY_STATUS__CREED_BRANCH, "true");
            database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "= ?", new String[]{"15"});
            //Легенда зоны увеличивает количество заражения, которое можно пережить
            double[] newContaminationMax = new double[3];
            Arrays.fill(newContaminationMax, MAX_CONTAMINATION_LEGEND);
            playerCharacter[current].setContaminationMax(newContaminationMax);
        }
        cursor.close();
    }
    /*
    * Проверяет находится ли игрок в радиусе 30 метров, если находится, то ставит локации
    * access_status значению true
    * */
    // TODO это надо перенести в класс локаций
    public void checkLocality(){
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
                    double distanceToLocality = location.distanceTo(myCurrentLocation);
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
    }


    // Переводит double[][] string
    private String twoDArrToString(double[][] matrix){
        String array = Arrays.toString(matrix[0]).replaceAll("[\\[\\]]", "");
        return array + ", " + Arrays.toString(matrix[1]).replaceAll("[\\[\\]]", "");
    }
    // переводит String в double[][]
    private double[][] stringToTwoDArr(String stringArr, boolean absZero){
        String defaultValue;
        if (absZero){
            defaultValue = "0, 0, 0, 0, 0, 0";
        } else {
            defaultValue = "0, 0, 0, 1000, 1000, 1000";
        }
        double[][] twoDimensional = {{0, 0, 0},{0, 0, 0}};
        double[] array = Arrays.stream(Objects.requireNonNull(sharedPreferences.getString(stringArr, defaultValue)).split(", ")).mapToDouble(Double::parseDouble).toArray();
        int rows = twoDimensional.length;
        int cols = twoDimensional[0].length;
        for (int i = 0, k = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                twoDimensional[i][j] = array[k++];
            }
        }
        return twoDimensional;
    }

    SharedPreferences sharedPreferences;
    public void loadStats() {
        sharedPreferences = this.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        current = sharedPreferences.getInt(CURRENT_KEY, 0);
        user_id = sharedPreferences.getInt(USER_ID_KEY, 1);
        maxDrift = sharedPreferences.getInt(MAX_DRIFT_KEY, 10);
        driftCorrection = sharedPreferences.getInt(DRIFT_CORRECTION_KEY, 3);
        scienceQR = sharedPreferences.getBoolean("ScienceQR", false);

        playerCharacter[current].setHealth(sharedPreferences.getFloat(HEALTH_KEY, 2000));
        playerCharacter[current].setMaxHealth(sharedPreferences.getInt(MAX_HEALTH_KEY, 2000));
        playerCharacter[current].setName(sharedPreferences.getString(NAME_KEY, "Иван"));
        playerCharacter[current].setFaction(sharedPreferences.getString(FACTION_KEY, "Вольный сталкер"));
        playerCharacter[current].setFactionPosition(sharedPreferences.getString(FACTION_POSITION_KEY, "рядовой"));
        playerCharacter[current].setLastTimeHitBy(sharedPreferences.getString(LAST_TIME_HIT_BY_KEY, ""));
        playerCharacter[current].setDead(sharedPreferences.getBoolean(DEAD_KEY, false));
        playerCharacter[current].setRadProtection(stringToTwoDArr(RAD_PROTECTION_KEY, true));
        playerCharacter[current].setBioProtection(stringToTwoDArr(BIO_PROTECTION_KEY, true));
        playerCharacter[current].setPsyProtection(stringToTwoDArr(PSY_PROTECTION_KEY, true));
        playerCharacter[current].setGesProtection(stringToTwoDArr(GESTALT_PROTECTION_KEY, true));
        playerCharacter[current].setContamination2D(stringToTwoDArr(CONTAMINATION_2D_KEY, false));
        playerCharacter[current].setProtectionMap();
        playerCharacter[current].setDefaultContaminationMap();
        playerCharacter[current].setSubProtectionMap();

        setAnomalyMap();
        setFactionMap();


        this.DischargeImmunity = Boolean.parseBoolean(sharedPreferences.getString("DischargeImmunity", "false"));
        this.IsUnlocked = Boolean.parseBoolean(sharedPreferences.getString("Lock", "true"));
        this.IS_ANOMALIES_AVAILABLE = Boolean.parseBoolean(Objects.requireNonNull(sharedPreferences.getString("IS_ANOMALIES_AVAILABLE", "true")));
    }

    public void saveStats() {
        sharedPreferences = this.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putFloat(HEALTH_KEY, (float) playerCharacter[current].getHealth());
        edit.putInt(MAX_HEALTH_KEY, playerCharacter[current].getMaxHealth());
        edit.putInt(PROTECTIONS_AVAILABLE_KEY, playerCharacter[current].getProtectionsAvailable());
        edit.putString(NAME_KEY, playerCharacter[current].getName());
        edit.putString(FACTION_KEY, playerCharacter[current].getFaction());
        edit.putString(FACTION_POSITION_KEY, playerCharacter[current].getFactionPosition());
        edit.putString(LAST_TIME_HIT_BY_KEY, playerCharacter[current].getLastTimeHitBy());
        edit.putBoolean(DEAD_KEY, playerCharacter[current].isDead());
        edit.putString(RAD_PROTECTION_KEY, twoDArrToString(playerCharacter[current].getRadProtection()));
        edit.putString(BIO_PROTECTION_KEY, twoDArrToString(playerCharacter[current].getBioProtection()));
        edit.putString(PSY_PROTECTION_KEY, twoDArrToString(playerCharacter[current].getPsyProtection()));
        edit.putString(GESTALT_PROTECTION_KEY, twoDArrToString(playerCharacter[current].getGesProtection()));
        edit.putString(CONTAMINATION_2D_KEY, twoDArrToString(playerCharacter[current].getContamination2D()));

        edit.putInt(CURRENT_KEY, current);
        edit.putInt(USER_ID_KEY, user_id);
        edit.putInt(MAX_DRIFT_KEY, maxDrift);
        edit.putInt(DRIFT_CORRECTION_KEY, driftCorrection);


        edit.putBoolean("ScienceQR", scienceQR);
        edit.putString("DischargeImmunity", Boolean.toString(this.DischargeImmunity));
        edit.putString("Lock", Boolean.toString(this.IsUnlocked));
        edit.putString("IS_ANOMALIES_AVAILABLE", Boolean.toString(this.IS_ANOMALIES_AVAILABLE));
        edit.apply();
    }
}
