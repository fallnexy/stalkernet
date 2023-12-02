package com.example.stalkernet.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.stalkernet.CodesQRAndText;
import com.example.stalkernet.DBHelper;
import com.example.stalkernet.Globals;
import com.example.stalkernet.MasterCode;
import com.example.stalkernet.R;
import com.example.stalkernet.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.stalkernet.StatsService.INTENT_SERVICE;
import static com.example.stalkernet.StatsService.INTENT_SERVICE_QR;

public class QRTab extends Fragment implements View.OnClickListener{
    private Globals globals;
    SQLiteDatabase database;
    DBHelper dbHelper;
    CodesQRAndText codesQRAndText;
    // use a compound button so either checkbox or switch widgets work.
    private SwitchMaterial autoApplyAid;
    private SwitchMaterial useFlash;
    private TextView statusMessage, barcodeValue, resultType, qrName, artFeature, artDangerous,
    artStage, artNameHard, qrType, artDangerousHard;
    private String applyCommand;

    private MasterCode masterCode;


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private Random random = new Random();

    private MaterialButton btnApplyQR, btnInteraction;
    private Button btnApplyWarring;

    public Location current_location = new Location("GPS");

    private long firstTime;
    private long secondTime;
    private long[] cooldown_time;

    boolean isScienceQR = true;

    public QRTab(Globals globals) {
        this.globals = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_qr, viewGroup, false);

        statusMessage = inflate.findViewById(R.id.status_message);
        barcodeValue = inflate.findViewById(R.id.barcode_value);
        resultType = inflate.findViewById(R.id.tvQRResultType);
        qrName = inflate.findViewById(R.id.tvQRName);
        artFeature = inflate.findViewById(R.id.tvArtFeature);
        artDangerous = inflate.findViewById(R.id.tvIsDangerous);
        artStage = inflate.findViewById(R.id.tvQRArtStage);
        artStage.setVisibility(View.GONE);
        qrType = inflate.findViewById(R.id.tvQRType);
        qrType.setVisibility(View.GONE);
        artNameHard = inflate.findViewById(R.id.tvQRNameHard);
        artNameHard.setVisibility(View.GONE);
        artDangerousHard = inflate.findViewById(R.id.tvQRIsDangerousHard);
        artDangerousHard.setVisibility(View.GONE);

        masterCode = new MasterCode();

        autoApplyAid = inflate.findViewById(R.id.auto_focus);
        useFlash = inflate.findViewById(R.id.use_flash);

        btnInteraction = inflate.findViewById(R.id.read_barcode);
        btnInteraction.setOnClickListener(this);
        btnApplyQR = inflate.findViewById(R.id.btnApplyQR);
        btnApplyQR.setOnClickListener(this);
        btnApplyQR.setVisibility(View.GONE);
        btnApplyWarring = inflate.findViewById(R.id.btnQRWarring);
        btnApplyWarring.setOnClickListener(this);
        btnApplyWarring.setVisibility(View.GONE);

        if (globals.scienceQR){
            btnInteraction.setIcon(getResources().getDrawable(R.drawable.scanner_active));
        } else {
            btnInteraction.setIcon(getResources().getDrawable(R.drawable.scanner_active3));
        }


        codesQRAndText = new CodesQRAndText(this, barcodeValue, globals);

        firstTime = Calendar.getInstance().getTimeInMillis();
        secondTime = 0;
        cooldown_time = new long[25];

        LoadBarcodeText();
        return inflate;
    }


