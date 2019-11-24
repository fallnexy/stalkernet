package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatTab extends Fragment {
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chat, viewGroup, false);
        final EditText editText = (EditText) inflate.findViewById(R.id.CommandLine);
        ((Button) inflate.findViewById(R.id.btnBroadcastCommand)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View r3) { //r3 - что такое, вопрос
                Intent var2;
                byte var3;
                label94: {
                    String var4 = String.valueOf(editText.getText());
                    editText.setText("");
                    var2 = new Intent("Command");
                    switch(var4.hashCode()) {
                        case -2126025274:
                            if (var4.equals("С‚РµРєРёР»Р°")) {
                                var3 = 13;
                                break label94;
                            }
                            break;
                        case -2059769973:
                            if (var4.equals("гагарин")) {
                                var3 = 0;
                                break label94;
                            }
                            break;
                        case -1631526413:
                            if (var4.equals("СЃС‚СЂР°РЅРЅРёРє")) {
                                var3 = 10;
                                break label94;
                            }
                            break;
                        case 1508416:
                            if (var4.equals("1111")) {
                                var3 = 18;
                                break label94;
                            }
                            break;
                        case 52301078:
                            if (var4.equals("71000")) {
                                var3 = 4;
                                break label94;
                            }
                            break;
                        case 52301233:
                            if (var4.equals("71050")) {
                                var3 = 5;
                                break label94;
                            }
                            break;
                        case 52302039:
                            if (var4.equals("71100")) {
                                var3 = 6;
                                break label94;
                            }
                            break;
                        case 53224599:
                            if (var4.equals("81000")) {
                                var3 = 7;
                                break label94;
                            }
                            break;
                        case 53224754:
                            if (var4.equals("81050")) {
                                var3 = 8;
                                break label94;
                            }
                            break;
                        case 53225560:
                            if (var4.equals("81100")) {
                                var3 = 9;
                                break label94;
                            }
                            break;
                        case 54148120:
                            if (var4.equals("91000")) {
                                var3 = 1;
                                break label94;
                            }
                            break;
                        case 54148275:
                            if (var4.equals("91050")) {
                                var3 = 2;
                                break label94;
                            }
                            break;
                        case 54149081:
                            if (var4.equals("91100")) {
                                var3 = 3;
                                break label94;
                            }
                            break;
                        case 54331765:
                            if (var4.equals("97531")) {
                                var3 = 14;
                                break label94;
                            }
                            break;
                        case 928548519:
                            if (var4.equals("Р±СЂРѕРґСЏРіР°")) {
                                var3 = 11;
                                break label94;
                            }
                            break;
                        case 1025116985:
                            if (var4.equals("РІРёСЃРєРё")) {
                                var3 = 12;
                                break label94;
                            }
                            break;
                        case 1534645504:
                            if (var4.equals("404404")) {
                                var3 = 16;
                                break label94;
                            }
                            break;
                        case 1563300753:
                            if (var4.equals("505050")) {
                                var3 = 15;
                                break label94;
                            }
                            break;
                        case 1662424529:
                            if (var4.equals("яестьгрут")) {
                                var3 = 17;
                                break label94;
                            }
                    }

                    var3 = -1;
                }

                switch(var3) {
                    case 0:
                        var2.putExtra("Command", "ResetStats");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 1:
                        var2.putExtra("Command", "SetPsyProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 2:
                        var2.putExtra("Command", "SetPsyProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 3:
                        var2.putExtra("Command", "SetPsyProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 4:
                        var2.putExtra("Command", "SetRadProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 5:
                        var2.putExtra("Command", "SetRadProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 6:
                        var2.putExtra("Command", "SetRadProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 7:
                        var2.putExtra("Command", "SetBioProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 8:
                        var2.putExtra("Command", "SetBioProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 9:
                        var2.putExtra("Command", "SetBioProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 10:
                        var2.putExtra("Command", "SetDischargeImmunityTrue");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 11:
                        var2.putExtra("Command", "SetDischargeImmunityFalse");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 12:
                        var2.putExtra("Command", "SetMaxHealth100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 13:
                        var2.putExtra("Command", "SetMaxHealth200");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 14:
                        var2.putExtra("Command", "MakeAlive");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 15:
                        var2.putExtra("Command", "ComboResetProtections");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 16:
                        var2.putExtra("Command", "Monolith");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 17:
                        var2.putExtra("Command", "God");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                        break;
                    case 18:
                        var2.putExtra("Command", "Discharge");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(var2);
                }

            }
        });
        ((Button) inflate.findViewById(R.id.btnStopVib)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("Command");
                intent.putExtra("Command", "StopVib");
                ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
            }
        });
        ((Button) inflate.findViewById(R.id.btnOnVib)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("Command");
                intent.putExtra("Command", "OnVib");
                ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
            }
        });
        return inflate;
    }
}
