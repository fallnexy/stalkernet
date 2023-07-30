package com.example.stalkernet.anomaly;
/*
* дата создания утеряна
* аномалия долбит, если timeStamp, получаемый телефоном от аномалии,
* принимает одно и тоже значение меньше 5 раз
* */
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.example.stalkernet.StatsService;

import java.util.List;

import androidx.core.util.Pair;

public class WifiAnomaly extends Anomaly{
    public static final String CONTROL_WIFI = "control";
    public static final String CHIMERA_WIFI = "chimera";

    Context context;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;

    private long controlTimestamp = 0;
    private long chimeraTimestamp = 0;
    private int controlCounter = 0;
    private int chimeraCounter = 0;

    public WifiAnomaly(StatsService service){
        super(service);
        context = service.getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        damage = 1d;
    }
    /*
    * возвращает список вай фаев, которые поямал телефон
    * */
    public boolean getWiFiScan(){
        wifiList = wifiManager.getScanResults();
        return wifiList.stream()
                .map(result -> result.SSID)
                .anyMatch(name -> name.equals("chimera") || name.equals("control"));
    }
    /*
    * timeStamp вай фая в телефоне меняется через каждые  5 тиков
    * то есть если тиков больше 5, а timeStamp не поменялся, то урон прекращается
    * */
    public Pair<String, Double> getDamage(String wifiType){
        try {
            for (ScanResult result : wifiList) {
                if (result.SSID.equals(wifiType)) {
                    long timeStamp = wifiType.equals(CONTROL_WIFI) ? controlTimestamp : chimeraTimestamp;
                    int counter = wifiType.equals(CONTROL_WIFI) ? controlCounter : chimeraCounter;
                    if (result.timestamp == timeStamp){
                        counter++;
                    } else{
                        counter = 0;
                    }
                    if (wifiType.equals(CONTROL_WIFI)) {
                        controlTimestamp = result.timestamp;
                        controlCounter = counter;
                        type = PSY;
                    } else{
                        chimeraTimestamp = result.timestamp;
                        chimeraCounter = counter;
                        type = BIO;
                    }
                    if (counter < 5){
                        return new Pair<>(type, damage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(OASIS, 0d);
    }
    /*
    * делает звук, если аномалия долбит
    * */
    public boolean makeSound(){
        return (chimeraCounter > 0 && chimeraCounter <5) || (controlCounter > 0 && controlCounter <5);
    }
}
