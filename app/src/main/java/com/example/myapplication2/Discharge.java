package com.example.myapplication2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;


/*
* Создано 19.02.23
* Должно содержать все про выброс
* */
public class Discharge {
    public static final String DISCHARGE = "dis";
    private StatsService service;
    private SQLiteDatabase database;
    private Cursor cursor;
    private Location location;
    private boolean warning;
    private boolean discharging;

    /*
    * Конструктор для сервиса
    * */
    public Discharge(StatsService service, SQLiteDatabase database, Cursor cursor){
        this.service = service;
        this.database = database;
        this.cursor = cursor;
        setWarning(false);
        setDischarging(false);
    }
    /*
    * конструктор для карты
    * */
    public Discharge(SQLiteDatabase database, Cursor cursor){
        this.database = database;
        this.cursor = cursor;
    }

    public void checkDischargeTime(){
        cursor = database.query(DBHelper.TABLE_DISCHARGE,null,
                 DBHelper.KEY_STATUS__DISCHARGE + "=?",
                new String[]{"false"},null,null,null);
        if (cursor.moveToFirst()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime datetime;
            ZonedDateTime zonedDateTime;
            do {
                int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE__DISCHARGE);
                int delayStatusIndex = cursor.getColumnIndex(DBHelper.KEY_DELAY_STATUS__DISCHARGE);
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__DISCHARGE);

                String delayStatus = cursor.getString(delayStatusIndex);
                datetime = LocalDateTime.parse(cursor.getString(dateIndex), formatter);
                zonedDateTime = datetime.atZone(ZoneId.systemDefault());
                long dischargeTimeMillis = zonedDateTime.toInstant().toEpochMilli();
                long currentTimeMillis = System.currentTimeMillis();

                if (dischargeTimeMillis - currentTimeMillis < 600000L && delayStatus.equals("false")){
                    setWarning(true);
                    sendContentValues(DBHelper.KEY_DELAY_STATUS__DISCHARGE, idIndex);
                }
                if (currentTimeMillis >= dischargeTimeMillis){
                    setDischarging(true);
                    sendContentValues(DBHelper.KEY_STATUS__DISCHARGE, idIndex);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void sendContentValues(String dbColumn, int idIndex){
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbColumn, "true");
        database.update(DBHelper.TABLE_DISCHARGE, contentValues,DBHelper.KEY_ID__DISCHARGE + "=" + cursor.getInt(idIndex),null);
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public boolean isDischarging() {
        return discharging;
    }

    public void setDischarging(boolean discharging) {
        this.discharging = discharging;
    }

    public boolean isInsideSafeZone() {
        location = new Location("");
        cursor = database.query(DBHelper.TABLE_SAFE_ZONE,null,null,null,null,null,null);
        int radiusIndex = cursor.getColumnIndex(DBHelper.KEY_RADIUS__SAFE_ZONE);
        int latitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__SAFE_ZONE);
        int longitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__SAFE_ZONE);
        if (cursor.moveToFirst()){
            do {
                double distance = distanceToCharacter(cursor.getDouble(latitudeIndex), cursor.getDouble(longitudeIndex));
                double radius = cursor.getDouble(radiusIndex);
                if (distance < radius){
                    cursor.close();
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    private double distanceToCharacter(double latitude, double longitude){
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location.distanceTo(service.myCurrentLocation);
    }
    /*
    * Вызывается в MapOSMTab
    * рисует зоны аномалий на карте.
    * */
    public void createSafeZones(MapView map) {
        cursor = database.query(DBHelper.TABLE_SAFE_ZONE,null,null,null,null,null,null);
        int radiusIndex = cursor.getColumnIndex(DBHelper.KEY_RADIUS__SAFE_ZONE);
        int latitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__SAFE_ZONE);
        int longitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__SAFE_ZONE);

        Polygon safeZone;
        if (cursor.moveToFirst()){
            do {
                double radius = cursor.getDouble(radiusIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double longitude = cursor.getDouble(longitudeIndex);

                safeZone = new Polygon();
                safeZone.getOutlinePaint().setStrokeWidth(2);
                safeZone.setPoints(Polygon.pointsAsCircle(new GeoPoint(latitude, longitude), radius));
                safeZone.getOutlinePaint().setColor(Color.parseColor("#ffffff"));
                map.getOverlayManager().add(safeZone);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

}

