package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GeneralTab extends Fragment {
    private Globals G;

    public GeneralTab(Globals globals) {
        this.G = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_main, viewGroup, false);
        ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.ProgressHealth);
        ProgressBar progressBar2 = (ProgressBar) inflate.findViewById(R.id.ProgressRad);
        ProgressBar progressBar3 = (ProgressBar) inflate.findViewById(R.id.ProgressBio);
        ProgressBar progressBar4 = (ProgressBar) inflate.findViewById(R.id.ProgressPsy);
       // ((ImageView) inflate.findViewById(R.id.ImageViewHealth)).setImageResource(getResources().getIdentifier("@drawable/healthsymbol", null, getActivity().getPackageName()));
        //((ImageView) inflate.findViewById(R.id.ImageViewRad)).setImageResource(getResources().getIdentifier("@drawable/radsymbol", null, getActivity().getPackageName()));
        //((ImageView) inflate.findViewById(R.id.ImageViewBio)).setImageResource(getResources().getIdentifier("@drawable/biosymbol", null, getActivity().getPackageName()));
        //((ImageView) inflate.findViewById(R.id.ImageViewPsy)).setImageResource(getResources().getIdentifier("@drawable/psysymbol", null, getActivity().getPackageName()));
        TextView textView = (TextView) inflate.findViewById(R.id.txtCoordinates);
        TextView textView2 = (TextView) inflate.findViewById(R.id.txtMessages);
        TextView txtHealthPercent = inflate.findViewById(R.id.txtHealthPercent);
        this.G.HealthBar = progressBar;
        this.G.RadBar = progressBar2;
        this.G.BioBar = progressBar3;
        this.G.PsyBar = progressBar4;
        this.G.CO = textView;
        this.G.Messages = textView2;
        this.G.HealthPercent = txtHealthPercent;
        return inflate;
    }
}
