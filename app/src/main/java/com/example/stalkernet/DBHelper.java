package com.example.stalkernet;

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
    private static String DB_NAME = "stalker.db"; // сделаная на пк база данных

    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "stalkerDB";
    public static final String TABLE_MARKERS = "markers";
    public static final String TABLE_COOLDOWNS = "coolDowns";
    public static final String TABLE_ANOMALY = "anomaly"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_LOCALITY = "locality"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_FACTION = "faction"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_QUEST = "quest"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_QUEST_STEP = "quest_step"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_CREED = "creed"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_CREED_BRANCH = "creed_branch"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_PERSON = "person"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_MONSTER = "monster"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_ARTEFACT = "artefact"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_MILESTONE = "milestone"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_ITEM = "item"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_SAFE_ZONE = "save_zone"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_DISCHARGE = "discharge"; // сделанная на пк таблица в stalker.db
    public static final String TABLE_USER = "user"; // сделанная на пк таблица в stalker.db

    // таблицы-костыли для expandableView
    public static final String TABLE_TABLE_OF_TABLES = "table_of_tables"; // сделанная на пк таблица в stalker.db


    // таблица table_of_tables
    public static final String KEY_ID_TABLE_OF_TABLES = "_id";
    public static final String KEY_NAME_TABLE_OF_TABLES = "table_name";
    public static final String KEY_KOSTYL_TABLE_OF_TABLES = "table_kostyl";
    public static final String KEY_IMAGE_TABLE_OF_TABLES = "table_image";


    //таблица anomaly
    public static final String KEY_ID__ANOMALY = "_id";
    public static final String KEY_POLYGON_TYPE__ANOMALY = "polygon_type";
    public static final String KEY_TYPE__ANOMALY = "type";
    public static final String KEY_POWER__ANOMALY = "power";
    public static final String KEY_MIN_POWER__ANOMALY = "min_power";
    public static final String KEY_RADIUS__ANOMALY = "radius";
    public static final String KEY_LATITUDE__ANOMALY = "latitude";
    public static final String KEY_LONGITUDE__ANOMALY = "longitude";
    public static final String KEY_GESTALT_STATUS__ANOMALY = "gestalt_status";
    public static final String KEY_BOOL_SHOWABLE__ANOMALY = "bool_showable";
    public static final String KEY_VISIBLE__ANOMALY = "bool_show_on_map";
    public static final String KEY_DAY_START__ANOMALY = "day_start";
    public static final String KEY_DAY_FINISH__ANOMALY = "day_finish";


    // таблица locality
    public static final String KEY_ID__LOCALITY = "_id";
    public static final String KEY_NAME__LOCALITY = "name";
    public static final String KEY_DESCRIPTION__LOCALITY = "description";
    public static final String KEY_LATITUDE__LOCALITY = "latitude";
    public static final String KEY_LONGITUDE__LOCALITY = "longitude";
    public static final String KEY_ACCESS_STATUS__LOCALITY = "access_status";
    public static final String KEY_ACCESS_KEY__LOCALITY = "access_key";
    public static final String KEY_IMAGE__LOCALITY = "image";
    public static final String KEY_KOSTYL__LOCALITY = "kostyl";


    // таблица faction
    public static final String KEY_ID__FACTION = "_id";
    public static final String KEY_NAME__FACTION = "name";
    public static final String KEY_DESCRIPTION_FACTION = "description";
    public static final String KEY_LOCATION_ID_FACTION = "location_id";
    public static final String KEY_LATITUDE_FACTION = "latitude";
    public static final String KEY_LONGITUDE_FACTION = "longitude";
    public static final String KEY_ACCESS_STATUS_FACTION = "access_status";
    public static final String KEY_ACCESS_KEY_FACTION = "access_key";
    public static final String KEY_IMAGE__FACTION = "image";
    public static final String KEY_KOSTYL_FACTION = "kostyl";


    // таблица person
    public static final String KEY_ID__PERSON = "_id";
    public static final String KEY_NAME__PERSON = "name";
    public static final String KEY_DESCRIPTION__PERSON = "description";
    public static final String KEY_LOCALITY_ID__PERSON = "locality_id";
    public static final String KEY_FACTION_ID__PERSON = "faction_id";
    public static final String KEY_FACTION_POSITION__PERSON = "faction_position";
    public static final String KEY_ACCESS_STATUS__PERSON = "access_status";
    public static final String KEY_ACCESS_KEY__PERSON = "access_key";
    public static final String KEY_IMAGE__PERSON = "image";
    public static final String KEY_KOSTYL__PERSON = "kostyl";


    // таблица monster
    public static final String KEY_ID__MONSTER = "_id";
    public static final String KEY_NAME__MONSTER = "name";
    public static final String KEY_DESCRIPTION__MONSTER = "description";
    public static final String KEY_ACCESS_STATUS__MONSTER = "access_status";
    public static final String KEY_ACCESS_KEY__MONSTER = "access_key";
    public static final String KEY_IMAGE__MONSTER = "image";
    public static final String KEY_KOSTYL__MONSTER = "kostyl";


    // таблица artefact
    public static final String KEY_ID__ARTEFACT = "_id";
    public static final String KEY_NAME__ARTEFACT = "name";
    public static final String KEY_DESCRIPTION__ARTEFACT = "description";
    public static final String KEY_VZAIMODEISTVIE__ARTEFACT = "vzaimodeistvie";
    public static final String KEY_ACCESS_STATUS__ARTEFACT = "access_status";
    public static final String KEY_ACCESS_KEY__ARTEFACT = "access_key";
    public static final String KEY_IMAGE__ARTEFACT = "image";
    public static final String KEY_KOSTYL__ARTEFACT = "kostyl";
    public static final String KEY_APPLY_ONE__ARTEFACT = "apply_one";
    public static final String KEY_APPLY_TWO__ARTEFACT = "apply_two";
    public static final String KEY_ACCESS_APPLY_TWO__ARTEFACT = "access_apply_two";
    public static final String KEY_APPLY_THREE__ARTEFACT = "apply_three";
    public static final String KEY_ACCESS_APPLY_THREE__ARTEFACT = "access_apply_three";
    public static final String KEY_APPLY_LEVEL__ARTEFACT = "apply_level";

    // таблица milestone
    public static final String KEY_ID__MILESTONE = "_id";
    public static final String KEY_NAME__MILESTONE = "name";
    public static final String KEY_DESCRIPTION__MILESTONE = "description";
    public static final String KEY_ACCESS_STATUS__MILESTONE = "access_status";
    public static final String KEY_ACCESS_KEY__MILESTONE = "access_key";
    public static final String KEY_IMAGE__MILESTONE = "image";
    public static final String KEY_KOSTYL__MILESTONE = "kostyl";
    public static final String KEY_LATITUDE__MILESTONE = "latitude";
    public static final String KEY_LONGITUDE__MILESTONE = "longitude";
    public static final String KEY_FINISH_STATUS__MILESTONE = "finish_status";

    // таблица item
    public static final String KEY_ID__ITEM = "_id";
    public static final String KEY_NAME__ITEM = "name";
    public static final String KEY_DESCRIPTION__ITEM = "description";
    public static final String KEY_VZAIMODEISTVIE__ITEM = "vzaimodeistvie";
    public static final String KEY_ACCESS_KEY__ITEM = "access_key";

    // таблица quest
    public static final String KEY_ID__QUEST = "_id";
    public static final String KEY_NAME_QUEST = "name";
    public static final String KEY_DESCRIPTION_QUEST = "description";
    public static final String KEY_STATUS__QUEST = "status";
    public static final String KEY_ACCESS_QUEST = "access_status";
    public static final String KEY_ACCESS_KEY_QUEST = "access_key";
    public static final String KEY_IMAGE_QUEST = "image";

    // таблица quest_step
    public static final String KEY_ID__QUEST_STEP = "_id";
    public static final String KEY_QUEST_ID_QUEST_STEP = "quest_id";
    public static final String KEY_DESCRIPTION_QUEST_STEP = "description";
    public static final String KEY_LOCATION_ID_QUEST_STEP = "location_id";
    public static final String KEY_FACTION_ID_QUEST_STEP = "faction_id";
    public static final String KEY_PERSON_ID_QUEST_STEP = "person_id";
    public static final String KEY_ARTEFACT_ID_QUEST_STEP = "artefact_id";
    public static final String KEY_STATUS_QUEST_STEP = "status"; //выполнено или нет
    public static final String KEY_ACCESS_STATUS__QUEST_STEP = "access_status"; // можно ли выполнять
    public static final String KEY_ACCESS_KEY_QUEST_STEP = "access_key";

    // таблица creed
    public static final String KEY_ID__CREED = "_id";
    public static final String KEY_NAME__CREED = "name";
    public static final String KEY_DESCRIPTION__CREED = "description";
    public static final String KEY_IMAGE__CREED = "image";

    // таблица creed_branch
    public static final String KEY_ID__CREED_BRANCH = "_id";
    public static final String KEY_BRANCH_ID__CREED_BRANCH = "branch_id";
    public static final String KEY_CREED_ID__CREED_BRANCH = "creed_id";
    public static final String KEY_NAME__CREED_BRANCH = "name";
    public static final String KEY_DESCRIPTION__CREED_BRANCH = "description";
    public static final String KEY_BONUS__CREED_BRANCH = "bonus";
    public static final String KEY_STATUS__CREED_BRANCH = "status"; //выполнено или нет
    public static final String KEY_ACCESS_STATUS__CREED_BRANCH = "access_status"; // можно ли выполнять
    public static final String KEY_ACCESS_KEY__CREED_BRANCH = "access_key";

    //таблица safe zone
    public static final String KEY_ID__SAFE_ZONE = "_id";
    public static final String KEY_POLYGON_TYPE__SAFE_ZONE = "polygon_type";
    public static final String KEY_RADIUS__SAFE_ZONE = "radius";
    public static final String KEY_LATITUDE__SAFE_ZONE = "latitude";
    public static final String KEY_LONGITUDE__SAFE_ZONE = "longitude";

    //таблица discharge
    public static final String KEY_ID__DISCHARGE = "_id";
    public static final String KEY_DATE__DISCHARGE = "date";
    public static final String KEY_STATUS__DISCHARGE = "status";
    public static final String KEY_DELAY_STATUS__DISCHARGE = "delay_status";

    //таблица юзеров
    public static final String KEY_ID__USER = "_id";
    public static final String KEY_NAME__USER = "name";
    public static final String KEY_FACTION_ID__USER = "faction_id";
    public static final String KEY_ACCESS_LEVEL__USER = "access_level";
    public static final String KEY_QUEST_ID__USER = "quest_id";
    public static final String KEY_SUIT__USER = "suit";
    public static final String KEY_BONUS__USER = "bonus";
    public static final String KEY_SCIENCE_QR__USER = "science_qr";
    public static final String KEY_APPLY_QR__USER = "apply_qr";

    // таблица markers
    public static final String KEY_ID__MARKER = "_id";
    public static final String KEY_NAME__MARKER = "name";
    public static final String KEY_ICON__MARKER = "icon";
    public static final String KEY_LATITUDE__MARKER = "latitude";
    public static final String KEY_LONGITUDE__MARKER = "longitude";
    public static final String KEY_COMMENT__MARKER = "comment";

    // таблица cooldowns
    public static final String KEY_ID_CD = "_id";
    public static final String KEY_NAME_CD = "name";
    public static final String KEY_TIME_CD = "icon";

    private Context myContext;
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext=context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL("create table " + TABLE_MARKERS + "("
                + KEY_ID__MARKER + " integer primary key,"
                + KEY_NAME__MARKER + " text,"
                + KEY_ICON__MARKER + " text,"
                + KEY_LATITUDE__MARKER + " text,"
                + KEY_LONGITUDE__MARKER + " text,"
                + KEY_COMMENT__MARKER + " text" + ")"
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
        sqLiteDb.execSQL("drop table if exists " + TABLE_TABLE_OF_TABLES);

        onCreate(sqLiteDb);
    }

    // нужно для бд, созданной на пк
    public void create_db(){

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
