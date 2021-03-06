package com.example.myapplication2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "stalkerDB";
    public static final String TABLE_MARKERS = "markers";
    public static final String TABLE_COOLDOWNS = "coolDowns";

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

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
}
