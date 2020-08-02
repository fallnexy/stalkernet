package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyArrayAdapter extends ArrayAdapter<MarkerOptions> {
    private Globals G;
    private Context mContext;
    private int resourceLayout;

    public MyArrayAdapter(@NonNull Context context, int i, int i2, @NonNull List<MarkerOptions> list, Globals globals) {
        super(context, i, i2, list);
        this.resourceLayout = i;
        this.mContext = context;
        this.G = globals;
    }

    @NonNull
    public View getView(final int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        /*if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(this.resourceLayout, null);
        }
        MarkerOptions markerOptions = (MarkerOptions) getItem(i);
        if (markerOptions != null) {
            TextView textView = (TextView) view.findViewById(R.id.firstline);
            TextView textView2 = (TextView) view.findViewById(R.id.secondline);
            TextView textView3 = (TextView) view.findViewById(R.id.coordinate);
            ((Button) view.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MyArrayAdapter.this.G.MarkerArray.remove(i);
                    MyArrayAdapter.this.G.Adapter.notifyDataSetChanged();
                    MyArrayAdapter.this.G.map.clear();
                    MyArrayAdapter.this.G.AddGroundOverlay(MyArrayAdapter.this.G.map);
                    MyArrayAdapter.this.G.redrawMarkers();
                }
            });
            textView.setText(markerOptions.getTitle());
            textView2.setText("Description");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Double.toString(markerOptions.getPosition().latitude));
            stringBuilder.append("-");
            stringBuilder.append(Double.toString(markerOptions.getPosition().longitude));
            textView3.setText(stringBuilder.toString());
        }*/
        return view;
    }
}
