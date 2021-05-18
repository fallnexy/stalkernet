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
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class StatsService extends Service {
    private static final int ID_SERVICE = 101;
    private static final int NUMBER_OF_ANOMALIES = 20;
    private static final int NUMBER_OF_GESTALT_ANOMALIES = 1;
    private static final int NUMBER_OF_SAVE_ZONES = 10;
    private boolean IS_ANOMALIES_AVAILABLE = true;
    public Anomaly[] anomalies;
    public SafeZone[] SafeZones;
    public EffectManager EM;
    public double Health = 2000.0d, MaxHealth = 2000.0d;
    public double Bio = 0.0d, Psy = 0.0d, Rad = 0.0d;
    public int MaxRad = 1000, MaxBio = 1000;
    public int BioProtection = 0, PsyProtection = 0, RadProtection = 0;
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

    private Calendar cal = Calendar.getInstance();
    private int Hour = cal.get(10);
    private int Minutes = cal.get(12);
    private int dayInt = cal.get(5);

    private Random random = new Random();
    private  int BioProtectionTemp = 0;
    private  int RadProtectionTemp = 0;
    private  int PsyProtectionTemp = 0;
    private boolean BioProtectionChangeability = true;
    private boolean RadProtectionChangeability = true;
    private boolean PsyProtectionChangeability = true;

    public boolean DolgDischargeImmunity = false;
    public boolean NaemnikiDischargeImmunity = false;

    public boolean fastRadPurification = false;

    public LatLng latLngAnomaly;
    public Double radiusAnomaly;

    long checkTime_in = 1620988200;  // 14 мая 13:30 // 1620988200
    long checkTime_out = 1621167600;  // 16 мая 15:20  // 1621167600

    private FusedLocationProviderClient mFusedLocationProvider;
    private PowerManager.WakeLock wl;

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
           byte var3;
           label110: {
               String var4 = intent.getStringExtra("Command");
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
                   case 1543390539:
                       if (var4.equals("TwoHoursRadProtection")) {
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
                   case 1787841802:
                       // этот и следующие 3 - шприцы от рад и био (бракованные и нет), цифры, которые минус, это проценты
                       if (var4.equals("minus50Rad")) {
                           var3 = 31;
                           break label110;
                       }
                       break;
                   case 1787826685:
                       if (var4.equals("minus50Bio")) {
                           var3 = 32;
                           break label110;
                       }
                       break;
                   case 1785220194:
                       if (var4.equals("minus25Rad")) {
                           var3 = 33;
                           break label110;
                       }
                       break;
                   case 1785205077:
                       if (var4.equals("minus25Bio")) {
                           var3 = 34;
                           break label110;
                       }
                       break;
                   case -201032814:
                       if (var4.equals("plus40Health")) {
                           var3 = 35;
                           break label110;
                       }
                       break;
                   case 608313812:
                       if (var4.equals("plus20Health")) {
                           var3 = 36;
                           break label110;
                       }
                       break;
                   case 795560281:
                       if (var4.equals("health5")) {
                           var3 = 37;
                           break label110;
                       }
                       break;
                   case -1107435105:
                       if (var4.equals("health25")) {
                           var3 = 38;
                           break label110;
                       }
                       break;
                   case -1107435017:
                       if (var4.equals("health50")) {
                           var3 = 39;
                           break label110;
                       }
                       break;
                   case -1107434950:
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
                       break;
                   case -1800724366:
                       if (var4.equals("radProt10030")) {
                           var3 = 42;
                           break label110;
                       }
                       break;
                   case -1800724273:
                       if (var4.equals("radProt10060")) {
                           var3 = 43;
                           break label110;
                       }
                       break;
                   case -1800724180:
                       if (var4.equals("radProt10090")) {
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
                   case -1975691677:
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
                   case -1449685624:
                       if (var4.equals("dolgDischargeImmunity")) {
                           var3 = 70;
                           break label110;
                       }
                       break;
                   case 1259972122:
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
                   case 551741458:
                       if (var4.equals("anomalyFreedomOn")) {
                           var3 = 85;
                           break label110;
                       }
                       break;
                   case -75884132:
                       if (var4.equals("anomalyFreedomOff")) {
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
                   case 1989494219:
                       if (var4.equals("monolithStrong")) {
                           var3 = 90;
                           break label110;
                       }
                       break;
                   case 1749658540:
                       if (var4.equals("monolithWeak")) {
                           var3 = 91;
                           break label110;
                       }
                       break;
                   case -63138094:
                       if (var4.equals("monolith_blessing")) {
                           var3 = 92;
                           break label110;
                       }
                       break;
                   case 852949013:
                       if (var4.equals("plus10RadProtection")) {
                           var3 = 93;
                           break label110;
                       }
                       break;
                   case -301504184:
                       if (var4.equals("plus10BioProtection")) {
                           var3 = 94;
                           break label110;
                       }
                       break;
               }

               var3 = -1;
           }

           Intent intent1;
           switch(var3) {
               case 0:
                   Health = 2000.0D;
                   MaxHealth = 2000.0D;
                   Rad = 0.0D;
                   Bio = 0.0D;
                   Psy = 0.0D;
                   RadProtection = 0;
                   BioProtection = 0;
                   PsyProtection = 0;
                   RadProtectionChangeability = true;
                   BioProtectionChangeability = true;
                   PsyProtectionChangeability = true;
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
               case 1:
                   PsyProtection = 0;
                   break;
               case 2:
                   PsyProtection = 50;
                   break;
               case 3:
                   PsyProtection = 100;
                   break;
               case 4:
                   RadProtection = 0;
                   break;
               case 5:
                   RadProtection = 50;
                   break;
               case 6: // используется не только цифровым кодом, но и qr
                   if (RadProtectionChangeability) {
                       RadProtection = 90;
                   }
                   break;
               case 7:
                   BioProtection = 0;
                   break;
               case 8:
                   BioProtection = 50;
                   break;
               case 9: // используется не только цифровым кодом, но и qr
                   if (BioProtectionChangeability) {
                       BioProtection = 90;
                   }
                   break;
               case 10:
                   DischargeImmunity = true;
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
               case 14:
                   Health = MaxHealth;
                   Rad = 0.0D;
                   Bio = 0.0D;
                   Psy = 0.0D;
                   IsDead = false;
                   break;
               case 15:
                   PsyProtection = 0;
                   RadProtection = 0;
                   BioProtection = 0;
                   break;
               case 16: //monolith
                   DischargeImmunity = true;
                   RadProtection = 50;
                   BioProtection = 50;
                   PsyProtection = 100;
                   GestaltProtection = true;
                   break;
               case 17: //god
                   DischargeImmunity = true;
                   RadProtection = 100;
                   BioProtection = 100;
                   PsyProtection = 100;
                   GestaltProtection = true;
                   break;
               case 18:
                   IsUnlocked = true;
                   break;
               case 19://Monolith2 - аномалия, которая лечит, но только монолит, точнее тех, у кого иммунитет к выбросам
                   if (DischargeImmunity) {
                       Health = 2000.0D;
                       Bio = 0.0D;
                       Rad = 0.0D;
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
                   //временная защита от радиации
                   if (RadProtectionChangeability) {
                       final int RadProtectionTemp_0 = RadProtection;
                       RadProtection = 100;
                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           public void run() {
                               RadProtection = RadProtectionTemp_0;
                           }
                       }, 60000);
                   }
                   break;
               case 30:
                   // 10минутный режим бога, чтобы до базы дойти
                   IS_ANOMALIES_AVAILABLE = false;
                   DischargeImmunity = true;
                   Handler handler2 = new Handler();
                   handler2.postDelayed(new Runnable() {
                       public void run() {
                           IS_ANOMALIES_AVAILABLE = true;
                           DischargeImmunity = false;
                           EM.PlaySound("Start", 1);
                       }
                   }, 600000);
                   break;
               case 31:
                   // этот и следующие 3 - шприцы от рад и био
                   Rad *= 0.5;
                   break;
               case 32:
                   Bio *= 0.5;
                   break;
               case 33:
                   Rad *= 0.75;
                   break;
               case 34:
                   Bio *= 0.75;
                   break;
               case 35:
                   // этот и следующий на + жизни
                   Health += 0.4 * MaxHealth;
                   if (Health > MaxHealth){
                       Health = MaxHealth;
                   }
                   break;
               case 36:
                   Health += 0.2 * MaxHealth;
                   if (Health > MaxHealth){
                       Health = MaxHealth;
                   }
                   break;
                   // этот и ещё 4 кода на жизни
               case 37:
                   Health = 0.05 * MaxHealth;
                   break;
               case 38:
                   Health = 0.25 * MaxHealth;
                   break;
               case 39:
                   Health = 0.5 * MaxHealth;
                   break;
               case 40:
                   Health = 0.75 * MaxHealth;
                   break;
               case 41:
                   Health = MaxHealth;
                   break;
               case 42: // рад защита на 30минут
                   SetTemporaryAnomalyProtection("Rad", 99, 1, 1800000);
                   break;
               case 43: // рад защита на 60минут
                   SetTemporaryAnomalyProtection("Rad", 99, 1, 3600000);
                   break;
               case 44: // рад защита на 90минут
                   SetTemporaryAnomalyProtection("Rad", 99, 1, 5400000);
                   break;
               case 45: // био защита на 30минут
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
                   break;
               case 51: // защита от выброса на 10 минут
                   final boolean DischargeImmunityTemp = DischargeImmunity;
                   DischargeImmunity = true;
                   Handler handler12 = new Handler();
                   handler12.postDelayed(new Runnable() {
                       public void run() {
                           DischargeImmunity = DischargeImmunityTemp;
                       }
                   }, 600000);
                   break;
               case 52: // защита от выброса на 45 минут
                   final boolean DischargeImmunityTemp_1 = DischargeImmunity;
                   DischargeImmunity = true;
                   Handler handler13 = new Handler();
                   handler13.postDelayed(new Runnable() {
                       public void run() {
                           DischargeImmunity = DischargeImmunityTemp_1;
                       }
                   }, 2700000);
                   break;
               case 53:  //коды болотного доктора на жизни
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
                   break;
               case 60:
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
               case 70:
                   DolgDischargeImmunity = true;
                   break;
               case 71:
                   NaemnikiDischargeImmunity = true;
                   break;
               case 72:
                   Rad -= Rad * (random.nextInt(30) + 61) / 100;
                   break;
               case 73:
                   Bio -= Bio * (random.nextInt(30) + 61) / 100;
                   break;
               case 74:
                   setHealthBy_BD (70, true, 10);
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
               case 79:
                   if (Health >= MaxHealth * 0.5){
                       Health -= 0.15 * Health;
                   }else {
                       SetTemporaryAnomalyProtection ("Rad", 69, 1, 1200000);
                   }
                   break;
               case 80:
                   Rad += 0.1 * MaxRad;
                   if (Rad >= MaxRad){
                       setDead(Boolean.TRUE);
                   }
                   break;
               case 81:
                   Bio += 0.1 * MaxBio;
                   if (Bio >= MaxBio){
                       setDead(Boolean.TRUE);
                   }
                   break;
               case 82:
                   if (Health >= MaxHealth * 0.5){
                       Health -= 0.15 * Health;
                   }else {
                       SetTemporaryAnomalyProtection ("Bio", 69, 1, 1200000);
                   }
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
               case 85:
                   // свободный кейс
                   break;
               case 86:
                   // свободный кейс
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
               case 90: //полная защита от аномалий на 12 часов для монолита
                   if (random.nextInt(2) == 1){
                       SetTemporaryAnomalyProtection ("Rad", 99, 1, 43200000);
                       SetTemporaryAnomalyProtection ("Bio", 99, 1, 43200000);
                   }
                   break;
               case 91: // снятие защиты у монолита на 12 часов - наверно уже не актуально
                   SetTemporaryAnomalyProtection ("Rad", -1, 1, 43200000);
                   SetTemporaryAnomalyProtection ("Bio", -1, 1, 43200000);
                   break;
               case 92: // благословение пастыря монолита на 15 минут защиты от пси
                   SetTemporaryAnomalyProtection("Psy", 99, 1, 90000);
                   break;
               case 93:
                   RadProtection += 10;
                   break;
               case 94:
                   BioProtection += 10;
                   break;
           }

       }
    };

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
// для qr болотного доктора
    private void setHealthBy_BD (int health, boolean isRandom, int rangeOfRandom){
        if(isRandom){
            Health = Health + (health + Math.signum(health) * (random.nextInt(rangeOfRandom) + 1)) * Health  / 100;
        } else {
            Health = Health + health * Health / 100;
        }
        if(Health > MaxHealth){
            Health = MaxHealth;
        }
    }
