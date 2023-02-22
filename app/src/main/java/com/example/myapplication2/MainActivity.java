package com.example.myapplication2;
//https://habr.com/ru/post/349102/ В Андроид 8 (у меня) службы все равно убиваются
// и с этим надо что то делать (ссылка). startForegroundService - запуск службы для андроид 8

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.myapplication2.fragments.GeneralTab;
import com.example.myapplication2.fragments.MapOSMTab;
import com.example.myapplication2.fragments.ParentTab;
import com.example.myapplication2.fragments.PointTab;
import com.example.myapplication2.fragments.QRTab;
import com.example.myapplication2.playerCharacter.StalkerCharacter;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;


public class MainActivity extends AppCompatActivity implements QuestConfirmInterface{

    public static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 1001;
    private boolean FineLocationPermissionGranted;
    private int Fine_Location_RequestCode = 1;
    private int Course_Location_RequestCode = 1;
    public Globals globals;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NonSwipeableViewPager mViewPager;
    public Boolean ServiceIsRunning;
    private LinearLayout mainLayout;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String[] split;
            try {
                split = intent.getStringExtra("Stats").split(":");
                if (Double.parseDouble(split[0]) <= 0.0d) {
                    globals.Health = "0";
                    mainLayout.setBackgroundResource(R.drawable.death_0521);
                } else {
                    globals.Health = split[0];
                    if (!mainLayout.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.fon).getConstantState())) {
                        mainLayout.setBackgroundResource(R.drawable.fon);
                    }
                }
                globals.Rad = split[1];
                globals.Bio = split[2];
                if (Double.parseDouble(split[3]) >= 1000.0d) {
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
                globals.CapacityProtectionRad = split[10];
                globals.CapacityProtectionBio = split[11];
                globals.CapacityProtectionPsy = split[12];
                globals.ProtectionRadArr = split[13];
                globals.ProtectionBioArr = split[14];
                globals.ProtectionPsyArr = split[15];
                globals.MaxProtectionAvailable.setText(getString(R.string.protectionsAmount) + split[16]);
                globals.UpdateStats();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String protectionTypes = intent.getStringExtra("protection");
            if (protectionTypes != null){
                StalkerCharacter stalkerCharacter = new StalkerCharacter(context);
                stalkerCharacter.nullifyProtectionDialog(protectionTypes);
            }
        }
    };


    BroadcastReceiver broadcastReceiverMessages = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String causeOfDeath = intent.getStringExtra("Message");
            if (causeOfDeath != null) {
                switch(Objects.requireNonNull(causeOfDeath)) {
                    case "H":
                        globals.setMassage("Вы умерли, направляйтесь к мертвяку..");
                        break;
                    case "P":
                        globals.setMassage("Вы умерли, следуйте к мертвяку в режиме зомби.");
                        break;
                    case "A":
                        globals.setMassage("");
                        break;
                    case "G":
                        globals.setMassage("Обнаружен Гештальт. Зафиксирована инверсия пси-поля, не покидайте границы безопасной зоны");
                        globals.setGestaltOpen(true);
                        break;
                    case "GP":
                        globals.setMassage("Поставлена временная защита от одного Гештальта");
                        globals.setGestaltOpen(false);
                        break;
                    case "GC":
                        globals.setMassage("Защита от Гештальта снята");
                        break;
                    case "O":
                        globals.setMassage("Системное сообщение: настало время чила и расслабона");
                        break;
                }
                globals.saveStats();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

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
        unregisterReceiver(this.broadcastReceiverMessages);
    }

    public void onResume() {

        registerReceiver(this.broadcastReceiver, new IntentFilter("StatsService.Update"));
        registerReceiver(this.broadcastReceiverMessages, new IntentFilter("StatsService.Message"));
        super.onResume();
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
            int positionIndex = cursor.getColumnIndex(DBHelper.KEY_ID_QUEST_STEP);
            int position = cursor.getInt(positionIndex);
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
        if (Build.VERSION.SDK_INT >= 29){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                FineLocationPermissionGranted = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Permission needed");
                builder.setMessage("This permission is needed to access location in the background.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request permission
                        requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION);
                    }
                });
                builder.create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
            }
        } else if (ContextCompat.checkSelfPermission(activity, "android.permission.ACCESS_FINE_LOCATION") != 0) {
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
