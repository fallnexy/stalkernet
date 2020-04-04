package com.example.myapplication2;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;

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

    public void PlaySound(String str, double d) {
        int i = (int) d;
        this.mediaplayer.reset();
        if (str.equals("Psy")) {
            switch (i) {
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
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_5);
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
                default:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
            }
        }
        if (str.equals("Bio")) {
            switch (i) {
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
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.psi_10);
                    break;
            }
        }
        if (str.equals("Rad")) {
            switch (i) {
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
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.rad_10);
                    break;
            }
        }
        if (str.equals("Ges")){
            switch (i){
                case 1:
                    this.mediaplayer = MediaPlayer.create(this.context, R.raw.gestalt_on);
            }
        }
        this.mediaplayer.start();
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
}