/*
* QR ученого
* */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnApplyQR) {
            Intent intent = new Intent(INTENT_SERVICE);
            intent.putExtra(INTENT_SERVICE_QR, applyCommand);
            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
            statusMessage.setText(masterCode.textConstructor(applyCommand));
            v.setClickable(false);
            v.setBackgroundColor(getResources().getColor(R.color.applyQROff));
        }
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, /*autoFocus.isChecked()*/true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.btnQRWarring){
            btnApplyWarring.setVisibility(View.GONE);
        }

    }


    /*

   вВООООООООООООООООТ ЗДЕСЬ

   */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    Intent intent;
                    intent = new Intent("Command");

                    resolveQR(barcode.displayValue);
                    // обновляет БД: ставит статус true в access
                    /*makeAccessTrue(DBHelper.TABLE_QUEST, DBHelper.KEY_ACCESS_QUEST, DBHelper.KEY_ACCESS_KEY_QUEST, barcode.displayValue);
                    makeAccessTrue(DBHelper.TABLE_LOCALITY, DBHelper.KEY_ACCESS_STATUS__LOCALITY, DBHelper.KEY_ACCESS_KEY__LOCALITY, barcode.displayValue);
                    makeAccessTrue(DBHelper.TABLE_FACTION, DBHelper.KEY_ACCESS_STATUS_FACTION, DBHelper.KEY_ACCESS_KEY_FACTION, barcode.displayValue);
                    makeAccessTrue(DBHelper.TABLE_QUEST_STEP, DBHelper.KEY_STATUS_QUEST_STEP, DBHelper.KEY_ACCESS_KEY_QUEST_STEP, barcode.displayValue);
                    try {
                        makeAccessTrue(DBHelper.TABLE_MILESTONE, DBHelper.KEY_ACCESS_STATUS__MILESTONE, DBHelper.KEY_ACCESS_KEY__MILESTONE, barcode.displayValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        makeAccessTrue(DBHelper.TABLE_ARTEFACT, DBHelper.KEY_ACCESS_STATUS__ARTEFACT, DBHelper.KEY_ACCESS_KEY__ARTEFACT, barcode.displayValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        simpleTextFromDB(barcode.displayValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    //считывает qr код и в соответствии с case выдает нужный текст

                    switch (barcode.displayValue){

                        /*case "наукада":  //включает QR ученого
                            //isScienceQR = true;
                            btnScienceQR.setVisibility(View.VISIBLE);
                            barcodeValue.setText(R.string.scienceQR_on);
                            intent.putExtra("Command", "ScienceQR");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "наука-":  //отключает QR ученого
                           // isScienceQR = false;
                            btnScienceQR.setVisibility(View.INVISIBLE);
                            barcodeValue.setText(R.string.scienceQR_off);
                            intent.putExtra("Command", "ScienceQRoff");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;*/
                            // скрыты гештальты
                        case "hjopgirgyo":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "hgpveepiws":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed_2");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        /*case "xgnn6u3h313kvqg":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc);
                            intent.putExtra("Command", "gestalt_closed_3");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "5gchows2io8mhg0":
                            textOnArt (R.string.gestalt_closed, R.string.gestalt_closed_Sc_2);
                            intent.putExtra("Command", "gestalt_closed_4");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;*/
                        /*case "safezonef": // сентябрь21 - чистое небо
                            textOnArt(R.string.dischargeImmunity, R.string.dischargeImmunitySc);
                            intent.putExtra("Command", "naemnikiDischargeImmunity");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;

                        case "явсемогущий+":
                            barcodeValue.setText("Неуязвимость включена");
                            intent.putExtra("Command", "God");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "явсемогущий-":
                            barcodeValue.setText("Неуязвимость выключена");
                            intent.putExtra("Command", "noMoreGod");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;*/


                        /*case "dh22ibvye055lq3":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Устройство зарегистрировано на 10 минут.");
                            intent.putExtra("Command", "discharge10Sc");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "i8qpfu02xjuvmzu":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Устройство зарегистрировано на 30 минут.");
                            intent.putExtra("Command", "discharge45");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "rdg9qhqrkffjvz2":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована незначительная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus2Health");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "22d78ptbvz3gxgy":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована слабая стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus5Health");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "em7npa1e96zwgzf":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована умеренная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus10Health");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "pcozme0htw7nn69":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована значительная стабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта.");
                            intent.putExtra("Command", "BDplus45HealthRandom");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "gxehzewa0pthph1":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована слабая дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus5Health");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "2omtlr5z3bji8mi":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована умеренная дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus10HealthRandom");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "z8fl7dt7zmpryjb":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Зафиксирована значительная дестабилизация жизненных показателей пользователя. Проверьте состояние пользователя для фиксации эффекта и обратитесь за медицинской помощью при первой возможности.");
                            intent.putExtra("Command", "BDminus21HealthRandom");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "u7bua8tzmdbbmga":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio6025");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "5o1wzi0b6ebokd9":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio6035");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "ji282s9rfofumsr":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad6025");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "kab6b1efzvg4sdx":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad6035");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "mpv8gnpr8r8oe2p":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена защита от пси-полей.");
                            intent.putExtra("Command", "BDprotectionPsy6025");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "odu0yajypyl2cng":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от биологического воздействия.");
                            intent.putExtra("Command", "BDprotectionBio120");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "lapursrlu0yv10k":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от радиационного воздействия.");
                            intent.putExtra("Command", "BDprotectionRad120");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "0deupx1zzl0yyxr":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Обнаружено воздействие на организм пользователя. Обнаружена слабая защита от пси-полей.");
                            intent.putExtra("Command", "BDprotectionPsy120");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "d8pzzgr8lru47z8":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Внимание! Критический уровень облучения! Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "setRadOn80Percent");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "cat0u5axnlsezs2":
                            barcodeValue.setText("ОШИБКА: Сбой оборудования. Внимание! Химический ожог! Требуется срочная медицинская помощь.");
                            intent.putExtra("Command", "setBioOn80Percent");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "h29fthyiij":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[3] > 43200000) {
                                barcodeValue.setText("Механизм применён. Выведение радиационного воздействия из организма.");
                                intent.putExtra("Command", "mechMinus60Rad");
                                QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
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
                                QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
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
                                QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                                cooldown_time[5] = firstTime;
                            } else {
                                barcodeValue.setText("Повторное применение в краткой срок опасно для здоровья. Функционал заблокирован.");
                            }
                            return;
                        case "ovg1m1ngxzp15ti":
                            barcodeValue.setText("Нейтрализация радиационного воздействия. Показатели пользователя в пределах нормы.");
                            intent.putExtra("Command", "setRad0");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "2lfzs5u7idb1yvl":
                            barcodeValue.setText("Нейтрализация биологического воздействия. Состояние пользователя удовлетворительно, рекомендован дополнительный курс лечения.");
                            intent.putExtra("Command", "setBio15");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xfy3srgs36tjwlu":
                            barcodeValue.setText("Нейтрализация биологического воздействия. Показатели пользователя в пределах нормы..");
                            intent.putExtra("Command", "setBio0");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;*/

                            //здесь и далее Глеб
                        /*case "radiation":
                            textAndCoolDown(intent, 0, 2400000, R.string.glebRad0,R.string.glebRadsc, R.string.glebRadDawn, "radiation");
                            return;
                        case "radiation1":
                            textAndCoolDown(intent, 1, 2400000, R.string.glebRad1,R.string.glebRadsc, R.string.glebRadDawn, "radiation1");
                            return;
                        case "radiation2":
                            textAndCoolDown(intent, 2, 2400000, R.string.glebRad2,R.string.glebRadsc, R.string.glebRadDawn, "radiation2");
                            return;
                        case "radiation3":
                            textAndCoolDown(intent, 3, 2400000, R.string.glebRad3,R.string.glebRadsc, R.string.glebRadDawn, "radiation3");
                            return;
                        case "biohazard":
                            textAndCoolDown(intent, 4, 2400000, R.string.glebBio0,R.string.glebRadsc, R.string.glebRadDawn, "biohazard");
                            return;
                        case "biohazard1":
                            textAndCoolDown(intent, 5, 2400000, R.string.glebBio1,R.string.glebRadsc, R.string.glebRadDawn, "biohazard1");
                            return;
                        case "biohazard2":
                            textAndCoolDown(intent, 6, 2400000, R.string.glebBio2,R.string.glebRadsc, R.string.glebRadDawn, "biohazard2");
                            return;
                        case "biohazard3":
                            textAndCoolDown(intent, 7, 2400000, R.string.glebBio3,R.string.glebRadsc, R.string.glebRadDawn, "biohazard3");
                            return;
                        case "health":
                            textAndCoolDown(intent, 8, 2400000, R.string.glebHP0,R.string.glebRadsc, R.string.glebRadDawn, "health");
                            return;
                        case "health1":
                            textAndCoolDown(intent, 9, 2400000, R.string.glebHP1,R.string.glebRadsc, R.string.glebRadDawn, "health1");
                            return;
                        case "health2":
                            textAndCoolDown(intent, 10, 2400000, R.string.glebHP2,R.string.glebRadsc, R.string.glebRadDawn, "health2");
                            return;
                        case "health3":
                            textAndCoolDown(intent, 11, 2400000, R.string.glebHP3,R.string.glebRadsc, R.string.glebRadDawn, "health3");
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
                        case "lbbbzgutsc":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }
                            textAndCoolDown(intent, 15, 300000, R.string.art_21_dang, R.string.art_21_03sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "juoqudtxgc":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 16, 300000, R.string.art_21_dang, R.string.art_21_06sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "opdcplctlz":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 17, 300000, R.string.art_21_dang, R.string.art_21_09sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "ktrewhbuhy":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 18, 300000, R.string.art_21_dang, R.string.art_21_11sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "ntpoqmdtsp":
                            textAndCoolDown(intent, 19, 600000, R.string.art_21_12, R.string.art_21_12sc, R.string.art_21_12_dawn_compas, "artCompass");
                            return;
                        case "kwsiajfcik":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 20, 300000, R.string.art_21_dang, R.string.art_21_13sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "mzfcfvscco":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 21, 300000, R.string.art_21_dang, R.string.art_21_18sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "bmwnngcjhq":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 22, 300000, R.string.art_21_dang, R.string.art_21_19sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "maaerpdbrz":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 23, 300000, R.string.art_21_dang, R.string.art_21_22sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "pjvmppohse":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 24, 300000, R.string.art_21_dang, R.string.art_21_28sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "ckxsuopjni":
                            if (!scienceQR && !applyQR) {
                                stalkerRoulette();
                            }

                            textAndCoolDown(intent, 14, 300000, R.string.art_21_dang, R.string.art_21_28sc, R.string.art_21_dawn_1, "nope");
                            return;
                        case "kvrvkkrscm":
                            textAndCoolDown(intent, 13, 300000, R.string.oasis_o, R.string.art_21_28sc, R.string.oasis_cd, "oasis");
                            return;
                        case "коссева": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм СЕВА (80% БИО)");
                            intent.putExtra("Command", "sc1, bio, suit, 80");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косзаря": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм ЗАРЯ (80% РАД)");
                            intent.putExtra("Command", "sc1, rad, suit, 80");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косстраж": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм СТРАЖ СВОБОДЫ (50% БИО)");
                            intent.putExtra("Command", "sc1, bio, suit, 50");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "стражснять": // здесь и далее установка защит
                            barcodeValue.setText("Костюм СТРАЖ СВОБОДЫ снят");
                            intent.putExtra("Command", "sc1, bio, suit, 0");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "косучен": // здесь и далее установка защит
                            barcodeValue.setText("Надет костюм ЭЗС Драговича М0.1");
                            intent.putExtra("Command", "sc1, bio, suit, 80");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            intent.putExtra("Command", "sc1, rad, suit, 80");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "штраф":
                            barcodeValue.setText("Применена штрафная санкция.");
                            intent.putExtra("Command", "штраф");
                            QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                            return;
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
                            return;*/
                        /*default:
                            barcodeValue.setText("иди своей дорогой, сталкер");*/
                    }

                    //codesQRAndText.checkCode(barcode.displayValue, globals.scienceQR, globals.applyQR);

                } else {
                    statusMessage.setText(R.string.barcode_failure);
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
    /*
    * определяет по коду таблицу и номер
    * */
    private String[] getTable(SQLiteDatabase database,String code){
        String db_table = "";
        String db_id = "";
        String db_column = "";
        Cursor cursor = database.query(DBHelper.TABLE_INTERTABLE, null, DBHelper.KEY_HUMAN_CODE__INTERTABLE + " =?", new String[]{code}, null, null, null);
        if (cursor.moveToFirst()){
            int tableIndex = cursor.getColumnIndex(DBHelper.KEY_DB_NAME__INTERTABLE);
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_DB_ID__INTERTABLE);
            int columnIndex = cursor.getColumnIndex(DBHelper.KEY_DB_COLUMN__INTERTABLE);
            db_table = cursor.getString(tableIndex);
            db_id = cursor.getString(idIndex);
            db_column = cursor.getString(columnIndex);
        } else {
            return new String[]{"ошибка в таблице", "ошибка в id", "ошибка в столбце"};
        }
        cursor.close();
        return new String[]{db_table, db_id, db_column};
    }
    /*
    * разрешает коды
    * */
    private void resolveQR (String code){
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.open();
        String[] strings = getTable(database, code);
        String tableName = strings[0];
        String id = strings[1];
        String column = strings[2];

        resultType.setText(tableName);

        switch (tableName){
            case DBHelper.TABLE_ARTEFACT:
                if (column.equals(DBHelper.KEY_ACCESS_STATUS__ARTEFACT)) {
                    resolveArt(database, tableName, id);
                } else {
                    resolveArtLevel(database, strings);
                }
                break;
            case DBHelper.TABLE_AID:

                break;

        }
    }
    /*
    * resolveQR в случае артоса
    * */
    private void resolveArt(SQLiteDatabase database, String tableName, String id){
        ContentValues cv;
        int textIndex;
        int nameIndex;
        int featureIndex;
        int dangerousIndex;
        int applyIndex;

        resultType.setVisibility(View.VISIBLE);
        artNameHard.setVisibility(View.VISIBLE);
        artFeature.setVisibility(View.VISIBLE);
        artDangerousHard.setVisibility(View.VISIBLE);
        artStage.setVisibility(View.VISIBLE);
        qrType.setVisibility(View.VISIBLE);
        qrName.setVisibility(View.VISIBLE);
        artDangerous.setVisibility(View.VISIBLE);


        Cursor cursor = database.query(tableName, null, DBHelper.KEY_ID__ARTEFACT + " =?", new String[]{id}, null, null, null);

        if (cursor.moveToFirst()) {
            nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME__ARTEFACT);
            dangerousIndex = cursor.getColumnIndex(DBHelper.KEY_IS_DANGEROUS__ARTEFACT);
            applyIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_LEVEL__ARTEFACT);
            featureIndex = cursor.getColumnIndex(DBHelper.KEY_FEATURE__ARTEFACT);

            int applyLevel = cursor.getInt(applyIndex);
            getApplyCommand(cursor, applyLevel);
            qrName.setText(cursor.getString(nameIndex));
            artStage.setText(colorChanger(applyLevel));
            if (cursor.getString(dangerousIndex).equals("true")){
                artDangerous.setText("Опасный");
            } else {
                artDangerous.setText("Безопасный");
            }
            String feature = cursor.getString(featureIndex);
            if (feature.equals("none")){
                artFeature.setVisibility(View.GONE);
                artStage.setVisibility(View.GONE);
                btnApplyQR.setVisibility(View.GONE);
                btnApplyWarring.setVisibility(View.GONE);
            } else {
                artFeature.setText(feature);
                artFeature.setVisibility(View.VISIBLE);
                artStage.setVisibility(View.VISIBLE);
                btnApplyQR.setVisibility(View.VISIBLE);
                if (globals.applyQR){
                    btnApplyQR.setClickable(true);
                    btnApplyQR.setBackgroundColor(getResources().getColor(R.color.creedTwo));
                    btnApplyWarring.setVisibility(View.GONE);
                } else {
                    btnApplyQR.setClickable(false);
                    btnApplyQR.setBackgroundColor(getResources().getColor(R.color.applyQROff));
                    btnApplyWarring.setVisibility(View.VISIBLE);
                }
            }
            if (globals.scienceQR){
                cv = new ContentValues();
                cv.put(DBHelper.KEY_ACCESS_STATUS__ARTEFACT, "true");
                database.update(tableName, cv, DBHelper.KEY_ID__ARTEFACT + "= ?", new String[]{id});

                textIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__ARTEFACT);

                barcodeValue.setText(cursor.getString(textIndex));
            } else {
                artFeature.setVisibility(View.GONE);

                cv = new ContentValues();
                cv.put(DBHelper.KEY_ACCESS_STATUS__ARTEFACT, "partial");
                database.update(tableName, cv, DBHelper.KEY_ID__ARTEFACT + "= ?", new String[]{id});

                if (cursor.getString(dangerousIndex).equals("true")){
                    stalkerRoulette();
                    barcodeValue.setText(R.string.art_dangerous_true);
                } else {
                    barcodeValue.setText(R.string.art_dangerous_false);
                }
            }
        }

        cursor.close();
    }
    private SpannableString colorChanger(int level){
        String feature = "Свойства:\nСтадия I\nСтадия II\nСтадия III";
        SpannableString spannableString = new SpannableString(feature);
        int colorForStageI = Color.GREEN;
        int startStageI;
        int endStageI;
        switch (level){
            default:
            case 1:
                startStageI = feature.indexOf("Стадия I");
                endStageI = startStageI + "Стадия I".length();
                break;
            case 2:
                startStageI = feature.indexOf("Стадия II");
                endStageI = startStageI + "Стадия II".length();
                break;
            case 3:
                startStageI = feature.indexOf("Стадия III");
                endStageI = startStageI + "Стадия III".length();
                break;
        }
        spannableString.setSpan(new ForegroundColorSpan(colorForStageI), startStageI, endStageI, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
    /*
    * выбирает нужную стадия артефакта для использования
    * */
    private void getApplyCommand(Cursor cursor, int level){
        int stageFeatureIndex;
        switch (level){
            default:
            case 1:
                stageFeatureIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_ONE__ARTEFACT);
                break;
            case 2:
                stageFeatureIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_TWO__ARTEFACT);
                break;
            case 3:
                stageFeatureIndex = cursor.getColumnIndex(DBHelper.KEY_APPLY_THREE__ARTEFACT);
                break;
        }
        applyCommand = cursor.getString(stageFeatureIndex);
    }

    /*
    * resolveQR для доступа к лучшим свойствам артосов
    * */
    private void resolveArtLevel(SQLiteDatabase database, String[] strings){
        artStage.setVisibility(View.GONE);
        artNameHard.setVisibility(View.GONE);
        artDangerousHard.setVisibility(View.GONE);
        btnApplyQR.setVisibility(View.GONE);
        btnApplyWarring.setVisibility(View.GONE);
        qrName.setVisibility(View.GONE);
        artDangerous.setVisibility(View.GONE);

        ContentValues cv = new ContentValues();
        String output = " перешли на Стадию";
        qrType.setVisibility(View.VISIBLE);
        if (strings[2].equals(DBHelper.KEY_ACCESS_APPLY_TWO__ARTEFACT)){
            cv.put(DBHelper.KEY_APPLY_LEVEL__ARTEFACT, "2");
            output += " II";
        } else {
            cv.put(DBHelper.KEY_APPLY_LEVEL__ARTEFACT, "3");
            output += " III";
        }
        database.update(strings[0], cv, DBHelper.KEY_ID__ARTEFACT + "= ?", new String[]{strings[1]});

        Cursor cursor = database.query(strings[0], new String[]{DBHelper.KEY_NAME__ARTEFACT}, DBHelper.KEY_ID__ARTEFACT + " =?", new String[]{strings[1]}, null,null,null);
        if (cursor.moveToFirst()){
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME__ARTEFACT);
            output = "Свойства артефакта " + cursor.getString(nameIndex) + output;
            barcodeValue.setText(output);
        }
        cursor.close();
    }

    /*
    * resolveQR в случае аптечки
    * */
    private void resolveAid(SQLiteDatabase database, String tableName, String id){
        resultType.setVisibility(View.VISIBLE);
        qrType.setVisibility(View.VISIBLE);

        Cursor cursor = database.query(tableName, null, DBHelper.KEY_ID__ARTEFACT + " =?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst()){

        }

        cursor.close();
    }


    // ищет в БД и делает доступной запись в соответствующих фрагментах
    private void makeAccessTrue(String table, String accessColumn, String accessKeyColumn, String barcodeText) {
        ContentValues cv;
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.open();
        cv = new ContentValues();

        resultType.setText(table);

        if (!table.equals(DBHelper.TABLE_ARTEFACT)) {
            cv.put(accessColumn, "true");
            database.update(table, cv, accessKeyColumn + "= ?", new String[]{barcodeText});
        } else {
            Cursor cursor = database.rawQuery("SELECT _id, description, interaction, is_dangerous FROM artefact WHERE access_key =?", new String[]{barcodeText});
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__ARTEFACT);
            int scienceIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__ARTEFACT);
            int nonScienceIndex = cursor.getColumnIndex(DBHelper.KEY_INTERACTION__ARTEFACT);
            int isDangerousIndex = cursor.getColumnIndex(DBHelper.KEY_IS_DANGEROUS__ARTEFACT);
            String scienceText = cursor.getString(scienceIndex);
            String nonScienceText = cursor.getString(nonScienceIndex);
            String isDangerous = cursor.getString(isDangerousIndex);
            if (globals.scienceQR){
                cv.put(accessColumn, "true");
                database.update(table, cv, accessKeyColumn + "= ?", new String[]{barcodeText});
                barcodeValue.setText(scienceText);
            } else if (globals.applyQR) {
                Intent intentNew = new Intent(INTENT_SERVICE);
                String str = "app, " + cursor.getString(idIndex);
                intentNew.putExtra(INTENT_SERVICE_QR, str);
                QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intentNew);
            } else {
                barcodeValue.setText(nonScienceText);
                /*switch (isDangerous){
                    case "true":
                        stalkerRoulette();
                }*/
            }
            cursor.close();
        }
        if (table.equals(DBHelper.TABLE_MILESTONE)){
            Cursor cursor = database.rawQuery("SELECT description FROM milestone WHERE access_key =?", new String[]{barcodeText});
            cursor.moveToFirst();
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__MILESTONE);
            String description = cursor.getString(descriptionIndex);
            barcodeValue.setText(description);
            cursor.close();
        }
        database.close();
    }

    // пок только для item
    private void simpleTextFromDB(String barcodeText){
        SQLiteDatabase database;
        DBHelper dbHelper;
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.open();
        Cursor cursor = database.rawQuery("SELECT description, vzaimodeistvie FROM item WHERE access_key =?", new String[]{barcodeText});
        cursor.moveToFirst();
        int scienceIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__ITEM);
        int nonScienceIndex = cursor.getColumnIndex(DBHelper.KEY_INTERACTION__ITEM);
        String scienceText = cursor.getString(scienceIndex);
        String nonScienceText = cursor.getString(nonScienceIndex);
        if (globals.scienceQR){
            barcodeValue.setText(scienceText);
        } else {
            barcodeValue.setText(nonScienceText);
        }
        cursor.close();
        database.close();
    }

    // отправляет результат рулетки
    private void stalkerRouletteSolved (String command, String text){
        Intent intent;
        intent = new Intent("StRoulette");
        intent.putExtra("StRoulette", command);
        statusMessage.setText(text);
        requireActivity().getApplicationContext().sendBroadcast(intent);
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


    private void textOnArt(int nonScience, int science) {
        textOnArt(nonScience, science, R.string.empty_string);
    }

    private void textOnArt (int nonScience, int science, int apply){
       if (globals.applyQR){
           barcodeValue.setText(apply);
       } else if(globals.scienceQR) {
           barcodeValue.setText(science);
       } else {
           barcodeValue.setText(nonScience);
       }
    }

    private void textAndCoolDown(Intent intent, int coolDawnNumber, int coolDawnMillis, int nonScience, int science, int txtCoolDawn, String command){
        if (globals.scienceQR){
            //barcodeValue.setText(science);
        } else if (globals.applyQR){

        } else {
            firstTime = Calendar.getInstance().getTimeInMillis();
            if (firstTime - cooldown_time[coolDawnNumber] > coolDawnMillis) {
                barcodeValue.setText(nonScience);
                intent.putExtra("Command", command);
                QRTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                cooldown_time[coolDawnNumber] = firstTime;
            } else {
                barcodeValue.setText(txtCoolDawn);
            }
        }

    }

    // Расстояние до точки
    private void check_point(LatLng latLng, double radius, String in_massage, String out_massage){
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
        //edit.putString("BarcodeValue", String.valueOf(barcodeValue.getText()));
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
        //barcodeValue.setText(defaultSharedPreferences.getString("BarcodeValue", ""));
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

