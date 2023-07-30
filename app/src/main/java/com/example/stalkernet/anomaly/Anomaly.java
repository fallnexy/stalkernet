package com.example.stalkernet.anomaly;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.StatsService;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.core.util.Pair;

import static com.example.stalkernet.StatsService.LOG_CHE;
import static com.example.stalkernet.anomaly.GestaltAnomaly.GESTALT_CLOSE;
import static com.example.stalkernet.anomaly.GestaltAnomaly.GESTALT_OPEN;
import static com.example.stalkernet.fragments.MapOSMTab.INTENT_MAP;
import static com.example.stalkernet.fragments.MapOSMTab.INTENT_MAP_VISIBLE;

/*
* 14.02.2023
* Начало переделывания класса
* тут должны остаться только RAD BIO PSY
* всякие гештальны, оазисы и qr должны переехать в свои подклассы
* */
public class Anomaly {
    public static final String RAD = "rad", BIO = "bio", PSY = "psy", GESTALT = "ges", OASIS = "oas", MINE = "min", QR = "qr";
    public static final String CIRCLE = "circle";

    protected String figure;
    protected double radius;
    protected String type;
    protected String gesStatus;
    protected double distance;
    protected double power, minPower;
    protected boolean showable;
    protected boolean inside;
    protected double damage;
    private int dayStart, dayFinish, id;

    private int idIndex, polygonIndex, latIndex, lonIndex, radiusIndex, dayStartIndex,
            dayFinishIndex, gestaltIndex, typeIndex, powerIndex, minPowerIndex,
            showableIndex, visibleIndex;
    private double[] latitudesArray, longitudesArray;

    private Location location;
    protected StatsService service;

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

