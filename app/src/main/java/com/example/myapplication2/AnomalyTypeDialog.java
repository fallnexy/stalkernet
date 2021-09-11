package com.example.myapplication2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.myapplication2.AnomalyTypeInterface;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AnomalyTypeDialog extends DialogFragment {

    private AnomalyTypeInterface anomalyTypeInterface;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        anomalyTypeInterface = (AnomalyTypeInterface) context;
    }


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String firstProtection = getArguments().getString("typeFirstProtection");
        String secondProtection = getArguments().getString("typeSecondProtection");
        String thirdProtection = getArguments().getString("typeThirdProtection");
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        return builder
                .setCancelable(false)
                .setTitle("Выбор защиты")
                .setMessage("Превышено количество доступных защит. Выберите, какую защиту оставить вместе с только что полученной:")
                .setPositiveButton(firstProtection, (dialogInterface, i) -> anomalyTypeInterface.nullifyProtection(secondProtection, thirdProtection))
                .setNegativeButton(secondProtection, (dialogInterface, i) -> anomalyTypeInterface.nullifyProtection(firstProtection, thirdProtection))
                .create();
    }

}
