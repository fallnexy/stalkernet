package com.example.myapplication2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "db_anomaly.db"; // сделаная на пк база данных

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "stalkerDB";
    public static final String TABLE_MARKERS = "markers";
    public static final String TABLE_COOLDOWNS = "coolDowns";
    public static final String TABLE_ANOMALY = "anomaly"; // сделанная на пк таблица в db_anomaly.db

    //таблица anomaly
    public static final String KEY_ID_ANOMALY = "_id";
    public static final String KEY_POLYGON_TYPE = "polygon_type";
    public static final String KEY_TYPE = "type";
    public static final String KEY_POWER = "power";
    public static final String KEY_MIN_POWER = "min_power";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_LATITUDE_ANOMALY = "latitude";
    public static final String KEY_LONGITUDE_ANOMALY = "longitude";
    public static final String KEY_STATSERVICE = "statservice";
    public static final String KEY_GESTALT_STATUS = "gestalt_status";
    public static final String KEY_BOOL_SHOWABLE = "bool_showable";
    public static final String KEY_BOOL_SHOW_ON_MAP = "bool_show_on_map";

    // таблица markers
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ICON = "icon";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_COMMENT = "comment";

    // таблица cooldowns
    public static final String KEY_ID_CD = "_id";
    public static final String KEY_NAME_CD = "name";
    public static final String KEY_TIME_CD = "icon";

    private Context myContext;
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL("create table " + TABLE_MARKERS + "("
                + KEY_ID + " integer primary key,"
                + KEY_NAME + " text,"
                + KEY_ICON + " text,"
                + KEY_LATITUDE + " text,"
                + KEY_LONGITUDE + " text,"
                + KEY_COMMENT + " text" + ")"
        );

        sqLiteDb.execSQL("create table " + TABLE_COOLDOWNS + "("
                + KEY_ID_CD + " integer primary key,"
                + KEY_NAME_CD + " text,"
                + KEY_TIME_CD + " text" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int oldVersion, int newVersion) {
        sqLiteDb.execSQL("drop table if exists " + TABLE_MARKERS);
        sqLiteDb.execSQL("drop table if exists " + TABLE_COOLDOWNS);

        onCreate(sqLiteDb);
    }

    // нужно для бд, созданной на пк
    void create_db(){

        File file = new File(DB_PATH);
        if (!file.exists()) {
            //получаем локальную бд как поток
            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
                // Открываем пустую бд
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

}
