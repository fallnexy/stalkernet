package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatTab extends Fragment {
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chat, viewGroup, false);
        final EditText editText = inflate.findViewById(R.id.CommandLine);
        inflate.findViewById(R.id.btnBroadcastCommand).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent;
                byte var3;
                label94: {
                    String code = String.valueOf(editText.getText());
                    editText.setText("");
                    intent = new Intent("Command");
                    switch(code.hashCode()) {
                            // старые коды
                        case -2074677495:
                            if (code.equals("хв2020")) {
                                var3 = 0;
                                break label94;
                            }
                            break;
                        case 1456976519:
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
                        case 1455129477:
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
                        case 1456052998:
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
                        case 1953035904:
                            if (code.equals("курьер")) {
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
                        case 53377460:
                            if (code.equals("86420")) {
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
                        case 2063766181:
                            if (code.equals("далматинец")) {
                                var3 = 16;
                                break label94;
                            }
                            break;
                        case 1711354489:
                            if (code.equals("азесмьцарь")) {
                                var3 = 17;
                                break label94;
                            }
                        case -2059769973:
                            if (code.equals("гагарин")) {
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
                    }

                    var3 = -1;
                }

                switch(var3) {
                    case 0:
                        intent.putExtra("Command", "ResetStats");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 1:
                        intent.putExtra("Command", "SetPsyProtection0");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 2:
                        intent.putExtra("Command", "SetPsyProtection50");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 3:
                        intent.putExtra("Command", "SetPsyProtection100");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 4:
                        intent.putExtra("Command", "SetRadProtection0");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 5:
                        intent.putExtra("Command", "SetRadProtection50");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 6:
                        intent.putExtra("Command", "SetRadProtection100");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 7:
                        intent.putExtra("Command", "SetBioProtection0");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 8:
                        intent.putExtra("Command", "SetBioProtection50");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 9:
                        intent.putExtra("Command", "SetBioProtection100");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 10:
                        intent.putExtra("Command", "SetDischargeImmunityTrue");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 11:
                        intent.putExtra("Command", "SetDischargeImmunityFalse");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 12:
                        intent.putExtra("Command", "SetMaxHealth100");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 13:
                        intent.putExtra("Command", "SetMaxHealth200");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 14:
                        intent.putExtra("Command", "MakeAlive");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 15:
                        intent.putExtra("Command", "ComboResetProtections");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 16:
                        intent.putExtra("Command", "Monolith");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 17:
                        intent.putExtra("Command", "God");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 18:
                        intent.putExtra("Command", "Discharge");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 19:
                        intent.putExtra("Command", "SetGesProtection");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                    case 20:
                        intent.putExtra("Command", "SetGesProtectionOFF");
                        Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
                        break;
                }

            }
        });
        inflate.findViewById(R.id.btnStopVib).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("Command");
                intent.putExtra("Command", "StopVib");
                Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
            }
        });
        inflate.findViewById(R.id.btnOnVib).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("Command");
                intent.putExtra("Command", "OnVib");
                Objects.requireNonNull(ChatTab.this.getActivity()).getApplicationContext().sendBroadcast(intent);
            }
        });
        return inflate;
    }
}
