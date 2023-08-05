package com.example.stalkernet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Tool {

    protected SQLiteDatabase database;
    protected Cursor cursor;

    public Tool(SQLiteDatabase database, Cursor cursor){
        this.database = database;
        this.cursor = cursor;
    }

    public void setScienceQR(int id, boolean on){
        int turn = on ? 1 : 0;
        /*cursor = database.query(DBHelper.TABLE_USER,
                new String[]{DBHelper.KEY_ID__USER, DBHelper.KEY_SCIENCE_QR__USER},
                DBHelper.KEY_ID__USER + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);*/
        ContentValues cv;
        cv = new ContentValues();
        cv.put(DBHelper.KEY_SCIENCE_QR__USER, turn);
        database.update(DBHelper.TABLE_USER, cv, DBHelper.KEY_ID__USER + "=" + (id), null);
    }

    public boolean getScienceQR(int id){
        cursor = database.query(DBHelper.TABLE_USER,
                new String[]{DBHelper.KEY_ID__USER, DBHelper.KEY_SCIENCE_QR__USER},
                DBHelper.KEY_ID__USER + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        cursor.moveToFirst();
        int scienceIndex = cursor.getColumnIndex(DBHelper.KEY_SCIENCE_QR__USER);
        boolean scienceQR = cursor.getInt(scienceIndex) == 1;
        cursor.close();
        return scienceQR;
    }
}
