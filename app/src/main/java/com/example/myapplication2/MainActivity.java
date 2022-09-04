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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.myapplication2.fragments.ParentTab;
import com.example.myapplication2.fragments.GeneralTab;
import com.example.myapplication2.fragments.MapOSMTab;
import com.example.myapplication2.fragments.PointTab;
import com.example.myapplication2.fragments.QRTab;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements AnomalyTypeInterface, QuestConfirmInterface{

    private boolean FineLocationPermissionGranted;
    private int Fine_Location_RequestCode = 1;
    private int Course_Location_RequestCode = 1;
    public Globals globals;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public Boolean ServiceIsRunning;
    private LinearLayout mainLayout;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String[] split = intent.getStringExtra("Stats").split(":");
            Log.d("main_split", String.valueOf(split[19]));
            if (Double.parseDouble(split[0]) <= 0.0d) {
                globals.Health = "Вы умерли.";
                mainLayout.setBackgroundResource(R.drawable.death_0521);
            } else {
                globals.Health = split[0];
                mainLayout.setBackgroundResource(R.drawable.fon/*fon_0521*/);
            }
            globals.Rad = split[1];
            globals.Bio = split[2];
            if (Double.parseDouble(split[3]) >= 1000.0d) {
                globals.Health = "Вы умерли.";
                mainLayout.setBackgroundResource(R.drawable.death_0521);
            } else {
                globals.Psy = split[3];
            }
            globals.location.setLatitude(Double.parseDouble(split[4]));
            globals.location.setLongitude(Double.parseDouble(split[5]));
            globals.ScienceQR = Integer.parseInt(split[6]);
            globals.TotalProtectionRad = split[7];
            globals.TotalProtectionBio = split[8];
            globals.TotalProtectionPsy = split[9];
            /*try {
                globals.anomalyCenter = new LatLng(Double.parseDouble(split[10]), Double.parseDouble(split[11]));
                globals.anomalyRadius = Double.parseDouble(split[12]);
            } catch (Exception e) {
                globals.anomalyCenter = new LatLng(0, 0);
                globals.anomalyRadius = 0d;
            }
            Log.d("аномалия", String.valueOf(globals.anomalyCenter));*/
            globals.CapacityProtectionRad = split[10];
            globals.MaxCapacityProtectionRad = split[11];
            globals.CapacityProtectionBio = split[12];
            globals.MaxCapacityProtectionBio = split[13];
            globals.CapacityProtectionPsy = split[14];
            globals.MaxCapacityProtectionPsy = split[15];
            globals.ProtectionRadArr = split[16];
            globals.ProtectionBioArr = split[17];
            globals.ProtectionPsyArr = split[18];
            globals.MaxProtectionAvailable.setText(getString(R.string.protectionsAmount) + split[19]);
            globals.UpdateStats();
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
                    globals.Messages.setText("Вы умерли, направляйтесь к мертвяку..");
                    break;
                case 1:
                    globals.Messages.setText("Вы умерли, следуйте к мертвяку в режиме зомби.");
                    break;
                case 2:
                    globals.Messages.setText("");
                    break;
                case 3:
                    globals.Messages.setText("Обнаружен Гештальт. Зафиксирована инверсия пси-поля, не покидайте границы безопасной зоны");
                    break;
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
                    if (var3 == 50547 && var4.equals("3000")) {
                        var5 = 1;
                        break label26;
                    }
                } else if (var4.equals("2000")) {
                    var5 = 0;
                    break label26;
                }

                var5 = -1;
            }

            switch(var5) {
                case 0:
                    globals.MaxHealth = "2000";
                    globals.UpdateStats();
                    break;
                case 1:
                    globals.MaxHealth = "3000";
                    globals.UpdateStats();
            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);

        globals = new Globals(this); // отличие от оригинала

        //запускает GeneralTab
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.mViewPager = findViewById(R.id.container);
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        this.mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(this.mViewPager));

        CheckPermissions(this);
    }
    /*
    * Меню три точки
    * единственный пункт - вибро
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private boolean isChecked = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_switch_vibration:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);

                Intent intent = new Intent("Command");
                if (isChecked){
                    intent.putExtra("Command", "OnVib");
                } else {
                    intent.putExtra("Command", "StopVib");
                }
                getApplicationContext().sendBroadcast(intent);
                return true;
            default:
                return false;
        }
        //return super.onOptionsItemSelected(item);
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

    // часть кода по обнулению третьей защиты,если разрешены только 2
    @Override
    public void nullifyProtection(String type, String nullifyType) {
        Intent intent = new Intent("Command");;
        switch (type){
            case "Rad":
                intent.putExtra("Command", "nullifyRad");
                getApplicationContext().sendBroadcast(intent);
                break;
            case "Bio":
                intent.putExtra("Command", "nullifyBio");
                getApplicationContext().sendBroadcast(intent);
                break;
            case "Psy":
                intent.putExtra("Command", "nullifyPsy");
                getApplicationContext().sendBroadcast(intent);
                break;
        }
    }
    /*
    * Кусок кода про подтверждения выполненного квеста из фрагмента QuestChildFragment в классе QuestAdapter.
    * Если из диалога приходит true, то менят статус выбранного подквеста на выполненный (true)
    */
    @Override
    public void confirmQuest(String result, String groupPosition, String childPosition) {
        if (result.equals("true")){
            DBHelper dbHelper;
            SQLiteDatabase database;
            Cursor cursor;
            dbHelper = new DBHelper(this);
            database = dbHelper.open();
            cursor = database.rawQuery("SELECT _id, access_key FROM quest_step WHERE quest_id =?", new String[]{groupPosition});
            cursor.moveToPosition(Integer.parseInt(childPosition));
            int position = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID_QUEST_STEP));
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.KEY_STATUS_QUEST_STEP, result);
            database.update(DBHelper.TABLE_QUEST_STEP, cv, DBHelper.KEY_ID_QUEST_STEP + "=" + position, null);
            cursor.close();
            dbHelper.close();
        }

    }

    @Override
    public void confirmCreed(String result, String groupPosition, String childPosition) {
        if (result.equals("true")){
            DBHelper dbHelper;
            SQLiteDatabase database;
            Cursor cursor;
            dbHelper = new DBHelper(this);
            database = dbHelper.open();
            /*cursor = database.rawQuery("SELECT _id, access_key FROM creed_branch WHERE creed_id =?", new String[]{groupPosition});
            cursor.moveToPosition(Integer.parseInt(childPosition));
            int position = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID__CREED_BRANCH));*/
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.KEY_STATUS__CREED_BRANCH, result);
            database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + groupPosition, null);

            cv = new ContentValues();
            int branch_id = Integer.parseInt(childPosition);
            cv.put(DBHelper.KEY_ACCESS_STATUS__CREED_BRANCH, "false");
            if (branch_id == 1){
                String creed_id_to_false = String.valueOf(Integer.parseInt(groupPosition) + 3);
                database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + creed_id_to_false, null);
            } else if (branch_id == 2){
                String creed_id_to_false = String.valueOf(Integer.parseInt(groupPosition) - 3);
                database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + creed_id_to_false, null);
            }
            //cursor.close();
            dbHelper.close();
        }
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
                    return new GeneralTab(globals);
                case 1:
                    return new MapOSMTab(globals);
                case 2:
                    return new PointTab(globals);
                case 3:
                    return new QRTab(globals);
                case 4:
                    return new ParentTab(globals);
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
            FineLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, Fine_Location_RequestCode);
            if (ContextCompat.checkSelfPermission(activity, "android.permission.ACCESS_COURSE_LOCATION") != 0){
                FineLocationPermissionGranted = false;
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.ACCESS_COURSE_LOCATION"}, Course_Location_RequestCode);
            }
            return;
        }
        FineLocationPermissionGranted = true;
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                FineLocationPermissionGranted = false;
            } else {
                FineLocationPermissionGranted = true;
            }
        }
    }
}
