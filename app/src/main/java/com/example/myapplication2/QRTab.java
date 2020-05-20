package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
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
        return inflate;
    }

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
                        case "Sonia didn't kill":  //включает QR ученого
                            btnScienceQR.setVisibility(View.VISIBLE);
                            barcodeValue.setText("ScienceQR on");
                            intent.putExtra("Command", "ScienceQR");
                            QRTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "Sonia kills":  //отключает QR ученого
                            btnScienceQR.setVisibility(View.INVISIBLE);
                            barcodeValue.setText("ScienceQR off");
                            intent.putExtra("Command", "ScienceQRoff");
                            QRTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "geshtalt closed":
                            barcodeValue.setText("Поздравляем, гештальт 1 закрыт! У вас минута, чтоб свалить отсюда");
                            intent.putExtra("Command", "geshtalt closed");
                            QRTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "geshtalt closed 2":
                            barcodeValue.setText("Поздравляем, гештальт 2 закрыт! У вас минута, чтоб свалить отсюда");
                            intent.putExtra("Command", "geshtalt closed 2");
                            QRTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                            return;
                        case "a":
                            Spanned str = Html.fromHtml("<font color=\"red\">Привет.</font> <font color=\"yellow\">как </font> <font color=\"blue\">дела?</font>");
                            barcodeValue.setText(str);
                            return;
                        case "a1":
                            if (scienceQR) {
                                barcodeValue.setText("сталкер, для тебя есть квест");
                            } else {
                                barcodeValue.setText("иди своей дорогой сталкер");
                            }
                            return;
                        case "abba":
                            stalkerRoulette();
                            return;
                        default:
                            stalkerRoulette();
                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    String str = "SetGesProtectionOFF"; //"Соня не убивала" -1022716346
                                              //"ScienceQR" -1555514523
                                              //"ScienceQRoff" -1930888214
                                              //"G" 71
                                              // "geshtalt closed" 2064168356
                                              // "geshtalt closed 2" -609099594
                                              // "всегдазакрыт" -1925203169
                                              //  "SetGesProtection" 317294316
                                              //  "теперьоткрыт" 1974805046
                                              //  "SetGesProtectionOFF" -707972381
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null " + str.hashCode());
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
        barcodeValue.setText(text);
        Objects.requireNonNull(getActivity()).getApplicationContext().sendBroadcast(intent);
    }
// знчения в рулекте увеличены
    private void stalkerRoulette(){
        int randomStalkerRoulette = random.nextInt(8);
        switch(randomStalkerRoulette) {
            case 0:
                stalkerRouletteSolved ("RadPlusOne", "+10 rad");
                break;
            case 1:
                stalkerRouletteSolved ("BioPlusOne", "+10 bio");
                break;
            case 2:
                stalkerRouletteSolved ("PsyPlusOne", "+10 psy");
                break;
            case 3:
                stalkerRouletteSolved ("HpPlusFive", "+10 hp");
                break;
            case 4:
                stalkerRouletteSolved ("HpPlusSeven", "+20 hp");
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
}

