package com.example.myapplication2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication2.Globals;
import com.example.myapplication2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GeneralTab extends Fragment {

    private Globals globals;

    public GeneralTab(Globals globals) {
        this.globals = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_main, viewGroup, false);
        ProgressBar progressBar = inflate.findViewById(R.id.ProgressHealth);
        ProgressBar progressBar2 = inflate.findViewById(R.id.ProgressRad);
        ProgressBar progressBar3 = inflate.findViewById(R.id.ProgressBio);
        ProgressBar progressBar4 = inflate.findViewById(R.id.ProgressPsy);
        TextView textView = inflate.findViewById(R.id.txtCoordinates);
        TextView textView2 = inflate.findViewById(R.id.txtMessages);
        TextView txtHealthPercent = inflate.findViewById(R.id.txtHealthPercent);
        TextView txtRadPercent = inflate.findViewById(R.id.txtRadPercent);
        TextView txtBioPercent = inflate.findViewById(R.id.txtBioPercent);
        TextView txtPsyPercent = inflate.findViewById(R.id.txtPsyPercent);
        TextView txtProtectionRad = inflate.findViewById(R.id.txtProtectionRad);
        TextView txtProtectionBio = inflate.findViewById(R.id.txtProtectionBio);
        TextView txtProtectionPsy = inflate.findViewById(R.id.txtProtectionPsy);
        TextView txtCapacityProtectionRad = inflate.findViewById(R.id.txtStrengthRadProtection);
        TextView txtCapacityProtectionBio = inflate.findViewById(R.id.txtStrengthBioProtection);
        TextView txtCapacityProtectionPsy = inflate.findViewById(R.id.txtStrengthPsyProtection);
        TextView txtMaxProtectionsAvailable = inflate.findViewById(R.id.txtMaxProtections);
        txtMaxProtectionsAvailable.setText("Защит? каких защит?");
        TextView txtGestaltOpen = inflate.findViewById(R.id.txtGestaltOpen);

        globals.loadStats();

        globals.HealthBar = progressBar;
        globals.RadBar = progressBar2;
        globals.BioBar = progressBar3;
        globals.PsyBar = progressBar4;
        globals.tvCoordinate = textView;
        globals.tvMessages = textView2;
        //globals.MaxProtectionAvailable = txtMaxProtectionsAvailable;
        globals.HealthPercent = txtHealthPercent;
        globals.RadPercent = txtRadPercent;
        globals.BioPercent = txtBioPercent;
        globals.PsyPercent = txtPsyPercent;
        globals.RadCapacityPercent = txtCapacityProtectionRad;
        globals.BioCapacityPercent = txtCapacityProtectionBio;
        globals.PsyCapacityPercent = txtCapacityProtectionPsy;
        globals.RadProtectionPercent = txtProtectionRad;
        globals.BioProtectionPercent = txtProtectionBio;
        globals.PsyProtectionPercent = txtProtectionPsy;
        globals.tvGestaltOpen = txtGestaltOpen;

        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        globals.saveStats();
    }

    @Override
    public void onPause() {
        super.onPause();
        globals.saveStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        globals.loadStats();
    }



}
