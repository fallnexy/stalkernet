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

        codesQRAndText = new CodesQRAndText(this, txtView);

        inflate.findViewById(R.id.btnBroadcastCommand).setOnClickListener(view -> {
            Intent intent;
            int var3;

            String code = String.valueOf(editText.getText());
            MakeSplit(code);
            if (textCode.equals("sc1") | textCode.equals("sc2")) {
                code = textCode;
            }
            codesQRAndText.checkCode(code);
            codesQRAndText.checkCode(code, globals.ScienceQR == 1);

            label94: {

                editText.setText("");
                intent = new Intent("Command");
                switch(code.hashCode()) {
                        // старые коды
                    // переехал в codesQRAndText
                   /* case 1025788929: // полное воскрешение со сбросом всех параметров
                        if (code.equals("гагры")) {
                            var3 = 0;
                            break label94;
                        }
                        break;*/
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
                    case -48468164:
                        if (code.equals("зона5звезд")) {
                            var3 = 10;
                            break label94;
                        }
                        break;
                    case 1698598526:
                        if (code.equals("доставщик")) {
                            var3 = 11;
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
                    case 702574009: // простое оживление
                        if (code.equals("приветбумеранг")) {
                            var3 = 14;
                            break label94;
                        }
                        break;
                    case 1563300753:
                        if (code.equals("505050")) {
                            var3 = 15;
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
                    case 113633: // умный код 1
                        if (code.equals("sc1")) {
                            var3 = 21;
                            break label94;
                        }
                    case 113634: // умный код 2
                        if (code.equals("sc2")) {
                            var3 = 22;
                            break label94;
                        }
                    case 697322052: //
                        if (code.equals("разреш1тип")) {
                            var3 = 23;
                            break label94;
                        }
                    case 697351843: //
                        if (code.equals("разреш2тип")) {
                            var3 = 24;
                            break label94;
                        }
                    case 697381634: //
                        if (code.equals("разреш3тип")) {
                            var3 = 25;
                            break label94;
                        }
                    case -604537487: //
                        if (code.equals("выходигрока")) {
                            var3 = 26;
                            break label94;
                        }
                    case -189541994: //
                        if (code.equals("снятьнеуяз")) {
                            var3 = 27;
                            break label94;
                        }
                    case 272021583: //
                        if (code.equals("коссева")) {
                            textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "80";
                            var3 = 21;
                            break label94;
                        }
                    case 271719333: //
                        if (code.equals("косзаря")) {
                            textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "rad";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "80";
                            var3 = 21;
                            break label94;
                        }
                    case -156863704: //
                        if (code.equals("косстраж")) {
                            textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "50";
                            var3 = 21;
                            break label94;
                        }
                    case -430325800: //
                        if (code.equals("стражснять")) {
                            textCodeSplitted[0] = "sc1";
                            textCodeSplitted[1] = "bio";
                            textCodeSplitted[2] = "suit";
                            textCodeSplitted[3] = "0";
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
                case 10:
                    intent.putExtra("Command", "SetDischargeImmunityTrue");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 11:
                    intent.putExtra("Command", "SetDischargeImmunityFalse");
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
                case 14:
                    intent.putExtra("Command", "MakeAlive");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 15:
                    intent.putExtra("Command", "ComboResetProtections");
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
                case 21: //sc1
                    //Log.d("wft", String.valueOf(textCodeSplitted));
                    anomalyTypeChecker(textCodeSplitted[1]);
                    intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 22: //sc2
                    //Log.d("wft", String.valueOf(textCodeSplitted));
                    intent.putExtra("Command", Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", ""));
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 23:
                    intent.putExtra("Command", "setOneProtAv");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 24:
                    intent.putExtra("Command", "setTwoProtAv");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 25:
                    intent.putExtra("Command", "setThreeProtAv");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 26:
                    intent.putExtra("Command", "15minutesGod");
                    ChatTab.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 27:
                    intent.putExtra("Command", "noMoreGod");
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

    public String textCode;
    public String[] textCodeSplitted = new String[4];
    public void MakeSplit(String input){
        try {
            Pattern pattern = Pattern.compile("[@]");
            String[] words = pattern.split(input);
            int i = 0;
            for(String word:words){
                textCodeSplitted[i] = word;
                i++;
            }
            if (textCodeSplitted[0].equals("sc1")) {
                if (textCodeSplitted[2].equals("suit") && Double.parseDouble(textCodeSplitted[3]) > 80){
                    textCodeSplitted[3] = "80";
                } else if (!textCodeSplitted[2].equals("suit") && Double.parseDouble(textCodeSplitted[3]) > 49.95){
                    textCodeSplitted[3] = "49.95";
                }
                if (Double.parseDouble(textCodeSplitted[3]) < 0){
                    textCodeSplitted[3] = "0";
                }
            }
            textCode = textCodeSplitted[0];
        } catch (Exception e) {
            textCode = input;
        }
    }


    String typeFirstProtection = "";
    String typeSecondProtection = "";
    //String typeThirdProtection = "";
    public void anomalyTypeChecker(String type){
        if (globals.MaxProtectionAvailable.getText().equals("Количество разрешенных защит: 2")) {
            int counter = 0;
            HashMap<String, String> protectionTypeMap = new HashMap<>();
            protectionTypeMap.put("rad", globals.TotalProtectionRad);
            protectionTypeMap.put("bio", globals.TotalProtectionBio);
            protectionTypeMap.put("psy", globals.TotalProtectionPsy);
            for (String protType : new String[] {"rad", "bio", "psy"}){
                if (!protType.equals(type) && Double.parseDouble(Objects.requireNonNull(protectionTypeMap.get(protType))) > 0){
                    counter++;
                }
            }

            if (counter == 2 && Double.parseDouble(Objects.requireNonNull(protectionTypeMap.get(type))) == 0){
                if (Double.parseDouble(globals.TotalProtectionRad) > 0){
                    typeFirstProtection = "Rad";
                    if (Double.parseDouble(globals.TotalProtectionBio) > 0){
                        typeSecondProtection = "Bio";
                       // typeThirdProtection = "Psy";
                    } else {
                        typeSecondProtection = "Psy";
                        //typeThirdProtection = "Bio";
                    }
                } else {
                    typeFirstProtection = "Bio";
                    typeSecondProtection = "Psy";
                    //typeThirdProtection = "Rad";
                }

                showDialog();

            }
        }
    }
    // Показывает диалог, в котором решается, какую лишнюю защиту оставить
    void showDialog(){
        AnomalyTypeDialog dialog = new AnomalyTypeDialog();
        Bundle args = new Bundle();
        args.putString("typeFirstProtection", typeFirstProtection);
        args.putString("typeSecondProtection", typeSecondProtection);
        //args.putString("typeThirdProtection", typeThirdProtection);
        dialog.setCancelable(false);
        dialog.setArguments(args);
        dialog.show(getActivity().getSupportFragmentManager(), "custom");
    }

}
