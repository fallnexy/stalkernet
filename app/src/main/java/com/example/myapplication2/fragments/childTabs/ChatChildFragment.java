package com.example.myapplication2.fragments.childTabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.AnomalyTypeDialog;
import com.example.myapplication2.CodesQRAndText;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatChildFragment extends Fragment {

    Globals globals;
    CodesQRAndText codesQRAndText;
    AnomalyTypeDialog dialog = new AnomalyTypeDialog();
    Bundle argsDialog = new Bundle();

    public ChatChildFragment(Globals globals) {
        this.globals = globals;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chat_child, viewGroup, false);
        final EditText editText = inflate.findViewById(R.id.CommandLine);
        TextView txtView = inflate.findViewById(R.id.txtViewChat);

        ImageView ivAddAnomaly = inflate.findViewById(R.id.ivAddAnomaly);
        ImageView ivRemoveAnomaly = inflate.findViewById(R.id.ivRemoveAnomaly);
        // Inflate the layout for this fragment
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
                case 4:
                    intent.putExtra("Command", "SetRadProtection0");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 5:
                    intent.putExtra("Command", "SetRadProtection50");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 6:
                    intent.putExtra("Command", "SetRadProtection100");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 7:
                    intent.putExtra("Command", "SetBioProtection0");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 8:
                    intent.putExtra("Command", "SetBioProtection50");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 9:
                    intent.putExtra("Command", "SetBioProtection100");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 12:
                    intent.putExtra("Command", "SetMaxHealth100");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 13:
                    intent.putExtra("Command", "SetMaxHealth200");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 16:
                    intent.putExtra("Command", "Monolith");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 17:
                    intent.putExtra("Command", "God");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 18:
                    intent.putExtra("Command", "Discharge");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 19:
                    intent.putExtra("Command", "SetGesProtection");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 20:
                    intent.putExtra("Command", "SetGesProtectionOFF");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 28:
                    intent.putExtra("Command", "sc1, rad, suit, 80");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    intent.putExtra("Command", "sc1, bio, suit, 80");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 29:
                    intent.putExtra("Command", "штраф");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 30:
                    intent.putExtra("Command", "discharge10BD");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 33:
                    intent.putExtra("Command", "ScienceQR");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
                case 34:
                    intent.putExtra("Command", "ScienceQRoff");
                    ChatChildFragment.this.requireActivity().getApplicationContext().sendBroadcast(intent);
                    break;
            }

        });

        String[] textCodeSplitted = new String[6];
        inflate.findViewById(R.id.btnAddAnomaly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addAnomaly = editText.getText().toString().trim();
                String delAnomaly = "WTF";

                try {
                    Pattern pattern = Pattern.compile("[@]");
                    String[] words = pattern.split(addAnomaly);
                    int i = 0;
                    for(String word:words){
                        textCodeSplitted[i] = word;
                        i++;
                    }
                    if (textCodeSplitted[0].equals("sc3")){
                        textCodeSplitted[0] = "del";
                        delAnomaly = Arrays.toString(textCodeSplitted).replaceAll("[\\[\\]]", "");
                        delAnomaly = delAnomaly.replaceAll(", ", "@");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(addAnomaly, BarcodeFormat.QR_CODE, 350, 350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    ivAddAnomaly.setImageBitmap(bitmap);
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                try {
                    BitMatrix matrix = writer.encode(delAnomaly, BarcodeFormat.QR_CODE, 350, 350);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    ivRemoveAnomaly.setImageBitmap(bitmap);
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        return inflate;
    }
}