package com.example.myapplication2;
//https://habr.com/ru/post/349102/ В Андроид 8 (у меня) службы все равно убиваются
// и с этим надо что то делать (ссылка). startForegroundService - запуск службы для андроид 8
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private boolean FineLocationPermissionGranted;
    private int Fine_Location_RequestCode = 1;
    public Globals G;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public Boolean ServiceIsRunning;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String[] split = intent.getStringExtra("Stats").split(":");
            if (Double.parseDouble(split[0]) <= 0.0d) {
                MainActivity.this.G.Health = "Вы умерли.";
            } else {
                MainActivity.this.G.Health = split[0];
            }
            MainActivity.this.G.Rad = split[1];
            MainActivity.this.G.Bio = split[2];
            MainActivity.this.G.CurrentBio = split[4];
            if (Double.parseDouble(split[3]) >= 100.0d) {
                MainActivity.this.G.Health = "Вы умерли.";
            } else {
                MainActivity.this.G.Psy = split[3];
            }
            MainActivity.this.G.location.setLatitude(Double.parseDouble(split[5]));
            MainActivity.this.G.location.setLongitude(Double.parseDouble(split[6]));
            MainActivity.this.G.ScienceQR = Integer.parseInt(split[7]);
            MainActivity.this.G.UpdateStats();
        }
    };
    BroadcastReceiver broadcastReceiverMessages = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            byte var5;
            label32: {
                String var4 = intent.getStringExtra("Message");
                int var3 = var4.hashCode();
                if (var3 != 65) {
                    if (var3 != 72) {
                        if (var3 != 80) {
                            if (var3 == 71 && var4.equals("G")) {
                                var5 = 3;
                                break label32;
                            }
                        } else if (var4.equals("P")){
                            var5 = 1;
                            break label32;
                        }
                    } else if (var4.equals("H")) {
                        var5 = 0;
                        break label32;
                    }
                } else if (var4.equals("A")) {
                    var5 = 2;
                    break label32;
                }

                var5 = -1;
            }

            switch(var5) {
                case 0:
                    MainActivity.this.G.Messages.setText("Вы умерли, направляйтесь к мертвяку..");
                    break;
                case 1:
                    MainActivity.this.G.Messages.setText("Вы умерли, подождите контролёра 5 минут, если его нет, то следуйте к мертвяку в режиме зомби.");
                    break;
                case 2:
                    MainActivity.this.G.Messages.setText("");
                    break;
                case 3:
                    MainActivity.this.G.Messages.setText("Не выходи из виброзоны, пока не закроешь гештальт.");
            }

        }
    };
    BroadcastReceiver broadcastReceiverHealth = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            byte var5;
            label26: {
                String var4 = intent.getStringExtra("Health");
                int var3 = var4.hashCode();
                if (var3 != 49586) {
                    if (var3 == 50547 && var4.equals("300")) {
                        var5 = 1;
                        break label26;
                    }
                } else if (var4.equals("200")) {
                    var5 = 0;
                    break label26;
                }

                var5 = -1;
            }

            switch(var5) {
                case 0:
                    MainActivity.this.G.MaxHealth = "200";
                    MainActivity.this.G.UpdateStats();
                    break;
                case 1:
                    MainActivity.this.G.MaxHealth = "300";
                    MainActivity.this.G.UpdateStats();
            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        G = new Globals(this); // отличие от оригинала

        //запускает GeneralTab
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.mViewPager = (ViewPager) findViewById(R.id.container);
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(this.mViewPager));
        CheckPermissions(this);


    }
    //Запуск службы, я надеюсь
    public void onStart(){
        super.onStart();
        this.ServiceIsRunning = isMyServiceRunning(StatsService.class);
        Intent intent = new Intent(this, StatsService.class);
        if (Build.VERSION.SDK_INT < 26 || this.ServiceIsRunning) {
            startService(intent);
        } else {
            startForegroundService(intent);
        }
    }
    public void onPause() {
        super.onPause();

        unregisterReceiver(this.broadcastReceiver);
        unregisterReceiver(this.broadcastReceiverHealth);
        unregisterReceiver(this.broadcastReceiverMessages);
    }

    public void onResume() {

        registerReceiver(this.broadcastReceiver, new IntentFilter("StatsService.Update"));
        registerReceiver(this.broadcastReceiverHealth, new IntentFilter("StatsService.HealthUpdate"));
        registerReceiver(this.broadcastReceiverMessages, new IntentFilter("StatsService.Message"));
        super.onResume();
    }
    //верхние кнопки
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public int getCount() {
            return 5;
        }

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new GeneralTab(MainActivity.this.G);
                case 1:
                    return new MapTab(MainActivity.this.G);
                case 2:
                    return new PointTab(MainActivity.this.G);
                case 3:
                    return new ChatTab();
                case 4:
                    return new QRTab(MainActivity.this.G);
                default:
                    return null;
            }
        }
    }
    // здесь осуществлена вставка в getSystemService ()
    private boolean isMyServiceRunning(Class<?> cls) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    // пока работает просто так
    private void CheckPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            this.FineLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, this.Fine_Location_RequestCode);
            return;
        }
        this.FineLocationPermissionGranted = true;
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 1) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                this.FineLocationPermissionGranted = false;
            } else {
                this.FineLocationPermissionGranted = true;
            }
        }
    }
}
