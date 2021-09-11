package com.example.myapplication2.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.example.myapplication2.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QRTab extends Fragment implements View.OnClickListener{
    private Globals globals;
    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private Random random = new Random();
    private boolean scienceQR = false;
    private boolean pre_scan = false;
    private Button btnScienceQR;

    public Location current_location = new Location("GPS");

    private long firstTime;
    private long secondTime;
    private long[] cooldown_time;
    private boolean[] compositionOfArts = new boolean[21];
    private Spanned[] composites;
    boolean isScienceQR;

    public QRTab(Globals globals) {
        this.globals = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_qr, viewGroup, false);

        statusMessage = inflate.findViewById(R.id.status_message);
        barcodeValue = inflate.findViewById(R.id.barcode_value);

        autoFocus = inflate.findViewById(R.id.auto_focus);
        useFlash = inflate.findViewById(R.id.use_flash);

        inflate.findViewById(R.id.read_barcode).setOnClickListener(this);
        inflate.findViewById(R.id.pre_read_barcode).setOnClickListener(this);
        btnScienceQR = inflate.findViewById(R.id.btnScienceQR);
        btnScienceQR.setOnClickListener(this);
        if (globals.ScienceQR == 1 /*|| isScienceQR*/) {
            btnScienceQR.setVisibility(View.VISIBLE);
        }
        if (globals.ScienceQR == 0 /*|| !isScienceQR*/){
            btnScienceQR.setVisibility(View.INVISIBLE);
        }
        firstTime = Calendar.getInstance().getTimeInMillis();
        secondTime = 0;
        cooldown_time = new long[25];
        composites = new Spanned[20];
        composites[0] = Html.fromHtml(getString(R.string.composite_detected));
        composites[1] = Html.fromHtml(getString(R.string.composite_continue));
        composites[2] = Html.fromHtml(getString(R.string.composite_canceled));
        composites[3] = Html.fromHtml(getString(R.string.composite_canceled_2));
        composites[4] = Html.fromHtml("getString(R.string.art_Sc_a5t322faqf)");
        composites[5] = Html.fromHtml("getString(R.string.art_Sc_a5t322faqf_2)");
        composites[6] = Html.fromHtml("getString(R.string.art_8nk3owbpzt)");
        composites[7] = Html.fromHtml("getString(R.string.art_86peq6qktl)");
        composites[8] = Html.fromHtml("getString(R.string.art_86peq6qktl_2)");
        composites[9] = Html.fromHtml("getString(R.string.art_zp1ivlcs7e)");
        composites[10] = Html.fromHtml(getString(R.string.art_status_nm7s576l0i));
        composites[11] = Html.fromHtml(getString(R.string.art_status_vz6rafxyei));
        composites[12] = Html.fromHtml(getString(R.string.art_status_jt0dfct2w0));
        composites[13] = Html.fromHtml("getString(R.string.art_status_monolithMech_fail)");
        composites[14] = Html.fromHtml("getString(R.string.art_status_monolithMech_fail_2)");

        LoadBarcodeText();
        return inflate;
    }
