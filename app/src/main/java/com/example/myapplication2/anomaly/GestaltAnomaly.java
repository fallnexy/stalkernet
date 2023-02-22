package com.example.myapplication2.anomaly;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.StatsService;

import androidx.core.util.Pair;

/*
* создано 16.02.23
* в отличии от рад био пси аномалий, гештальт наносит урон тогда,
* когда игрок находится вне ее.
* Чтобы перестать получать урон, надо осканировать qr внутри аномалии
* и выйти из нее, пока действует иммунитет
* Круг аномалии видно, если из него выйти
* */
public class GestaltAnomaly extends Anomaly{

    public static final String GESTALT_OPEN = "open", GESTALT_CLOSE = "close", GESTALT_PROTECTED = "protected";

    private ContentValues contentValues;
    private Context context;

    public GestaltAnomaly(StatsService service, SQLiteDatabase database, Cursor cursor) {
        super(service, database, cursor);
        context = service.getApplicationContext();
    }
    /*
    * наносит урон, если гештальт открыт
    * */
    @Override
    public Pair<String, Double> getDamage() {
        if (gesStatus.equals(GESTALT_OPEN)) {
            double damage = power * (distance / radius - 1);
            damage = Math.max(damage, minPower);
            damage = Math.min(damage, power);
            return new Pair<>(GESTALT, damage);
        } else {
            return new Pair<>(GESTALT, 0d);
        }
    }
    /*
    * вызывается в статсервисе, если тип аномалии гештальт
    * берет статы, которые получил родительский класс
    * */
    public void isProtected(boolean inside, String gesStatus, double[] gestaltDamage, int position){
        this.gesStatus = gesStatus;
        this.distance = gestaltDamage[0];
        this.radius = gestaltDamage[1];
        this.power = gestaltDamage[2];
        this.minPower = gestaltDamage[3];
        if (inside && this.gesStatus.equals(GESTALT_CLOSE)){
            setCV(GESTALT_OPEN, String.valueOf(position), "G");
        } else if (inside && this.gesStatus.equals(GESTALT_OPEN)){
            sendIntent("G");
        }
    }
    /*
    * вызывается где-нибудь, чтобы изменить статус на протектед
    * а через некоторое время на закрыто
    * */
    public void setProtected(String id){
        setCV(GESTALT_PROTECTED, id, "GP");
        Handler handler = new Handler();
        handler.postDelayed(() -> setCV(GESTALT_CLOSE, id, "GC"), 12000);
    }
    /*
    * вызывается в setProtected, чтобы внести изменения в базу данных
    * */
    private void setCV (String gesStatus, String id, String massage){
        contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_GESTALT_STATUS__ANOMALY, gesStatus);
        this.database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=?", new String[]{id});
        sendIntent(massage);
    }
    /*
    * вызывается в setCV, чтобы отправить сообщение в mainActivity
    * */
    private void sendIntent (String message){
        Intent intent = new Intent("StatsService.Message");
        intent.putExtra("Message", message);
        context.sendBroadcast(intent);
    }
}
