package com.example.stalkernet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
/*
* Диалог, который возникает, когда нажимается кнопка подтвержить квест в class QuestChildFragment.
* Если введенный текст совпадает с кодом текущего подквеста или с универсальным паролем universal,
* то передает в MainActivity значение true, что приводит к тому что в нужной таблице меняется статус
* подквеста на выполненный
*/
public class QuestConfirmDialog extends DialogFragment {

    private QuestConfirmInterface questConfirmInterface;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        questConfirmInterface = (QuestConfirmInterface) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quest_confirm, null);
        EditText eTxtCode = view.findViewById(R.id.eTxtCode);
        String type = getArguments().getString("type");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        if (type.equals("quest")) {
            String code = getArguments().getString("code");
            String groupPosition = getArguments().getString("group_position");
            String childPosition = getArguments().getString("child_position");

            return builder
                    .setView(view)
                    .setTitle("Подтверждение выполнения квеста")
                    .setMessage("Для подтверждения введите пароль и нажмите ОК")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (eTxtCode.getText().toString().equals(code) || eTxtCode.getText().toString().equals("universal")){
                                questConfirmInterface.confirmQuest("true", groupPosition, childPosition);
                            } else{
                                questConfirmInterface.confirmQuest("false", groupPosition, childPosition);
                            }
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .create();
        } else { // то есть creed
            String code = getArguments().getString("code");
            String groupPosition = getArguments().getString("group_position");
            String childPosition = getArguments().getString("child_position");
            return builder
                    .setView(view)
                    .setTitle("Подтверждение выбора кредо")
                    .setMessage("Для подтверждения введите пароль и нажмите ОК")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (eTxtCode.getText().toString().equals(code) || eTxtCode.getText().toString().equals("выполнил")){
                                questConfirmInterface.confirmCreed("true", groupPosition, childPosition);
                            } else{
                                questConfirmInterface.confirmCreed("false", groupPosition, childPosition);
                            }
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .create();
        }
    }
}
