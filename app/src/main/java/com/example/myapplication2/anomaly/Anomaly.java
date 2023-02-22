package com.example.myapplication2.anomaly;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.StatsService;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.HashMap;

import androidx.core.util.Pair;

/*
* 14.02.2023
* Начало переделывания класса
* тут должны остаться только RAD BIO PSY
* всякие гештальны, оазисы и qr должны переехать в свои подклассы
* */
public class Anomaly {
    public static final String RAD = "rad", BIO = "bio", PSY = "psy", GESTALT = "ges", OASIS = "oas", QR = "qr";

    protected double radius;
    protected String type;
    protected String gesStatus;
    protected double distance;
    protected double power, minPower;
    protected boolean showable;
    protected boolean inside;
    protected double damage;
    private int dayStart, dayFinish;

    private int latIndex, lonIndex, radiusIndex, dayStartIndex, dayFinishIndex, gestaltIndex, typeIndex, powerIndex, minPowerIndex, showableIndex;

    private Location location;
    private StatsService service;

    protected SQLiteDatabase database;
    protected Cursor cursor;
    /*
    * конструктор для всех аномалий кроме QR
    * */
    public Anomaly(StatsService service, SQLiteDatabase database, Cursor cursor){
        this.service = service;
        this.database = database;
        this.cursor = cursor;
        location = new Location("");
    }
    /*
    * конструктор для карты
    * */
    public Anomaly(SQLiteDatabase database, Cursor cursor) {
        this.database = database;
        this.cursor = cursor;
    }
    /*
    * конструктов для оазиса
    * */
    public Anomaly(StatsService service){

    }
    /*
     * конструктор для QR аномалии
     * */
    public Anomaly() {

    }

    // setters and getters
    /*
    * поясняет, находится ли игрок внутри аномалии, если она работает в этот игровой день
    * */
    public Pair<Boolean, String> isInside(int day) {
        dayStart = cursor.getInt(dayStartIndex);
        dayFinish = cursor.getInt(dayFinishIndex);
        power = cursor.getDouble(powerIndex);
        minPower = cursor.getDouble(minPowerIndex);
        radius = cursor.getDouble(radiusIndex);
        distance = distanceToCharacter(cursor.getDouble(latIndex), cursor.getDouble(lonIndex));
        gesStatus = cursor.getString(gestaltIndex);
        type = cursor.getString(typeIndex);
        showable = cursor.getString(showableIndex).equals("true");
        cursor.moveToNext();
        inside = distance < radius && day >= dayStart && day <= dayFinish;
        return new Pair<>(inside, type);
    }
    public double getPower() {
        return damage;
    }
    public String getType() {
        return type;
    }
    // setters and getters были выше

