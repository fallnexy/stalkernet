package com.example.stalkernet;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;

import static com.example.stalkernet.anomaly.Anomaly.BIO;
import static com.example.stalkernet.anomaly.Anomaly.GESTALT;
import static com.example.stalkernet.anomaly.Anomaly.MINE;
import static com.example.stalkernet.anomaly.Anomaly.PSY;
import static com.example.stalkernet.anomaly.Anomaly.RAD;

public class EffectManager {
    public MediaPlayer buzzer;
    private Context context;
    public MediaPlayer mediaplayer;
    public Vibrator vibrator;

    public EffectManager(Context context) {
        this.context = context;
        this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_1);
        this.buzzer = MediaPlayer.create(this.context, R.raw.buzzer);
        this.vibrator = ((Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    public void VibrateInPattern() {
        long[] jArr = new long[]{0, 100, 4000};
        if (Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(1000);
        } else {
            this.vibrator.vibrate(1000);
        }
    }

    public void PlaySound(String type, double power) {
        if (!type.equals(MINE)) {
            mediaplayer.reset();
        }
        if (type.equals(PSY)) {
            switch ((int) power) {
                case 1:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_1);
                    break;
                case 2:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_2);
                    break;
                case 3:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_3);
                    break;
                case 4:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_4);
                    break;
                case 5:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.come_to_me);
                    break;
                case 6:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_6);
                    break;
                case 7:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_7);
                    break;
                case 8:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_8);
                    break;
                case 9:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_9);
                    break;
                case 10:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
                case 11:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
                case 15:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
                case 20:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
                case 51://демиург
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_11);
                    break;
                default:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_1);
                    break;
            }
        }
        if (type.equals(BIO)) {
            switch ((int) power) {
                case 1:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_1);
                    break;
                case 2:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_2);
                    break;
                case 3:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_3);
                    break;
                case 4:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_4);
                    break;
                case 5:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_5);
                    break;
                case 6:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_6);
                    break;
                case 7:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_7);
                    break;
                case 8:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_8);
                    break;
                case 9:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_9);
                    break;
                case 10:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_10);
                    break;
                default:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.bio_1);
                    break;
            }
        }
        if (type.equals(RAD)) {
            switch ((int) power) {
                case 1:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_1);
                    break;
                case 2:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_2);
                    break;
                case 3:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_3);
                    break;
                case 4:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_4);
                    break;
                case 5:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_5);
                    break;
                case 6:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_6);
                    break;
                case 7:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_7);
                    break;
                case 8:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_8);
                    break;
                case 9:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_9);
                    break;
                case 10:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_10);
                    break;
                default:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_1);
                    break;
            }
        }
        if (type.equals(GESTALT)){
            switch ((int) power){
                default:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.gestalt_on);
                    break;
            }
        }
        if (type.equals("Start")){
            switch ((int) power){
                case 1:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.stalker);
                    break;
            }
        }
        if (!type.equals(MINE)) {
            mediaplayer.start();
        }
    }

    public void mineActivated(){
        mediaplayer = MediaPlayer.create(context, R.raw.mine_active);
        mediaplayer.start();
    }
    public void mineDisActivated(){
        mediaplayer = MediaPlayer.create(context, R.raw.mine_disactivated);
        mediaplayer.start();
    }
    public void mineExplosion(){
        mediaplayer = MediaPlayer.create(context, R.raw.mine_explosion);
        mediaplayer.start();
    }

    public void PlayBuzzer() {
        this.buzzer.reset();
        this.buzzer = MediaPlayer.create(this.context, R.raw.buzzer);
        this.buzzer.start();
    }

    public void StopActions() {
        if (this.vibrator != null) {
            this.vibrator.cancel();
        }
    }
    public void stopSound() {
        if (mediaplayer.isPlaying()) {
            mediaplayer.stop();
        }
    }
}
