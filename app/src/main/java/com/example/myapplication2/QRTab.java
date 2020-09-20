package com.example.myapplication2;

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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

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
    private Button btnScienceQR;

    private long firstTime;
    private long secondTime;
    private long[] cooldown_time;
    private boolean[] compositionOfArts = new boolean[21];
    private Spanned[] composites;
    private boolean[] monolithMech = new boolean[4];


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
        btnScienceQR = inflate.findViewById(R.id.btnScienceQR);
        btnScienceQR.setOnClickListener(this);
        if (globals.ScienceQR == 1) {
            btnScienceQR.setVisibility(View.VISIBLE);
        }
        if (globals.ScienceQR == 0){
            btnScienceQR.setVisibility(View.INVISIBLE);
        }
        firstTime = Calendar.getInstance().getTimeInMillis();
        secondTime = 0;
        cooldown_time = new long[15];
        composites = new Spanned[20];
        composites[0] = Html.fromHtml(getString(R.string.composite_detected));
        composites[1] = Html.fromHtml(getString(R.string.composite_continue));
        composites[2] = Html.fromHtml(getString(R.string.composite_canceled));
        composites[3] = Html.fromHtml(getString(R.string.composite_canceled_2));
        composites[4] = Html.fromHtml(getString(R.string.art_Sc_a5t322faqf));
        composites[5] = Html.fromHtml(getString(R.string.art_Sc_a5t322faqf_2));
        composites[6] = Html.fromHtml(getString(R.string.art_8nk3owbpzt));
        composites[7] = Html.fromHtml(getString(R.string.art_86peq6qktl));
        composites[8] = Html.fromHtml(getString(R.string.art_86peq6qktl_2));
        composites[9] = Html.fromHtml(getString(R.string.art_zp1ivlcs7e));
        composites[10] = Html.fromHtml(getString(R.string.art_status_nm7s576l0i));
        composites[11] = Html.fromHtml(getString(R.string.art_status_vz6rafxyei));
        composites[12] = Html.fromHtml(getString(R.string.art_status_jt0dfct2w0));
        composites[13] = Html.fromHtml(getString(R.string.art_status_monolithMech_fail));
        composites[14] = Html.fromHtml(getString(R.string.art_status_monolithMech_fail_2));

        LoadBarcodeText();
        return inflate;
    }
