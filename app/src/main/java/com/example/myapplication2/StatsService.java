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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class StatsService extends Service {
    private static final int ID_SERVICE = 101;
    private static final int NUMBER_OF_ANOMALIES = 348;
    private static final int NUMBER_OF_GESTALT_ANOMALIES = 8;
    private static final int NUMBER_OF_SAVE_ZONES = 4;
    private boolean IS_ANOMALIES_AVAILABLE = true;
    public Anomaly[] anomalies;
    public SafeZone[] SafeZones;
    public EffectManager EM;
    public double Health = 2000.0d, MaxHealth = 2000.0d;
    public double Bio = 0.0d, Psy = 0.0d, Rad = 0.0d;
    public int MaxRad = 1000, MaxBio = 1000;
    public int BioProtection = 0, PsyProtection = 0, RadProtection = 0;
    public double CurrentBio = 0.0d;
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

    public boolean anomalyFreedom = false;
    public boolean apocalypseFreedom = true;
    public boolean fastRadPurification = false;




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
                   CurrentBio = 0.0D;
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
                       RadProtection = 100;
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
                       BioProtection = 100;
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
                   CurrentBio = 0.0D;
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
                   int g = 3;
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
                   // 40минутный режим бога, чтобы до базы дойти
                   IS_ANOMALIES_AVAILABLE = false;
                   DischargeImmunity = true;
                   Handler handler2 = new Handler();
                   handler2.postDelayed(new Runnable() {
                       public void run() {
                           IS_ANOMALIES_AVAILABLE = true;
                           DischargeImmunity = false;
                           EM.PlaySound("Start", 1);
                       }
                   }, 2400000);
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
                   anomalyFreedom = true;
                   break;
               case 86:
                   anomalyFreedom = false;
                   apocalypseFreedom = false;
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
               case 90:
                   if (random.nextInt(2) == 1){
                       RadProtection = 100;
                       BioProtection = 100;
                       PsyProtection = 100;
                   }
                   break;
               case 91:
                   RadProtection = 0;
                   BioProtection = 0;
                   break;
               case 92:
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
    private FusedLocationProviderClient mFusedLocationProvider;
    private PowerManager.WakeLock wl;

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


    public void onCreate() {
        super.onCreate();
        this.wl = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "STALKERNET:My_Partial_Wake_Lock");
        this.wl.acquire(10*60*1000L /*10 minutes*/);   //timeout заставила студия поставить, не знаю как это работает
        this.EM = new EffectManager(this);
        GetAnomalies();
        CreateSafeZones();
        LoadStats();
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
        }, 180000);
    }

    // список аномалий
    // d - сила
    // d2 - радиус
    // гештальт должен идти первым
    // 0 - не гештальт, 1 - закрыто, 2 - открыто
    private void GetAnomalies() {
        gesStatus = 1;
        Anomaly[] anomalyArr = new Anomaly[351];  //из 101 аномалии 3 для сталкерской рулетки и не учитываются в CheckAnomalies()
        // две уникальные аномалии свободы
        anomalyArr[0] = new Anomaly("Circle", "RadF", 0.075d, 30.0d, new LatLng(64.351083d, 40.736226d), this, 0);
        anomalyArr[1] = new Anomaly("Circle", "Rad", 20.0d, 30.0d, new LatLng(64.351083d, 40.736226d), this, 0);
        // шесть постоянных гештальтов
        anomalyArr[2] = new Anomaly("Circle", "Ges", 0.1d, 30.0d, new LatLng(64.352548d, 40.735175d), this, gesStatus); //66-37-1
        anomalyArr[3] = new Anomaly("Circle", "Ges", 0.1d, 50.0d, new LatLng(64.354221d, 40.722431d), this, gesStatus); // перед блокпостом
        anomalyArr[4] = new Anomaly("Circle", "Ges", 0.1d, 50.0d, new LatLng(64.355471d, 40.729002d), this, gesStatus); // карьер
        anomalyArr[5] = new Anomaly("Circle", "Ges", 0.1d, 25.0d, new LatLng(64.354140, 40.743951d), this, gesStatus); // монолит
        anomalyArr[6] = new Anomaly("Circle", "Ges", 50.0d, 5.0d, new LatLng(60.351608d, 40.732768d), this, gesStatus);  //
        anomalyArr[7] = new Anomaly("Circle", "Ges", 1.0d, 50.0d, new LatLng(60.550774d, 39.787427d), this, gesStatus);//
        // постоянные аномалии
        anomalyArr[8] = new Anomaly("Circle", "Psy", 51.0d, 13.0d, new LatLng(64.356126d, 40.740157d), this, 0);  //аномалия демиурга
        anomalyArr[9] = new Anomaly("Circle", "Rad", 20.0d, 75.0d, new LatLng(64.352729d, 40.720197d), this, 0);  // 66-33-3
        anomalyArr[10] = new Anomaly("Circle", "Bio", 20.0d, 50.0d, new LatLng(64.351890d, 40.719835d), this, 0); //66-33-4
        anomalyArr[11] = new Anomaly("Circle", "Psy", 20.0d, 30.0d, new LatLng(64.351239d, 40.720307d), this, 0); // 66-33-5
        anomalyArr[11].minstrenght = 5;
        anomalyArr[12] = new Anomaly("Circle", "Psy", 45.0d, 25.0d, new LatLng(64.350756d, 40.720683d), this, 0); // 67-34-1
        anomalyArr[13] = new Anomaly("Circle", "Rad", 30.0d, 30.0d, new LatLng(64.349734d, 40.738518d), this, 0); //67-37-4
        anomalyArr[13].minstrenght = 5;
        anomalyArr[14] = new Anomaly("Circle", "Psy", 10.0d, 15.0d, new LatLng(64.352559d, 40.743075d), this, 0); //66-38-3
        anomalyArr[15] = new Anomaly("Circle", "Psy", 10.0d, 30.0d, new LatLng(64.352917d, 40.742903d), this, 0);  //65-38-5
        anomalyArr[15].minstrenght = 2;
        anomalyArr[16] = new Anomaly("Circle", "Psy", 10.0d, 25.0d, new LatLng(64.353147d, 40.742058d), this, 0); // 65-38-6
        anomalyArr[16].minstrenght = 2;
        anomalyArr[17] = new Anomaly("Circle", "Psy", 50.0d, 24.0d, new LatLng(64.353385d, 40.742798d), this, 0);  // 65-38-5
        anomalyArr[17].minstrenght = 5;
        anomalyArr[18] = new Anomaly("Circle", "Psy", 5.0d, 15.0d, new LatLng(64.353618d, 40.743541d), this, 0);  //камень
        anomalyArr[19] = new Anomaly("Circle", "Psy", 3.0d, 15.0d, new LatLng(64.353740d, 40.742535d), this, 0); //65-38-4
        anomalyArr[20] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.352859d, 40.746178d), this, 0); // 66-39-2
        anomalyArr[21] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.351104d, 40.743024d), this, 0);  // 66-38-5
        anomalyArr[22] = new Anomaly("Circle", "Rad", 5.0d, 10.0d, new LatLng(64.351609d, 40.725945d), this, 0);  // болотный доктор
        anomalyArr[23] = new Anomaly("Circle", "Bio", 5.0d, 10.0d, new LatLng(64.351479d, 40.725596d), this, 0);  // 64.351479, 40.725596
        anomalyArr[24] = new Anomaly("Circle", "Psy", 5.0d, 10.0d, new LatLng(64.351636d, 40.725038d), this, 0);  // 64.351636, 40.725038
        anomalyArr[25] = new Anomaly("Circle", "Rad", 5.0d, 10.0d, new LatLng(64.351950d, 40.725073d), this, 0);  // 64.351950, 40.725073
        anomalyArr[26] = new MonolithAnomaly("Circle", "", 30.0d, (double) 0, new LatLng(64.353373d, 40.742782d), this, 0); // monolith2 64.353373, 40.742782
        anomalyArr[27] = new Anomaly("Circle", "Rad", 10.0d, 7.0d, new LatLng(64.352731d, 40.735183d), this, 0); // 66-37-1 с гештальтом с сквозняка
        anomalyArr[28] = new Anomaly("Circle", "Rad", 10.0d, 10.0d, new LatLng(64.355593d, 40.729067d), this, 0);  // карьер
        anomalyArr[29] = new Anomaly("Circle", "Bio", 20.0d, 30.0d, new LatLng(64.355441d, 40.742621d), this, 0);  // затычка у монолита
        anomalyArr[30] = new Anomaly("Circle", "Rad", 20.0d, 30.0d, new LatLng(64.355719d, 40.744187d), this, 0);  // затычка у монолита
        anomalyArr[31] = new Anomaly("Circle", "Psy", 50.0d, 75.0d, new LatLng(64.356110d, 40.746301d), this, 0);  // затычка у монолита
        anomalyArr[32] = new Anomaly("Circle", "Rad", 30.0d, 30.0d, new LatLng(64.355779d, 40.741848d), this, 0);  // затычка у монолита
        /*
        день первый
        * 17:00 14.09 - 18:00 15.09
        *
        */
        /*стена между свободой и баром*/
        anomalyArr[33] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351039d, 40.734708d ), this, 0); //64.526125, 40.603979
        anomalyArr[33].minstrenght = 10;
        anomalyArr[34] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350672d, 40.734730d), this, 0); //64.350672, 40.734730
        anomalyArr[34].minstrenght = 10;
        anomalyArr[35] = new Anomaly("Circle", "Psy", 5.0d, 22.0d, new LatLng(64.350327d, 40.734955d), this, 0); //64.350327, 40.734955
        anomalyArr[35].minstrenght = 1;
        anomalyArr[36] = new Anomaly("Circle", "Bio", 10.0d, 22.0d, new LatLng(64.350121d, 40.735728d), this, 0); // 64.350121, 40.735728
        anomalyArr[36].minstrenght = 2;
        anomalyArr[37] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350107d, 40.736597d), this, 0);  //64.350107, 40.736597
        anomalyArr[37].minstrenght = 10;
        anomalyArr[38] = new Anomaly("Circle", "Rad", 5.0d, 22.0d, new LatLng(64.350274d, 40.734816d), this, 0); //64.350274, 40.734816
        anomalyArr[39] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351181d, 40.734429d), this, 0);  //64.351181, 40.734429
        anomalyArr[39].minstrenght = 10;
        anomalyArr[40] = new Anomaly("Circle", "Psy", 60.0d, 30.0d, new LatLng(64.351688, 40.733947d), this, 0);  //прямо на дороге, поэтому сильнее
        anomalyArr[40].minstrenght = 10;
        anomalyArr[41] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352018d, 40.733625d), this, 0); // 64.352018, 40.733625
        anomalyArr[41].minstrenght = 10;
        anomalyArr[42] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352322d, 40.733255d), this, 0); // 64.352322, 40.733255
        anomalyArr[42].minstrenght = 10;
        anomalyArr[43] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.352662d, 40.732895d), this, 0); // 64.352662, 40.732895
        anomalyArr[43].minstrenght = 10;
        /*между стеной свободы и долга*/
        anomalyArr[44] = new Anomaly("Circle", "Psy", 5.0d, 22.0d, new LatLng(64.350212d, 40.732777d), this, 0); //64.350212, 40.732777
        anomalyArr[44].minstrenght = 5;
        anomalyArr[45] = new Anomaly("Circle", "Rad", 20.0d, 22.0d, new LatLng(64.350203d, 40.731876d), this, 0);  // 64.350203, 40.731876
        anomalyArr[45].minstrenght = 5;
        anomalyArr[46] = new Anomaly("Circle", "Rad", 20.0d, 22.0d, new LatLng(64.350208d, 40.731125d), this, 0); //  64.350208, 40.731125
        anomalyArr[46].minstrenght = 5;
        anomalyArr[47] = new Anomaly("Circle", "Rad", 30.0d, 22.0d, new LatLng(64.350305d, 40.730299d), this, 0); // 64.350305, 40.730299
        anomalyArr[47].minstrenght = 5;
        anomalyArr[48] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351152d, 40.731522d), this, 0); // 64.351152, 40.731522
        anomalyArr[48].minstrenght = 5;
        anomalyArr[49] = new Anomaly("Circle", "Psy", 40.0d, 22.0d, new LatLng(64.351489d, 40.731806d), this, 0); // 64.351489, 40.731806
        anomalyArr[49].minstrenght = 5;
        anomalyArr[50] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351484d, 40.732552d), this, 0); // 64.351484, 40.732552
        anomalyArr[50].minstrenght = 5;
        anomalyArr[51] = new Anomaly("Circle", "Psy", 20.0d, 22.0d, new LatLng(64.350271d, 40.728765d), this, 0); // 64.350271, 40.728765
        /*стена между долгом и баром*/
        anomalyArr[52] = new Anomaly("Circle", "Bio", 50.0d, 25.0d, new LatLng(64.351546d, 40.729297d), this, 0); //64.351546, 40.729297
        anomalyArr[53] = new Anomaly("Circle", "Psy", 50.0d, 25.0d, new LatLng(64.351451d, 40.729255d), this, 0); // 64.351451, 40.729255
        anomalyArr[54] = new Anomaly("Circle", "Rad", 50.0d, 25.0d, new LatLng(64.351367d, 40.729195d), this, 0);  // 64.351367, 40.729195
        anomalyArr[55] = new Anomaly("Circle", "Rad", 50.0d, 25.0d, new LatLng(64.351288d, 40.729142d), this, 0);  //
        anomalyArr[56] = new Anomaly("Circle", "Rad", 50.0d, 20.0d, new LatLng(64.351204d, 40.729056d), this, 0);  // 64.351263, 40.728184
        anomalyArr[57] = new Anomaly("Circle", "Bio", 50.0d, 20.0d, new LatLng(64.351125d, 40.728922d), this, 0);  // 64.351182, 40.728098
        anomalyArr[58] = new Anomaly("Circle", "Rad", 50.0d, 20.0d, new LatLng(64.351023d, 40.728761d), this, 0);  // 64.351014, 40.727938
        anomalyArr[59] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351640d, 40.729220d), this, 0);  // 64.351430,
        anomalyArr[60] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351727d, 40.729190d), this, 0);  // 64.351430,
        anomalyArr[61] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351825d, 40.729171d), this, 0);  // 64.351430,
        anomalyArr[62] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[63] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[64] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352085d, 40.729222d), this, 0);  // 64.351430,
        anomalyArr[65] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352175d, 40.729244d), this, 0);  // 64.351430,
        anomalyArr[66] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352269d, 40.729260d), this, 0);  // 64.351430,
        anomalyArr[67] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352355d, 40.729287d), this, 0);  // 64.351430,
        anomalyArr[68] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352446d, 40.729314d), this, 0);  // 64.351430,
        anomalyArr[69] = new Anomaly("Circle", "Psy", 50.0d, 13.0d, new LatLng(64.352533d, 40.729340d), this, 0);  // 64.351430,
        anomalyArr[70] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352619d, 40.729370d), this, 0);  // 64.351430,
        anomalyArr[71] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352710d, 40.729405d), this, 0);  // 64.351430,
        anomalyArr[72] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352794d, 40.729445d), this, 0);  // 64.351430,
        anomalyArr[73] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352971d, 40.729469d), this, 0);  // 64.351430,
        anomalyArr[74] = new Anomaly("Circle", "Bio", 20.0d, 20.0d, new LatLng(64.353278d, 40.729614d), this, 0);  // 64.351430,
        /*между блокпостом и колючкой*/
        anomalyArr[75] = new Anomaly("Circle", "Rad", 20.0d, 50.0d, new LatLng(64.354430d, 40.720039d), this, 0);  // 64.351430,
        anomalyArr[76] = new Anomaly("Circle", "Bio", 15.0d, 22.0d, new LatLng(64.354268d, 40.724770d), this, 0);  // 64.351430,
        anomalyArr[77] = new Anomaly("Circle", "Psy", 11.0d, 15.0d, new LatLng(64.355437d, 40.723955d), this, 0);  // Добавить голос для коменданта
        anomalyArr[78] = new Anomaly("Circle", "Psy", 10.0d, 15.0d, new LatLng(64.353465d, 40.733485d), this, 0); // 65-36-4
        anomalyArr[79] = new Anomaly("Circle", "Rad", 10.0d, 35.0d, new LatLng(64.355518d, 40.726717d), this, 0);  //
        anomalyArr[80] = new Anomaly("Circle", "Rad", 10.0d, 20.0d, new LatLng(64.356279d, 40.737570d), this, 0);  //
        /*вокруг болотного доктора*/
        anomalyArr[81] = new Anomaly("Circle", "Rad", 10.0d, 25.0d, new LatLng(64.350820d, 40.726519d), this, 0);  //
        anomalyArr[82] = new Anomaly("Circle", "Psy", 10.0d, 5.0d, new LatLng(64.351910d, 40.725875d), this, 0);  //
        anomalyArr[83] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.352475d, 40.726082d), this, 0);  //
        anomalyArr[84] = new Anomaly("Circle", "Bio", 15.0d, 15.0d, new LatLng(64.352575d, 40.726881d), this, 0);  //
        anomalyArr[85] = new Anomaly("Circle", "Rad", 10.0d, 20.0d, new LatLng(64.353257d, 40.726854d), this, 0);  //
        anomalyArr[86] = new Anomaly("Circle", "Bio", 10.0d, 20.0d, new LatLng(64.353084d, 40.725417d), this, 0);  //
        anomalyArr[87] = new Anomaly("Circle", "Rad", 20.0d, 42.0d, new LatLng(64.352117d, 40.722778d), this, 0);  //
        anomalyArr[88] = new Anomaly("Circle", "Psy", 10.0d, 30.0d, new LatLng(64.353852d, 40.727788d), this, 0);  // в болоте слева
        /*справа от болотной тропы и до колючки*/
        anomalyArr[89] = new Anomaly("Circle", "Rad", 30.0d, 20.0d, new LatLng(64.354435d, 40.729183d), this, 0);  // заглушка на тропу
        anomalyArr[90] = new Anomaly("Circle", "Bio", 30.0d, 20.0d, new LatLng(64.354005d, 40.729671d), this, 0);  // по середине тропы
        anomalyArr[91] = new Anomaly("Circle", "Rad", 15.0d, 20.0d, new LatLng(64.354963d, 40.732616d), this, 0);  //
        anomalyArr[92] = new Anomaly("Circle", "Bio", 15.0d, 20.0d, new LatLng(64.355021d, 40.733919d), this, 0);  //
        anomalyArr[93] = new Anomaly("Circle", "Rad", 15.0d, 20.0d, new LatLng(64.355235d, 40.735169d), this, 0);  //
        /*в районе монолита и свободы*/
        anomalyArr[94] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.354892d, 40.740475d), this, 0);  //
        anomalyArr[95] = new Anomaly("Circle", "Bio", 10.0d, 20.0d, new LatLng(64.354250d, 40.739552d), this, 0);  //
        anomalyArr[96] = new Anomaly("Circle", "Rad", 10.0d, 15.0d, new LatLng(64.353409d, 40.738597d), this, 0);  //
        anomalyArr[97] = new Anomaly("Circle", "Rad", 30.0d, 15.0d, new LatLng(64.351935d, 40.737374d), this, 0);  //
        anomalyArr[98] = new Anomaly("Circle", "Rad", 10.0d, 7.0d, new LatLng(64.352879d, 40.739852d), this, 0);  //
        anomalyArr[99] = new Anomaly("Circle", "Bio", 10.0d, 50.0d, new LatLng(64.351233d, 40.739498d), this, 0);  //
        anomalyArr[100] = new Anomaly("Circle", "Bio", 15.0d, 32.0d, new LatLng(64.352535d, 40.740657d), this, 0);  //
        anomalyArr[101] = new Anomaly("Circle", "Rad", 15.0d, 40.0d, new LatLng(64.350656d, 40.741279d), this, 0);  //
        /*мина на дороге в бар*/
        anomalyArr[102] = new Anomaly("Circle", "Rad", 20.0d, 2.0d, new LatLng(64.351991d, 40.732283d), this, 0);  //
        /*
        * 2 день
        * 15 - 16 сентября
        * 18:00 - 11:00
        */
        /*стена между свободой и баром*/
        anomalyArr[103] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.351039d, 40.734708d), this, 0); //от базы вниз 64.526125, 40.603979
        anomalyArr[103].minstrenght = 10;
        anomalyArr[104] = new Anomaly("Circle", "Rad", 10.0d, 22.0d, new LatLng(64.350672d, 40.734730d), this, 0); //64.350672, 40.734730
        anomalyArr[104].minstrenght = 10;
        anomalyArr[105] = new Anomaly("Circle", "Psy", 5.0d, 22.0d, new LatLng(64.350327d, 40.734955d), this, 0); //64.350327, 40.734955
        anomalyArr[105].minstrenght = 10;
        anomalyArr[106] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.350121d, 40.735728d), this, 0); // 64.350121, 40.735728
        anomalyArr[106].minstrenght = 10;
        anomalyArr[107] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.350107d, 40.736597d), this, 0);  //самый низ
        anomalyArr[107].minstrenght = 10;
        anomalyArr[108] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.350274d, 40.734816d), this, 0); //аппендикс
        anomalyArr[109] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.351181d, 40.734429d), this, 0);  //чуть выше базы и вверх
        anomalyArr[109].minstrenght = 10;
        anomalyArr[110] = new Anomaly("Circle", "Psy", 60.0d, 30.0d, new LatLng(64.351688, 40.733947d), this, 0);  //прямо на дороге, поэтому сильнее
        anomalyArr[110].minstrenght = 10;
        anomalyArr[111] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352018d, 40.733625d), this, 0); // 64.352018, 40.733625
        anomalyArr[111].minstrenght = 10;
        anomalyArr[112] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352322d, 40.733255d), this, 0); // 64.352322, 40.733255
        anomalyArr[112].minstrenght = 10;
        anomalyArr[113] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.352662d, 40.732895d), this, 0); // 64.352662, 40.732895
        anomalyArr[113].minstrenght = 10;
        /*стена между баром и колючкой*/
        anomalyArr[114] = new Anomaly("Circle", "Rad", 5.0d, 22.0d, new LatLng(64.352797d, 40.732354d), this, 0); // начинается от стены свободы
        anomalyArr[114].minstrenght = 10;
        anomalyArr[115] = new Anomaly("Circle", "Bio", 20.0d, 12.0d, new LatLng(64.352776d, 40.731780d), this, 0); //
        anomalyArr[115].minstrenght = 10;
        anomalyArr[116] = new Anomaly("Circle", "Rad", 20.0d, 12.0d, new LatLng(64.352764d, 40.731367d), this, 0); //
        anomalyArr[116].minstrenght = 10;
        anomalyArr[117] = new Anomaly("Circle", "Bio", 50.0d, 12.0d, new LatLng(64.352767d, 40.730938d), this, 0); //
        anomalyArr[117].minstrenght = 10;
        anomalyArr[118] = new Anomaly("Circle", "Rad", 50.0d, 12.0d, new LatLng(64.352725d, 40.730541d), this, 0); //
        anomalyArr[118].minstrenght = 10;
        anomalyArr[119] = new Anomaly("Circle", "Psy", 50.0d, 12.0d, new LatLng(64.352706d, 40.730117d), this, 0); //
        anomalyArr[119].minstrenght = 10;
        anomalyArr[120] = new Anomaly("Circle", "Psy", 50.0d, 12.0d, new LatLng(64.352720d, 40.729715d), this, 0); //
        anomalyArr[120].minstrenght = 10;
        /*стена между долгом и баром*/
        anomalyArr[121] = new Anomaly("Circle", "Bio", 50.0d, 25.0d, new LatLng(64.351546d, 40.729297d), this, 0); //64.351546, 40.729297
        anomalyArr[122] = new Anomaly("Circle", "Psy", 50.0d, 25.0d, new LatLng(64.351451d, 40.729255d), this, 0); // 64.351451, 40.729255
        anomalyArr[123] = new Anomaly("Circle", "Rad", 50.0d, 25.0d, new LatLng(64.351367d, 40.729195d), this, 0);  // 64.351367, 40.729195
        anomalyArr[124] = new Anomaly("Circle", "Rad", 50.0d, 25.0d, new LatLng(64.351288d, 40.729142d), this, 0);  //
        anomalyArr[125] = new Anomaly("Circle", "Rad", 50.0d, 20.0d, new LatLng(64.351204d, 40.729056d), this, 0);  // 64.351263, 40.728184
        anomalyArr[126] = new Anomaly("Circle", "Bio", 50.0d, 20.0d, new LatLng(64.351125d, 40.728922d), this, 0);  // 64.351182, 40.728098
        anomalyArr[127] = new Anomaly("Circle", "Rad", 50.0d, 20.0d, new LatLng(64.351023d, 40.728761d), this, 0);  // 64.351014, 40.727938
        anomalyArr[128] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351640d, 40.729220d), this, 0);  // 64.351430,
        anomalyArr[129] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351727d, 40.729190d), this, 0);  // 64.351430,
        anomalyArr[130] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351825d, 40.729171d), this, 0);  // 64.351430,
        anomalyArr[131] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[132] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[133] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352085d, 40.729222d), this, 0);  // 64.351430,
        anomalyArr[134] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352175d, 40.729244d), this, 0);  // 64.351430,
        anomalyArr[135] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352269d, 40.729260d), this, 0);  // 64.351430,
        anomalyArr[136] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352355d, 40.729287d), this, 0);  // 64.351430,
        anomalyArr[137] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352446d, 40.729314d), this, 0);  // 64.351430,
        anomalyArr[138] = new Anomaly("Circle", "Psy", 50.0d, 13.0d, new LatLng(64.352533d, 40.729340d), this, 0);  // 64.351430,
        anomalyArr[139] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352619d, 40.729370d), this, 0);  // 64.351430,
        anomalyArr[140] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352710d, 40.729405d), this, 0);  // 64.351430,
        anomalyArr[140] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352794d, 40.729445d), this, 0);  // 64.351430,
        /*между стеной свободы и долга*/
        anomalyArr[141] = new Anomaly("Circle", "Bio", 5.0d, 22.0d, new LatLng(64.350212d, 40.732777d), this, 0); //64.350212, 40.732777
        anomalyArr[141].minstrenght = 5;
        anomalyArr[142] = new Anomaly("Circle", "Bio", 20.0d, 22.0d, new LatLng(64.350203d, 40.731876d), this, 0);  // 64.350203, 40.731876
        anomalyArr[142].minstrenght = 5;
        anomalyArr[143] = new Anomaly("Circle", "Bio", 20.0d, 22.0d, new LatLng(64.350208d, 40.731125d), this, 0); //  64.350208, 40.731125
        anomalyArr[143].minstrenght = 5;
        anomalyArr[144] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.350305d, 40.730299d), this, 0); // 64.350305, 40.730299
        anomalyArr[144].minstrenght = 5;
        anomalyArr[145] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.351152d, 40.731522d), this, 0); // 64.351152, 40.731522
        anomalyArr[145].minstrenght = 5;
        anomalyArr[146] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.351489d, 40.731806d), this, 0); // 64.351489, 40.731806
        anomalyArr[146].minstrenght = 5;
        anomalyArr[147] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.351484d, 40.732552d), this, 0); // 64.351484, 40.732552
        anomalyArr[147].minstrenght = 5;
        anomalyArr[148] = new Anomaly("Circle", "Bio", 20.0d, 22.0d, new LatLng(64.350271d, 40.728765d), this, 0); // 64.350271, 40.728765
        /*между блокпостом и колючкой*/
        anomalyArr[149] = new Anomaly("Circle", "Rad", 50.0d, 20.0d, new LatLng(64.353790d, 40.725051d), this, 0);  // затычка на вход
        anomalyArr[149].minstrenght = 10;
        anomalyArr[150] = new Anomaly("Circle", "Bio", 15.0d, 27.0d, new LatLng(64.354287d, 40.723661d), this, 0);  //
        anomalyArr[151] = new Anomaly("Circle", "Rad", 11.0d, 40.0d, new LatLng(64.353920d, 40.719498d), this, 0);  //
        anomalyArr[152] = new Anomaly("Circle", "Rad", 10.0d, 25.0d, new LatLng(64.354780d, 40.729981d), this, 0); //
        anomalyArr[153] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.356210d, 40.736777d), this, 0);  //
        anomalyArr[154] = new Anomaly("Circle", "Rad", 10.0d, 40.0d, new LatLng(64.355340d, 40.734331d), this, 0);  //
        anomalyArr[155] = new Anomaly("Circle", "Psy", 11.0d, 15.0d, new LatLng(64.355437d, 40.723955d), this, 0);  // Добавить голос для коменданта
        /*вокруг болотного доктора*/
        anomalyArr[156] = new Anomaly("Circle", "Rad", 10.0d, 20.0d, new LatLng(64.352753d, 40.726456d), this, 0);  // дырка в заборе
        anomalyArr[157] = new Anomaly("Circle", "Psy", 10.0d, 30.0d, new LatLng(64.353852d, 40.727788d), this, 0);  // в болоте слева
        anomalyArr[158] = new Anomaly("Circle", "Bio", 10.0d, 48.0d, new LatLng(64.352216d, 40.723248d), this, 0);  //
        anomalyArr[159] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.353332d, 40.729321d), this, 0);  // у выхода с болотной тропы
        anomalyArr[160] = new Anomaly("Circle", "Rad", 15.0d, 30.0d, new LatLng(64.350728d, 40.726928d), this, 0);  //
        /*справа от болотной тропы и до колючки*/
        anomalyArr[161] = new Anomaly("Circle", "Bio", 15.0d, 20.0d, new LatLng(64.353462d, 40.733473d), this, 0);  //
        anomalyArr[162] = new Anomaly("Circle", "Rad", 15.0d, 32.0d, new LatLng(64.353462d, 40.733473d), this, 0);  //
        anomalyArr[163] = new Anomaly("Circle", "Rad", 15.0d, 30.0d, new LatLng(64.355210d, 40.739449d), this, 0);  //
        anomalyArr[164] = new Anomaly("Circle", "Bio", 12.0d, 22.0d, new LatLng(64.353750d, 40.730780d), this, 0);  //
        anomalyArr[165] = new Anomaly("Circle", "Bio", 20.0d, 15.0d, new LatLng(64.354331d, 40.730994d), this, 0);  //
        /*в районе монолита и свободы*/
        anomalyArr[166] = new Anomaly("Circle", "Bio", 30.0d, 15.0d, new LatLng(64.351928d, 40.737378d), this, 0);  // перекресток
        anomalyArr[167] = new Anomaly("Circle", "Bio", 10.0d, 20.0d, new LatLng(64.353495d, 40.740093d), this, 0);  //
        anomalyArr[168] = new Anomaly("Circle", "Psy", 10.0d, 25.0d, new LatLng(64.351156d, 40.738548d), this, 0);  //
        anomalyArr[169] = new Anomaly("Circle", "Rad", 15.0d, 30.0d, new LatLng(64.351704d, 40.741992d), this, 0);  //
        anomalyArr[170] = new Anomaly("Circle", "Bio", 10.0d, 28.0d, new LatLng(64.352397d, 40.739899d), this, 0);  //
        anomalyArr[171] = new Anomaly("Circle", "Bio", 10.0d, 21.0d, new LatLng(64.353057d, 40.737121d), this, 0);  //
        /*
        * постоянная аномалия малого монолита и несколько временных
        * */
        // 15.09 18:?? - 18:40
        anomalyArr[172] = new Anomaly("Circle", "Psy", 10.0d, 10.0d, new LatLng(64.358104d, 40.722633d), this, 0);  // монолит
        anomalyArr[173] = new Anomaly("Circle", "Psy", 5.0d, 10.0d, new LatLng(64.355986d, 40.722880d), this, 0);  // БП
        anomalyArr[174] = new Anomaly("Circle", "Psy", 5.0d, 10.0d, new LatLng(64.357067d, 40.720553d), this, 0);  // палатки
        anomalyArr[175] = new Anomaly("Circle", "Psy", 5.0d, 10.0d, new LatLng(64.356830d, 40.721672d), this, 0);  // ученые
        /*
        *3 день
        * 16 - 17 сентября
        * 11:00 - 16:00
        *
        * */
        /*стена между свободой и баром*/
        anomalyArr[176] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351039d, 40.734708d), this, 0); //от базы вниз  64.526125, 40.603979
        anomalyArr[176].minstrenght = 10;
        anomalyArr[177] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.350672d, 40.734730d), this, 0); //
        anomalyArr[177].minstrenght = 10;
        anomalyArr[178] = new Anomaly("Circle", "Psy", 5.0d, 22.0d, new LatLng(64.350327d, 40.734955d), this, 0); //
        anomalyArr[178].minstrenght = 1;
        anomalyArr[179] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.350121d, 40.735728d), this, 0); //
        anomalyArr[179].minstrenght = 10;

        anomalyArr[180] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.349798d, 40.736982d), this, 0); //
        anomalyArr[181] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.349468d, 40.737057d), this, 0); // самый низ
        anomalyArr[182] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351530d, 40.733879d), this, 0);  //чуть выше базы и вверх
        anomalyArr[182].minstrenght = 10;//
        anomalyArr[183] = new Anomaly("Circle", "Psy", 60.0d, 30.0d, new LatLng(64.351688d, 40.733947d), this, 0);  //прямо на дороге, поэтому сильнее
        anomalyArr[183].minstrenght = 10;
        anomalyArr[184] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351931d, 40.734238d), this, 0); //
        anomalyArr[184].minstrenght = 10;
        anomalyArr[185] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352082d, 40.734968d), this, 0); //
        anomalyArr[185].minstrenght = 10;
        anomalyArr[186] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352121d, 40.735754d), this, 0); //
        anomalyArr[186].minstrenght = 10;
        anomalyArr[187] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352120d, 40.736600d), this, 0); //
        anomalyArr[187].minstrenght = 10;
        anomalyArr[188] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352202d, 40.737389d), this, 0); //
        anomalyArr[188].minstrenght = 10;
        anomalyArr[189] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352435d, 40.738011d), this, 0); //
        anomalyArr[189].minstrenght = 10;
        /*стена между долгом и баром*/
        anomalyArr[190] = new Anomaly("Circle", "Bio", 50.0d, 25.0d, new LatLng(64.351546d, 40.729297d), this, 0); //64.351546, 40.729297
        anomalyArr[191] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351640d, 40.729220d), this, 0);  // 64.351430,
        anomalyArr[192] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351727d, 40.729190d), this, 0);  // 64.351430,
        anomalyArr[193] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351825d, 40.729171d), this, 0);  // 64.351430,
        anomalyArr[194] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[195] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[196] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352085d, 40.729222d), this, 0);  // 64.351430,
        anomalyArr[197] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352175d, 40.729244d), this, 0);  // 64.351430,
        anomalyArr[198] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352269d, 40.729260d), this, 0);  // 64.351430,
        anomalyArr[199] = new Anomaly("Circle", "Bio", 1.0d, 1.0d, new LatLng(64.352269d, 40.729260d), this, 0);  // 64.351430,
        anomalyArr[200] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352355d, 40.729287d), this, 0);  // 64.351430,
        anomalyArr[201] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352446d, 40.729314d), this, 0);  // 64.351430,
        anomalyArr[202] = new Anomaly("Circle", "Psy", 50.0d, 13.0d, new LatLng(64.352533d, 40.729340d), this, 0);  // 64.351430,
        anomalyArr[203] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352619d, 40.729370d), this, 0);  // 64.351430,
        anomalyArr[204] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352710d, 40.729405d), this, 0);  // 64.351430,
        anomalyArr[205] = new Anomaly("Circle", "Bio", 50.0d, 16.0d, new LatLng(64.352794d, 40.729445d), this, 0);  // 64.351430,
        anomalyArr[206] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352834d, 40.730045d), this, 0);  // 64.351430,
        anomalyArr[207] = new Anomaly("Circle", "Bio", 50.0d, 15.0d, new LatLng(64.352935d, 40.730638d), this, 0);  // 64.351430,
        anomalyArr[208] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352948d, 40.730780d), this, 0);  // 64.351430,
        anomalyArr[209] = new Anomaly("Circle", "Psy", 50.0d, 15.0d, new LatLng(64.353025d, 40.731270d), this, 0);  // 64.351430,
        anomalyArr[210] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.353135d, 40.731812d), this, 0);  // 64.351430,
        anomalyArr[211] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.353237d, 40.732345d), this, 0);  // 64.351430,
        /*между стеной свободы и долга*/
        anomalyArr[212] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351346d, 40.729807d), this, 0); //64.350212, 40.732777
        anomalyArr[212].minstrenght = 5;
        anomalyArr[213] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.351367d, 40.730644d), this, 0);  // 64.350203, 40.731876
        anomalyArr[213].minstrenght = 5;
        anomalyArr[214] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351374d, 40.731417d), this, 0); //  64.350208, 40.731125
        anomalyArr[214].minstrenght = 5;
        anomalyArr[215] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351392d, 40.732213d), this, 0); // 64.350305, 40.730299
        anomalyArr[215].minstrenght = 5;
        anomalyArr[216] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.351392d, 40.732213d), this, 0); // 64.351152, 40.731522
        anomalyArr[216].minstrenght = 5;
        anomalyArr[217] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351392d, 40.732213d), this, 0); // 64.351489, 40.731806
        anomalyArr[217].minstrenght = 5;
        anomalyArr[218] = new Anomaly("Circle", "Rad", 40.0d, 22.0d, new LatLng(64.351438d, 40.734552d), this, 0); // 64.351484, 40.732552
        anomalyArr[218].minstrenght = 5;
        /*между блокпостом и колючкой*/
        anomalyArr[219] = new Anomaly("Circle", "Rad", 30.0d, 40.0d, new LatLng(64.354463d, 40.729075d), this, 0);  // затычка на вход
        anomalyArr[219].minstrenght = 10;
        anomalyArr[220] = new Anomaly("Circle", "Bio", 50.0d, 15.0d, new LatLng(64.354463d, 40.729075d), this, 0);  // вторая затычка на тот же ход
        anomalyArr[221] = new Anomaly("Circle", "Rad", 11.0d, 20.0d, new LatLng(64.355840d, 40.725846d), this, 0);  //
        anomalyArr[222] = new Anomaly("Circle", "Rad", 10.0d, 45.0d, new LatLng(64.353971d, 40.723604d), this, 0); //
        anomalyArr[223] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.355648d, 40.731989d), this, 0);  //
        anomalyArr[224] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.355460d, 40.735368d), this, 0);  //
        anomalyArr[225] = new Anomaly("Circle", "Psy", 11.0d, 15.0d, new LatLng(64.355437d, 40.723955d), this, 0);  // Добавить голос для коменданта
        anomalyArr[226] = new Anomaly("Circle", "Rad", 12.0d, 15.0d, new LatLng(64.356759d, 40.739445d), this, 0);  //
        /*вокруг болотного доктора*/
        anomalyArr[227] = new Anomaly("Circle", "Psy", 15.0d, 20.0d, new LatLng(64.353430d, 40.724822d), this, 0);  //
        anomalyArr[228] = new Anomaly("Circle", "Rad", 10.0d, 20.0d, new LatLng(64.353181d, 40.726056d), this, 0);  //
        anomalyArr[229] = new Anomaly("Circle", "Bio", 10.0d, 18.0d, new LatLng(64.353842d, 40.728110d), this, 0);  //
        anomalyArr[230] = new Anomaly("Circle", "Rad", 10.0d, 7.0d, new LatLng(64.351938d, 40.725863d), this, 0);  //
        anomalyArr[231] = new Anomaly("Circle", "Rad", 15.0d, 20.0d, new LatLng(64.350666d, 40.726839d), this, 0);  //
        /*справа от болотной тропы и до колючки*/
        anomalyArr[232] = new Anomaly("Circle", "Bio", 15.0d, 20.0d, new LatLng(64.354225d, 40.733780d), this, 0);  //
        anomalyArr[233] = new Anomaly("Circle", "Rad", 10.0d, 32.0d, new LatLng(64.354867d, 40.737332d), this, 0);  //
        anomalyArr[234] = new Anomaly("Circle", "Bio", 15.0d, 20.0d, new LatLng(64.355076d, 40.735948d), this, 0);  //
        anomalyArr[235] = new Anomaly("Circle", "Rad", 12.0d, 22.0d, new LatLng(64.355020d, 40.739520d), this, 0);  //
        /*в районе монолита и свободы*/
        anomalyArr[236] = new Anomaly("Circle", "Rad", 30.0d, 15.0d, new LatLng(64.352709d, 40.739703d), this, 0);  // перекресток другой
        anomalyArr[237] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.354188d, 40.741945d), this, 0);  //
        anomalyArr[238] = new Anomaly("Circle", "Bio", 10.0d, 20.0d, new LatLng(64.353351d, 40.737557d), this, 0);  //
        anomalyArr[239] = new Anomaly("Circle", "Psy", 15.0d, 10.0d, new LatLng(64.351868d, 40.737890d), this, 0);  //
        anomalyArr[240] = new Anomaly("Circle", "Rad", 10.0d, 20.0d, new LatLng(64.352310d, 40.741806d), this, 0);  //
        /*
         *4 день
         * 17 - 18 сентября
         * 16:00 - 9:00
         *
         * */
        /*стена между свободой и баром*/
        anomalyArr[241] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351039d, 40.734708d), this, 0); //от базы вниз  64.526125, 40.603979
        anomalyArr[241].minstrenght = 10;
        anomalyArr[242] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350672d, 40.734730d), this, 0); //
        anomalyArr[242].minstrenght = 10;
        anomalyArr[243] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350327d, 40.734955d), this, 0); //
        anomalyArr[243].minstrenght = 10;
        anomalyArr[244] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350121d, 40.735728d), this, 0); //
        anomalyArr[244].minstrenght = 10;
        anomalyArr[245] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350107d, 40.736597d), this, 0);  //
        anomalyArr[245].minstrenght = 10;
        anomalyArr[246] = new Anomaly("Circle", "Bio", 2.0d, 22.0d, new LatLng(64.349798d, 40.736982d), this, 0); //
        anomalyArr[247] = new Anomaly("Circle", "Bio", 40.0d, 22.0d, new LatLng(64.349468d, 40.737057d), this, 0); // самый низ
        anomalyArr[248] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351530d, 40.733879d), this, 0);  //чуть выше базы и вверх
        anomalyArr[248].minstrenght = 10;//
        anomalyArr[249] = new Anomaly("Circle", "Bio", 60.0d, 30.0d, new LatLng(64.351688d, 40.733947d), this, 0);  //прямо на дороге, поэтому сильнее
        anomalyArr[249].minstrenght = 10;
        anomalyArr[250] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351897d, 40.733907d), this, 0); //
        anomalyArr[250].minstrenght = 10;
        anomalyArr[251] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351897d, 40.733907d), this, 0); //
        anomalyArr[251].minstrenght = 10;
        anomalyArr[252] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352572d, 40.733708d), this, 0); //
        anomalyArr[252].minstrenght = 10;
        anomalyArr[253] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352895d, 40.733617d), this, 0); //
        anomalyArr[253].minstrenght = 10;
        anomalyArr[254] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353227d, 40.733526d), this, 0); //
        anomalyArr[254].minstrenght = 10;
        anomalyArr[255] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353399d, 40.733907d), this, 0); //
        anomalyArr[255].minstrenght = 10;
        anomalyArr[256] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353516d, 40.734615d), this, 0); //
        anomalyArr[257] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353581d, 40.735318d), this, 0); //
        anomalyArr[258] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353697d, 40.736069d), this, 0); //
        anomalyArr[259] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353750d, 40.736605d), this, 0); //
        anomalyArr[260] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353836d, 40.737420d), this, 0); //
        anomalyArr[260].minstrenght = 10;
        anomalyArr[261] = new Anomaly("Circle", "Rad", 2.0d, 15.0d, new LatLng(64.353753d, 40.738107d), this, 0); // проход
        anomalyArr[262] = new Anomaly("Circle", "Bio", 50.0d, 15.0d, new LatLng(64.353564d, 40.738719d), this, 0); //
        anomalyArr[262].minstrenght = 10;
        anomalyArr[263] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.353295d, 40.738670d), this, 0); //
        anomalyArr[264] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352918d, 40.738826d), this, 0); //
        anomalyArr[265] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352600d, 40.738992d), this, 0); //
        anomalyArr[266] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352337d, 40.739276d), this, 0); //
        anomalyArr[267] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.352016d, 40.739593d), this, 0); //
        anomalyArr[268] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351960d, 40.740424d), this, 0); //
        anomalyArr[269] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351879d, 40.741213d), this, 0); //
        anomalyArr[270] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351640d, 40.741803d), this, 0); //
        anomalyArr[271] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351368d, 40.742334d), this, 0); //
        anomalyArr[272] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351069d, 40.742919d), this, 0); //
        anomalyArr[273] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.351069d, 40.742919d), this, 0); //
        anomalyArr[274] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350518d, 40.742340d), this, 0); //
        anomalyArr[275] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.350274d, 40.741886d), this, 0); //
        anomalyArr[276] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.349988d, 40.741463d), this, 0); //
        anomalyArr[277] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.349660d, 40.740840d), this, 0); //
        anomalyArr[278] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.349395d, 40.740293d), this, 0); //
        anomalyArr[279] = new Anomaly("Circle", "Bio", 50.0d, 22.0d, new LatLng(64.349172, 40.739719d), this, 0); //
        /*меджу долгом и свободой*/
        anomalyArr[280] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351574d, 40.733819d), this, 0); //
        anomalyArr[281] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351528d, 40.733020d), this, 0); //
        anomalyArr[282] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351491d, 40.732124d), this, 0); //
        anomalyArr[283] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351491d, 40.732124d), this, 0); //
        anomalyArr[284] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351491d, 40.732124d), this, 0); //
        anomalyArr[285] = new Anomaly("Circle", "Psy", 50.0d, 22.0d, new LatLng(64.351346d, 40.729748d), this, 0); //
        /*стена между долгом и баром*/
        anomalyArr[286] = new Anomaly("Circle", "Bio", 50.0d, 25.0d, new LatLng(64.351546d, 40.729297d), this, 0); //64.351546, 40.729297
        anomalyArr[287] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351640d, 40.729220d), this, 0);  // 64.351430,
        anomalyArr[288] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351727d, 40.729190d), this, 0);  // 64.351430,
        anomalyArr[289] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.351825d, 40.729171d), this, 0);  // 64.351430,
        anomalyArr[290] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.351903d, 40.729185d), this, 0);  // 64.351430,
        anomalyArr[291] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352085d, 40.729222d), this, 0);  // 64.351430,
        anomalyArr[292] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352175d, 40.729244d), this, 0);  // 64.351430,
        anomalyArr[293] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352269d, 40.729260d), this, 0);  // 64.351430,
        anomalyArr[294] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352355d, 40.729287d), this, 0);  // 64.351430,
        anomalyArr[295] = new Anomaly("Circle", "Bio", 50.0d, 13.0d, new LatLng(64.352446d, 40.729314d), this, 0);  // 64.351430,
        anomalyArr[296] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352533d, 40.729340d), this, 0);  // 64.351430,
        anomalyArr[297] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352619d, 40.729370d), this, 0);  // 64.351430,
        anomalyArr[298] = new Anomaly("Circle", "Rad", 50.0d, 13.0d, new LatLng(64.352710d, 40.729405d), this, 0);  // 64.351430,
        anomalyArr[299] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352730d, 40.728846d), this, 0);  // 64.351430,
        anomalyArr[300] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352734d, 40.728245d), this, 0);  // 64.351430,
        anomalyArr[301] = new Anomaly("Circle", "Bio", 50.0d, 17.0d, new LatLng(64.352755d, 40.727623d), this, 0);  // 64.351430,
        anomalyArr[302] = new Anomaly("Circle", "Rad", 50.0d, 17.0d, new LatLng(64.352755d, 40.727623d), this, 0);  // 64.351430,
        anomalyArr[303] = new Anomaly("Circle", "Psy", 50.0d, 17.0d, new LatLng(64.352755d, 40.727623d), this, 0);  // 64.351430,
        anomalyArr[304] = new Anomaly("Circle", "Rad", 50.0d, 17.0d, new LatLng(64.352797d, 40.725863d), this, 0);  // 64.351430,
        anomalyArr[305] = new Anomaly("Circle", "Rad", 50.0d, 17.0d, new LatLng(64.352797d, 40.725863d), this, 0);  // 64.351430,
        anomalyArr[306] = new Anomaly("Circle", "Bio", 2.0d, 10.0d, new LatLng(64.352997d, 40.724817d), this, 0);  // 64.351430,
        anomalyArr[307] = new Anomaly("Circle", "Rad", 50.0d, 15.0d, new LatLng(64.352997d, 40.724817d), this, 0);  // 64.351430,
        anomalyArr[308] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.353227d, 40.723707d), this, 0);  // 64.351430,
        anomalyArr[309] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.353232d, 40.722870d), this, 0);  // 64.351430,
        anomalyArr[310] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.353178d, 40.722055d), this, 0);  // 64.351430,
        anomalyArr[311] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.353178d, 40.722055d), this, 0);  // 64.351430,
        anomalyArr[312] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352990d, 40.720494d), this, 0);  // 64.351430,
        anomalyArr[313] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.352990d, 40.720494d), this, 0);  // 64.351430,
        anomalyArr[314] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.350858d, 40.721218d), this, 0);  // 64.351430,
        anomalyArr[315] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.350898d, 40.722087d), this, 0);  // 64.351430,
        anomalyArr[316] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.350970d, 40.722908d), this, 0);  // 64.351430,
        anomalyArr[317] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.351067d, 40.723697d), this, 0);  // 64.351430,
        anomalyArr[318] = new Anomaly("Circle", "Bio", 2.0d, 10.0d, new LatLng(64.351147d, 40.724303d), this, 0);  // 64.351430,
        anomalyArr[319] = new Anomaly("Circle", "Rad", 50.0d, 22.0d, new LatLng(64.351219d, 40.724898d), this, 0);  // 64.351430,
        anomalyArr[320] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351135d, 40.725440d), this, 0);  // 64.351430,
        anomalyArr[321] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351067d, 40.726041d), this, 0);  // 64.351430,
        anomalyArr[322] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351016d, 40.726663d), this, 0);  // 64.351430,
        anomalyArr[323] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351016d, 40.727291d), this, 0);  // 64.351430,
        anomalyArr[324] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.350991d, 40.727918d), this, 0);  // 64.351430,
        anomalyArr[325] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.350974d, 40.728557d), this, 0);  // 64.351430,
        anomalyArr[326] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351044d, 40.729179d), this, 0);  // 64.351430,
        anomalyArr[327] = new Anomaly("Circle", "Rad", 50.0d, 16.0d, new LatLng(64.351251d, 40.729614d), this, 0);  // 64.351430,
        /*везде*/
        anomalyArr[328] = new Anomaly("Circle", "Rad", 50.0d, 30.0d, new LatLng(64.353808d, 40.725029d), this, 0);  // 64.351430,
        anomalyArr[329] = new Anomaly("Circle", "Bio", 40.0d, 15.0d, new LatLng(64.353808d, 40.725029d), this, 0);  // 64.351430,
        anomalyArr[330] = new Anomaly("Circle", "Bio", 50.0d, 30.0d, new LatLng(64.354477d, 40.729192d), this, 0);  //
        anomalyArr[331] = new Anomaly("Circle", "Rad", 10.0d, 35.0d, new LatLng(64.354696d, 40.720226d), this, 0);  //
        anomalyArr[332] = new Anomaly("Circle", "Bio", 5.0d, 15.0d, new LatLng(64.356095d, 40.728927d), this, 0);  //
        anomalyArr[333] = new Anomaly("Circle", "Bio", 15.0d, 35.0d, new LatLng(64.356751d, 40.739270d), this, 0);  //
        anomalyArr[334] = new Anomaly("Circle", "Rad", 10.0d, 17.0d, new LatLng(64.354240d, 40.733562d), this, 0);  //
        anomalyArr[335] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.353152d, 40.737155d), this, 0);  //
        anomalyArr[336] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.351948d, 40.725858d), this, 0);  //
        anomalyArr[337] = new Anomaly("Circle", "Rad", 10.0d, 30.0d, new LatLng(64.354500d, 40.740009d), this, 0);  //
        anomalyArr[338] = new Anomaly("Circle", "Bio", 10.0d, 15.0d, new LatLng(64.354886d, 40.737316d), this, 0);  //
        /*
         *5 день
         * 18 сентября
         * c 9:00
         *
         * */
        anomalyArr[339] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.352706d, 40.739698d), this, 0);  //
        anomalyArr[340] = new Anomaly("Circle", "Psy", 15.0d, 15.0d, new LatLng(64.351929d, 40.737359d), this, 0);  //
        anomalyArr[341] = new Anomaly("Circle", "Bio", 15.0d, 15.0d, new LatLng(64.351734d, 40.732832d), this, 0);  //
        anomalyArr[342] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.351608d, 40.731480d), this, 0);  //
        anomalyArr[343] = new Anomaly("Circle", "Psy", 15.0d, 15.0d, new LatLng(64.351562d, 40.729248d), this, 0);  //
        anomalyArr[344] = new Anomaly("Circle", "Bio", 15.0d, 15.0d, new LatLng(64.353812d, 40.725021d), this, 0);  //
        anomalyArr[345] = new Anomaly("Circle", "Rad", 15.0d, 15.0d, new LatLng(64.353087d, 40.730579d), this, 0);  //
        anomalyArr[346] = new Anomaly("Circle", "Rad", 15.0d, 30.0d, new LatLng(64.355291d, 40.734237d), this, 0);  //
        /*----------------*/
        anomalyArr[347] = new MonolithAnomaly("Circle", "", 100.0d, (double) 0, new LatLng(64.573684d, 45.516567d), this, 0); //добавлено str2, d2   // 45 -> 40 и аномалия будет над моим домом
        /**/
        anomalyArr[348] = new Anomaly("QR", "Rad", 50d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[349] = new Anomaly("QR", "Bio", 50d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[350] = new Anomaly("QR", "Psy", 50d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalies = anomalyArr;
    }

    private void GetTime() {
        cal = Calendar.getInstance();
        dayInt = cal.get(5);
        Hour = cal.get(11);
        Minutes = cal.get(12);
    }
    // применяет аномалии
    public void CheckAnomalies() {
        if (IS_ANOMALIES_AVAILABLE) {
            GetTime();
            // аномалия свободы
            if (anomalyFreedom){
                anomalies[0].Apply();
            }
            // взбешенная аномалия свободы
            if (apocalypseFreedom & ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600417800)){
                anomalies[1].Apply();
            }
            // постоянные аномалии
            if ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600092000) {
                for (int i = 2; i < 33; i++) {
                    anomalies[i].Apply();
                }



            }
            // аномалия малого монолита после первого выброса
            if ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600182000){
                anomalies[172].Apply();
            }
            // временные аномалии в лагере из-за малого монолита 15.09 18:?? - 18:40
            // аномалии первого дня 14.09 17:00 - 15.09 18:00
            /*CheckAnomaliesRegular(1600092000, 1600182000, 33, 103);*/
            // аномалии второго дня 15.09 18:00 - 16.09 11:00
            /*CheckAnomaliesRegular(1600182000, 1600243200, 103, 172);*/
            // аномалии третьего дня 16.09 11:00 - 17.09 16:00
            CheckAnomaliesRegular(1600243200, 1600347600, 176, 241);
            // аномалии четвертого дня 17.09 16:00 - 18.09 9:00
            CheckAnomaliesRegular(1600347600, 1600408800, 241, 339);
            // аномалии пятого дня 18.09 9:00 - до мая
            if ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600408800){
                for (int i = 339; i < 347; i++) {
                    anomalies[i].Apply();
                }
            }
        }
    }
    public void CheckAnomaliesRegular(long timeStart, long timeFinish, int anomalyStart, int anomalyFinish){
        if((Calendar.getInstance().getTimeInMillis() / 1000) > timeStart  && (Calendar.getInstance().getTimeInMillis() / 1000) < timeFinish){
            for (int i = anomalyStart; i < anomalyFinish; i++) {
                anomalies[i].Apply();
            }
        }
    }
    public void CheckIfInAnyAnomalyRegular(long timeStart, long timeFinish, int anomalyStart, int anomalyFinish){
        if ((Calendar.getInstance().getTimeInMillis() / 1000) > timeStart  && (Calendar.getInstance().getTimeInMillis() / 1000) < timeFinish){
            for (int i = anomalyStart; i < anomalyFinish; i++) {
                if (anomalies[i].IsInside) {
                    IsInsideAnomaly = Boolean.TRUE;
                    break;
                }
            }
        }
    }
    public void CheckIfInAnyAnomaly() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        this.IsInsideAnomaly = Boolean.FALSE;
        if (anomalyFreedom){
            if (anomalies[0].IsInside){
                IsInsideAnomaly = Boolean.TRUE;
            }
        }
        if (apocalypseFreedom & ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600417800)){
            if (anomalies[1].IsInside){
                IsInsideAnomaly = Boolean.TRUE;
            }
        }
        // аномалия малого монолита после первого выброса
        if ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600182000) {
            if (anomalies[172].IsInside){
                IsInsideAnomaly = Boolean.TRUE;
            }
        }
        // временные аномалии в лагере из-за малого монолита 15.09 18:?? - 18:40
        /*постоянные аномалии, они тоже должны иметь ограничение по времени снизу, но я уже забил*/
        /*for (int i = 2 ; i < 33; i++) { // +2 из-за аномалии свободы
            if (anomalies[i].IsInside) {
                IsInsideAnomaly = Boolean.TRUE;
                if (!GestaltProtection) {                                                 //проверка на защиту от открытия гештальта
                    if (i+2*//*из-зи свободы*//* < NUMBER_OF_GESTALT_ANOMALIES && anomalies[i].gesStatus == 1){  //если конкретный гештальт закрыт
                        if (gesLockoutList[i] != 1) {                                     // проверяет можно ли конкретный гештальт открыть
                            anomalies[i].gesStatus = 2;                                   // открываем гештальт
                            *//*ЕСЛИ ГЕШТАЛЬТ ОТКРЫВАЕТСЯ, ТО СТАВИТ ЕГО КООРДИНАТУ НА КАРТУ*//*
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.KEY_NAME, "!!!GESTALT!!!");
                            contentValues.put(DBHelper.KEY_ICON, "icon");
                            contentValues.put(DBHelper.KEY_LATITUDE, Double.toString(anomalies[i].Center.latitude));
                            contentValues.put(DBHelper.KEY_LONGITUDE, Double.toString(anomalies[i].Center.longitude));
                            contentValues.put(DBHelper.KEY_COMMENT, "Обнаружен Гештальт");
                            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
                            dbHelper.close();
                        }
                    }
                }
            }
        }*/
        for (int i = 2 ; i < 33; i++) { // +2 из-за аномалии свободы
            if (anomalies[i].IsInside) {
                IsInsideAnomaly = Boolean.TRUE;
                if (!GestaltProtection) {                                                 //проверка на защиту от открытия гештальта
                    if (i+2/*из-зи свободы*/ < NUMBER_OF_GESTALT_ANOMALIES && anomalies[i].gesStatus == 1){  //если конкретный гештальт закрыт
                        if (gesLockoutList[i] != 1) {                                     // проверяет можно ли конкретный гештальт открыть
                            anomalies[i].gesStatus = 2;                                   // открываем гештальт
                            /*ЕСЛИ ГЕШТАЛЬТ ОТКРЫВАЕТСЯ, ТО СТАВИТ ЕГО КООРДИНАТУ НА КАРТУ*/
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.KEY_NAME, "!!!GESTALT!!!");
                            contentValues.put(DBHelper.KEY_ICON, "icon");
                            contentValues.put(DBHelper.KEY_LATITUDE, Double.toString(anomalies[i].Center.latitude));
                            contentValues.put(DBHelper.KEY_LONGITUDE, Double.toString(anomalies[i].Center.longitude));
                            contentValues.put(DBHelper.KEY_COMMENT, "Обнаружен Гештальт");
                            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
                            dbHelper.close();
                        }
                    }
                }
            }
        }
        // аномалии первого дня 14.09 17:00 - 15.09 18:00
        /*CheckIfInAnyAnomalyRegular(1600092000, 1600182000, 33, 103);*/
        // аномалии второго дня 15.09 18:00 - 16.09 11:00
        /*CheckIfInAnyAnomalyRegular(1600182000, 1600243200, 103, 172);*/
        // аномалии третьего дня 16.09 11:00 - 17.09 16:00
        //CheckIfInAnyAnomalyRegular(1600243200, 1600347600, 176, 241);
        // аномалии четвертого дня 17.09 16:00 - 18.09 9:00
        CheckIfInAnyAnomalyRegular(1600347600, 1600408800, 241, 339);
        // аномалии пятого дня 18.09 9:00 - до мая
        if ((Calendar.getInstance().getTimeInMillis() / 1000) > 1600092000) {
            for (int i = 339; i < 347; i++) {
                if (anomalies[i].IsInside){
                    IsInsideAnomaly = Boolean.TRUE;
                    break;
                }
            }
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
                    Rad -= 0.2; // должно выводиться за два 40
                }
            }
            Psy = 0.0d;
            EM.StopActions();
        }
    }

    public void CreateSafeZones() {
        SafeZone[] safeZoneArr = new SafeZone[7];
        // денисовичи
        safeZoneArr[0] = new SafeZone("Circle", 145.0d, new LatLng(64.356858d, 40.722128d), this);
        // свобода
        safeZoneArr[1] = new SafeZone("Circle", 20.0d, new LatLng(64.351080d, 40.736227d), this);
        // долг
        safeZoneArr[2] = new SafeZone("Circle", 15.0d, new LatLng(64.351620d, 40.727617d), this);
        // наемники
        safeZoneArr[3] = new SafeZone("Circle", 23.0d, new LatLng(64.349791d, 40.726553d), this);
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
        this.anomalyFreedom = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("anomalyFreedom", "false")));
        this.apocalypseFreedom = Boolean.parseBoolean(Objects.requireNonNull(defaultSharedPreferences.getString("anomalyFreedom", "true")));
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
        edit.putString("anomalyFreedom", Boolean.toString(this.anomalyFreedom));
        edit.putString("apocalypseFreedom", Boolean.toString(this.apocalypseFreedom));
        edit.putString("fastRadPurification", Boolean.toString(this.fastRadPurification));
        edit.commit();
    }
}
