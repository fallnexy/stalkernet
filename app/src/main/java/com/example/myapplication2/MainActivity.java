package com.example.myapplication2;
//https://habr.com/ru/post/349102/ В Андроид 8 (у меня) службы все равно убиваются
// и с этим надо что то делать (ссылка). startForegroundService - запуск службы для андроид 8
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
    //объявление срани из интернета
    public final static String PARAM_TASK = "task";
    public final static String PARAM_STATUS = "status";
    public final static int STATUS_START = 100;
    public final static String BROADCAST_ACTION = "com.example.myapplication2";

    private boolean FineLocationPermissionGranted;
    private int Fine_Location_RequestCode = 1;
    public Globals G /*= new Globals()*/;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public Boolean ServiceIsRunning;
    BroadcastReceiver br;
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
           // MainActivity.this.G.location.setLatitude(Double.parseDouble(split[0]));//5
          //  MainActivity.this.G.location.setLongitude(Double.parseDouble(split[1]));//6
           // MainActivity.this.G.UpdateStats();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        G = new Globals(this);

        //запускает GeneralTab
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.mViewPager = (ViewPager) findViewById(R.id.container);
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(this.mViewPager));
        CheckPermissions(this);

        //срань из интернета работает. Подключил health, но оно не работает может быть - потому что надо проверить команды, которые постаупают в приемник
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int task = 50; //intent.getIntExtra(PARAM_TASK, 0);
              //  Double task = intent.getDoubleExtra(PARAM_TASK, 0);
                int status = intent.getIntExtra(PARAM_STATUS, 0);
                if (status  == STATUS_START){
                    MainActivity.this.G.Messages.setText("Так держать, товарищ сталкер!");
                    if (task <= 0.0d){
                        MainActivity.this.G.Health = "Вы умерли.";
                    } else {
                        MainActivity.this.G.Health = String.valueOf(task);
                    }
                   /* String[] split = intent.getStringExtra(PARAM_TASK).split(":");
                    if (Double.parseDouble(split[0]) <= 0.0d) {
                        MainActivity.this.G.Health = "Вы умерли.";
                    } else {
                        MainActivity.this.G.Health = split[0];
                    }
                    MainActivity.this.G.Rad = split[1];
                    MainActivity.this.G.Bio = split[2];
                  //  MainActivity.this.G.CurrentBio = split[4];
                    if (Double.parseDouble(split[3]) >= 100.0d) {
                        MainActivity.this.G.Health = "Вы умерли.";
                    } else {
                        MainActivity.this.G.Psy = split[3];
                    }*/
                  //  MainActivity.this.G.location.setLatitude(Double.parseDouble(split[0]));//5
                  //  MainActivity.this.G.location.setLongitude(Double.parseDouble(split[1]));//6
                    MainActivity.this.G.UpdateStats();  // обновляет координаты в реальном времени
                  //  MainActivity.this.G.Messages.setText(task);
                  //  MainActivity.this.G.CO.setText(split[1]);
                }
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
    }
    //Запуск службы, я надеюсь
    public void onStart(){
        super.onStart();
        this.ServiceIsRunning = Boolean.valueOf(isMyServiceRunning(StatsService.class));
        Intent intent = new Intent(this, StatsService.class);
        if (Build.VERSION.SDK_INT < 26 || this.ServiceIsRunning.booleanValue()) {
            startService(intent);
        } else {
            startForegroundService(intent);
        }
    }
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        //unregisterReceiver(this.broadcastReceiver);
        //unregisterReceiver(this.broadcastReceiverHealth);
        //unregisterReceiver(this.broadcastReceiverMessages);
    }

    public void onResume() {
        registerReceiver(this.br, new IntentFilter(BROADCAST_ACTION));
        //registerReceiver(this.broadcastReceiver, new IntentFilter("StatsService.Update"));
        //registerReceiver(this.broadcastReceiverHealth, new IntentFilter("StatsService.HealthUpdate"));
        //registerReceiver(this.broadcastReceiverMessages, new IntentFilter("StatsService.Message"));
        super.onResume();
    }
    //верхние кнопки
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public int getCount() {
            return 1;
        }

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new GeneralTab(MainActivity.this.G);
              /*  case 1:
                    return new MapTab(MainActivity.this.G);
                case 2:
                    return new PointTab(MainActivity.this.G);
                case 3:
                    return new ChatTab();*/
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
}
