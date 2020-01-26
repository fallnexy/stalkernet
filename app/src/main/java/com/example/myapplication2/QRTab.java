package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QRTab extends Fragment implements View.OnClickListener{

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    Random random = new Random();

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_qr, viewGroup, false);

        statusMessage = (TextView)inflate.findViewById(R.id.status_message);
        barcodeValue = (TextView)inflate.findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) inflate.findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) inflate.findViewById(R.id.use_flash);

        inflate.findViewById(R.id.read_barcode).setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
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

                    //считывает qr код и в соответствии с case выдает нужный текст
                    switch (barcode.displayValue){
                        case "a":
                            barcodeValue.setText("приветствую тебя, товарищ сталкер");
                            return;
                        case "a1":
                            barcodeValue.setText("иди своей дорогой сталкер");
                            return;
                        case "abba":
                            stalkerRoulette();
                            return;
                        default:
                            barcodeValue.setText(barcode.displayValue);
                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
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

    public void stalkerRoulette (){
        int randomStalkerRoulette = random.nextInt(8);
        switch (randomStalkerRoulette) {
            case 0:
                barcodeValue.setText("-10% hp");
                return;
            case 1:
                barcodeValue.setText("-20% hp");
                return;
            case 2:
                barcodeValue.setText("-25% hp");
                return;
            case 3:
                barcodeValue.setText("+1 rad");
                return;
            case 4:
                barcodeValue.setText("+1 bio");
                return;
            case 5:
                barcodeValue.setText("+1 psi");
                return;
            case 6:
                barcodeValue.setText("+5 hp");
                return;
            case 7:
                barcodeValue.setText("+7 rad");
        }
    }
}

