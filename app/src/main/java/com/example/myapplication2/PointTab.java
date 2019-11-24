package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PointTab extends Fragment {
    private Globals G;

    public PointTab(Globals globals) {
        this.G = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_point, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(R.id.ListViewPoints);
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(getActivity(), R.layout.listpointitem, R.id.firstline, this.G.MarkerArray, this.G);
        this.G.Adapter = myArrayAdapter;
        listView.setAdapter(myArrayAdapter);
        return inflate;
    }
}