/*
* QR ученого
* */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            scienceQR = false;
            // launch barcode activity.
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.btnScienceQR){
            scienceQR = true;
            Intent intent = new Intent(v.getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }


    /*

   вВООООООООООООООООТ ЗДЕСЬ

   */@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    Intent intent;
                    intent = new Intent("Command");
                    //считывает qr код и в соответствии с case выдает нужный текст
                    switch (barcode.displayValue){
                        case "aww5lg7az1dfadh":  //включает QR ученого
                            btnScienceQR.setVisibility(View.VISIBLE);
                            barcodeValue.setText(R.string.scienceQR_on);
                            intent.putExtra("Command", "ScienceQR");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xvwtoskeulkykxc":  //отключает QR ученого
                            btnScienceQR.setVisibility(View.INVISIBLE);
                            barcodeValue.setText(R.string.scienceQR_off);
                            intent.putExtra("Command", "ScienceQRoff");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "cv7mtd4tm4knk8w":
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
                            return;
                        case "12543659521":
                            barcodeValue.setText("Защита от радиации на 2 часа поставлена");
                            intent.putExtra("Command", "TwoHoursRadProtection");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                            ///////////////////////////////////////////////////////////////////////////////
                            ///////////////////////////////////////////////////////////////////////////////
                            ////////////////////////////////////////////////////////////////////////////// сентябрь 2020
                        case "8sx5aziy0i6hi1e":
                            barcodeValue.setText("15 минут, чтоб до базы дойти");
                            intent.putExtra("Command", "15minutesGod");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "ixeno3mfgnoayhh":  // этот и 3 следующих -  4 шприца на минус рад и био (бранованный и нет)
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[0] > 600000) {
                                barcodeValue.setText("Препарат применён. Выведение радиацоноого воздействия из организма.");
                                intent.putExtra("Command", "minus50Rad");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[0] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "wbshfnwxb834xxm":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[1] > 600000) {
                                barcodeValue.setText("Препарат применён. Выведение биологического воздействия из организма.");
                                intent.putExtra("Command", "minus50Bio");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[1] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "tnqdxijx1ukib70":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[0] > 600000) {
                                barcodeValue.setText("Препарат применён. Выведение радиацоноого воздействия из организма.");
                                intent.putExtra("Command", "minus25Rad");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[0] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "8mjrvqou1xjnbrm":
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[1] > 600000) {
                                barcodeValue.setText("Препарат применён. Выведение биологического воздействия из организма.");
                                intent.putExtra("Command", "minus25Bio");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[1] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "gjxplb1wfxm3hx9": //этот и следующий - кода на плюс жизнь
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[2] > 600000) {
                                barcodeValue.setText("Препарат применён. Жизненные показатели пользователя стабилизированы.");
                                intent.putExtra("Command", "plus40Health");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[2] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "ry37f6wmrj71x8h": //этот и следующий - кода на плюс жизнь
                            firstTime = Calendar.getInstance().getTimeInMillis();
                            if (firstTime - cooldown_time[2] > 600000) {
                                barcodeValue.setText("Препарат применён. Жизненные показатели пользователя стабилизированы.");
                                intent.putExtra("Command", "plus20Health");
                                Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                cooldown_time[2] = firstTime;
                            } else {
                                barcodeValue.setText("Высокий уровень токсинов в организме. Пожалуйста, подождите.");
                            }
                            return;
                        case "s2rc4zdwjbvf02c": // здесь и далее qr просто со словами
                            if (scienceQR) {
                                barcodeValue.setText("Автономный Источник Питания высокой плотности для различного вида устройств. Обладает достаточным зарядом чтобы обеспечивать энергией одну еденицу техники на протяжении нескольких месяцев. Данная модель АИП нуждается в обязательном аксессуаре - Адаптивном Регуляторе Напряжения, который следует подключать в цепь между источником питания и непосредственно техникой.");
                            } else {
                                barcodeValue.setText("Автономный Источник Питания.");
                            }
                            return;
                        case "7wlayhe8lu3y1ve":
                            if (scienceQR) {
                                barcodeValue.setText("Адаптивный Регулятор Напряжения для различного вида устройств. Способен автоматически подстраивать напряжение источников питания под нужны устройств. Для максимального эффекта необходимо встроить его в цепь между необходимым устройством и источником питания.");
                            } else {
                                barcodeValue.setText("Адаптивный Регулятор Напряжения.");
                            }
                            return;
                        case "clfoqrf1ol09as8":
                            if (scienceQR) {
                                barcodeValue.setText("Миниатюрный Рассеиватель Вредоносных Частиц - это младший брат устройств, изобретённых и установленных в лаборатории Рассвет и на различных КПП ЧЗО. Это устройство призвано защищать людей от вредоносного влияния так называемых Выбросов, часто происходящих в ЧЗО. М-РВЧ сможет обеспечить безопасность небольшой группе лиц и защитить одно небольшое строение. Для полноценной работы устройства необходим Калибровочный Блок М-РВЧ и Направленный Излучатель М-РВЧ.");
                            } else {
                                barcodeValue.setText("Миниатюрный Рассеиватель Вредоносных Частиц. Для полноценной работы устройства необходим Калибровочный Блок М-РВЧ и Направленный Излучатель М-РВЧ. Координаты М-РВЧ введены. Цифровая подпись Рассвет-3. Внесение изменений невозможно.");
                            }
                            return;
                        case "b3s7u6hgi6nhuws":
                            if (scienceQR) {
                                barcodeValue.setText("Калибровочный Блок М-РВЧ представляет из себя мини-компьютер, осуществляющий расчёты для основного блока Миниатюрного Рассеивателя Вредоносных Частиц при выполнении различного рода операций.");
                            } else {
                                barcodeValue.setText("Калибровочный Блок М-РВЧ. Координаты М-РВЧ введены. Цифровая подпись Рассвет-3. Калибровочный функционал купирован, терминал заблокирован. Внесение изменений невозможно.");
                            }
                            return;
                        case "1cr5kon8ev24203":
                            if (scienceQR) {
                                barcodeValue.setText("Направленный Излучатель М-РВЧ сферического типа устанавливается на Миниатюрный Рассеиватель Вредоносных Частиц и обеспечивает направленную защиту от вредоносного воздействия так называемых Выбросов. В зависимости от типа местности, такой излучатель обладает мощностью, достаточной для создания зоны безопасности с радиусом до 25 метров.");
                            } else {
                                barcodeValue.setText("Направленный Излучатель М-РВЧ.");
                            }
                            return;
                        case "ync702dagvdmr2n":
                            if (scienceQR) {
                                barcodeValue.setText("Портативный Генератор Приводного Поля. Для полноценной работы данной модификации необходим Миниатюрный Генератор Экранирующего Поля и Фокусирующий Излучатель Штайнера."+ "\n\n" + "Портативный Генератор Приводного Поля был собран учёными Рассвет-3 на основе Генератора Приводного Поля, найденного ими в одной из экспедиций 2019 года. Учёные, принявшие участие в этой экспедиции уверяют, что вступили в контакт с так называемым Фантомом, который передал им не только устройство и его составные части, но и инструкцию по сборке. Кроме того Фантом просил членов экспедиции, цитата \"найти членов пропавшей экспедиции и спасти их из трёх заблокированных лабораторий\". Считается, что Генераторы Приводных Полей всегда должны образовывать пару, однако для данной модификации пара не предусмотрена.");
                            } else {
                                barcodeValue.setText("Портативный Генератор Приводного Поля. Для полноценной работы данной модификации необходим Миниатюрный Генератор Экранирующего Поля и Фокусирующий Излучатель Штайнера." + "\n\n" + "Портативный Генератор Приводного Поля был собран учёными Рассвет-3 на основе Генератора Приводного Поля, найденного ими в одной из экспедиций 2019 года. Учёные, принявшие участие в этой экспедиции уверяют, что вступили в контакт с так называемым Фантомом, который передал им не только устройство и его составные части, но и инструкцию по сборке. Кроме того Фантом просил членов экспедиции, цитата \"найти членов пропавшей экспедиции и спасти их из трёх заблокированных лабораторий\". Считается, что Генераторы Приводных Полей всегда должны образовывать пару, однако для данной модификации пара не предусмотрена.");
                            }
                            return;
                        case "07kw70txx0hdwg1":
                            if (scienceQR) {
                                barcodeValue.setText("Миниатюрный Генератор Экранирующего Поля." + "\n\n" + "МГЭП, или так называемый Генератор Пузырей - есть не что иное как тонко настроенное под определённые нужды устройство, ответственное за нейтрализацию вредоносных эффектов окружающей среды на малой территории. Технология генерации пузырей экспериментальна и основана на теории Гештальтов. Руководство ВНИИ ЧЗО внимательно следит за развитием этой технологии после серии неудачных экспериментов, отправивших подопытные объекты на несколько минут в прошлое.");
                            } else {
                                barcodeValue.setText("Миниатюрный Генератор Экранирующего Поля." + "\n\n" + "МГЭП, или так называемый Генератор Пузырей - есть не что иное как тонко настроенное под определённые нужды устройство, ответственное за нейтрализацию вредоносных эффектов окружающей среды на малой территории. Технология генерации пузырей экспериментальна и основана на теории Гештальтов. Руководство ВНИИ ЧЗО внимательно следит за развитием этой технологии после серии не вполне удачных экспериментов, отправивших подопытные объекты на несколько минут в прошлое.");
                            }
                            return;
                        case "hvbs5u4b2uxqdi3":
                            if (scienceQR) {
                                barcodeValue.setText("Фокусирующий Излучатель Штайнера." + "\n\n" + "Лукас Штайнер - знаменитый деятель науки, ответственный за недавнее развитие исследований в области теории относительности и Ноосферы применимо пространственно-временных аномалий Чернобыльской Зоны Отчуждения. Законы физики, локально и аномально изменённые в ЧЗО, открывают широкий простор для экспериментов в области теории и практики локальной структуры пространства-времени. Так, недавние прорывы позволили учёным Всемирного НИИ ЧЗО отправлять неодушевлённые предметы в будущее на несколько минут, а так же, благодаря Новейшей Теории Ноосферы, наблюдать некоторые события ушедших времён. Каждый такой эксперимент имеет множество факторов, влияющих на конечный результат и зачастую результаты одних и тех же экспериментов разнятся. Например, не все эксперименты реализуемы за пределами Чернобыльской Зоны Отчуждения. Представители ВНИИ ЧЗО считают, что практическая ценность этих технологий бесспорна, однако к этой технологии следует подходить крайне осторожно во избежание непредвиденных последствий.");
                            } else {
                                barcodeValue.setText("Фокусирующий Излучатель Штайнера." + "\n\n" + "Лукас Штайнер - знаменитый деятель науки, ответственный за недавнее развитие исследований в области теории относительности и Ноосферы применимо пространственно-временных аномалий Чернобыльской Зоны Отчуждения. Законы физики, локально и аномально изменённые в ЧЗО, открывают широкий простор для экспериментов в области теории и практики локальной структуры пространства-времени. Так, недавние прорывы позволили учёным Всемирного НИИ ЧЗО отправлять неодушевлённые предметы в будущее на несколько минут, а так же, благодаря Новейшей Теории Ноосферы, наблюдать некоторые события ушедших времён. Каждый такой эксперимент имеет множество факторов, влияющих на конечный результат и зачастую результаты одних и тех же экспериментов разнятся. Например, не все эксперименты реализуемы за пределами Чернобыльской Зоны Отчуждения. Представители ВНИИ ЧЗО считают, что практическая ценность этих технологий бесспорна, однако к этой технологии следует подходить крайне осторожно во избежание непредвиденных последствий.");
                            }
                            return;
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
                            intent.putExtra("Command", "SetRadProtection100");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "t5izlre4vhdegt0": // это защита и предыдущая используют код от цифровых ключей
                            barcodeValue.setText("Активирована защита костюма от биологического воздействия.");
                            intent.putExtra("Command", "SetBioProtection100");
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
                        case "uunnn7zh6yuknjp":
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
                        case "hbyiueigrj":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Сохранение координат. Создание локальной точки безопасности от Выброса.");
                            intent.putExtra("Command", "dolgDischargeImmunity");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "xjamcfsysr":
                            barcodeValue.setText("Синхронизация оборудования с локальным рассеивателем вредоносных частиц. Сохранение координат. Создание локальной точки безопасности от Выброса.");
                            intent.putExtra("Command", "naemnikiDischargeImmunity");
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
                        case "8xxv2bxw26":  // тут и далее композитные артосы
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
                            return;
                        case "en5575ignsjx051": // включить уникальную аномалию свободы
                            barcodeValue.setText("Аномалия Зафиксирована");
                            intent.putExtra("Command", "anomalyFreedomOn");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "3s2x9ehv8q": // выключить уникальную аномалию свободы
                            barcodeValue.setText("Синхронизация осуществлена успешно.");
                            intent.putExtra("Command", "anomalyFreedomOff");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "ig4yagonph7tkuj": // основной код квеста свободы
                            String x = String.valueOf(1600417800 - Calendar.getInstance().getTimeInMillis() / 1000);
                            if (scienceQR) {
                                barcodeValue.setText("Лаборатория \"Выжигатель\".\n\nОборонное Устройство Направленного Пси-Воздействия, образец 3.\nЧастота воздействия: полуразумные подопытные субъекты.\n\nУстройство активно.\n\nДиагностика завершена.\n\nВнимание! Обнаружена дегерметизация топливных трубок. Потеря энергоэффективности. Усиление радиационного фона. Требуется перекалибровка излучателя 2,5,6. Требуется срочный техосмотр. Ожидаемое время работы в текущих условиях: " + x + "сек.\nВнимание! В случае продолжения работы устройства возможно возникновение каскадного отказа системы. Задействован механизм перехвата инициативы путём самоуничтожения.\nВнимание! Использован просроченный ключ активации. Задействована Гравитационная Якорная Система Клойзнера, устройство зафиксировано. Оставайтесь на месте до прибытия Службы Безопасности.\n\nУправление заблокировано.");
                            } else {
                                barcodeValue.setText("Внимание! Устройство активно.\n\nЗафиксирована внутренняя нестабильность устройства. Расчётное время точки невозврата: " + x + "сек.\nЗафиксирована гравитационная аномалия внутри устройства. Передвижение устройства не представляется возможным.\n\nУправление заблокировано.\nВ доступе отказано.");
                            }
                            return;
                        case "0uokkh386pdgr2c": // здесь и далее беспонотовые артосы
                            textOnArt(R.string.art_0uokkh386pdgr2c, R.string.art_sc_0uokkh386pdgr2c);
                            return;
                        case "2nesjlyax0go7uj":
                            textOnArt(R.string.art_2nesjlyax0go7uj, R.string.art_sc_2nesjlyax0go7uj);
                            return;
                        case "y1bloh1oinkmmvg":
                            textOnArt(R.string.art_y1bloh1oinkmmvg, R.string.art_sc_y1bloh1oinkmmvg);
                            return;
                        case "nmimzb9st47y6sa":
                            textOnArt(R.string.art_nmimzb9st47y6sa, R.string.art_sc_nmimzb9st47y6sa);
                            return;
                        case "f4q15up66dji6oh":
                            textOnArt(R.string.art_f4q15up66dji6oh, R.string.art_sc_f4q15up66dji6oh);
                            return;
                        case "tfpa926w6euaxlm":
                            textOnArt(R.string.art_tfpa926w6euaxlm, R.string.art_sc_tfpa926w6euaxlm);
                            return;
                        case "1kov5ncq69f78bb":
                            textOnArt(R.string.art_1kov5ncq69f78bb, R.string.art_sc_1kov5ncq69f78bb);
                            return;
                        case "uhivfps0rffu40t":
                            textOnArt(R.string.art_uhivfps0rffu40t, R.string.art_sc_uhivfps0rffu40t);
                            return;
                        case "mokvtx8m5klen2q":
                            textOnArt(R.string.art_mokvtx8m5klen2q, R.string.art_sc_mokvtx8m5klen2q);
                            return;
                        case "dksfma2ukv445t9":
                            textOnArt(R.string.art_dksfma2ukv445t9, R.string.art_sc_dksfma2ukv445t9);
                            return;
                        case "3rrc7ojff20w4fb":
                            barcodeValue.setText(R.string.art_3rrc7ojff20w4fb);
                            return;
                        case "y1tb4a41ax5bx2f": // загадать желеание монолиту
                            barcodeValue.setText(R.string.art_y1tb4a41ax5bx2f);
                            return;
                        case "p3jtg9p5mbcpseq":
                            textOnArt(R.string.art_p3jtg9p5mbcpseq, R.string.art_sc_p3jtg9p5mbcpseq);
                            return;
                        case "vvtdu8kyg6bicoe":
                            textOnArt(R.string.art_vvtdu8kyg6bicoe, R.string.art_sc_vvtdu8kyg6bicoe);
                            return;
                        case "c727lv5eov12phh":
                            textOnArt(R.string.art_c727lv5eov12phh, R.string.art_sc_c727lv5eov12phh);
                            return;
                        case "spnk46oxe12mtgn":
                            textOnArt(R.string.art_spnk46oxe12mtgn, R.string.art_sc_spnk46oxe12mtgn);
                            return;
                        case "g6j2wmv4oa0i9py":
                            textOnArt(R.string.art_g6j2wmv4oa0i9py, R.string.art_sc_g6j2wmv4oa0i9py);
                            return;
                        case "np3acmdvt6xr8hn":
                            textOnArt(R.string.art_np3acmdvt6xr8hn, R.string.art_sc_np3acmdvt6xr8hn);
                            return;
                        case "fiaivebni6cnb9p":
                            textOnArt(R.string.art_fiaivebni6cnb9p, R.string.art_sc_fiaivebni6cnb9p);
                            return;
                        case "gxh140w1ecda3za":
                            textOnArt(R.string.art_gxh140w1ecda3za, R.string.art_sc_gxh140w1ecda3za);
                            return;
                        case "aafnwujc0qo26s7":
                            barcodeValue.setText(R.string.art_aafnwujc0qo26s7);
                            return;
                        case "en82khxmk0":
                            textOnArt(R.string.art_en82khxmk0, R.string.art_sc_en82khxmk0);
                            return;
                        case "wwpe7bv0it":
                            textOnArt(R.string.art_wwpe7bv0it, R.string.art_sc_wwpe7bv0it);
                            return;
                        case "3ujhpmjg62":// сердце оазиса - артос хохла
                            textOnArt(R.string.saveArt, R.string.art_sc_3ujhpmjg62);
                            intent.putExtra("Command", "art_oasis");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "tuo2y36yos":
                            textOnArt(R.string.saveArt, R.string.art_sc_tuo2y36yos);
                            return;
                        case "z4bc2o17wu":
                            textOnArt(R.string.saveArt, R.string.art_sc_z4bc2o17wu);
                            return;
                        case "6c8qk0t4ae":
                            textOnArt(R.string.saveArt, R.string.art_sc_6c8qk0t4ae);
                            return;
                        case "nqhfvux1mx":
                            textOnArt(R.string.saveArt, R.string.art_sc_nqhfvux1mx);
                            return;
                        case "986yh87wk1":
                            textOnArt(R.string.saveArt, R.string.art_sc_986yh87wk1);
                            return;
                        case "3o9gx3u9t1":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_3o9gx3u9t1);
                            stalkerRoulette();
                            return;
                        case "u52chbbba2":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_u52chbbba2);
                            stalkerRoulette();
                            return;
                        case "t2qe5jps6z":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_t2qe5jps6z);
                            stalkerRoulette();
                            return;
                        case "gegquzng80":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_gegquzng80);
                            stalkerRoulette();
                            return;
                        case "4d3z35kuj9":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_4d3z35kuj9);
                            stalkerRoulette();
                            return;
                        case "2q8sxnv21f":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_2q8sxnv21f);
                            stalkerRoulette();
                            return;
                        case "3we3y0nawh":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_3we3y0nawh);
                            stalkerRoulette();
                            return;
                        case "huuldrzpbi":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_huuldrzpbi);
                            stalkerRoulette();
                            return;
                        case "wl46pjuhp9":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_wl46pjuhp9);
                            stalkerRoulette();
                            return;
                        case "bfedem00cd":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_bfedem00cd);
                            stalkerRoulette();
                            return;
                        case "705i20mbr4":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_705i20mbr4);
                            stalkerRoulette();
                            return;
                        case "0t043n0v26":
                            textOnArt(R.string.dangerousArt, R.string.art_sc_0t043n0v26);
                            stalkerRoulette();
                            return;
                        case "yypfxvjd8s96uem":
                            textOnArt(R.string.art_yypfxvjd8s96uem, R.string.art_sc_yypfxvjd8s96uem);
                            return;
                        case "fVGQDa7DLoGm7Dd":
                            textOnArt(R.string.art_fVGQDa7DLoGm7Dd, R.string.art_sc_fVGQDa7DLoGm7Dd);
                            return;
                        case "dke53J5WmAcZ3h3":
                            textOnArt(R.string.art_dke53J5WmAcZ3h3, R.string.art_sc_dke53J5WmAcZ3h3);
                            return;
                        case "x66qcc1tm5i247i":
                            textOnArt(R.string.art_x66qcc1tm5i247i, R.string.art_sc_x66qcc1tm5i247i);
                            return;
                        case "yp85b0d6d6kymw6":
                            textOnArt(R.string.art_yp85b0d6d6kymw6, R.string.art_sc_yp85b0d6d6kymw6);
                            return;
                        case "2u2j1kps9l3gfso":
                            textOnArt(R.string.art_2u2j1kps9l3gfso, R.string.art_sc_2u2j1kps9l3gfso);
                            return;
                        case "xonasooeq6evjnx":
                            textOnArt(R.string.art_xonasooeq6evjnx, R.string.art_sc_xonasooeq6evjnx);
                            return;
                        case "nkf9a8t7opd0ram":
                            textOnArt(R.string.art_xonasooeq6evjnx, R.string.art_sc_xonasooeq6evjnx);
                            return;
                        case "59b1izcgek2tide":
                            textOnArt(R.string.art_59b1izcgek2tide, R.string.art_sc_59b1izcgek2tide);
                            return;
                        case "ibyyrhj490ppw5q":
                            textOnArt(R.string.art_ibyyrhj490ppw5q, R.string.art_sc_ibyyrhj490ppw5q);
                            return;
                        case "16bipesl1lsl0wg":
                            textOnArt(R.string.art_16bipesl1lsl0wg, R.string.art_sc_16bipesl1lsl0wg);
                            return;
                        case "d7thk3dtj8un343":
                            textOnArt(R.string.art_d7thk3dtj8un343, R.string.art_sc_d7thk3dtj8un343);
                            return;
                        case "cwchdhrv4fccn5e":
                            textOnArt(R.string.art_cwchdhrv4fccn5e, R.string.art_sc_cwchdhrv4fccn5e);
                            return;
                        case "0s2vvvef7bbutrs":
                            textOnArt(R.string.art_0s2vvvef7bbutrs, R.string.art_sc_0s2vvvef7bbutrs);
                            return;
                        case "bxw7w19a9f8czcv":
                            textOnArt(R.string.art_bxw7w19a9f8czcv, R.string.art_sc_bxw7w19a9f8czcv);
                            return;
                        case "wU81zqJLwLTYFAm":
                            textOnArt(R.string.art_wU81zqJLwLTYFAm, R.string.art_sc_wU81zqJLwLTYFAm);
                            return;
                        case "yv0gu012zj93vsl":
                            textOnArt(R.string.art_yv0gu012zj93vsl, R.string.art_sc_yv0gu012zj93vsl);
                            return;
                        case "rybkjlnsl38tjsp":
                            textOnArt(R.string.art_rybkjlnsl38tjsp, R.string.art_sc_rybkjlnsl38tjsp);
                            return;
                        case "owg0zvzs5xoag91":
                            textOnArt(R.string.art_owg0zvzs5xoag91, R.string.art_sc_owg0zvzs5xoag91);
                            return;
                        case "aaobu7zvbwcknhc":
                            textOnArt(R.string.art_aaobu7zvbwcknhc, R.string.art_sc_aaobu7zvbwcknhc);
                            return;
                        case "nm7s576l0i":
                            Arrays.fill(monolithMech, false);
                            monolithMech[0] = true;
                            statusMessage.setText(composites[10]);
                            barcodeValue.setText(R.string.art_nm7s576l0i);
                            return;
                        case "vz6rafxyei":
                            if (monolithMech[0] & !monolithMech[1]) {
                                monolithMech[1] = true;
                                statusMessage.setText(composites[11]);
                                barcodeValue.setText(R.string.art_vz6rafxyei);
                            }else{
                                statusMessage.setText(composites[13]);
                                Arrays.fill(monolithMech, false);
                            }
                            return;
                        case "jt0dfct2w0": // механизм монолита
                            if (Calendar.getInstance().getTimeInMillis() - cooldown_time[14] > 43200000) {
                                if (monolithMech[0] & monolithMech[1]) {
                                    Arrays.fill(monolithMech, false);
                                    cooldown_time[14] = Calendar.getInstance().getTimeInMillis();
                                    statusMessage.setText(composites[12]);
                                    barcodeValue.setText(R.string.art_jt0dfct2w0);
                                    intent.putExtra("Command", "monolithStrong");
                                    Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                }else{
                                    statusMessage.setText(composites[13]);
                                    Arrays.fill(monolithMech, false);
                                }
                            }else{
                                statusMessage.setText(composites[14]);
                                Arrays.fill(monolithMech, false);
                            }
                            return;
                        case "jtodfct2w0":
                            if (scienceQR){
                                barcodeValue.setText(R.string.art_sc_jtodfct2w0);
                            }
                            if (Calendar.getInstance().getTimeInMillis() - cooldown_time[14] > 43200000) {
                                if (monolithMech[0] & monolithMech[1]) {
                                    Arrays.fill(monolithMech, false);
                                    cooldown_time[14] = Calendar.getInstance().getTimeInMillis();
                                    statusMessage.setText(R.string.art_jt0dfct2w0);
                                    barcodeValue.setText(R.string.art_jtodfct2w0);
                                    intent.putExtra("Command", "monolithWeak");
                                    Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                                }else{
                                    statusMessage.setText(composites[13]);
                                    Arrays.fill(monolithMech, false);
                                }
                            }else{
                                statusMessage.setText(composites[14]);
                                Arrays.fill(monolithMech, false);
                            }
                            return;
                        case "p58QbpBST5KvByt": // благословление монолита на 15 минут защиты от пси
                            barcodeValue.setText(R.string.art_p58QbpBST5KvByt);
                            intent.putExtra("Command", "monolith_blessing");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "4kL3GuNJaG9ZxSM":
                            textOnArt(R.string.art_4kL3GuNJaG9ZxSM, R.string.art_sc_4kL3GuNJaG9ZxSM);
                            return;
                        case "uyAqwPXXLU8MY3r":
                            textOnArt(R.string.art_uyAqwPXXLU8MY3r, R.string.art_sc_uyAqwPXXLU8MY3r);
                            return;
                        case "gUrZPkqLZ4xJ8ZR": // +10 защиты от рад
                            barcodeValue.setText(R.string.art_gUrZPkqLZ4xJ8ZR);
                            intent.putExtra("Command", "plus10RadProtection");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "tbNk88P4NyE3A39": // +10 защиты от био
                            barcodeValue.setText(R.string.art_tbNk88P4NyE3A39);
                            intent.putExtra("Command", "plus10BioProtection");
                            Objects.requireNonNull(QRTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                            return;
                        case "mutAPnJHY84gG6f":
                            textOnArt(R.string.art_mutAPnJHY84gG6f, R.string.art_sc_mutAPnJHY84gG6f);
                            return;
                        case "mezfB2Jn7H8n2JP":
                            barcodeValue.setText(R.string.art_mezfB2Jn7H8n2JP);
                            return;
                        case "FuJyu9rPLWKq6rw":
                            barcodeValue.setText(R.string.art_FuJyu9rPLWKq6rw);
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
                        default:
                            barcodeValue.setText("иди своей дорогой, сталкер");
                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    String str = "plus10RadProtection"; //"Соня не убивала" -1022716346     // "health5" 795560281       // "BDplus2Health"    -16716590  // "dolgDischargeImmunity"  -1449685624 // gestalt_closed_3   1910275381
                    String str2 = "plus10BioProtection"; //"ScienceQR" -1555514523    // "health25" -1107435105     // "BDplus5Health"   -1649172843  // "dolgDischargeImmunity"  1259972122      // gestalt_closed_4   1910275382
                    String str3 = "долматинец"; //"ScienceQRoff" -1930888214 //  "health50" -1107435017    // "BDplus10Health"    1381804599  // "mechMinus60Rad"  -1658045336             // monolithStrong   1989494219
                    String str4 = "азесмьцарь"; //"G" 71                  // "health75"  -1107434950       // "BDplus45HealthRandom"  1036792636  //  "mechMinus60Bio"  -1658060453        // monolithWeak  1749658540
                    String str5 = "доставщик"; // "gestalt_closed" 1704779201     // "health100"  29249045         // "BDminus5Health"  -944954941  // "mechPlus70Health"  -232827188      // monolith_blessing -63138094
                    String str6 = "шпагат";             // "gestalt_closed_2" 1910275380   // "radProtection100" 1293683299  // "BDminus10HealthRandom"  804709100     // "setRad0"  1984920125   // plus10RadProtection  852949013
                    String str7 = "поперечный";               // "всегдазакрыт" -1925203169       // "radProt10030"  -1800724366   // "BDminus21HealthRandom"  5747468       // "setBio15"  1388454378  //  plus10BioProtection  -301504184
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
    private void textOnArt (int nonScience, int science){
        if (scienceQR) {
            barcodeValue.setText(science);
        } else {
            barcodeValue.setText(nonScience);
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
// сохраняет текст от последнего отсканированного qr
    public void SaveBarcodeText() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
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
        edit.commit();
    }
// загружает текст от последнего отсканированного qr
    public void LoadBarcodeText() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SaveBarcodeText();
    }
}

