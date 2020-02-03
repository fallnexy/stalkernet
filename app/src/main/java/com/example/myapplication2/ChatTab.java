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
                        case -2126025274:
                            if (code.equals("текила")) {
                                var3 = 13;
                                break label94;
                            }
                            break;
                        case -2059769973:
                            if (code.equals("гагарин")) {
                                var3 = 0;
                                break label94;
                            }
                            break;
                        case -1631526413:
                            if (code.equals("странник")) {
                                var3 = 10;
                                break label94;
                            }
                            break;
                        case 1508416:
                            if (code.equals("1111")) {
                                var3 = 18;
                                break label94;
                            }
                            break;
                        case 52301078:
                            if (code.equals("71000")) {
                                var3 = 4;
                                break label94;
                            }
                            break;
                        case 52301233:
                            if (code.equals("71050")) {
                                var3 = 5;
                                break label94;
                            }
                            break;
                        case 52302039:
                            if (code.equals("71100")) {
                                var3 = 6;
                                break label94;
                            }
                            break;
                        case 53224599:
                            if (code.equals("81000")) {
                                var3 = 7;
                                break label94;
                            }
                            break;
                        case 53224754:
                            if (code.equals("81050")) {
                                var3 = 8;
                                break label94;
                            }
                            break;
                        case 53225560:
                            if (code.equals("81100")) {
                                var3 = 9;
                                break label94;
                            }
                            break;
                        case 54148120:
                            if (code.equals("91000")) {
                                var3 = 1;
                                break label94;
                            }
                            break;
                        case 54148275:
                            if (code.equals("91050")) {
                                var3 = 2;
                                break label94;
                            }
                            break;
                        case 54149081:
                            if (code.equals("91100")) {
                                var3 = 3;
                                break label94;
                            }
                            break;
                        case 54331765:
                            if (code.equals("97531")) {
                                var3 = 14;
                                break label94;
                            }
                            break;
                        case 928548519:
                            if (code.equals("бродяга")) {
                                var3 = 11;
                                break label94;
                            }
                            break;
                        case 1025116985:
                            if (code.equals("виски")) {
                                var3 = 12;
                                break label94;
                            }
                            break;
                        case 1534645504:
                            if (code.equals("404404")) {
                                var3 = 16;
                                break label94;
                            }
                            break;
                        case 1563300753:
                            if (code.equals("505050")) {
                                var3 = 15;
                                break label94;
                            }
                            break;
                        case 1662424529:
                            if (code.equals("яестьгрут")) {
                                var3 = 17;
                                break label94;
                            }
                    }

                    var3 = -1;
                }

                switch(var3) {
                    case 0:
                        intent.putExtra("Command", "ResetStats");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 1:
                        intent.putExtra("Command", "SetPsyProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 2:
                        intent.putExtra("Command", "SetPsyProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 3:
                        intent.putExtra("Command", "SetPsyProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 4:
                        intent.putExtra("Command", "SetRadProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 5:
                        intent.putExtra("Command", "SetRadProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 6:
                        intent.putExtra("Command", "SetRadProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 7:
                        intent.putExtra("Command", "SetBioProtection0");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 8:
                        intent.putExtra("Command", "SetBioProtection50");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 9:
                        intent.putExtra("Command", "SetBioProtection100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 10:
                        intent.putExtra("Command", "SetDischargeImmunityTrue");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 11:
                        intent.putExtra("Command", "SetDischargeImmunityFalse");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 12:
                        intent.putExtra("Command", "SetMaxHealth100");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 13:
                        intent.putExtra("Command", "SetMaxHealth200");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 14:
                        intent.putExtra("Command", "MakeAlive");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 15:
                        intent.putExtra("Command", "ComboResetProtections");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 16:
                        intent.putExtra("Command", "Monolith");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 17:
                        intent.putExtra("Command", "God");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
                        break;
                    case 18:
                        intent.putExtra("Command", "Discharge");
                        ChatTab.this.getActivity().getApplicationContext().sendBroadcast(intent);
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
