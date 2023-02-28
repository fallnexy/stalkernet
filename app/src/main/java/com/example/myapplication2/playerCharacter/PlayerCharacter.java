package com.example.myapplication2.playerCharacter;
/*
* создано 10.02.23
* Тут должно быть всякие особенности персонажей
* Самый убогий персонаж:
* максимальное количество защит от аномалий - 1
* принимает урон от аномалий - стандартный урон от всех аномалий
* */

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.myapplication2.MainActivity;
import com.example.myapplication2.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;

import static com.example.myapplication2.Discharge.DISCHARGE;
import static com.example.myapplication2.MainActivity.INTENT_MAIN;
import static com.example.myapplication2.MainActivity.INTENT_MAIN_MINE;
import static com.example.myapplication2.StatsService.CHANNEL_ID;
import static com.example.myapplication2.StatsService.INTENT_SERVICE;
import static com.example.myapplication2.StatsService.INTENT_SERVICE_SOUND;
import static com.example.myapplication2.anomaly.Anomaly.BIO;
import static com.example.myapplication2.anomaly.Anomaly.GESTALT;
import static com.example.myapplication2.anomaly.Anomaly.MINE;
import static com.example.myapplication2.anomaly.Anomaly.OASIS;
import static com.example.myapplication2.anomaly.Anomaly.PSY;
import static com.example.myapplication2.anomaly.Anomaly.RAD;
import static com.example.myapplication2.anomaly.MineAnomaly.MINE_ACTIVATION;
import static com.example.myapplication2.anomaly.MineAnomaly.MINE_COUNT_DOWN;
import static com.example.myapplication2.anomaly.MineAnomaly.MINE_EXPLOSION;

public class PlayerCharacter {
    //константа
    public static final int DEFAULT_CHARACTER = 0;
    public static final int[] MAX_PROTECTION_STRENGTH = {2000, 2000, 100000};
    // значения, которые может принимать lastTimeHitBy
    private static final String[] ALLOWED_VALUES = {"rad", "bio", "psy", "dis", "ges", "min", ""};

    public static final String PREFERENCE_NAME = "player_character_preferences";
    public static final String NAME_KEY = "name_key";
    public static final String FACTION_KEY = "faction_key";
    public static final String FACTION_POSITION_KEY = "faction_position_key";
    public static final String LAST_TIME_HIT_BY_KEY = "last_time_hit_by_key";
    public static final String MAX_HEALTH_KEY = "max_health_key";
    public static final String HEALTH_KEY = "health_key";
    public static final String DEAD_KEY = "dead_key";
    public static final String MAX_RAD_KEY = "max_rad_key";
    public static final String MAX_BIO_KEY = "max_bio_key";
    public static final String MAX_PSY_KEY = "max_psy_key";
    public static final String RAD_PROTECTION_KEY = "rad_protection_key";
    public static final String BIO_PROTECTION_KEY = "bio_protection_key";
    public static final String PSY_PROTECTION_KEY = "psy_protection_key";
    public static final String GESTALT_PROTECTION_KEY = "ges_protection_key";
    public static final String CONTAMINATION_2D_KEY = "contamination_2d_key";
    public static final String SUIT = "suit", ARTEFACT = "art", QUEST = "quest";

    private String name;
    private String faction;
    private String factionPosition;
    private String lastTimeHitBy;
    private int maxHealth;
    private double health;
    private boolean dead;
    private boolean mineAvailable = true;

    /*
    * contamination - матрица с 2 строками и 3 столбцами
    * первая строка - текущее значение заражения: рад био пси
    * вторая строка - максимально возможно заражение: рад био пси
    * на данный момент максимальные значения изменять нельзя
    * */
    private double[][] contamination = {{0, 0, 0},{1000, 1000, 1000}};
    private HashMap<String, Integer> contaminationMap = new HashMap<>();

    /*
     * protections - матрицы с 2 строками и 3 столбцами
     * первая строка - прочночность защиты, вторая строка - процент защиты
     * первый столбец - quest, второй -  art, третий -  suit
     * */
    private double[][] radProtection = {{0,0,0}, {0,0,0}};
    private double[][] bioProtection = {{0,0,0}, {0,0,0}};
    private double[][] psyProtection = {{0,0,0}, {0,0,0}};
    private double[][] gesProtection = {{0,0,0}, {0,0,0}};
    private double[][] oasProtection = {{0,0,0}, {0,0,0}};

    private HashMap<String, double[][]> protectionMap = new HashMap<>();
    private HashMap<String, Integer> subProtectionMap = new HashMap<>();

    private Context context;
    private SharedPreferences sharedPreferences;
    private  PowerManager powerManager;