/*
* QR ученого
* */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pre_read_barcode) {
            //isScienceQR = false;
            scienceQR = false;
            pre_scan = true;
            // launch barcode activity.
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.read_barcode) {
            //isScienceQR = false;
            scienceQR = false;
            pre_scan = false;
            // launch barcode activity.
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.btnScienceQR){
            //isScienceQR = true;
            scienceQR = true;
            //pre_scan = false;
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }


    /*

   вВООООООООООООООООТ ЗДЕСЬ

   */

    public String bcode;
    public String[] barcodeSplitted = new String[4];
    public void MakeSplit(String input){
        try {
            Pattern pattern = Pattern.compile("[@]");
            String[] words = pattern.split(input);
            int i = 0;
            for(String word:words){
                barcodeSplitted[i] = word;
                i++;
            }
            bcode = barcodeSplitted[3];
        } catch (Exception e) {
            bcode = input;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    Intent intent;
                    intent = new Intent("Command");
                    //MakeSplit(barcode.displayValue);
                    Log.d(TAG, "splitted: " + bcode);
                    bcode = barcode.displayValue;
                    //считывает qr код и в соответствии с case выдает нужный текст
                    switch (bcode){
                        case "наукада":  //включает QR ученого
                            //isScienceQR = true;
                            btnScienceQR.setVisibility(View.VISIBLE);
                            barcodeValue.setText(R.string.scienceQR_on);
                            intent.putExtra("Command", "ScienceQR");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "наука-":  //отключает QR ученого
                           // isScienceQR = false;
                            btnScienceQR.setVisibility(View.INVISIBLE);
                            barcodeValue.setText(R.string.scienceQR_off);
                            intent.putExtra("Command", "ScienceQRoff");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                            // скрыты гештальты
                       /* case "cv7mtd4tm4knk8w":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "5ckxozbw2r4gm5p":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed_2");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xgnn6u3h313kvqg":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed_3");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "5gchows2io8mhg0":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc_2);
                            intent.putExtra("Command", "gestalt_closed_4");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;*/
                        case "safezonef": // сентябрь21 - чистое небо
                            textOnArt(R.string.dischargeImmunity, R.string.dischargeImmunitySc);
                            intent.putExtra("Command", "naemnikiDischargeImmunity");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;

                        case "12543659521":
                            barcodeValue.setText("Защита от радиации на 2 часа поставлена");
                            intent.putExtra("Command", "TwoHoursRadProtection");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "приветбумеранг":
                            barcodeValue.setText("Старт игры");
                            intent.putExtra("Command", "MakeAlive");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "выходигрока":
                            barcodeValue.setText("Установлен иммунитет для выхода в зону респауна");
                            intent.putExtra("Command", "15minutesGod");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "снятьнеуяз":
                            barcodeValue.setText("Точка респауна достигнута, иммунитет снят. Старт игры");
                            intent.putExtra("Command", "noMoreGod");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "явсемогущий+":
                            barcodeValue.setText("Неуязвимость включена");
                            intent.putExtra("Command", "God");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "явсемогущий-":
                            barcodeValue.setText("Неуязвимость выключена");
                            intent.putExtra("Command", "noMoreGod");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "чекинвыброс":
                            barcodeValue.setText("Пользователь защищён от выброса на 10 минут.");
                            intent.putExtra("Command", "discharge10BD");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "зона5звезд":
                            barcodeValue.setText("Иммунитет к выбросу");
                            intent.putExtra("Command", "SetDischargeImmunityTrue");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "чнзнает":
                            barcodeValue.setText("Чистое Небо знает путь");
                            intent.putExtra("Command", "dolgDischargeImmunity");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "aperfjbasj":
                            barcodeValue.setText("Боец Чистого Неба показывает вам безопасный путь к базе");
                            intent.putExtra("Command", "clear4");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                            ///////////////////////////////////////////////////////////////////////////////
                            ///////////////////////////////////////////////////////////////////////////////
                            ////////////////////////////////////////////////////////////////////////////// сентябрь 2020

                        case "z1z1ab6fu0kf3ie": // этот и ещё 4 кодов на жизни
                            barcodeValue.setText("Пользователь при смерти. Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "health5");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "a1a3d8a04e4v97b":
                            barcodeValue.setText("Пользователь в критическом состоянии. Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "health25");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xoiu32t19zp0qew":
                            barcodeValue.setText("Пользователь в стабильном состоянии. Обратитесь за квалифицированной помощью при первой возможности.");
                            intent.putExtra("Command", "health50");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "glykfcose20cc46":
                            barcodeValue.setText("Жизненные показатели в пределах нормы.");
                            intent.putExtra("Command", "health75");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "wr73d9dw8uv1tsi":
                            barcodeValue.setText("Пользователь здоров.");
                            intent.putExtra("Command", "health100");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "012zfy971xlsoez": // далее идут различные защиты
                            barcodeValue.setText("Активирована защита костюма от радиационного воздействия.");
                            intent.putExtra("Command", "SetRadProtection100"); // написано, что 100, но на самом деле 90
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "t5izlre4vhdegt0": // это защита и предыдущая используют код от цифровых ключей
                            barcodeValue.setText("Активирована защита костюма от биологического воздействия.");
                            intent.putExtra("Command", "SetBioProtection100"); // написано, что 100, но на самом деле 90
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "1k35ibpbiinxq38":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от радиационного воздействия: нестабильный.");
                            intent.putExtra("Command", "radProt10030");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "ex2jvbswz1wmgyy":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от радиационного воздействия: умеренный.");
                            intent.putExtra("Command", "radProt10060");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "77gnxcdh115bd32":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от радиационного воздействия: надёжный.");
                            intent.putExtra("Command", "radProt10090");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "burx446yy1c6z3p":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от биологического воздействия: нестабильный.");
                            intent.putExtra("Command", "bioProt10030");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "qev1ngqe58yz70r":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от биологического воздействия: умеренный.");
                            intent.putExtra("Command", "bioProt10060");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "r7j2l73s278ty3l":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от биологического воздействия: надёжный.");
                            intent.putExtra("Command", "bioProt10090");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "k6q8g4dstmtzfau":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от воздействия пси-полей: нестабильный.");
                            intent.putExtra("Command", "psyProt10030");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "3q7nd4zssllogyw":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от воздействия пси-полей: умеренный.");
                            intent.putExtra("Command", "psyProt10060");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "876kpgh006m08tx":
                            barcodeValue.setText("Применён эффект аномального образования. Уровень защиты от воздействия пси-полей: надёжный.");
                            intent.putExtra("Command", "psyProt10090");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "dh22ibvye055lq3":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Устройство зарегистрировано на 10 минут.");
                            intent.putExtra("Command", "discharge10Sc");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "30f23o9miysuvas":
                            barcodeValue.setText("Зафиксировано кратковременное локальное искажение пространства. Пользователь защищён от Выброса на короткое время.");
                            intent.putExtra("Command", "discharge10BD");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;

                        case "i8qpfu02xjuvmzu":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Устройство зарегистрировано на 30 минут.");
                            intent.putExtra("Command", "discharge45");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "rdg9qhqrkffjvz2":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована незначительная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus2Health");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "22d78ptbvz3gxgy":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована слабая стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus5Health");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "em7npa1e96zwgzf":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована умеренная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus10Health");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "pcozme0htw7nn69":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована значительная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus45HealthRandom");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "gxehzewa0pthph1":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована слабая дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus5Health");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "2omtlr5z3bji8mi":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована умеренная дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus10HealthRandom");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "z8fl7dt7zmpryjb":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована значительная дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus21HealthRandom");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "u7bua8tzmdbbmga":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio6025");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "5o1wzi0b6ebokd9":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio6035");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "ji282s9rfofumsr":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad6025");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "kab6b1efzvg4sdx":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad6035");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "mpv8gnpr8r8oe2p":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от пси-полей.");
                            intent.putExtra("Command", "BDprotectionPsy6025");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "odu0yajypyl2cng":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio120");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "lapursrlu0yv10k":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad120");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "0deupx1zzl0yyxr":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от пси-полей.");
                            intent.putExtra("Command", "BDprotectionPsy120");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "sz0api94eaisrsf":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружена локальная акселерация регенерации тканей и внутренних органов пользователя. Проверьте состояние здоровья и обратитесь за квалифицированной помощью при первой возможности.");
                            return;
                        case "pcy8x7v42hdag6f":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено дегенеративное воздействие на организм пользователя. Проверьте состояние здоровья и обратитесь за квалифицированной помощью при первой возможности.");
                            return;
                        case "8wz78zbb4wfsagu":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксировано кратковременное локальное искажение пространства. Зафиксировано неизвестное воздействие на организм пользователя. Проверьте состояние здоровья и обратитесь за квалифицированной помощью при первой возможности.");
                            return;
                        case "d8pzzgr8lru47z8":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Внимание! Критический уровень облучения! Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "setRadOn80Percent");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "cat0u5axnlsezs2":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Внимание! Химический ожог! Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "setBioOn80Percent");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "h29fthyiij":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[3] > 43200000) {
                                barcodeValue.setText("Механизм применён. Выведение радиационного воздействия из организма.");
                                intent.putExtra("Command", "mechMinus60Rad");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[3] = firstTime;
                            } else {
                                barcodeValue.setText("Повторное применение в краткой срок опасно для здоровья. Функционал заблокирован.");
                            }

                            return;
                        case "2lvrbe1qkm":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[4] > 43200000) {
                                barcodeValue.setText("Механизм применён. Выведение биологического воздействия из организма.");
                                intent.putExtra("Command", "mechMinus60Bio");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[4] = firstTime;
                            } else {
                                barcodeValue.setText("Повторное применение в краткой срок опасно для здоровья. Функционал заблокирован.");
                            }
                            return;
                        case "f5dm9wn7wr":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[5] > 43200000) {
                                barcodeValue.setText("Механизм применён. Жизненные показатели пользователя стабилизированы.");
                                intent.putExtra("Command", "mechPlus70Health");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[5] = firstTime;
                            } else {
                                barcodeValue.setText("Повторное применение в краткой срок опасно для здоровья. Функционал заблокирован.");
                            }
                            return;
                        case "nuyzi2sg7y3vq5f":
                            barcodeValue.setText("Старт игры");
                            intent.putExtra("Command", "ResetStats");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            Arrays.fill(cooldown_time, 0);
                            return;
                        case "ovg1m1ngxzp15ti":
                            barcodeValue.setText("Нейтрализация радиационного воздействия. Показатели пользователя в пределах нормы.");
                            intent.putExtra("Command", "setRad0");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "2lfzs5u7idb1yvl":
                            barcodeValue.setText("Нейтрализация биологического воздействия. Состояние пользователя удовлетворительно, рекомендован дополнительный курс лечения.");
                            intent.putExtra("Command", "setBio15");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xfy3srgs36tjwlu":
                            barcodeValue.setText("Нейтрализация биологического воздействия. Показатели пользователя в пределах нормы..");
                            intent.putExtra("Command", "setBio0");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        // тут и далее композитные артосы
                       /* case "8xxv2bxw26":
                            textOnArt (R.string.saveArt, R.string.art_8xxv2bxw26);
                            statusMessage.setText(composites[0]);
                            Arrays.fill(compositionOfArts, false);
                            cooldown_time[6] = Calendar.getInstance().getTimeInMillis();
                            for (int i = 0; i < compositionOfArts.length; i += 3) {
                                compositionOfArts[i] = true;
                            }
                            return;
                        case "a5t322faqf":
                            textOnArt (R.string.saveArt, R.string.art_a5t322faqf);
                            if ((compositionOfArts[7] & compositionOfArts[6]) & compositeTimeCheck()){
                                compositeFinalPart (7, composites[4], "minus15Rad");
                            } else if (compositionOfArts[0] & compositeTimeCheck()){
                                compositeArt(0, 1);
                            }else{
                                compositeFails();
                            }
                            return;
                        case "qe56n9tvvl":
                            textOnArt (R.string.saveArt, R.string.art_a5t322faqf);
                            if ((compositionOfArts[13] & compositionOfArts[12]) & compositeTimeCheck()){
                            compositeFinalPart (8, composites[5], "ifLess50healthSet70RadProt");
                            }else{
                                compositeFails();
                            }
                            return;
                        case "8nk3owbpzt":
                            textOnArt (R.string.saveArt, R.string.art_Sc_8nk3owbpzt);
                            if ((compositionOfArts[0] & compositionOfArts[1]) & compositeTimeCheck()) {
                                compositeFinalPart (9, composites[6], "plus10Rad");
                            } else if (compositionOfArts[6] & compositeTimeCheck()){
                                compositeArt(6, 7);
                            }else{
                                compositeFails();
                            }
                            return;
                        case "86peq6qktl":
                            textOnArt (R.string.saveArt, R.string.art_Sc_86peq6qktl);
                            if (compositionOfArts[3] & compositeTimeCheck()){
                                compositeArt(3, 4);
                            }else if ((compositionOfArts[9] & compositionOfArts[10]) & compositeTimeCheck()){
                                compositeFinalPart (10, composites[7], "minus15Bio");
                            }else{
                                compositeFails();
                            }
                            return;
                        case "wq4qc5pbr4":
                            textOnArt (R.string.saveArt, R.string.art_Sc_wq4qc5pbr4);
                            if ((compositionOfArts[15] & compositionOfArts[16]) & compositeTimeCheck()){
                                compositeFinalPart (11, composites[8], "ifLess50healthSet70BioProt");
                            }else{
                                compositeFails();
                            }
                            return;
                        case "zp1ivlcs7e":
                            textOnArt (R.string.saveArt, R.string.art_Sc_zp1ivlcs7e);
                            if ((compositionOfArts[3] & compositionOfArts[4]) & compositeTimeCheck()){
                                compositeFinalPart (12, composites[9], "plus10Bio");
                            }else if (compositionOfArts[9] & compositeTimeCheck()){
                                compositeArt(9, 10);
                            }else{
                                compositeFails();
                            }
                            return;
                        case "i6ynzi1r78":
                            textOnArt (R.string.saveArt, R.string.art_Sc_i6ynzi1r78);
                            if (compositionOfArts[12] & compositeTimeCheck()){
                                statusMessage.setText(composites[1]);
                                Arrays.fill(compositionOfArts, false);
                                compositionOfArts[12] = true;
                                compositionOfArts[13] = true;
                                compositionOfArts[15] = true;
                                compositionOfArts[16] = true;
                            }else {
                                compositeFails();
                            }
                            return;
                        case "phryprgtdu":
                            textOnArt (R.string.saveArt, R.string.art_Sc_phryprgtdu);
                            if (compositionOfArts[18] & compositeTimeCheck()){
                                compositeArt(18, 19);
                            }else{
                                compositeFails();
                            }
                            return;
                        case "3z5awliwar":
                            textOnArt (R.string.saveArt, R.string.art_Sc_3z5awliwar);
                            if ((compositionOfArts[18] & compositionOfArts[19]) & compositeTimeCheck()) {
                                if (compositeTimeCheck_2(13)) {
                                    barcodeValue.setText("Зафиксировано кратковременное локальное искажение пространства. ОШИБКА: Сбой оборудования. Проверьте состояние пользователя для фиксации эффекта.");
                                    intent.putExtra("Command", "ifLess50healthPlus25Health");
                                    Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                    Arrays.fill(compositionOfArts, false);
                                    cooldown_time[13] = Calendar.getInstance().getTimeInMillis();
                                } else {
                                    barcodeValue.setText(R.string.composite_cooldown);
                                    Arrays.fill(compositionOfArts, false);
                                }
                            }else {
                                compositeFails();
                            }
                            return;*/

                        case "mezfB2Jn7H8n2JP":
                            barcodeValue.setText(R.string.art_mezfB2Jn7H8n2JP);
                            return;

                            /*
                            2021 год
                            */

                        // чтец ноосферы
                        case "n888x6powrkmojj":
                            int str = R.string.quest_21_02sc;
                            Location point_location = new Location("GPS");
                            point_location.setLatitude(64.35084939443712d);
                            point_location.setLongitude(40.7199666393824d);
                            double distanceToPoint = point_location.distanceTo(globals.location);
                            if(distanceToPoint <= 40){
                                //str = R.string.item_0521_08s_1;
                                textOnArt(R.string.quest_21_02_01, str);
                                return;
                            }
                            Location point_location_1 = new Location("GPS");
                            point_location_1.setLatitude(64.35304402555298d);
                            point_location_1.setLongitude(40.73663992875095d);
                            double distanceToPoint_1 = point_location_1.distanceTo(globals.location);
                            if (distanceToPoint_1 <= 20){
                                //str = R.string.item_0521_08s_2;
                                textOnArt(R.string.quest_21_02_02, str);
                                return;
                            }
                            Location point_location_2 = new Location("GPS");
                            point_location_2.setLatitude(64.35248480210454d);
                            point_location_2.setLongitude(40.7318765714623d);
                            double distanceToPoint_2 = point_location_2.distanceTo(globals.location);
                            if (distanceToPoint_2 <= 20){
                                //str = R.string.item_0521_08s_3;
                                textOnArt(R.string.quest_21_02_03, str);
                                return;
                            }
                            Location point_location_3 = new Location("GPS");
                            point_location_3.setLatitude(64.35215333997971d);
                            point_location_3.setLongitude(40.730394103138664d);
                            double distanceToPoint_3 = point_location_3.distanceTo(globals.location);
                            if (distanceToPoint_3 <= 30){
                                //str = R.string.item_0521_08s_4;
                                textOnArt(R.string.quest_21_02_04, str);
                                return;
                            }
                            Location point_location_4 = new Location("GPS");
                            point_location_4.setLatitude(64.35205497292027d);
                            point_location_4.setLongitude(40.737743477046465d);
                            double distanceToPoint_4 = point_location_4.distanceTo(globals.location);
                            if (distanceToPoint_4 <= 66){
                                //str = R.string.item_0521_08s_5;
                                textOnArt(R.string.quest_21_02_05, str);
                                return;
                            }
                            Location point_location_5 = new Location("GPS");
                            point_location_5.setLatitude(64.35266067713242d);
                            point_location_5.setLongitude(40.73464018333524d);
                            double distanceToPoint_5 = point_location_5.distanceTo(globals.location);
                            if (distanceToPoint_5 <= 30){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_06, str);
                                return;
                            }
                            Location point_location_6 = new Location("GPS");
                            point_location_6.setLatitude(64.35155365136347d);
                            point_location_6.setLongitude(40.72763529934451d);
                            double distanceToPoint_6 = point_location_6.distanceTo(globals.location);
                            if (distanceToPoint_6 <= 15){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_07, str);
                                return;
                            }
                            Location point_location_7 = new Location("GPS");
                            point_location_7.setLatitude(64.34994500125134d);
                            point_location_7.setLongitude(40.726044220119505d);
                            double distanceToPoint_7 = point_location_7.distanceTo(globals.location);
                            if (distanceToPoint_7 <= 30){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_08, str);
                                return;
                            }
                            Location point_location_8 = new Location("GPS");
                            point_location_8.setLatitude(64.35109295085896d);
                            point_location_8.setLongitude(40.73626007320802d);
                            double distanceToPoint_8 = point_location_8.distanceTo(globals.location);
                            if (distanceToPoint_8 <= 30){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_09, str);
                                return;
                            }
                            Location point_location_9 = new Location("GPS");
                            point_location_9.setLatitude(64.35222932765502d);
                            point_location_9.setLongitude(40.72776883876071d);
                            double distanceToPoint_9 = point_location_9.distanceTo(globals.location);
                            if (distanceToPoint_9 <= 20){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_10, str);
                                return;
                            }
                            Location point_location_10 = new Location("GPS");
                            point_location_10.setLatitude(64.35715845524946d);
                            point_location_10.setLongitude(40.721214319321d);
                            double distanceToPoint_10 = point_location_10.distanceTo(globals.location);
                            if (distanceToPoint_10 <= 30){//64.532707, 40.155037
                                //str = R.string.item_0521_08s_6;
                                textOnArt(R.string.quest_21_02_11, str);
                                return;
                            }
                            //str = R.string.item_0521_08s_7;
                            textOnArt(R.string.quest_21_02_0, str);
                            return;
                        case "olkkihlsyf":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_1, R.string.quest_21_06sc, 20d);
                            return;
                        case "qirhtruhoc":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_2, R.string.quest_21_06sc, 20d);
                            return;
                        case "zvkwridurj":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_3, R.string.quest_21_06sc, 20d);
                            return;
                        case "enogktjwkl":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_4, R.string.quest_21_06sc, 20d);
                            return;
                        case "gcedsruvlp":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_5, R.string.quest_21_06sc, 20d);
                            return;
                        case "nzwgnqmzpc":
                            simpleLocationDepend(64.35714851769004d, 40.72133372676017d, R.string.quest_21_06out, R.string.quest_21_06_6, R.string.quest_21_06sc, 20d);
                            return;
                            // Этот и следующие два: доступны одна, две, три защиты
                        case "разреш1тип":
                            barcodeValue.setText("Установлено разрешение на защиту от 1 типа урона одновременно");
                            intent.putExtra("Command", "setOneProtAv");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "разреш2тип":
                            barcodeValue.setText("Установлено разрешение на защиту от 2 типов урона одновременно");
                            intent.putExtra("Command", "setTwoProtAv");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "разреш3тип":
                            barcodeValue.setText("Установлено разрешение на защиту от 3 типов урона одновременно");
                            intent.putExtra("Command", "setThreeProtAv");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                            //здесь и далее Глеб
                        case "radiation":
                            textAndCoolDawn(intent, 0, 2400000, R.string.glebRad0,R.string.glebRadsc, R.string.glebRadDawn, "radiation");
                            return;
                        case "radiation1":
                            textAndCoolDawn(intent, 1, 2400000, R.string.glebRad1,R.string.glebRadsc, R.string.glebRadDawn, "radiation1");
                            return;
                        case "radiation2":
                            textAndCoolDawn(intent, 2, 2400000, R.string.glebRad2,R.string.glebRadsc, R.string.glebRadDawn, "radiation2");
                            return;
                        case "radiation3":
                            textAndCoolDawn(intent, 3, 2400000, R.string.glebRad3,R.string.glebRadsc, R.string.glebRadDawn, "radiation3");
                            return;
                        case "biohazard":
                            textAndCoolDawn(intent, 4, 2400000, R.string.glebBio0,R.string.glebRadsc, R.string.glebRadDawn, "biohazard");
                            return;
                        case "biohazard1":
                            textAndCoolDawn(intent, 5, 2400000, R.string.glebBio1,R.string.glebRadsc, R.string.glebRadDawn, "biohazard1");
                            return;
                        case "biohazard2":
                            textAndCoolDawn(intent, 6, 2400000, R.string.glebBio2,R.string.glebRadsc, R.string.glebRadDawn, "biohazard2");
                            return;
                        case "biohazard3":
                            textAndCoolDawn(intent, 7, 2400000, R.string.glebBio3,R.string.glebRadsc, R.string.glebRadDawn, "biohazard3");
                            return;
                        case "health":
                            textAndCoolDawn(intent, 8, 2400000, R.string.glebHP0,R.string.glebRadsc, R.string.glebRadDawn, "health");
                            return;
                        case "health1":
                            textAndCoolDawn(intent, 9, 2400000, R.string.glebHP1,R.string.glebRadsc, R.string.glebRadDawn, "health1");
                            return;
                        case "health2":
                            textAndCoolDawn(intent, 10, 2400000, R.string.glebHP2,R.string.glebRadsc, R.string.glebRadDawn, "health2");
                            return;
                        case "health3":
                            textAndCoolDawn(intent, 11, 2400000, R.string.glebHP3,R.string.glebRadsc, R.string.glebRadDawn, "health3");
                            return;
                        case "pngxtfgtdo":
                            textOnArt(R.string.item_21_01, R.string.item_21_01sc);
                            return;
                        case "vpqntomoro":
                            textOnArt(R.string.item_21_02, R.string.item_21_02sc);
                            return;
                        case "yabfencbeh":
                            textOnArt(R.string.item_21_03, R.string.item_21_03sc);
                            return;
                        case "wxvbbxvpqp":
                            textOnArt(R.string.item_21_04, R.string.item_21_04sc);
                            return;
                        case "fbhlgqbwty":
                            textOnArt(R.string.item_21_05, R.string.item_21_05sc);
                            return;
                        case "xvbghtzjmc":
                            textOnArt(R.string.item_21_06, R.string.item_21_06sc);
                            return;
                        case "nlzaxlghou":
                            textOnArt(R.string.item_21_07, R.string.item_21_07sc);
                            return;
                        case "zlyopchbci":
                            textOnArt(R.string.item_21_08, R.string.item_21_08sc);
                            return;
                        case "qfbenyqbls":
                            textOnArt(R.string.item_21_09, R.string.item_21_09sc);
                            return;
                        case "cycmkdenil":
                            textOnArt(R.string.item_21_10, R.string.item_21_10sc);
                            return;
                        case "lhktfjrqju":
                            textOnArt(R.string.item_21_11, R.string.item_21_11sc);
                            return;
                        case "spbptowvss":
                            textOnArt(R.string.item_21_12, R.string.item_21_12sc);
                            return;
                        case "csvdavjshl":
                            textOnArt(R.string.item_21_13, R.string.item_21_13sc);
                            return;
                        case "pcmbkoehgq":
                            textOnArt(R.string.item_21_14, R.string.item_21_14sc);
                            return;
                        case "mpjvqlzkws": // этот и два следующих - шприцы от рад, био и хп
                            textAndCoolDawn(intent, 12, 900000, R.string.injectorRad,R.string.injectorRadSc, R.string.injectorRadDawn, "injectorRad");
                            return;
                        case "xrjoqykant":
                            textAndCoolDawn(intent, 13, 960000, R.string.injectorBio,R.string.injectorBioSc, R.string.injectorBioDawn, "injectorBio");
                            return;
                        case "pjiscyunaf":
                            textAndCoolDawn(intent, 14, 1020000, R.string.injectorHP,R.string.injectorHPsc, R.string.injectorHPdawn, "injectorHP");
                            return;
                        case "yzvdzfbesq": // здесь и далее артосы 2021
                            textOnArt(R.string.art_21_norm, R.string.art_21_01sc);
                            return;
                        case "kghmzmtrfh":
                            textOnArt(R.string.art_21_norm, R.string.art_21_02sc);
                            return;
                        case "lbbbzgutsc":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 15, 300000, R.string.art_21_dang, R.string.art_21_03sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "ejrzgsbynq":
                            textOnArt(R.string.art_21_norm, R.string.art_21_04sc);
                            return;
                        case "suqxdukcpx":
                            textOnArt(R.string.art_21_norm, R.string.art_21_05sc);
                            return;
                        case "juoqudtxgc":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 16, 300000, R.string.art_21_dang, R.string.art_21_06sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "jujztkmtay":
                            textOnArt(R.string.art_21_norm, R.string.art_21_07sc);
                            return;
                        case "xsqebiqemi":
                            textOnArt(R.string.art_21_norm, R.string.art_21_08sc);
                            return;
                        case "opdcplctlz":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 17, 300000, R.string.art_21_dang, R.string.art_21_09sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "fewbuvfgin":
                            textOnArt(R.string.art_21_norm, R.string.art_21_10sc);
                            return;
                        case "ktrewhbuhy":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 18, 300000, R.string.art_21_dang, R.string.art_21_11sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "ntpoqmdtsp":
                            textAndCoolDawn(intent, 19, 600000, R.string.art_21_12, R.string.art_21_12sc, R.string.art_21_12_dawn_compas, "artCompass");
                            return;
                        case "kwsiajfcik":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 20, 300000, R.string.art_21_dang, R.string.art_21_13sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "duytiylzfg":
                            textOnArt(R.string.art_21_norm, R.string.art_21_14sc);
                            return;
                        case "hjplmdsekj":
                            textOnArt(R.string.art_21_norm, R.string.art_21_15sc);
                            return;
                        case "wnlaygkgex":
                            textOnArt(R.string.art_21_norm, R.string.art_21_16sc);
                            return;
                        case "doitupwwvc":
                            textOnArt(R.string.art_21_norm, R.string.art_21_17sc);
                            return;
                        case "mzfcfvscco":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 21, 300000, R.string.art_21_dang, R.string.art_21_18sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "bmwnngcjhq":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 22, 300000, R.string.art_21_dang, R.string.art_21_19sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "yharsxnqll":
                            textOnArt(R.string.art_21_norm, R.string.art_21_20sc);
                            return;
                        case "zzoxwlkyzp":
                            textOnArt(R.string.art_21_norm, R.string.art_21_21sc);
                            return;
                        case "maaerpdbrz":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 23, 300000, R.string.art_21_dang, R.string.art_21_22sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "theleeyyrw":
                            textOnArt(R.string.art_21_norm, R.string.art_21_23sc);
                            return;
                        case "cqrdpwrdpr":
                            textOnArt(R.string.art_21_norm, R.string.art_21_24sc);
                            return;
                        case "dxpdhslptg":
                            textOnArt(R.string.art_21_norm, R.string.art_21_25sc);
                            return;
                        case "uwnxmyrzew":
                            textOnArt(R.string.art_21_norm, R.string.art_21_26sc);
                            return;
                        case "tkeqcqlvlu":
                            textOnArt(R.string.art_21_norm, R.string.art_21_27sc);
                            return;
                        case "pjvmppohse":
                            stalkerRoulette();
                            textAndCoolDawn(intent, 24, 300000, R.string.art_21_dang, R.string.art_21_28sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "gbmiavcnwe":
                            textOnArt(R.string.quest_21_01, R.string.quest_21_01sc);
                            return;
                        case "hwhfkrpois":
                            textOnArt(R.string.quest_21_05, R.string.quest_21_05sc);
                            return;
                        case "y1tb4a41ax5bx2f":
                            textOnArt(R.string.quest_21_12, R.string.empty_string);
                            return;
                        case "ohcgmehbyb":
                            textOnArt(R.string.quest_21_13, R.string.quest_21_13sc);
                            return;
                        case "igtdaixicb":
                            barcodeValue.setText(R.string.quest_21_14);
                            return;
                        case "lnbhxllvvt":
                            barcodeValue.setText(R.string.quest_21_15);
                            return;
                        case "samnwsyuxx":
                            barcodeValue.setText(R.string.quest_21_16);
                            return;
                        case "swygbgulnj":
                            barcodeValue.setText(R.string.quest_21_17);
                            return;
                        case "cpehrpgzsi":
                            barcodeValue.setText(R.string.quest_21_18);
                            return;
                        case "rrescqknte":
                            barcodeValue.setText(R.string.quest_21_19);
                            return;
                        case "yctwpgxnnq":
                            barcodeValue.setText(R.string.quest_21_20);
                            return;
                        case "elfjpnbrhb":
                            barcodeValue.setText(R.string.quest_21_21);
                            return;
                        case "zwtcojlmsr":
                            barcodeValue.setText(R.string.quest_21_22);
                            return;
                        case "xmzbqdtpps":
                            barcodeValue.setText(R.string.quest_21_23);
                            return;
                        case "eebpfgdiqw":
                            barcodeValue.setText(R.string.quest_21_24);
                            return;
                        case "coslhafgne":
                            barcodeValue.setText(R.string.quest_21_25);
                            return;
                        case "gktdfyrrda":
                            barcodeValue.setText(R.string.quest_21_26);
                            return;
                        case "amkstawjtg":
                            barcodeValue.setText(R.string.quest_21_27);
                            return;
                        case "zyrbhecbvs":
                            barcodeValue.setText(R.string.quest_21_28);
                            return;
                        case "uhgjtwdygz":
                            textOnArt(R.string.quest_21_29, R.string.quest_21_29sc);
                            return;
                        case "yswhfypvtg":
                            barcodeValue.setText(R.string.quest_21_30);
                            return;
                        case "psytwdicgo":
                            barcodeValue.setText(R.string.quest_21_31);
                            return;
                        case "wuwapfrfci":
                            barcodeValue.setText(R.string.quest_21_32);
                            return;
                        case "isfjzmeadf":
                            barcodeValue.setText(R.string.quest_21_33);
                            return;
                        case "ltrxtkpapb":
                            barcodeValue.setText(R.string.quest_21_34);
                            return;
                        case "sihroxooxg":
                            barcodeValue.setText(R.string.quest_21_35);
                            return;
                        case "wciotomxpk":
                            barcodeValue.setText(R.string.quest_21_36);
                            return;
                        case "rnlbvlfaqj":
                            barcodeValue.setText(R.string.quest_21_37);
                            return;
                        case "hfcqafirdj":
                            barcodeValue.setText(R.string.quest_21_38);
                            return;
                        case "hjravnikxn":
                            textOnArt(R.string.quest_21_39, R.string.quest_21_39sc);
                            return;
                        case "eukebgdxuz":
                            textOnArt(R.string.quest_21_40, R.string.quest_21_40sc);
                            return;
                        case "tfvajqrxvh":
                            barcodeValue.setText(R.string.quest_21_41);
                            return;
                        case "pryynkqqsm":
                            barcodeValue.setText(R.string.quest_21_42);
                            return;
                        case "kwxtyregob":
                            barcodeValue.setText(R.string.quest_21_43);
                            return;
                        case "rbcldsgiyu":
                            barcodeValue.setText(R.string.quest_21_44);
                            return;
                        case "csyvxiwfmp":
                            barcodeValue.setText(R.string.quest_21_45);
                            return;
                        case "pxgqbanfuz":
                            textOnArt(R.string.quest_21_46, R.string.quest_21_46sc);
                            return;
                        case "ixdpwulpic": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_04);
                            intent.putExtra("Command", "sc1, bio, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "vaxhhgwbob": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_05);
                            intent.putExtra("Command", "sc1, rad, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "fgmkzxrddw": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_06);
                            intent.putExtra("Command", "sc1, bio, suit, 50");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "mtqlvkqorz": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_07);
                            intent.putExtra("Command", "sc1, bio, suit, 25");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "rqxdohhlcs": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_08);
                            intent.putExtra("Command", "sc1, rad, suit, 25");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "bzqglooxoc": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_09);
                            intent.putExtra("Command", "sc1, bio, suit, 50");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "kpqufqpwae": // здесь и далее установка защит
                            barcodeValue.setText(R.string.protection_21_10);
                            intent.putExtra("Command", "sc1, rad, suit, 50");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "коссева": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм СЕВА (80% БИО)");
                            intent.putExtra("Command", "sc1, bio, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косзаря": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм ЗАРЯ (80% РАД)");
                            intent.putExtra("Command", "sc1, rad, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косстраж": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм СТРАЖ СВОБОДЫ (50% БИО)");
                            intent.putExtra("Command", "sc1, bio, suit, 50");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "стражснять": // здесь и далее установка защит
                            barcodeValue.setText("Костюм СТРАЖ СВОБОДЫ снят");
                            intent.putExtra("Command", "sc1, bio, suit, 0");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косучен": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм ЭЗС Драговича М0.1");
                            intent.putExtra("Command", "sc1, bio, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            intent.putExtra("Command", "sc1, rad, suit, 80");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "штраф":
                            barcodeValue.setText("Применена штрафная санкция.");
                            intent.putExtra("Command", "штраф");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;

                        //////////////////////////////////////////////////////////////////////////////
                            //////////////////////////////////////////////////////////////////////////////
                            //////////////////////////////////////////////////////////////////////////////
                        case "a":
                            Spanned str2 = Html.fromHtml("<font color=\"red\">Привет.</font> <font color=\"yellow\">как </font> <font color=\"blue\">дела?</font>");
                            barcodeValue.setText(str2);
                            return;
                        case "a1":
                            if (scienceQR) {
                                barcodeValue.setText("сталкер, для тебя есть квест");
                            } else {
                                barcodeValue.setText("Автономный Источник Питания.");
                            }
                            return;
                        case "abba":
                            stalkerRoulette();
                            return;
                        case "a2":
                            check_point(new LatLng(64.429695d, 40.716239d), 10d, "ты пришел в нужную точку", "тут ничего нет");
                            return;
                        default:
                            barcodeValue.setText("иди своей дорогой, сталкер");
                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    String str = "gloriamonolithhaereticorummors"; //"Соня не убивала" -1022716346     // "health5" 795560281       // "BDplus2Health"    -16716590  // "dolgDischargeImmunity"  -1449685624 // gestalt_closed_3   1910275381
                    String str2 = "наукада"; //"ScienceQR" -1555514523    // "health25" -1107435105     // "BDplus5Health"   -1649172843  // "dolgDischargeImmunity"  1259972122      // gestalt_closed_4   1910275382
                    String str3 = "наука-"; //"ScienceQRoff" -1930888214 //  "health50" -1107435017    // "BDplus10Health"    1381804599  // "mechMinus60Rad"  -1658045336             // monolithStrong   1989494219
                    String str4 = "гагры"; //"G" 71                  // "health75"  -1107434950       // "BDplus45HealthRandom"  1036792636  //  "mechMinus60Bio"  -1658060453        // monolithWeak  1749658540
                    String str5 = "штраф"; // "gestalt_closed" 1704779201     // "health100"  29249045         // "BDminus5Health"  -944954941  // "mechPlus70Health"  -232827188      // monolith_blessing -63138094
                    String str6 = "200";             // "gestalt_closed_2" 1910275380   // "radProtection100" 1293683299  // "BDminus10HealthRandom"  804709100     // "setRad0"  1984920125   // plus10RadProtection  852949013
                    String str7 = "300";               // "всегдазакрыт" -1925203169       // "radProt10030"  -1800724366   // "BDminus21HealthRandom"  5747468       // "setBio15"  1388454378  //  plus10BioProtection  -301504184
                                              //  "SetGesProtection" 317294316     // "radProt10060" -1800724273   // "BDprotectionBio6025"  1323666026      // "setBio0"  1984451498
                                              //  "теперьоткрыт" 1974805046        // "radProt10090" -1800724180   // "BDprotectionBio6035"  1323666057  // "minus15Rad"  1784296673
                                              //  "SetGesProtectionOFF" -707972381  // "bioProt10030" 1071529183   // "BDprotectionRad6025"  -1895336201  // "ifLess50healthSet70RadProt"  1265750414
                                              // "TwoHoursRadProtection" 1543390539 // "bioProt10060" 1071529276   // "BDprotectionRad6035"  -1895336170  //  "plus10Rad"  -1523616740
                                              // "15minutesGod" -1151237055         // "bioProt10090" 1071529369   // "BDprotectionPsy6025"  1159342392  //  "plus10Bio"  -1523631857
                                              // "minus50Rad" 1787841802            // "psyProt10030" 123907793    // "BDprotectionBio120"  735430818   // "ifLess50healthSet70BioProt"  189785345
                                              // "minus50Bio" 1787826685            // "psyProt10060" 123907886    // "BDprotectionRad120"  1185781365  // "minus15Bio"  1784281556
                                              // "minus25Rad" 1785220194            // "psyProt10090" 123907979    // "BDprotectionPsy120"  1145772052  // "ifLess50healthPlus25Health"  -1576308282
                                              // "minus25Bio" 1785205077            // discharge10Sc  -1975691119  // "setRadOn80Percent"   -1167097637  // "anomalyFreedomOn"   551741458
                                              // "plus40Health" -201032814          // discharge10BD   -1975691677  // "setBioOn80Percent"  1699558920  // "anomalyFreedomOff"  -75884132
                                              // "plus20Health" 608313812           // discharge45    1271685827    // "discharge10OA"   -1975691277    // "art_oasis" 283987183
                                              // irfD5rXx 607639868  3lk98Q5H -1127128685 LxPfas9O 823391914
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null " + str.hashCode() + "  " + str2.hashCode() + "  " + str3.hashCode() + "  " + str4.hashCode() + "  " + str5.hashCode() + "  " + str6.hashCode() + "  " + str7.hashCode());
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // отправляет результат рулетки
    private void stalkerRouletteSolved (String command, String text){
        Intent intent;
        intent = new Intent("StRoulette");
        intent.putExtra("StRoulette", command);
        statusMessage.setText(text);
        Objects.requireNonNull(getActivity()).getApplicationContext().sendBroadcast(intent);
    }
// знчения в рулекте увеличены
    private void stalkerRoulette(){
        int randomStalkerRoulette = random.nextInt(8);
        switch(randomStalkerRoulette) {
            case 0:
                stalkerRouletteSolved ("RadPlusOne", "+50 rad");
                break;
            case 1:
                stalkerRouletteSolved ("BioPlusOne", "+50 bio");
                break;
            case 2:
                stalkerRouletteSolved ("PsyPlusOne", "+50 psy");
                break;
            case 3:
                stalkerRouletteSolved ("HpPlusFive", "+100 hp");
                break;
            case 4:
                stalkerRouletteSolved ("HpPlusSeven", "+200 hp");
                break;
            case 5:
                stalkerRouletteSolved ("HpMinus25perCent", "-25% hp");
                break;
            case 6:
                stalkerRouletteSolved ("HpMinus20perCent", "-20% hp");
                break;
            case 7:
                stalkerRouletteSolved ("HpMinus10perCent", "-10% hp");
                break;
        }

    }

    //текст в зависимости от местоположения
    public void simpleLocationDepend(double lat, double lng, int outLoc, int inLoc, int sc, double radius){
        Location location = new Location("GPS");
        location.setLatitude(lat);
        location.setLongitude(lng);
        double distanceToPoint = location.distanceTo(globals.location);
        if(distanceToPoint <= radius){
            textOnArt(inLoc, sc);
        } else {
          textOnArt(outLoc, sc);
        }
    }
    // композитные артефакты
    private boolean compositeTimeCheck(){
       return ((Calendar.getInstance().getTimeInMillis() - cooldown_time[6]) < 180000);
    }
    private boolean compositeTimeCheck_2(int i){
        return ((Calendar.getInstance().getTimeInMillis() - cooldown_time[i]) > 28800000);
    }
    private void compositeArt(int firstArt, int secondArt){
        statusMessage.setText(composites[1]);
        Arrays.fill(compositionOfArts, false);
        compositionOfArts[firstArt] = true;
        compositionOfArts[secondArt] = true;
    }

    private void textOnArt(int nonScience, int science) {
        textOnArt(nonScience, science, R.string.empty_string);
    }

    private void textOnArt (int nonScience, int science, int pre_scanning){
       if (pre_scan){
           barcodeValue.setText(pre_scanning);
       } else if(scienceQR) {
           barcodeValue.setText(science);
       } else {
           barcodeValue.setText(nonScience);
       }
    }

    private void textAndCoolDawn(Intent intent, int coolDawnNumber, int coolDawnMillis, int nonScience, int science, int txtCoolDawn, String command){
        if (scienceQR){
            barcodeValue.setText(science);
        } else {
            firstTime = Calendar.getInstance().getTimeInMillis();
            if (firstTime - cooldown_time[coolDawnNumber] > coolDawnMillis) {
                barcodeValue.setText(nonScience);
                intent.putExtra("Command", command);
                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                cooldown_time[coolDawnNumber] = firstTime;
            } else {
                barcodeValue.setText(txtCoolDawn);
            }
        }

    }


    private void compositeFinalPart (int checkCooldown, Spanned message, String barcode){
        if (compositeTimeCheck_2(checkCooldown)) {
            Intent intent;
            intent = new Intent("Command");
            statusMessage.setText(message);
            intent.putExtra("Command", barcode);
            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
            Arrays.fill(compositionOfArts, false);
            cooldown_time[checkCooldown] = Calendar.getInstance().getTimeInMillis();
        } else {
            barcodeValue.setText(R.string.composite_cooldown);
            Arrays.fill(compositionOfArts, false);
        }
    }
    private void compositeFails(){
        if (compositionOfArts[0]) {
            statusMessage.setText(composites[2]);
        } else {
            statusMessage.setText(composites[3]);
        }
        Arrays.fill(compositionOfArts, false);
    }
    // Расстояние до точки
    private void check_point(LatLng latLng, Double radius, String in_massage, String out_massage){
        Location point_location = new Location("");
        point_location.setLatitude(latLng.latitude);
        point_location.setLongitude(latLng.longitude);
        double distanceToPoint = point_location.distanceTo(globals.location);
        if(distanceToPoint <= radius){
            barcodeValue.setText(in_massage);
        } else {
            barcodeValue.setText(out_massage);
        }
    }


// сохраняет текст от последнего отсканированного qr
    public void SaveBarcodeText() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        edit.putString("isScienceQR", String.valueOf(isScienceQR));
        edit.putString("BarcodeValue", String.valueOf(barcodeValue.getText()));
        edit.putString("SecondTime", String.valueOf(secondTime)); //майский тест
        edit.putString("Cooldown_0", String.valueOf(cooldown_time[0]));
        edit.putString("Cooldown_1", String.valueOf(cooldown_time[1]));
        edit.putString("Cooldown_2", String.valueOf(cooldown_time[2]));
        edit.putString("Cooldown_3", String.valueOf(cooldown_time[3]));
        edit.putString("Cooldown_4", String.valueOf(cooldown_time[4]));
        edit.putString("Cooldown_5", String.valueOf(cooldown_time[5]));
        edit.putString("Cooldown_6", String.valueOf(cooldown_time[6]));
        edit.putString("Cooldown_7", String.valueOf(cooldown_time[7]));
        edit.putString("Cooldown_8", String.valueOf(cooldown_time[8]));
        edit.putString("Cooldown_9", String.valueOf(cooldown_time[9]));
        edit.putString("Cooldown_10", String.valueOf(cooldown_time[10]));
        edit.putString("Cooldown_11", String.valueOf(cooldown_time[11]));
        edit.putString("Cooldown_12", String.valueOf(cooldown_time[12]));
        edit.putString("Cooldown_13", String.valueOf(cooldown_time[13]));
        edit.putString("Cooldown_14", String.valueOf(cooldown_time[14]));
        edit.putString("Cooldown_15", String.valueOf(cooldown_time[15]));
        edit.putString("Cooldown_16", String.valueOf(cooldown_time[16]));
        edit.putString("Cooldown_17", String.valueOf(cooldown_time[17]));
        edit.putString("Cooldown_18", String.valueOf(cooldown_time[18]));
        edit.putString("Cooldown_19", String.valueOf(cooldown_time[19]));
        edit.putString("Cooldown_20", String.valueOf(cooldown_time[20]));
        edit.putString("Cooldown_21", String.valueOf(cooldown_time[21]));
        edit.putString("Cooldown_22", String.valueOf(cooldown_time[22]));
        edit.putString("Cooldown_23", String.valueOf(cooldown_time[23]));
        edit.putString("Cooldown_24", String.valueOf(cooldown_time[24]));
        edit.apply();
    }
// загружает текст от последнего отсканированного qr
    public void LoadBarcodeText() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isScienceQR = Boolean.parseBoolean(defaultSharedPreferences.getString("isScienceQR", "false"));
        barcodeValue.setText(defaultSharedPreferences.getString("BarcodeValue", "ждем-с сканирования"));
        secondTime = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("SecondTime", "0"))); // майски тест
        cooldown_time[0] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_0", "0")));
        cooldown_time[1] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_1", "0")));
        cooldown_time[2] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_2", "0")));
        cooldown_time[3] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_3", "0")));
        cooldown_time[4] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_4", "0")));
        cooldown_time[5] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_5", "0")));
        cooldown_time[6] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_6", "0")));
        cooldown_time[7] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_7", "0")));
        cooldown_time[8] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_8", "0")));
        cooldown_time[9] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_9", "0")));
        cooldown_time[10] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_10", "0")));
        cooldown_time[11] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_11", "0")));
        cooldown_time[12] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_12", "0")));
        cooldown_time[13] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_13", "0")));
        cooldown_time[14] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_14", "0")));
        cooldown_time[15] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_15", "0")));
        cooldown_time[16] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_16", "0")));
        cooldown_time[17] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_17", "0")));
        cooldown_time[18] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_18", "0")));
        cooldown_time[19] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_19", "0")));
        cooldown_time[20] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_20", "0")));
        cooldown_time[21] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_21", "0")));
        cooldown_time[22] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_22", "0")));
        cooldown_time[23] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_23", "0")));
        cooldown_time[24] = Long.parseLong(Objects.requireNonNull(defaultSharedPreferences.getString("Cooldown_24", "0")));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SaveBarcodeText();
    }
}
