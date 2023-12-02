package com.example.stalkernet.anomaly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.StatsService;

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
    public static final String GESTALT_AVAILABLE = "ges_available";

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
    public void isProtected(Pair<Double[], String> gestaltDamage){
        this.gesStatus = gestaltDamage.second;
        this.distance = gestaltDamage.first[0];
        this.radius = gestaltDamage.first[1];
        this.power = gestaltDamage.first[2];
        this.minPower = gestaltDamage.first[3];
        /*if (inside && this.gesStatus.equals(GESTALT_CLOSE)){
            setCV(GESTALT_OPEN, String.valueOf(position), "G");
        } else if (inside && this.gesStatus.equals(GESTALT_OPEN)){
            sendIntent("G");
        }*/
    }
    /*
    * вызывается где-нибудь, чтобы изменить статус на протектед
    * а через некоторое время на закрыто
    * */
    public void setProtected(String id){
        setCV(GESTALT_PROTECTED, id, "GP");
        Handler handler = new Handler();
        handler.postDelayed(() -> setCV(GESTALT_CLOSE, id, "GC"), 600000);
    }
    /*
    * вызывается в setProtected, чтобы внести изменения в базу данных
    * */
    private void setCV (String gesStatus, String id, String massage){
        contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_GESTALT_STATUS__ANOMALY, gesStatus);
        this.database.update(DBHelper.TABLE_ANOMALY, contentValues, DBHelper.KEY_ID__ANOMALY + "=?", new String[]{id});
        sendIntent("StatsService.Message","Message",massage);
    }


}
