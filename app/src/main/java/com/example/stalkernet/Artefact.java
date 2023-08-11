package com.example.stalkernet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Artefact {

    private SQLiteDatabase database;
    private Cursor cursor;

    public Artefact(SQLiteDatabase database, Cursor cursor){
        this.database = database;
        this.cursor = cursor;
    }

    /*
    * применение артоса
    * */
    public String apply(String id){
        cursor = database.query(DBHelper.TABLE_ARTEFACT,
                new String[] {DBHelper.KEY_ID__ARTEFACT, DBHelper.KEY_APPLY_ONE__ARTEFACT,
                        DBHelper.KEY_APPLY_TWO__ARTEFACT, DBHelper.KEY_APPLY_THREE__ARTEFACT, DBHelper.KEY_APPLY_LEVEL__ARTEFACT},
                DBHelper.KEY_ID__ARTEFACT + " =? ", new String[]{id}, null, null, null);
        int levelIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_LEVEL__ARTEFACT);
        int oneIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_ONE__ARTEFACT);
        int twoIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_TWO__ARTEFACT);
        int threeIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_THREE__ARTEFACT);
        String apply;
        if (cursor.moveToFirst()){
            switch (cursor.getInt(levelIndex)){
                case 1:
                    apply = cursor.getString(oneIndex);
                    break;
                case 2:
                    apply = cursor.getString(twoIndex);
                    break;
                case 3:
                    apply = cursor.getString(threeIndex);
                    break;
                default:
                    return "error";
            }
            cursor.close();
            return apply;
        }
        cursor.close();
        return "error";
    }
    /*
    * открытие уровня доступа
    * */
    public void open_access(String id, String accessLevel){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_APPLY_LEVEL__ARTEFACT, accessLevel);
        database.update(DBHelper.TABLE_ARTEFACT, contentValues, DBHelper.KEY_ID__ARTEFACT + "=" + id, null);
    }
}