    /*
     * берет из базы данных инфо о аномалии и возвращает количество аномалий
     * */
    public int getAnomalyInfo(){
        cursor = database.query(DBHelper.TABLE_ANOMALY,null, null,null,null,null,null);
        idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
        polygonIndex = cursor.getColumnIndex(DBHelper.KEY_POLYGON_TYPE__ANOMALY);
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
        visibleIndex = cursor.getColumnIndex(DBHelper.KEY_VISIBLE__ANOMALY);
        cursor.moveToFirst();
        return cursor.getCount();
    }
    /*
    * поясняет, находится ли игрок внутри аномалии, если она работает в этот игровой день
    * */
    public Pair<Boolean, String> isInside(int day) {
        id = cursor.getInt(idIndex);
        figure = cursor.getString(polygonIndex);
        dayStart = cursor.getInt(dayStartIndex);
        dayFinish = cursor.getInt(dayFinishIndex);
        if (figure.equals(CIRCLE)){
            radius = cursor.getDouble(radiusIndex);
            distance = distanceToCharacter(cursor.getDouble(latIndex), cursor.getDouble(lonIndex));
            inside = distance < radius && day >= dayStart && day <= dayFinish;
        } else{
            latitudesArray = Arrays.stream(cursor.getString(latIndex).split(","))
                    .mapToDouble(Double::parseDouble).toArray();
            longitudesArray = Arrays.stream(cursor.getString(lonIndex).split(","))
                    .mapToDouble(Double::parseDouble).toArray();
            Coordinate point = new Coordinate(service.myCurrentLocation.getLatitude(), service.myCurrentLocation.getLongitude());
            inside = getPolygon(latitudesArray, longitudesArray).contains(new GeometryFactory().createPoint(point)) && day >= dayStart && day <= dayFinish;
        }
        power = cursor.getDouble(powerIndex);
        minPower = cursor.getDouble(minPowerIndex);
        gesStatus = cursor.getString(gestaltIndex);
        type = cursor.getString(typeIndex);
        showable = cursor.getString(showableIndex).equals("true");

        if (type.equals(GESTALT) && inside){
            if (gesStatus.equals(GESTALT_CLOSE)) {
                updateDB(DBHelper.KEY_GESTALT_STATUS__ANOMALY, GESTALT_OPEN, id);
                sendIntent("StatsService.Message","Message","G");
            } else if (gesStatus.equals(GESTALT_OPEN)){
                sendIntent("StatsService.Message","Message","G");
            }
        }
        // окончательное определение inside
        inside = (inside && !type.equals(GESTALT)) || (!inside && type.equals(GESTALT) && gesStatus.equals(GESTALT_OPEN));
        boolean visible = cursor.getString(visibleIndex).equals("true");
        if (inside && !visible){
            Log.d(LOG_CHE, "WTF");
            updateDB(DBHelper.KEY_VISIBLE__ANOMALY, "true", id);
            String message = (id - 1) + ":" + "true";
            Log.d(LOG_CHE, message);
            sendIntent(INTENT_MAP, INTENT_MAP_VISIBLE, message);
        } else if (!inside && visible){
            Log.d(LOG_CHE, "WTF_false");
            updateDB(DBHelper.KEY_VISIBLE__ANOMALY, "false", id);
            sendIntent(INTENT_MAP, INTENT_MAP_VISIBLE, (id - 1) + ":" + "false");
        }
        cursor.moveToNext();
        return new Pair<>(inside, type);
    }
    /*
     * вызывается, чтобы внести изменения в базу данных
     * */
    private void updateDB(String column, String status, int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, status);
        database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=?", new String[]{String.valueOf(id)});
    }
    /*
     * вызывается, чтобы отправить сообщение в mainActivity
     * */
    protected void sendIntent (String action, String name, String message){
        Intent intent = new Intent(action);
        intent.putExtra(name, message);
        service.getApplicationContext().sendBroadcast(intent);
    }
    /*
    * polygon из jts для рассчетов
    * если figure != circle, то вызывается этот метод, который возвращает
    * полигон, вершины которого имеют координаты из базы данных
    * */
    private Polygon getPolygon(double[] latitudes, double[] longitudes){
        int length = latitudes.length;
        Coordinate[] coordinates = new Coordinate[length + 1];
        for (int i = 0; i < length; i++){
            coordinates[i] = new Coordinate(latitudes[i], longitudes[i]);
        }
        coordinates[length] = new Coordinate(latitudes[0], longitudes[0]);
        LinearRing linearRing = new GeometryFactory().createLinearRing(coordinates);
        return new Polygon(linearRing, null, new GeometryFactory());
    }
    /*
    * polygon из osmdroid для рисования на карте
    * */
    public org.osmdroid.views.overlay.Polygon getPolygonOSM(double[] latitudes, double[] longitudes){
        List<GeoPoint> vertices = new ArrayList<>();
        for (int i = 0; i < latitudes.length; i++){
            vertices.add(new GeoPoint(latitudes[i], longitudes[i]));
        }
        vertices.add(new GeoPoint(latitudes[0], longitudes[0]));
        org.osmdroid.views.overlay.Polygon polygon = new org.osmdroid.views.overlay.Polygon();
        polygon.setPoints(vertices);
        return polygon;
    }

    public double getPower() {
        return damage;
    }
    public String getType() {
        return type;
    }
    // setters and getters были выше


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
        damage = figure.equals(CIRCLE) ? (power * (1 - Math.pow(distance / radius, 2))) : damage;
        damage = Math.max(damage, Math.max(minPower, 0.001d));
        return new Pair<>(type, damage);
    }
    /*
    * нужно, чтобо передать в гештальт класс инфу о его дамаге
    * */
    public Pair<Double[], String> getGestaltDamageInfo(){
        return new Pair<>(new Double[]{distance, radius, power, minPower}, gesStatus);
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
        anomalyFillPaintMap.put(MINE, "#1Efa0505");
        anomalyOutlinePaintMap.put(RAD, "#fca800");
        anomalyOutlinePaintMap.put(BIO, "#00ff2b");
        anomalyOutlinePaintMap.put(PSY, "#0011ff");
        anomalyOutlinePaintMap.put(GESTALT, "#58585c");
        anomalyOutlinePaintMap.put(OASIS, "#f227e1");
        anomalyOutlinePaintMap.put(MINE, "#eb5252");
    }
    /**
    * далее методы для карты
    * */
    /*
     * В onCreate все аномалии добавляются на карту, но с параметром setVisible(false).
     * Когда координата меняется, то происходит цепочка вызовов, в результате которой вызывается drawCirceAnomaly().
     * Метод drawCirceAnomaly() проверяет следующее: если в таблице DBHelper.TABLE_ANOMALY
     * строка DBHelper.KEY_BOOL_SHOW_ON_MAP равна "true", то для аномалии ставится setVisible(true)
     */
    private boolean[] visible;
    public org.osmdroid.views.overlay.Polygon[] createPolygons(MapView map){
        setAnomalyPaintMap();
        org.osmdroid.views.overlay.Polygon[] polygons;

        cursor = database.query(DBHelper.TABLE_ANOMALY, null, null, null, null, null, null);
        visible = new boolean[cursor.getCount()];
       // Log.d(LOG_CHE_CHE, "createLength" + visible.length);
        polygons = new org.osmdroid.views.overlay.Polygon[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int polygonIndex = cursor.getColumnIndex(DBHelper.KEY_POLYGON_TYPE__ANOMALY);
            int typeIndex = cursor.getColumnIndex(DBHelper.KEY_TYPE__ANOMALY);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE__ANOMALY);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE__ANOMALY);
            int visibleIndex = cursor.getColumnIndex(DBHelper.KEY_VISIBLE__ANOMALY);

            do {
                int index = cursor.getInt(idIndex) - 1;
                polygons[index] = new org.osmdroid.views.overlay.Polygon();
                if (cursor.getString(polygonIndex).equals(CIRCLE)){
                    int radiusIndex = cursor.getColumnIndex(DBHelper.KEY_RADIUS__ANOMALY);
                    polygons[index].setPoints(org.osmdroid.views.overlay.Polygon.pointsAsCircle(new GeoPoint(cursor.getDouble(latIndex), cursor.getDouble(lonIndex)), cursor.getDouble(radiusIndex)));
                } else {
                    latitudesArray = Arrays.stream(cursor.getString(latIndex).split(","))
                            .mapToDouble(Double::parseDouble).toArray();
                    longitudesArray = Arrays.stream(cursor.getString(lonIndex).split(","))
                            .mapToDouble(Double::parseDouble).toArray();
                    polygons[index] = getPolygonOSM(latitudesArray, longitudesArray);
                }
                try {
                    polygons[index].getFillPaint().setColor(Color.parseColor(anomalyFillPaintMap.get(cursor.getString(typeIndex)))); // цвет заливки
                    polygons[index].getOutlinePaint().setColor(Color.parseColor(anomalyOutlinePaintMap.get(cursor.getString(typeIndex)))); // цвет окружности
                }catch (Exception e){
                    polygons[index].getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
                }
                polygons[index].getOutlinePaint().setStrokeWidth(3); // толщина окружности
                polygons[index].setVisible(cursor.getString(visibleIndex).equals("true"));
                map.getOverlayManager().add(polygons[index]);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return polygons;
    }

    public boolean[] isVisible(){
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{DBHelper.KEY_ID__ANOMALY, DBHelper.KEY_VISIBLE__ANOMALY}, null, null, null, null,null );
       // Log.d(LOG_CHE_CHE, "cursor.getCount " + cursor.getCount());
        //Log.d(LOG_CHE_CHE, "visible.length " + visible.length);
        if (cursor.moveToFirst()){
            //Log.d(LOG_CHE_CHE,"cursor position " + cursor.getPosition());
            int visibleIndex = cursor.getColumnIndex(DBHelper.KEY_VISIBLE__ANOMALY);
            do {
              //  Log.d(LOG_CHE_CHE, "cursor position " + cursor.getPosition());
               // Log.d(LOG_CHE_CHE, "isVisible " + cursor.getString(visibleIndex));
                visible[cursor.getPosition()] = cursor.getString(visibleIndex).equals("true");
            } while (cursor.moveToNext());
        }
       // Log.d(LOG_CHE_CHE,"isVisible " + visible[1]);
        cursor.close();
        return visible;
    }

    /*
     * Когда координата меняется, то происходит цепочка вызовов, которая приводит к setAnomalyVisible().
     * Этот метод проверяет в таблице DBHelper.TABLE_ANOMALY значение строки DBHelper.KEY_BOOL_SHOW_ON_MAP:
     * если "true", то показываем круг на карте setVisible(true), "false" то скрываем setVisible(false)
     */
    public ArrayList<Integer> setVisible(Boolean[] visibleOnMapList){
        ArrayList<Integer> changedVisibleList = new ArrayList<>();
        cursor = database.query(DBHelper.TABLE_ANOMALY, new String[]{DBHelper.KEY_ID__ANOMALY, DBHelper.KEY_VISIBLE__ANOMALY}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ANOMALY);
            int visible = cursor.getColumnIndex(DBHelper.KEY_VISIBLE__ANOMALY);
            do {
                int index = cursor.getInt(idIndex) - 1;
                if (cursor.getString(visible).equals("true") && !visibleOnMapList[index]) {
                    changedVisibleList.add(index);
                } else if(cursor.getString(visible).equals("false") && visibleOnMapList[index]){
                    changedVisibleList.add(index);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return changedVisibleList;
    }

}