    /*
    * берет из базы данных инфо о аномалии
    * */
    public int getAnomalyInfo(){
        cursor = database.query(DBHelper.TABLE_ANOMALY,
                new String[]{DBHelper.KEY_ID__ANOMALY, DBHelper.KEY_LATITUDE__ANOMALY,
                        DBHelper.KEY_LONGITUDE__ANOMALY, DBHelper.KEY_RADIUS__ANOMALY,
                        DBHelper.KEY_DAY_START__ANOMALY, DBHelper.KEY_DAY_FINISH__ANOMALY,
                        DBHelper.KEY_GESTALT_STATUS__ANOMALY, DBHelper.KEY_TYPE__ANOMALY,
                        DBHelper.KEY_POWER__ANOMALY, DBHelper.KEY_MIN_POWER__ANOMALY,
                        DBHelper.KEY_BOOL_SHOWABLE__ANOMALY},
                null,null,null,null,null);
        latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__ANOMALY);
        lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__ANOMALY);
        radiusIndex = cursor.getColumnIndex(DBHelper.KEY_RADIUS__ANOMALY);
        dayStartIndex = cursor.getColumnIndex(DBHelper.KEY_DAY_START__ANOMALY);
        dayFinishIndex = cursor.getColumnIndex(DBHelper.KEY_DAY_FINISH__ANOMALY);
        gestaltIndex = cursor.getColumnIndex(DBHelper.KEY_GESTALT_STATUS__ANOMALY);
        typeIndex = cursor.getColumnIndex(DBHelper.KEY_TYPE__ANOMALY);
        powerIndex = cursor.getColumnIndex(DBHelper.KEY_POWER__ANOMALY);
        minPowerIndex = cursor.getColumnIndex(DBHelper.KEY_MIN_POWER__ANOMALY);
        showableIndex = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOWABLE__ANOMALY);
        cursor.moveToFirst();
        return cursor.getCount();
    }
    /*
    * определяет расстояние до игрока
    * */
    private double distanceToCharacter(double latitude, double longitude){
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location.distanceTo(service.myCurrentLocation);
    }
    /*
    * вызывается в серсиве, чтобы получить урон и тип, который на самом деле получен в isInside
    * */
    public Pair<String, Double> getDamage(){
        damage = power * (1 - Math.pow(distance / radius, 2));
        damage = Math.max(damage, Math.max(minPower, 0.001d));
        return new Pair<>(type, damage);
    }

    /*
    * разрешает аномалии показываться на карте
    * предполагает, что база данных открыта
    * */
    public void setShowable(int i){
        if (showable) {
            ContentValues contentValues;
            contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_BOOL_SHOW_ON_MAP__ANOMALY, "true");
            database.update(DBHelper.TABLE_ANOMALY, contentValues,  DBHelper.KEY_ID__ANOMALY + "=" + (i+1), null);
        }
    }
    /*
    * нужно, чтобы передать в гештальт класс его статус
    * */
    public String getGestaltStatus(){
        return gesStatus;
    }
    /*
    * нужно, чтобо передать в гештальт класс инфу о его дамаге
    * */
    public double[] getGestaltDamageInfo(){
        return new double[] {distance, radius, power, minPower};
    }
    /*
     * создает карты для выбора цвета окружности и внутренности аномалий
     * */
    HashMap<String, String> anomalyFillPaintMap;
    HashMap<String, String> anomalyOutlinePaintMap;
    private void setAnomalyPaintMap(){

        anomalyFillPaintMap = new HashMap<>();
        anomalyOutlinePaintMap = new HashMap<>();
        anomalyFillPaintMap.put(RAD, "#1Efca800");
        anomalyFillPaintMap.put(BIO, "#1E00ff2b");
        anomalyFillPaintMap.put(PSY, "#1E0011ff");
        anomalyFillPaintMap.put(GESTALT, "#1E58585c");
        anomalyFillPaintMap.put(OASIS, "#1Ef227e1");
        anomalyOutlinePaintMap.put(RAD, "#fca800");
        anomalyOutlinePaintMap.put(BIO, "#00ff2b");
        anomalyOutlinePaintMap.put(PSY, "#0011ff");
        anomalyOutlinePaintMap.put(GESTALT, "#58585c");
        anomalyOutlinePaintMap.put(OASIS, "#f227e1");
    }
    /*
     * В onCreate все аномалии добавляются на карту, но с параметром setVisible(false).
     * Когда координата меняется, то происходит цепочка вызовов, в результате которой вызывается drawCirceAnomaly().
     * Метод drawCirceAnomaly() проверяет следующее: если в таблице DBHelper.TABLE_ANOMALY
     * строка DBHelper.KEY_BOOL_SHOW_ON_MAP равна "true", то для аномалии ставится setVisible(true)
     */
    public Polygon[] createCircleAnomaly(MapView map){
        setAnomalyPaintMap();
        Polygon[] circleAnomaly;

        cursor = database.query(DBHelper.TABLE_ANOMALY, null, null, null, null, null, null);
        circleAnomaly = new Polygon[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int type = cursor.getColumnIndex(DBHelper.KEY_TYPE__ANOMALY);
            int radius = cursor.getColumnIndex(DBHelper.KEY_RADIUS__ANOMALY);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__ANOMALY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__ANOMALY);
            do {
                circleAnomaly[cursor.getInt(idIndex) - 1] = new Polygon();
                try {
                    circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor(anomalyFillPaintMap.get(cursor.getString(type)))); // цвет заливки
                    circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setColor(Color.parseColor(anomalyOutlinePaintMap.get(cursor.getString(type)))); // цвет окружности
                }catch (Exception e){
                    circleAnomaly[cursor.getInt(idIndex) - 1].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                }
                circleAnomaly[cursor.getInt(idIndex) - 1].getOutlinePaint().setStrokeWidth(3); // толщина окружности
                circleAnomaly[cursor.getInt(idIndex) - 1].setPoints(Polygon.pointsAsCircle(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), cursor.getDouble(radius)));
                map.getOverlayManager().add(circleAnomaly[cursor.getInt(idIndex) - 1]);
                circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return circleAnomaly;
    }
    /*
     * Когда координата меняется, то происходит цепочка вызовов, которая приводит к setAnomalyVisible().
     * Этот метод проверяет в таблице DBHelper.TABLE_ANOMALY значение строки DBHelper.KEY_BOOL_SHOW_ON_MAP:
     * если "true", то показываем круг на карте setVisible(true), "false" то скрываем setVisible(false)
     */
    public void setAnomalyVisible(Polygon[] circleAnomaly){
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{"_id", "bool_show_on_map"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int boolShowOnMap = cursor.getColumnIndex(DBHelper.KEY_BOOL_SHOW_ON_MAP__ANOMALY);

            do {
                if (cursor.getString(boolShowOnMap).equals("true") && !circleAnomaly[cursor.getInt(idIndex) - 1].isVisible()) {
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(true);
                } else if(cursor.getString(boolShowOnMap).equals("false")){
                    circleAnomaly[cursor.getInt(idIndex) - 1].setVisible(false);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