// установка временной защиты от аномалий
    private void SetTemporaryAnomalyProtection (String protectionType, int protectionPower, int protectionRandomRange, int time){
        switch (protectionType){
            case "Rad":
                if (RadProtectionChangeability) {
                    RadProtectionTemp = RadProtection;
                    RadProtection = protectionPower + (random.nextInt(protectionRandomRange) + 1);
                    RadProtectionChangeability = false;
                    Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        public void run() {
                            RadProtection = RadProtectionTemp;
                            RadProtectionTemp = 0;
                            RadProtectionChangeability = true;
                        }
                    }, time);
                }
                break;
            case "Bio":
                if (BioProtectionChangeability) {
                    BioProtectionTemp = BioProtection;
                    BioProtection = protectionPower + (random.nextInt(protectionRandomRange) + 1);
                    BioProtectionChangeability = false;
                    Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        public void run() {
                            BioProtection = BioProtectionTemp;
                            BioProtectionTemp = 0;
                            BioProtectionChangeability = true;
                        }
                    }, time);
                }
                break;
            case "Psy":
                if (PsyProtectionChangeability) {
                    PsyProtectionTemp = PsyProtection;
                    PsyProtection = protectionPower + (random.nextInt(protectionRandomRange) + 1);
                    PsyProtectionChangeability = false;
                    Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        public void run() {
                            PsyProtection = PsyProtectionTemp;
                            PsyProtectionTemp = 0;
                            PsyProtectionChangeability = true;
                        }
                    }, time);
                }
                break;
        }
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
        GetAnomalies();
        CreateSafeZones();
        LoadStats();
        Create_super_save_zones();
        this.mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        //startForeground(101, new Builder(this, Build.VERSION.SDK_INT >= 26 ? createNotificationChannel((NotificationManager) getSystemService("notification")) : "").setOngoing(true).setSmallIcon(R.drawable.ic_launcher_background).setPriority(1).setCategory("service").setContentTitle("StatsService").setContentText("Stats are being updated.").build());
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        dbHelper = new DBHelper(getApplicationContext());

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

    private void CheckPermissions() {
        while (!this.LocationUpdatesStarted) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                LocationRequest create = LocationRequest.create();
                create.setPriority(100).setInterval(1000).setFastestInterval(1000);
                this.LocationCallback = new MyLocationCallback(this.MyCurrentLocation, this);
                this.mFusedLocationProvider.requestLocationUpdates(create, this.LocationCallback, null);
                this.LocationUpdatesStarted = true;
                Toast.makeText(this, "Location updates have been started.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // гештальт аномалию 180 сек. нельзя снова открыть
    private void GestaltLockout(final int gesIndex){
       Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                gesLockoutList[gesIndex] = 0;
            }
        }, 1200000);
    }

    // список аномалий
    // d - сила
    // d2 - радиус
    // гештальт должен идти первым
    // 0 - не гештальт, 1 - закрыто, 2 - открыто
    // вызывается в onCreate()
    private void GetAnomalies() {
        gesStatus = 1;
        Anomaly[] anomalyArr = new Anomaly[23];  //из 101 аномалии 3 для сталкерской рулетки и не учитываются в CheckAnomalies()
        // гештальт май21
        anomalyArr[0] = new Anomaly("Circle", "Ges", 0.01d, 35.0d, new LatLng(64.534434d, 40.152383d), this, gesStatus, false); // 65-42-4
        // постояные май21
        anomalyArr[1] = new Anomaly("Circle", "Psy", 20.0d, 25.0d, new LatLng(64.533965d, 40.155087d), this, 0, true); // 66-43-4
        anomalyArr[2] = new Anomaly("Circle", "Psy", 20.0d, 20.0d, new LatLng(64.535440d, 40.154287d), this, 0, true); // 64-43-9
        anomalyArr[3] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.534937d, 40.152989d), this, 0, false); // 65-42-3
        anomalyArr[4] = new Anomaly("Circle", "Bio", 0.3d, 30.0d, new LatLng(64.533953d, 40.153558d), this, 0, false); // 66-43-8
        anomalyArr[5] = new Anomaly("Circle", "Bio", 0.3d, 15.0d, new LatLng(64.534854d, 40.150913d), this, 0, false); // 65-41-3
        anomalyArr[6] = new Anomaly("Circle", "Bio", 0.3d, 15.0d, new LatLng(64.531503d, 40.155351d), this, 0, false); // 69-44-1
        anomalyArr[7] = new Anomaly("Circle", "Bio", 0.3d, 200.0d, new LatLng(64.533927d, 40.145173d), this, 0, false);// 66-39
        anomalyArr[8] = new Anomaly("Circle", "Psy", 0.3d, 90.0d, new LatLng(64.532701d, 40.152694d), this, 0, false);  // 67-42
        anomalyArr[9] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.531316d, 40.145014d), this, 0, true);  // 69-38-4
        anomalyArr[10] = new Anomaly("Circle", "Psy", 10.0d, 20.0d, new LatLng(64.529505d, 40.146785d), this, 0, true); //71-38-5
        anomalyArr[11] = new Anomaly("Circle", "Rad", 5.0d, 13.0d, new LatLng(64.531466d, 40.147349d), this, 0, true); // 68-41-5 бродячая
        anomalyArr[12] = new Anomaly("Circle", "Bio", 5.0d, 13.0d, new LatLng(64.531020d, 40.152906d), this, 0, true); // 69-43 бродячая
        // вход долгий
        anomalyArr[13] = new Anomaly("Circle", "Psy", 20.0d, 170.0d, new LatLng(64.532822d, 40.155424d), this, 0, true); //67-44 вход
        anomalyArr[13].minstrenght = 5;
        anomalyArr[14] = new Anomaly("Circle", "Psy", 10.0d, 115.0d, new LatLng(64.530637d, 40.158825d), this, 0, true); // 77-45 вход
        anomalyArr[15] = new Anomaly("Circle", "Rad", 10.0d, 35.0d, new LatLng(64.529540, 40.157033d), this, 0, true);  //65-38-5
        anomalyArr[15].minstrenght = 5;
        anomalyArr[16] = new Anomaly("Circle", "Bio", 15.0d, 45.0d, new LatLng(64.530752d, 40.154388d), this, 0, true); // 70-43-3
        anomalyArr[16].minstrenght = 5;
        // выход
        anomalyArr[17] = new Anomaly("Circle", "Psy", 20.0d, 234.0d, new LatLng(64.532200d, 40.153651d), this, 0, true); // выход
        anomalyArr[17].minstrenght = 15;
        anomalyArr[18] = new Anomaly("Circle", "Rad", 20.0d, 75.0d, new LatLng(64.535696d, 40.152235d), this, 0, true);  // выход
        /*----------------*/
        anomalyArr[19] = new MonolithAnomaly("Circle", "", 0.0d,  30.0d, new LatLng(64.528918d, 40.156468d), this, 0, false); // 72-44-4
        /**/
        anomalyArr[20] = new Anomaly("QR", "Rad", 1d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[21] = new Anomaly("QR", "Bio", 2d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[22] = new Anomaly("QR", "Psy", 1d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalies = anomalyArr;
    }

    public void GetTime() {
        this.cal = Calendar.getInstance();
        this.dayInt = this.cal.get(5);
        this.Hour = this.cal.get(11);
        this.Minutes = this.cal.get(12);
    }

    public LatLng moving_anomalies(LatLng start_LatLng, LatLng finish_latLng){
        double dLat = (start_LatLng.latitude - finish_latLng.latitude) / 60;
        double dLng = (start_LatLng.longitude - finish_latLng.longitude) / 60;
        Log.d("minutes", String.valueOf(new LatLng(start_LatLng.latitude + (dLat * (double) Minutes), start_LatLng.longitude + (dLng * (double) Minutes))));
        //return start_LatLng;
        return new LatLng(start_LatLng.latitude - (dLat * (double) Minutes), start_LatLng.longitude - (dLng * (double) Minutes));
    }

    // применяет аномалии
    // вызывается в MyLocationCallback()
    public void CheckAnomalies() {
        if (IS_ANOMALIES_AVAILABLE) {
            long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
            // постоянные аномалии
            if (timeInSeconds > checkTime_in) { // 14 мая 13:30
                for (int i = 0; i < 13; i++) {
                    anomalies[i].Apply();
                }

            }
            //14 мая 13:30 - 14:30
            CheckAnomaliesRegular(checkTime_in, (checkTime_in + 3600), 13, 17);
            // 16 мая с 15:20
            CheckAnomaliesRegular(checkTime_out, (checkTime_out + 4000), 17, 19);
            //
            CheckAnomaliesRegular(checkTime_in, (checkTime_out + 4000), 19, 20);
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
            radiusAnomaly = 0d;  // нужно для того, чтобы аномалии на карте рисовались

            for (int i = anomalyStart; i < anomalyFinish; i++) {
                if (anomalies[i].IsInside) {
                    if (anomalies[i].toShow) {
                        latLngAnomaly = anomalies[i].center;  // нужно для того, чтобы аномалии на карте рисовались
                        radiusAnomaly = anomalies[i].radius;  //
                    }
                    //   anomalyIndex = i;
                    IsInsideAnomaly = Boolean.TRUE;
                    break;
                }
            }
        }
    }

    // вызывается в MyLocationCallback()
    public void CheckIfInAnyAnomaly() {
        long timeInSeconds = (Calendar.getInstance().getTimeInMillis() / 1000);
        this.IsInsideAnomaly = Boolean.FALSE;
        detectWifi();

        radiusAnomaly = 0d; // нужно для того, чтобы аномалии на карте рисовались
        anomalies[11].center = moving_anomalies(new LatLng(64.531466d, 40.147349d), new LatLng(64.531956d, 40.151458d));
        anomalies[12].center = moving_anomalies(new LatLng(64.531020d, 40.152906d), new LatLng(64.530452d, 40.148013d));
        for (int i = 0 ; i < 13; i++) {
            if (anomalies[i].IsInside) {

                if (anomalies[i].toShow) {
                    latLngAnomaly = anomalies[i].center; // нужно для того, чтобы аномалии на карте рисовались
                    radiusAnomaly = anomalies[i].radius; //
                } else {
                    radiusAnomaly = 0d;
                }
                IsInsideAnomaly = Boolean.TRUE;
                if (!GestaltProtection) {                                                 //проверка на защиту от открытия гештальта
                    if (i < NUMBER_OF_GESTALT_ANOMALIES && anomalies[i].gesStatus == 1){  //если конкретный гештальт закрыт
                        if (gesLockoutList[i] != 1) {                                     // проверяет можно ли конкретный гештальт открыть
                            SQLiteDatabase database = dbHelper.getWritableDatabase();
                            anomalies[i].gesStatus = 2;                                   // открываем гештальт
                            /*ЕСЛИ ГЕШТАЛЬТ ОТКРЫВАЕТСЯ, ТО СТАВИТ ЕГО КООРДИНАТУ НА КАРТУ*/
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.KEY_NAME, "!!!GESTALT!!!");
                            contentValues.put(DBHelper.KEY_ICON, "icon");
                            contentValues.put(DBHelper.KEY_LATITUDE, Double.toString(anomalies[i].center.latitude));
                            contentValues.put(DBHelper.KEY_LONGITUDE, Double.toString(anomalies[i].center.longitude));
                            contentValues.put(DBHelper.KEY_COMMENT, "Обнаружен Гештальт");
                            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
                            dbHelper.close();
                        }
                    }
                }
            }
        }
        //14 мая 13:30 - 14:30
        CheckIfInAnyAnomalyRegular(checkTime_in, (checkTime_in + 3600), 13, 17);
        // 16 мая с 15:20
        CheckIfInAnyAnomalyRegular(checkTime_out, (checkTime_out + 4000), 17, 19);
        // monolith
        CheckIfInAnyAnomalyRegular(checkTime_in, (checkTime_out + 4000), 19, 20);
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
    }



    SuperSaveZone[] superSaveZones = new SuperSaveZone[8];
    public void Create_super_save_zones(){
        superSaveZones[0] = new SuperSaveZone(checkTime_in + 900, 0, 180, 20d, "stalkers_in");
        superSaveZones[1] = new SuperSaveZone(checkTime_in + 990, 1, 180, 20d, "stalkers_in");
        superSaveZones[2] = new SuperSaveZone(checkTime_in, 0, 180, 20d, "military_in");
        superSaveZones[3] = new SuperSaveZone(checkTime_in + 90, 1, 180, 20d, "military_in");
        superSaveZones[4] = new SuperSaveZone(checkTime_out, 0, 180, 30d, "stalkers_out");
        superSaveZones[5] = new SuperSaveZone(checkTime_out + 90, 1, 180, 30d, "stalkers_out");
        superSaveZones[6] = new SuperSaveZone(checkTime_out, 0, 180, 20d, "green_out");
        superSaveZones[7] = new SuperSaveZone(checkTime_out + 90, 1, 180, 20d, "green_out");
    }
    public void Super_save_zone_check(){
        if (((Calendar.getInstance().getTimeInMillis() / 1000) >= checkTime_in) && ((Calendar.getInstance().getTimeInMillis() / 1000) <= (checkTime_in + 3600))){
            for (SuperSaveZone superSaveZone : superSaveZones){
                LatLng check_save = superSaveZone.Check_super_save_zone();
                Location location = new Location("");
                location.setLatitude(check_save.latitude);
                location.setLongitude(check_save.longitude);
                if (location.distanceTo(MyCurrentLocation) <= superSaveZone.circle_radius){
                    if (Rad > 0) {
                        Rad -= 25; // должно выводиться за ...
                    }
                    if (Bio > 0) {
                        Bio -= 20; // должно выводиться за ...
                    }
                    Psy = 0.0d;
                    Health +=50;
                    if (Health > MaxHealth){
                        Health = MaxHealth;
                    }
                }
            }
        }
        if (((Calendar.getInstance().getTimeInMillis() / 1000) >= checkTime_out) && ((Calendar.getInstance().getTimeInMillis() / 1000) <= (checkTime_out + 3600))){
            for (SuperSaveZone superSaveZone : superSaveZones){
                LatLng check_save = superSaveZone.Check_super_save_zone();
                Location location = new Location("");
                location.setLatitude(check_save.latitude);
                location.setLongitude(check_save.longitude);
                if (location.distanceTo(MyCurrentLocation) <= superSaveZone.circle_radius){
                    if (Rad > 0) {
                        Rad -= 25; // должно выводиться за ...
                    }
                    if (Bio > 0) {
                        Bio -= 20; // должно выводиться за ...
                    }
                    Psy = 0.0d;
                    Health +=20;
                    if (Health > MaxHealth){
                        Health = MaxHealth;
                    }
                }
            }
        }
    }


    public void CreateSafeZones() {
        SafeZone[] safeZoneArr = new SafeZone[NUMBER_OF_SAVE_ZONES];
        safeZoneArr[0] = new SafeZone("Circle", 35.0d, new LatLng(64.356858d, 40.722128d), this);
        safeZoneArr[1] = new SafeZone("Circle", 35.0d, new LatLng(64.351080d, 40.736227d), this);
        safeZoneArr[2] = new SafeZone("Circle", 35.0d, new LatLng(64.351620d, 40.727617d), this);
        safeZoneArr[3] = new SafeZone("Circle", 35.0d, new LatLng(64.349791d, 40.726553d), this);
        safeZoneArr[4] = new SafeZone("Circle", 35.0d, new LatLng(64.356858d, 40.722128d), this);
        safeZoneArr[5] = new SafeZone("Circle", 35.0d, new LatLng(64.351080d, 40.736227d), this);
        safeZoneArr[6] = new SafeZone("Circle", 35.0d, new LatLng(64.351620d, 40.727617d), this);
        safeZoneArr[7] = new SafeZone("Circle", 35.0d, new LatLng(64.349791d, 40.726553d), this);
        safeZoneArr[8] = new SafeZone("Circle", 35.0d, new LatLng(64.351620d, 40.727617d), this);
        safeZoneArr[9] = new SafeZone("Circle", 35.0d, new LatLng(64.349791d, 40.726553d), this);
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
        Toast.makeText(getApplicationContext(), "Близиться выброс.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
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
            }
        }, 60000);
    }

    public void LoadStats() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.MaxHealth = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("MaxHealth", "2000")));
        this.Health = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Health", "2000")));
        this.Rad = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Rad", "0")));
        this.Bio = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Bio", "0")));
        this.Psy = Double.parseDouble(Objects.requireNonNull(defaultSharedPreferences.getString("Psy", "0")));
        this.PsyProtection = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("PsyProtection", "0")));
        this.RadProtection = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("RadProtection", "0")));
        this.BioProtection = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("BioProtection", "0")));
        this.GestaltProtection = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("GesProtection", "false")));
        this.anomalies[0].gesStatus = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("GesStatus", "1")));
        this.ScienceQR = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("ScienceQR", "0")));
        this.DischargeImmunity = Boolean.parseBoolean(defaultSharedPreferences.getString("DischargeImmunity", "false"));
        this.IsUnlocked = Boolean.parseBoolean(defaultSharedPreferences.getString("Lock", "true"));
        this.IS_ANOMALIES_AVAILABLE = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("IS_ANOMALIES_AVAILABLE", "true")));
        this.BioProtectionChangeability = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("BioProtectionChangeability", "true")));
        this.RadProtectionChangeability = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("RadProtectionChangeability", "true")));
        this.PsyProtectionChangeability = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("PsyProtectionChangeability", "true")));
        this.RadProtectionTemp = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("RadProtectionTemporary", "0")));
        this.BioProtectionTemp = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("BioProtectionTemporary", "0")));
        this.PsyProtectionTemp = Integer.parseInt(Objects.requireNonNull(defaultSharedPreferences.getString("PsyProtectionTemporary", "0")));
        this.DolgDischargeImmunity = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("DolgDischargeImmunity", "false")));
        this.NaemnikiDischargeImmunity = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("NaemnikiDischargeImmunity", "false")));
        this.fastRadPurification = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("fastRadPurification", "false")));
    }

    public void SaveStats() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("MaxHealth", Double.toString(this.MaxHealth));
        edit.putString("Health", Double.toString(this.Health));
        edit.putString("Rad", Double.toString(this.Rad));
        edit.putString("Bio", Double.toString(this.Bio));
        edit.putString("Psy", Double.toString(this.Psy));
        edit.putString("PsyProtection", Integer.toString(this.PsyProtection));
        edit.putString("BioProtection", Integer.toString(this.BioProtection));
        edit.putString("RadProtection", Integer.toString(this.RadProtection));
        edit.putString("GesProtection", Boolean.toString(this.GestaltProtection));
        edit.putString("GesStatus", Integer.toString(this.anomalies[0].gesStatus));
        edit.putString("ScienceQR", Integer.toString(this.ScienceQR));
        edit.putString("DischargeImmunity", Boolean.toString(this.DischargeImmunity));
        edit.putString("Lock", Boolean.toString(this.IsUnlocked));
        edit.putString("IS_ANOMALIES_AVAILABLE", Boolean.toString(this.IS_ANOMALIES_AVAILABLE));
        edit.putString("BioProtectionChangeability", Boolean.toString(this.BioProtectionChangeability));
        edit.putString("RadProtectionChangeability", Boolean.toString(this.RadProtectionChangeability));
        edit.putString("PsyProtectionChangeability", Boolean.toString(this.PsyProtectionChangeability));
        edit.putString("RadProtectionTemporary", Integer.toString(this.RadProtectionTemp));
        edit.putString("BioProtectionTemporary", Integer.toString(this.BioProtectionTemp));
        edit.putString("PsyProtectionTemporary", Integer.toString(this.PsyProtectionTemp));
        edit.putString("DolgDischargeImmunity", Boolean.toString(this.DolgDischargeImmunity));
        edit.putString("NaemnikiDischargeImmunity", Boolean.toString(this.NaemnikiDischargeImmunity));
        edit.putString("fastRadPurification", Boolean.toString(this.fastRadPurification));
        edit.commit();
    }
}
