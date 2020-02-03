package com.example.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class StatsService extends Service {
    private static final int ID_SERVICE = 101;
    public Anomaly[] Anomalys;
    public double Bio = 0.0d;
    public int BioProtection = 0;
    public double CurrentBio = 0.0d;
    public boolean DischargeImmunity = false;
    public EffectManager EM;
    public double Health = 100.0d;
    public Boolean IsDead = Boolean.FALSE;
    public Boolean IsDischarging = Boolean.FALSE;
    public Boolean IsInsideAnomaly = Boolean.FALSE;
    public Boolean IsInsideSafeZone = Boolean.TRUE;
    public Boolean IsUnlocked = Boolean.TRUE;
    public Date LastTimeChanged;
    public String LastTimeHitBy = "";
    private MyLocationCallback LocationCallback;
    private boolean LocationUpdatesStarted = false;
    public double MaxHealth = 100.0d;
    public double MaxRad = 100.0d;
    public Location MyCurrentLocation = new Location("GPS");
    public double Psy = 0.0d;
    public int PsyProtection = 0;
    public double Rad = 0.0d;
    public int RadProtection = 0;
    public SafeZone[] SafeZones;
    public String TypeAnomalyIn = "";
    public Boolean Vibrate = Boolean.TRUE;
    public int ScienceQR = 0;


    BroadcastReceiver broadcastReceiverQR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String var4 = intent.getStringExtra("StRoulette");
            // значение изменений увеличены для теста, кроме последних трех
            switch (var4){
                case "RadPlusOne":
                    Anomalys[48].Apply();
                    break;
                case "BioPlusOne":
                    Anomalys[49].Apply();
                    break;
                case "PsyPlusOne":
                    Anomalys[50].Apply();
                    break;
                case "HpPlusFive":
                    Health += 10;
                    break;
                case "HpPlusSeven":
                    Health += 20;
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

           byte var3;
           label110: {
               String var4 = intent.getStringExtra("Command");
               Toast.makeText(StatsService.this.getApplicationContext(), var4, Toast.LENGTH_LONG).show();
               switch(var4.hashCode()) {
                   case -1930888214:
                       if (var4.equals("ScienceQRoff")) {
                           var3 = 24;
                           break label110;
                       }
                       break;
                   case -1555514523:
                       if (var4.equals("ScienceQR")) {
                           var3 = 23;
                           break label110;
                       }
                       break;
                   // старые коды
                   case -2120901255:
                       if (var4.equals("ComboResetProtections")) {
                           var3 = 10;
                           break label110;
                       }
                       break;
                   case -1756574876:
                       if (var4.equals("Unlock")) {
                           var3 = 18;
                           break label110;
                       }
                       break;
                   case -1052620961:
                       if (var4.equals("MakeAlive")) {
                           var3 = 15;
                           break label110;
                       }
                       break;
                   case -782617084:
                       if (var4.equals("SetRadProtection0")) {
                           var3 = 6;
                           break label110;
                       }
                       break;
                   case -475738427:
                       if (var4.equals("SetRadProtection100")) {
                           var3 = 5;
                           break label110;
                       }
                       break;
                   case -258857420:
                       if (var4.equals("Monolith")) {
                           var3 = 16;
                           break label110;
                       }
                       break;
                   case -219690867:
                       if (var4.equals("StopVib")) {
                           var3 = 21;
                           break label110;
                       }
                       break;
                   case -159331209:
                       if (var4.equals("SetDischargeImmunityFalse")) {
                           var3 = 12;
                           break label110;
                       }
                       break;
                   case -115337820:
                       if (var4.equals("SetPsyProtection100")) {
                           var3 = 2;
                           break label110;
                       }
                       break;
                   case 71772:
                       if (var4.equals("God")) {
                           var3 = 17;
                           break label110;
                       }
                       break;
                   case 76321168:
                       if (var4.equals("OnVib")) {
                           var3 = 20;
                           break label110;
                       }
                       break;
                   case 146202227:
                       if (var4.equals("SetMaxHealth100")) {
                           var3 = 13;
                           break label110;
                       }
                       break;
                   case 146203188:
                       if (var4.equals("SetMaxHealth200")) {
                           var3 = 14;
                           break label110;
                       }
                       break;
                   case 180714426:
                       if (var4.equals("SetBioProtection50")) {
                           var3 = 7;
                           break label110;
                       }
                       break;
                   case 305958064:
                       if (var4.equals("ResetStats")) {
                           var3 = 0;
                           break label110;
                       }
                       break;
                   case 411921544:
                       if (var4.equals("SetPsyProtection50")) {
                           var3 = 1;
                           break label110;
                       }
                       break;
                   case 565354622:
                       if (var4.equals("Monolith2")) {
                           var3 = 19;
                           break label110;
                       }
                       break;
                   case 1307176114:
                       if (var4.equals("SetBioProtection100")) {
                           var3 = 8;
                           break label110;
                       }
                       break;
                   case 1508674375:
                       if (var4.equals("SetRadProtection50")) {
                           var3 = 4;
                           break label110;
                       }
                       break;
                   case 1796409274:
                       if (var4.equals("SetDischargeImmunityTrue")) {
                           var3 = 11;
                           break label110;
                       }
                       break;
                   case 1875682466:
                       if (var4.equals("Discharge")) {
                           var3 = 22;
                           break label110;
                       }
                       break;
                   case 1952950435:
                       if (var4.equals("SetPsyProtection0")) {
                           var3 = 3;
                           break label110;
                       }
                       break;
                   case 2084039473:
                       if (var4.equals("SetBioProtection0")) {
                           var3 = 9;
                           break label110;
                       }
               }

               var3 = -1;
           }

           Intent var5;
           switch(var3) {
               case 0:
                   StatsService.this.Health = 200.0D;
                   StatsService.this.MaxHealth = 200.0D;
                   StatsService.this.Rad = 0.0D;
                   StatsService.this.Bio = 0.0D;
                   StatsService.this.Psy = 0.0D;
                   StatsService.this.CurrentBio = 0.0D;
                   StatsService.this.RadProtection = 0;
                   StatsService.this.BioProtection = 0;
                   StatsService.this.PsyProtection = 0;
                   StatsService.this.DischargeImmunity = false;
                   StatsService.this.IsDead = false;
                   var5 = new Intent("StatsService.HealthUpdate");
                   var5.putExtra("Health", "200");
                   StatsService.this.sendBroadcast(var5);
                   var5 = new Intent("StatsService.Message");
                   var5.putExtra("Message", "A");
                   StatsService.this.sendBroadcast(var5);
                   break;
               case 1:
                   StatsService.this.PsyProtection = 50;
                   break;
               case 2:
                   StatsService.this.PsyProtection = 100;
                   break;
               case 3:
                   StatsService.this.PsyProtection = 0;
                   break;
               case 4:
                   StatsService.this.RadProtection = 50;
                   break;
               case 5:
                   StatsService.this.RadProtection = 100;
                   break;
               case 6:
                   StatsService.this.RadProtection = 0;
                   break;
               case 7:
                   StatsService.this.BioProtection = 50;
                   break;
               case 8:
                   StatsService.this.BioProtection = 100;
                   break;
               case 9:
                   StatsService.this.BioProtection = 0;
                   break;
               case 10:
                   StatsService.this.PsyProtection = 0;
                   StatsService.this.RadProtection = 0;
                   StatsService.this.BioProtection = 0;
                   break;
               case 11:
                   StatsService.this.DischargeImmunity = true;
                   break;
               case 12:
                   StatsService.this.DischargeImmunity = false;
                   break;
               case 13:
                   StatsService.this.MaxHealth = 200.0D;
                   var5 = new Intent("StatsService.HealthUpdate");
                   var5.putExtra("Health", "200");
                   StatsService.this.sendBroadcast(var5);
                   break;
               case 14:
                   StatsService.this.MaxHealth = 300.0D;
                   var5 = new Intent("StatsService.HealthUpdate");
                   var5.putExtra("Health", "300");
                   StatsService.this.sendBroadcast(var5);
                   break;
               case 15:
                   StatsService.this.Health = StatsService.this.MaxHealth;
                   StatsService.this.Rad = 0.0D;
                   StatsService.this.Bio = 0.0D;
                   StatsService.this.CurrentBio = 0.0D;
                   StatsService.this.Psy = 0.0D;
                   StatsService.this.IsDead = false;
                   break;
               case 16:
                   StatsService.this.DischargeImmunity = true;
                   StatsService.this.RadProtection = 50;
                   StatsService.this.BioProtection = 50;
                   StatsService.this.PsyProtection = 100;
                   break;
               case 17:
                   StatsService.this.DischargeImmunity = true;
                   StatsService.this.RadProtection = 100;
                   StatsService.this.BioProtection = 100;
                   StatsService.this.PsyProtection = 100;
                   break;
               case 18:
                   StatsService.this.IsUnlocked = true;
                   break;
               case 19:
                   StatsService.this.Health = 200.0D;
                   StatsService.this.CurrentBio = 0.0D;
                   break;
               case 20:
                   StatsService.this.Vibrate = true;
                   break;
               case 21:
                   StatsService.this.Vibrate = false;
                   break;
               case 22:
                   StatsService.this.Discharge();
                   //новые коды
               case 23:
                   StatsService.this.ScienceQR = 1;
                   break;
               case 24:
                   StatsService.this.ScienceQR = 0;
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

    public void setHealth(double d) {
        if (d > 0.0d) {
            this.Health = d;
            return;
        }
        this.Health = d;
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
                    Toast.makeText(this.getApplicationContext(), "Вы умерли от Радио", Toast.LENGTH_LONG).show();
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
        GetAnomalys();
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
// список аномалий
    private void GetAnomalys() {
        Anomaly[] anomalyArr = new Anomaly[51];                                                                                                                            //me
        anomalyArr[0] = new Anomaly("Circle", "Rad", 5.0d, 100.0d, new LatLng(64.573684d, 45.516567d), this);                     //64.573714, 40.516067
        anomalyArr[1] = new Anomaly("Circle", "Rad", 1.0d, 47.0d, new LatLng(64.526824d, 45.604426d), this); //64.526824, 40.604426; 64.355037d, 40.722809d
        anomalyArr[2] = new Anomaly("Circle", "Rad", 1.0d, 47.0d, new LatLng(64.526613d, 45.604308d), this); //64.526613, 40.604308; 64.355765d, 40.726628d
        anomalyArr[3] = new Anomaly("Circle", "Psy", 100.0d, 35.0d, new LatLng(64.355666d, 40.730297d), this);
        anomalyArr[3].minstrenght = 20;
        anomalyArr[4] = new Anomaly("Circle", "Rad", 6.0d, 43.0d, new LatLng(64.526400d, 45.604193d), this); //64.526400, 40.604193; 64.353653d, 40.720639d
        anomalyArr[4].minstrenght = 4;
        anomalyArr[5] = new Anomaly("Circle", "Psy", 5.0d, 40.0d, new LatLng(64.573874d, 40.526857d), this);//64.526283, 40.604053; 64.354245d, 40.723951d
        anomalyArr[6] = new Anomaly("Circle", "Rad", 1.0d, 43.0d, new LatLng(64.526261d, 45.603729d), this);//64.526261, 40.603729; 64.354504d, 40.729021d
        anomalyArr[7] = new Anomaly("Circle", "Rad", 1.0d, 47.0d, new LatLng(64.526201d, 45.603257d), this);//64.526201, 40.603257; 64.354913d, 40.734928d
        anomalyArr[8] = new Anomaly("Circle", "Rad", 1.0d, 47.0d, new LatLng(64.355273d, 40.737138d), this);
        anomalyArr[9] = new Anomaly("Circle", "Rad", 4.0d, 43.0d, new LatLng(64.35564d, 40.739666d), this);
        anomalyArr[10] = new Anomaly("Circle", "Bio", 5.0d, 43.0d, new LatLng(64.5236101d, 40.5161934d), this);                     //64.352632d, 40.720082d
        anomalyArr[11] = new Anomaly("Circle", "Psy", 10.0d, 17.0d, new LatLng(64.353251d, 40.722448d), this);
        anomalyArr[11].minstrenght = 10;
        anomalyArr[12] = new Anomaly("Circle", "Rad", 3.0d, 45.0d, new LatLng(64.353528d, 40.725061d), this);
        anomalyArr[13] = new Anomaly("Circle", "Psy", 10.0d, 14.0d, new LatLng(64.353732d, 40.729691d), this);
        anomalyArr[13].minstrenght = 5;
        anomalyArr[14] = new Anomaly("Circle", "Bio", 4.0d, 41.0d, new LatLng(64.353956d, 40.733538d), this);
        anomalyArr[14].minstrenght = 2;
        anomalyArr[15] = new Anomaly("Circle", "Bio", 4.0d, 43.0d, new LatLng(64.354117d, 40.738498d), this);
        anomalyArr[15].minstrenght = 2;
        anomalyArr[16] = new Anomaly("Circle", "Psy", 80.0d, 41.0d, new LatLng(64.353835d, 40.741092d), this);
        anomalyArr[16].minstrenght = 20;
        anomalyArr[17] = new Anomaly("Circle", "Bio", 4.0d, 32.0d, new LatLng(64.354134d, 40.743004d), this);
        anomalyArr[18] = new Anomaly("Circle", "Bio", 5.0d, 43.0d, new LatLng(64.351863d, 40.720743d), this);
        anomalyArr[19] = new Anomaly("Circle", "Bio", 3.0d, 43.0d, new LatLng(37.4219983d, -122.084d), this); //64.352634d, 40.723915d  эмулятор
        anomalyArr[20] = new Anomaly("Circle", "Rad", 2.0d, 32.0d, new LatLng(64.352871d, 40.726926d), this);
        anomalyArr[21] = new Anomaly("Circle", "Rad", 4.0d, 39.0d, new LatLng(64.353072d, 40.730551d), this);
        anomalyArr[22] = new Anomaly("Circle", "Psy", 10.0d, 24.0d, new LatLng(64.351589d, 40.72423d), this);
        anomalyArr[22].minstrenght = 10;
        anomalyArr[23] = new Anomaly("Circle", "Bio", 1.0d, 34.0d, new LatLng(64.351293d, 40.726321d), this);
        anomalyArr[24] = new Anomaly("Circle", "Psy", 100.0d, 46.0d, new LatLng(64.352241d, 40.72768d), this);
        anomalyArr[24].minstrenght = 40;
        anomalyArr[25] = new Anomaly("Circle", "Bio", 2.0d, 23.0d, new LatLng(64.351685d, 40.727501d), this);
        anomalyArr[26] = new Anomaly("Circle", "Psy", 20.0d, 17.0d, new LatLng(64.352563d, 40.729143d), this);
        anomalyArr[26].minstrenght = 10;
        anomalyArr[27] = new Anomaly("Circle", "Rad", 1.0d, 25.0d, new LatLng(64.352595d, 40.732628d), this);
        anomalyArr[28] = new Anomaly("Circle", "Rad", 2.0d, 30.0d, new LatLng(64.351638d, 40.731446d), this);
        anomalyArr[29] = new Anomaly("Circle", "Bio", 5.0d, 43.0d, new LatLng(64.352027d, 40.733248d), this);
        anomalyArr[30] = new Anomaly("Circle", "Rad", 3.0d, 36.0d, new LatLng(64.352743d, 40.735396d), this);
        anomalyArr[31] = new Anomaly("Circle", "Psy", 10.0d, 5.0d, new LatLng(64.353069d, 40.736801d), this);
        anomalyArr[32] = new Anomaly("Circle", "Psy", 40.0d, 30.0d, new LatLng(64.353463d, 40.738272d), this);
        anomalyArr[32].minstrenght = 10;
        anomalyArr[33] = new Anomaly("Circle", "Bio", 2.0d, 26.0d, new LatLng(64.352924d, 40.739808d), this);
        anomalyArr[34] = new Anomaly("Circle", "Psy", 50.0d, 30.0d, new LatLng(64.353323d, 40.742572d), this);
        anomalyArr[34].minstrenght = 10;
        anomalyArr[35] = new Anomaly("Circle", "Psy", 100.0d, 30.0d, new LatLng(64.353684d, 40.744285d), this);
        anomalyArr[35].minstrenght = 20;
        anomalyArr[36] = new Anomaly("Circle", "Psy", 80.0d, 24.0d, new LatLng(64.352946d, 40.741586d), this);
        anomalyArr[36].minstrenght = 20;
        anomalyArr[37] = new Anomaly("Circle", "Psy", 100.0d, 34.0d, new LatLng(64.352136d, 40.739134d), this);
        anomalyArr[37].minstrenght = 20;
        anomalyArr[38] = new Anomaly("Circle", "Psy", 100.0d, 28.0d, new LatLng(64.352475d, 40.742778d), this);
        anomalyArr[38].minstrenght = 60;
        anomalyArr[39] = new Anomaly("Circle", "Psy", 100.0d, 29.0d, new LatLng(64.352753d, 40.74444d), this);
        anomalyArr[39].minstrenght = 90;
        anomalyArr[40] = new Anomaly("Circle", "Rad", 10.0d, 45.0d, new LatLng(64.349937d, 40.731093d), this);
        anomalyArr[41] = new Anomaly("Circle", "Bio", 3.0d, 35.0d, new LatLng(64.351069d, 40.735967d), this);
        anomalyArr[42] = new Anomaly("Circle", "Rad", 3.0d, 59.0d, new LatLng(64.3512d, 40.738147d), this);
        anomalyArr[43] = new Anomaly("Circle", "Psy", 30.0d, 26.0d, new LatLng(64.350528d, 40.737087d), this);
        anomalyArr[43].minstrenght = 10;
        anomalyArr[44] = new Anomaly("Circle", "Psy", 5.0d, 43.0d, new LatLng(64.351336d, 40.742537d), this);
        anomalyArr[45] = new MonolithAnomaly("Circle", "", 8.0d, (double) 0, new LatLng(64.352518d, 40.743582d), this); //добавлено str2, d2
        anomalyArr[46] = new MonolithAnomaly("Circle", "", 50.0d, (double) 0, new LatLng(64.3523367d, 40.7430442d), this); //добавлено str2, d2
        anomalyArr[47] = new Anomaly("Circle", "Bio", 10.0d, 10.0d, new LatLng(64.51027d, 40.6791d), this);
        anomalyArr[48] = new Anomaly("QR", "Rad", 10d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[49] = new Anomaly("QR", "Bio", 10d, 1d, this);  //QR рулетка - нигде не учитывается
        anomalyArr[50] = new Anomaly("QR", "Psy", 10d, 1d, this);  //QR рулетка - нигде не учитывается
        Anomalys = anomalyArr;
    }

    public void CheckAnomalys() {
        for (int i = 0; i <= 47; i++) {
            Anomalys[i].Apply();
        }
    }

// число аномалий задается цифрой 47, надо зменить, чтоб не было ошибок
    /* рад выводится само со временем
    пси выводится сразу
    био само не выводится
     */
    public void CheckIfInAnyAnomaly() {
        int i = 0;
        this.IsInsideAnomaly = Boolean.FALSE;
        while (i <= 47) {
            if (this.Anomalys[i].IsInside) {
                this.IsInsideAnomaly = Boolean.TRUE;
            }
            i++;
        }
        if (!this.IsInsideAnomaly) {
            if (Rad > 0) {
                Rad -= 0.1;
            }
            Psy = 0.0d;
           // this.Bio = 0.0d;
            this.EM.StopActions();
        }
    }

    public void CreateSafeZones() {
        SafeZone[] safeZoneArr = new SafeZone[7];
        safeZoneArr[0] = new SafeZone("Circle", 62.0d, new LatLng(64.356037d, 40.72262d), this);
        safeZoneArr[1] = new SafeZone("Circle", 78.0d, new LatLng(64.357008d, 40.721367d), this);
        safeZoneArr[2] = new SafeZone("Circle", 18.0d, new LatLng(64.3524816d, 40.7320684d), this);
        safeZoneArr[3] = new SafeZone("Circle", 23.0d, new LatLng(64.351917d, 40.725722d), this);
        safeZoneArr[4] = new SafeZone("Circle", 16.0d, new LatLng(64.3525714d, 40.7430442d), this);
        safeZoneArr[5] = new SafeZone("Circle", 25.0d, new LatLng(64.508752d, 40.681068d), this);
        safeZoneArr[6] = new SafeZone("Circle", 46.0d, new LatLng(64.667986d, 40.522734d), this);
        this.SafeZones = safeZoneArr;
    }
//// число зон безопасности задается цифрой 6, надо зменить, чтоб не было ошибок
    public void CheckIfInAnySafezone() {
        int i = 0;
        this.IsInsideSafeZone = Boolean.FALSE;
        while (i <= 6) {
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
                StatsService.this.CheckIfInAnySafezone();
                StatsService.this.EM.PlayBuzzer();
                if (!(StatsService.this.IsInsideSafeZone || StatsService.this.DischargeImmunity)) {
                    StatsService.this.LastTimeHitBy = "Dis";
                    StatsService.this.setDead(Boolean.TRUE);
                    StatsService.this.Health = 0.0d;
                    Intent intent = new Intent("StatsService.Message");
                    intent.putExtra("Message", "H");
                    StatsService.this.sendBroadcast(intent);
                }
                Toast.makeText(StatsService.this.getApplicationContext(), "Выброс Окончен!!", Toast.LENGTH_SHORT).show();
                StatsService.this.IsDischarging = Boolean.FALSE;
            }
        }, 60000);
    }

   public void LoadStats() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.MaxHealth = Double.parseDouble(defaultSharedPreferences.getString("MaxHealth", "200"));
        this.Health = Double.parseDouble(defaultSharedPreferences.getString("Health", "200"));
        this.Rad = Double.parseDouble(defaultSharedPreferences.getString("Rad", "0"));
        this.Bio = Double.parseDouble(defaultSharedPreferences.getString("Bio", "0"));
        this.Psy = Double.parseDouble(defaultSharedPreferences.getString("Psy", "0"));
        this.PsyProtection = Integer.parseInt(defaultSharedPreferences.getString("PsyProtection", "0"));
        this.RadProtection = Integer.parseInt(defaultSharedPreferences.getString("RadProtection", "0"));
        this.BioProtection = Integer.parseInt(defaultSharedPreferences.getString("BioProtection", "0"));
        this.ScienceQR = Integer.parseInt(defaultSharedPreferences.getString("ScienceQR", "0"));
        this.DischargeImmunity = Boolean.parseBoolean(defaultSharedPreferences.getString("DischargeImmunity", "false"));
        this.IsUnlocked = Boolean.parseBoolean(defaultSharedPreferences.getString("Lock", "true"));
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
        edit.putString("ScienceQR", Integer.toString(this.ScienceQR));
        edit.putString("DischargeImmunity", Boolean.toString(this.DischargeImmunity));
        edit.putString("Lock", Boolean.toString(this.IsUnlocked));
        edit.commit();
    }
}
