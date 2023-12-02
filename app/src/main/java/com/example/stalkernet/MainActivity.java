package com.example.stalkernet;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.stalkernet.fragments.MapOSMTab;
import com.example.stalkernet.fragments.ParentTab;
import com.example.stalkernet.fragments.PointTab;
import com.example.stalkernet.fragments.QRTab;
import com.example.stalkernet.playerCharacter.StalkerCharacter;
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

import static com.example.stalkernet.StatsService.SEND_BIO_PROTECTIONS;
import static com.example.stalkernet.StatsService.SEND_CONTAMINATION;
import static com.example.stalkernet.StatsService.SEND_PSY_PROTECTIONS;
import static com.example.stalkernet.StatsService.SEND_RAD_PROTECTIONS;
import static com.example.stalkernet.StatsService.SEND_TOTAL_PROTECTIONS;
import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;


public class MainActivity extends AppCompatActivity implements QuestConfirmInterface{

    public static final String INTENT_MAIN = "StatsService.Update";
    public static final String INTENT_MAIN_PROTECTION = "protection";
    public static final String INTENT_MAIN_MINE = "mine_field";
    public static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 1001;
    private static final int REQUEST_CODE = 123;
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

            int[] contamination = intent.getIntArrayExtra(SEND_CONTAMINATION);
            if (contamination != null) {
                globals.setContamination(contamination);
            }
            double[] totalProtections = intent.getDoubleArrayExtra(SEND_TOTAL_PROTECTIONS);
            if (totalProtections != null){
                globals.setTotalProtections(totalProtections);
            }
            double[] radProtection = intent.getDoubleArrayExtra(SEND_RAD_PROTECTIONS);
            if (radProtection != null){
                globals.setProtections(RAD, radProtection);
            }
            double[] bioProtection = intent.getDoubleArrayExtra(SEND_BIO_PROTECTIONS);
            if (bioProtection != null){
                globals.setProtections(BIO, bioProtection);
            }
            double[] psyProtection = intent.getDoubleArrayExtra(SEND_PSY_PROTECTIONS);
            if (psyProtection != null){
                globals.setProtections(PSY, psyProtection);
            }
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
                globals.location.setLatitude(Double.parseDouble(split[1]));
                globals.location.setLongitude(Double.parseDouble(split[2]));
                globals.scienceQR = Boolean.parseBoolean(split[3]);
                globals.applyQR = Boolean.parseBoolean(split[4]);
                globals.updateStats();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String protectionTypes = intent.getStringExtra(INTENT_MAIN_PROTECTION);
            if (protectionTypes != null){
                StalkerCharacter stalkerCharacter = new StalkerCharacter(context);
                stalkerCharacter.nullifyProtectionDialog(protectionTypes);
            }
            String mine = intent.getStringExtra(INTENT_MAIN_MINE);
            if (mine != null){
                MineDialogFragment exampleDialog = MineDialogFragment.newInstance(mine.equals("true"));
                exampleDialog.show(getSupportFragmentManager(), "example_dialog");
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

        //запускает первый ряд вкладок
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(this.mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        unregisterReceiver(this.broadcastReceiverMessages);
    }

    public void onResume() {

        registerReceiver(this.broadcastReceiver, new IntentFilter(INTENT_MAIN));
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
            dbHelper = new DBHelper(this);
            database = dbHelper.open();
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.KEY_STATUS_QUEST_STEP, result);
            database.update(DBHelper.TABLE_QUEST_STEP, cv, DBHelper.KEY_ID__QUEST_STEP + "=" + childPosition, null);
            dbHelper.close();
        }

    }

    @Override
    public void confirmCreed(String result, String groupPosition, String childPosition) {
        if (result.equals("true")){
            DBHelper dbHelper;
            SQLiteDatabase database;
            dbHelper = new DBHelper(this);
            database = dbHelper.open();
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
            dbHelper.close();
        }
    }

    //верхние кнопки
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public int getCount() {
            return 4;
        }

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new MapOSMTab(globals);
                case 1:
                    return new PointTab(globals);
                case 2:
                    return new QRTab(globals);
                case 3:
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
        if (Build.VERSION.SDK_INT >= 31){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Get the package name of your app
                String packageName = getPackageName();

                // Create an intent to open the application settings
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));

                // Check if there is an activity that can handle the intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Start the activity to open the application settings
                    startActivity(intent);
                }
            }
        } else if (Build.VERSION.SDK_INT >= 29){
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
