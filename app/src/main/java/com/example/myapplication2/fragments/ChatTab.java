package com.example.myapplication2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication2.AnomalyTypeDialog;
import com.example.myapplication2.CodesQRAndText;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatTab extends Fragment {

    Globals globals;
    CodesQRAndText codesQRAndText;
    AnomalyTypeDialog dialog = new AnomalyTypeDialog();
    Bundle argsDialog = new Bundle();

    public ChatTab(Globals globals) {
        this.globals = globals;
    }


    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chat, viewGroup, false);
        final EditText editText = inflate.findViewById(R.id.CommandLine);
        TextView txtView = inflate.findViewById(R.id.txtViewChat);

        codesQRAndText = new CodesQRAndText(this, txtView, globals);

        inflate.findViewById(R.id.btnBroadcastCommand).setOnClickListener(view -> {
            Intent intent;
            int var3;

            String code = String.valueOf(editText.getText());

            codesQRAndText.checkCode(code, globals.ScienceQR == 1);

            label94: {

                editText.setText("");
                intent = new Intent("Command");
                switch(code.hashCode()) {
                        // старые коды
                    case 1456976519: // пси
                        if (code.equals("191000")) {
                            var3 = 1;
                            break label94;
                        }
                        break;
                    case 1456976674:
                        if (code.equals("191050")) {
                            var3 = 2;
                            break label94;
                        }
                        break;
                    case 1456977480:
                        if (code.equals("191100")) {
                            var3 = 3;
                            break label94;
                        }
                        break;
                    case 1455129477: //рад
                        if (code.equals("171000")) {
                            var3 = 4;
                            break label94;
                        }
                        break;
                    case 1455129632:
                        if (code.equals("171050")) {
                            var3 = 5;
                            break label94;
                        }
                        break;
                    case 1455130438:
                        if (code.equals("171100")) {
                            var3 = 6;
                            break label94;
                        }
                        break;
                    case 1456052998: // био
                        if (code.equals("181000")) {
                            var3 = 7;
                            break label94;
                        }
                        break;
                    case 1456053153:
                        if (code.equals("181050")) {
                            var3 = 8;
                            break label94;
                        }
                        break;
                    case 1456053959:
                        if (code.equals("181100")) {
                            var3 = 9;
                            break label94;
                        }
                        break;


                    case -1945318196:
                        if (code.equals("шпагат")) {
                            var3 = 12;
                            break label94;
                        }
                        break;
                    case 1572782670:
                        if (code.equals("поперечный")) {
                            var3 = 13;
                            break label94;
                        }
                        break;
                    case 2063766181: // монолит
                        if (code.equals("далматинец")) {
                            var3 = 16;
                            break label94;
                        }
                        break;
                    case 1711354489: // режим бога
                        if (code.equals("азесмьцарь")) {
                            var3 = 17;
                            break label94;
                        }
                    case 1730932747: //персональный выброс
                        if (code.equals("выброс")) {
                            var3 = 18;
                            break label94;
                        }
                        break;
                        // новые коды
                    case -1925203169: //гештальт защита
                        if (code.equals("всегдазакрыт")) {
                            var3 = 19;
                            break label94;
                        }
                    case 1974805046: //гештальт защита снята
                        if (code.equals("теперьоткрыт")) {
                            var3 = 20;
                            break label94;
                        }


                    case 272021583: //
                        if (code.equals("коссева")) {
                            /*textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "80";*/
                            var3 = 21;
                            break label94;
                        }
                    case 271719333: //
                        if (code.equals("косзаря")) {
                            /*textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "rad";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "80";*/
                            var3 = 21;
                            break label94;
                        }
                    case -156863704: //
                        if (code.equals("косстраж")) {
                            /*textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "50";*/
                            var3 = 21;
                            break label94;
                        }
                    case -430325800: //
                        if (code.equals("стражснять")) {
                            /*textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "0";*/
                            var3 = 21;
                            break label94;
                        }
                    case 272098569: //
                        if (code.equals("косучен")) {
                            var3 = 28;
                            break label94;
                        }
                    case 1045731098: //
                        if (code.equals("штраф")) {
                            var3 = 29;
                            break label94;
                        }
                    case 1825756908: //
                        if (code.equals("явсемогущий-")) {
                            var3 = 27;
                            break label94;
                        }
                    case 1825756906: //
                        if (code.equals("явсемогущий+")) {
                            var3 = 17;
                            break label94;
                        }
                    case 425265276: //
                        if (code.equals("чекинвыброс")) {
                            var3 = 30;
                            break label94;
                        }
                    case -1113320671: //
                        if (code.equals("чнзнает")) {
                            var3 = 31;
                            break label94;
                        }
                    case -1792935999: //
                        if (code.equals("gloriamonolithhaereticorummors")) {
                            var3 = 32;
                            break label94;
                        }
                    case -1759609022: //
                        if (code.equals("наукада")) {
                            var3 = 33;
                            break label94;
                        }
                    case 2021447335: //
                        if (code.equals("наука-")) {
                            var3 = 34;
                            break label94;
                        }
                }

                var3 = -1;
            }

            switch(var3) {
                case 0:
                    intent.putExtra("Command", "ResetStats");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 1:
                    argsDialog.putString("type", "Psy");
                    dialog.setArguments(argsDialog);
                    dialog.show(requireActivity().getSupportFragmentManager(), "custom");
                    intent.putExtra("Command", "SetPsyProtection00");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 2:
                    intent.putExtra("Command", "SetPsyProtection50");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 3:
                    intent.putExtra("Command", "SetPsyProtection100");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 4:
                    intent.putExtra("Command", "SetRadProtection0");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 5:
                    intent.putExtra("Command", "SetRadProtection50");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 6:
                    intent.putExtra("Command", "SetRadProtection100");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 7:
                    intent.putExtra("Command", "SetBioProtection0");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 8:
                    intent.putExtra("Command", "SetBioProtection50");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 9:
                    intent.putExtra("Command", "SetBioProtection100");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;

                case 12:
                    intent.putExtra("Command", "SetMaxHealth100");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 13:
                    intent.putExtra("Command", "SetMaxHealth200");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;


                case 16:
                    intent.putExtra("Command", "Monolith");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 17:
                    intent.putExtra("Command", "God");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 18:
                    intent.putExtra("Command", "Discharge");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 19:
                    intent.putExtra("Command", "SetGesProtection");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 20:
                    intent.putExtra("Command", "SetGesProtectionOFF");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;


                case 28:
                    intent.putExtra("Command", "sc1, rad, suit, 80");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                     intent.putExtra("Command", "sc1, bio, suit, 80");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 29:
                    intent.putExtra("Command", "штраф");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 30:
                    intent.putExtra("Command", "discharge10BD");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 31:
                    intent.putExtra("Command", "dolgDischargeImmunity");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 32:
                    txtView.setText("Если у тебя нет жетона Монолита, то ты становишься адептом Монолита. Выйди на связь с бойцами Монолита, чтобы получить задание. Задача по умолчанию: не допустить уничтожение Монолита.\nКоличество разрешенных защит увеличено на 1\n\nЕсли у тебя есть жетон, то с возвращением в семью, брат!");
                    intent.putExtra("Command", "SetPsyProtection0");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 33:
                    intent.putExtra("Command", "ScienceQR");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 34:
                    intent.putExtra("Command", "ScienceQRoff");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
            }

        });
        inflate.findViewById(R.id.btnStopVib).setOnClickListener(view -> {
            Intent intent = new Intent("Command");
            intent.putExtra("Command", "StopVib");
            ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
        });
        inflate.findViewById(R.id.btnOnVib).setOnClickListener(view -> {
            Intent intent = new Intent("Command");
            intent.putExtra("Command", "OnVib");
            ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
        });
        return inflate;
    }
}