    public PlayerCharacter(Context context) {
        this.context = context;
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // нужны ли все эти this?
        this.name = "Иван";
        this.faction = "Вольный сталкер";
        this.factionPosition = "рядовой";
        this.lastTimeHitBy = "";
        this.maxHealth = 2000;
        this.health = 2000;
        this.dead = false;
    }

    public Context getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getFactionPosition() {
        return factionPosition;
    }

    public void setFactionPosition(String factionPosition) {
        this.factionPosition = factionPosition;
    }

    public String getLastTimeHitBy() {
        return lastTimeHitBy;
    }

    public void setLastTimeHitBy(String lastTimeHitBy) {
        if (Arrays.asList(ALLOWED_VALUES).contains(lastTimeHitBy)) {
            this.lastTimeHitBy = lastTimeHitBy;
        } else {
            throw new IllegalArgumentException("Invalid value for LastTimeHitBy. Only 'rad', 'bio', 'psy', 'dis', 'ges', or '' are allowed.");
        }
    }
    /*
    * а не соединить ли здоровье и макс здоровье в один массив
    * */
    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MAX_HEALTH_KEY, maxHealth);
        editor.apply();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (!dead) {
            if (health > 0 && health <= maxHealth) {
                this.health = health;
            } else if (health > maxHealth) {
                this.health = maxHealth;
            } else {
                this.health = 0;
                setDead(true);
            }
        }
    }
    /*
    * карта по умолчанию RAD - 0, BIO - 1, PSY - 2
    * */
    public void setDefaultContaminationMap(){
        contaminationMap.put(RAD,0);
        contaminationMap.put(BIO,1);
        contaminationMap.put(PSY,2);
    }
    /*
    * выставляет значение рад био пси
    * макс значения не выставляет
    * */
    public void setContaminationUnit(String type, double value){
        double maxValue = contamination[1][contaminationMap.get(type)];
        if (value >= maxValue){
            contamination[0][contaminationMap.get(type)] = value;
            setLastTimeHitBy(type);
            setHealth(0);
        } else {
            contamination[0][contaminationMap.get(type)] = value < 0 ? 0 : value;
        }
    }
    /*
    * получить любое double значение из матрицы, даже макс значение
    * */
    public double getContaminationUnit(String type, int row){
        return contamination[row][contaminationMap.get(type)];
    }
    /*
    * увеличивает выбранное значение на value
    * макс значения недоступны
    * */
    public void increaseContaminationUnit(String type, double value){
        setContaminationUnit(type, getContaminationUnit(type, 0) + value);
    }
    /*
    * увеличивает выбранное значение на value процент от максимальное
    * */
    public void increaseContaminationUnitByPercent(String type, double value){
        setContaminationUnit(type, getContaminationUnit(type, 0) + value * getContaminationUnit(type, 1) / 100d);
    }
    /*
    * выставляет весь массив значений заражения рад био пси
    * макс значения недоступны
    * */
    public void setContamination(double[] contamination){
        this. contamination[0] = Arrays.copyOf(contamination, contamination.length);
    }
    /*
    * выставляет весь массив максимальных значений заражения рад био пси
    * */
    public void setContaminationMax(double[] contamination){
        this. contamination[1] = Arrays.copyOf(contamination, contamination.length);
    }
    /*
    * выставляет всю матрицу, даже макс значения рад био пси
    * */
    public void setContamination2D(double[][] contamination){
        this. contamination = Arrays.copyOf(contamination, contamination.length);
    }
    /*
    * получает всю матрицу, даже макс значение рад био пси
    * */
    public double[][] getContamination2D(){
        return Arrays.copyOf(contamination, contamination.length);
    }
    /*
    * проверяет, мертв ли
    * */
    public boolean isDead() {
        return dead;
    }
    /*
    * выставляет мертв ли
    * */
    public void setDead(boolean dead) {
        if (!dead) {
            this.dead = false;
        } else {
            this.dead = true;
            switch (lastTimeHitBy) {
                case RAD:
                    sendDeathInfo("Вы умерли от Радиации", "H");
                    break;
                case BIO:
                    sendDeathInfo("Вы умерли от Биозаражения", "H");
                    break;
                case PSY:
                    sendDeathInfo("Вы умерли от Пси воздействия", "P");
                    break;
                case DISCHARGE:
                    sendDeathInfo("Вы умерли от выброса", "H");
                    break;
                case MINE:
                    sendDeathInfo("Вы подорвались на мине", "H");
                    break;
                case "":
                    sendDeathInfo("Вы умерли?", "H");
                    break;
            }
        }
    }
     //вызывается внутри setDead
    private void sendDeathInfo(String causeOfDeath, String letter) {
        Toast.makeText(context, causeOfDeath, Toast.LENGTH_LONG).show();
        Intent intent = new Intent("StatsService.Message");
        intent.putExtra("Message", letter);
        context.sendBroadcast(intent);
    }
    /*
    * выдает все матрицу рад защиты: и защиту и прочность
    * */
    public double[][] getRadProtection() {
        return Arrays.copyOf(radProtection, radProtection.length);
    }
    /*
    * выставляет всю матрицу рад защиты: и защитц и прочность
    * */
    public void setRadProtection(double[][] radProtection) {
        this.radProtection = Arrays.copyOf(radProtection, radProtection.length);
    }
    /*
     * выдает все матрицу рад защиты: и защиту и прочность
     * */
    public double[][] getBioProtection() {
        return Arrays.copyOf(bioProtection, bioProtection.length);
    }
    /*
     * выставляет всю матрицу рад защиты: и защитц и прочность
     * */
    public void setBioProtection(double[][] bioProtection) {
        this.bioProtection = Arrays.copyOf(bioProtection, bioProtection.length);
    }
    /*
     * выдает все матрицу рад защиты: и защиту и прочность
     * */
    public double[][] getPsyProtection() {
        return Arrays.copyOf(psyProtection, psyProtection.length);
    }
    /*
     * выставляет всю матрицу рад защиты: и защитц и прочность
     * */
    public void setPsyProtection(double[][] psyProtection) {
        this.psyProtection = Arrays.copyOf(psyProtection, psyProtection.length);
    }

    /*
    *  выдает всю матрицу гештальт защиты
    * */
    public double[][] getGesProtection() {
        return Arrays.copyOf(gesProtection, gesProtection.length);
    }
    /*
    * выставляет всю матрицу гештальт защиты,
    * в именно увеличивает [1][0] до тех пор, поке он != 100
    * */
    public void setGesProtection(double[][] gesProtection) {
        gesProtection[0][0] = gesProtection[0][0] < 10000 ? gesProtection[0][0] + 100 : 10000;
        gesProtection[1][0] = gesProtection[1][0] < 100 ? gesProtection[1][0] + 0.1 : 100;
        this.gesProtection = Arrays.copyOf(gesProtection, gesProtection.length);
    }
    /*
     * выдает все матрицу защиты от оазиса: и защиту и прочность
     * */
    public double[][] getOasProtection() {
        return Arrays.copyOf(oasProtection, oasProtection.length);
    }
    /*
     * выставляет всю матрицу защиты от оазиса: и защиту и прочность
     * */
    public void setOasProtection(double[][] oasProtection) {
        this.oasProtection = Arrays.copyOf(oasProtection, oasProtection.length);
    }
    /*
    * выдает сумму защит от определенной аномалии, складывая их по правилам релятивистского сложения скоростей
    * */
    public double getTotalProtection(double[] protectionType){
        double suitPlusArt = (protectionType[0] + protectionType[1]) / (1 + (protectionType[0] * protectionType[1]) / 10000);
        return (suitPlusArt + protectionType[2]) / (1 + (suitPlusArt * protectionType[2]) / 10000);
    }
    /*
     * карта по умолчанию RAD - radProtection, BIO - bioProtection, PSY - psyProtection
     * */
    public void setProtectionMap() {
        protectionMap.put(RAD, radProtection);
        protectionMap.put(BIO, bioProtection);
        protectionMap.put(PSY, psyProtection);
        protectionMap.put(GESTALT, gesProtection);
        protectionMap.put(OASIS, gesProtection);
    }
    public HashMap<String, double[][]> getProtectionMap() {
        setProtectionMap();
        return protectionMap;
    }
    /*
    * увеличивает здоровье на величину value
    * */
    public void increaseHealth(double value){
        setHealth(health + value);
    }
    /*
     * увеличивает здоровье на процент value от максимального здоровья
     * */
    public void increaseHealthPercent(double value){
        setHealth(health + value * maxHealth / 100d);
    }
    /*
    * применяет защиту в ответ на воздействие аномалии damagePair:
    * изменяет прочность защит
    * увеличивает заражение
    * уменьшает здоровье
    * */
    // TODO нужно избавиться от свитч
    public void applyProtection(Pair<String,Double> damagePair){
        String type = damagePair.first;
        double damage = damagePair.second;
        if (!type.equals(MINE)) {
            double[][] protection = protectionMap.get(type);
            double totalProtection = getTotalProtection(protection[1]);
            double decreaseProtection = damage * totalProtection / 100d;
            double decreaseHealth = damage * (1 - totalProtection / 100d);
            for (int i = 0; i < 3; i++ ){
                if (protection[0][i] > 0) {
                    protection[0][i] -= decreaseProtection;
                    if (protection[0][i] < 0) {
                        protection[0][i] = 0;
                        protection[1][i] = 0;
                    }
                    break;
                }
            }
            switch (type){
                case RAD:
                    setRadProtection(protection);
                    break;
                case BIO:
                    setBioProtection(protection);
                    break;
                case PSY:
                    setPsyProtection(protection);
                    break;
                case GESTALT:
                    setGesProtection(protection);
                    break;
                default:
                    break;
            }
            if (!type.equals(GESTALT) && !type.equals(OASIS)) {
                increaseContaminationUnit(type, decreaseHealth);
            }
            increaseHealth(-decreaseHealth);
        } else {
            resolveMine(damage, "false");
            setLastTimeHitBy(type);
        }
    }
    public boolean isMineAvailable() {
        return mineAvailable;
    }

    public void setMineAvailable(boolean mineAvailable) {
        this.mineAvailable = mineAvailable;
    }

    /*
    * обработка минного поля
    * */


    private int notificationId = 2;
    private int counter = 0;
    protected void resolveMine(double chance, String protection){
        double newChance = chance;
        if (chance < 20d){
            counter = 0;
        } else if (chance > 40d){
            counter++;
            newChance = chance + counter * 5;
            if (newChance > 100){
                newChance = 100;
            }
        }

        if (powerManager.isInteractive()) {
            String toastString = "вероятность активации мины составляет: " + newChance + "%";
            Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
            if (mineAvailable) {
                Random rand = new Random();
                int randomNum = rand.nextInt(101);
                if (randomNum <  newChance){
                    Intent intent = new Intent(INTENT_MAIN);
                    intent.putExtra(INTENT_MAIN_MINE, protection);
                    context.sendBroadcast(intent);
                    intent = new Intent(INTENT_SERVICE);
                    intent.putExtra(INTENT_SERVICE_SOUND, MINE_ACTIVATION);
                    context.sendBroadcast(intent);
                }
                mineAvailable = false;
                Handler handler = new Handler();
                handler.postDelayed(() -> setMineAvailable(true), MINE_COUNT_DOWN);
            }
        } else {
            Intent intent = new Intent(getContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.profile)
                    .setContentTitle("МИННОЕ ПОЛЕ")
                    .setContentText("Откройте приложение, чтобы иметь возможномть деактивировать активированную мину")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.notify(notificationId, builder.build());

            if (mineAvailable) {
                Random rand = new Random();
                int randomNum = rand.nextInt(101);
                if (randomNum <  newChance){
                    increaseHealthPercent(-50);
                    intent = new Intent(INTENT_SERVICE);
                    intent.putExtra(INTENT_SERVICE_SOUND, MINE_EXPLOSION);
                    context.sendBroadcast(intent);
                }
                mineAvailable = false;
                Handler handler = new Handler();
                handler.postDelayed(() -> setMineAvailable(true), MINE_COUNT_DOWN);
            }
        }
    }
    /*
    * выведение аномалий из организма, если персонаж находится все аномалий
    * RAD выводится со временем
    * PSY выводится сразу же
    * */
    public void purification(){
        increaseContaminationUnit(RAD, -0.3);
        setContaminationUnit(PSY, 0);
    }
    /*
     * карта по умолчанию QUEST - 0, ARTEFACT - 1, SUIT - 2
     * */
    public void setSubProtectionMap(){
        subProtectionMap.put(QUEST,0);
        subProtectionMap.put(ARTEFACT,1);
        subProtectionMap.put(SUIT,2);
    }
    public HashMap<String, Integer> getSubProtectionMap() {
        setSubProtectionMap();
        return subProtectionMap;
    }
    /*
    * выставляет защиту и выключает лишнюю: разрешено иметь только одну защиту из рад био пси
    * если защита = 0, то и прочность ставит = 0
    * выключает другие защиты, если выставляемая > 0
    * */
    public void setProtection(String type, String subType, double value){
        double strength = value == 0 ? 0: MAX_PROTECTION_STRENGTH[subProtectionMap.get(subType)];
        protectionMap.get(type)[0][subProtectionMap.get(subType)] = strength;
        protectionMap.get(type)[1][subProtectionMap.get(subType)] = value;
        if (value > 0) {
            for (String anomalyType : new String[]{RAD, BIO, PSY}){
                if (!type.equals(anomalyType)){
                    Arrays.fill(protectionMap.get(anomalyType)[0], 0);
                    Arrays.fill(protectionMap.get(anomalyType)[1], 0);
                }
            }
        }
    }
    /*
    * реакция на выброс
    * */
    public void responseDischarge(){
        setLastTimeHitBy(DISCHARGE);
        setHealth(0);
    }
}
