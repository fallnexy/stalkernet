package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MapTab extends Fragment implements OnMapReadyCallback {
    private Globals G;
    public MarkerOptions LastMarker;
    private GoogleMap mMap;

    public MapTab(Globals globals) {
        this.G = globals;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);  // почему здесь не R.row.map?
        return inflate;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.G.map = this.mMap;
        this.mMap.setMapType(0);
        this.mMap.getUiSettings().setCompassEnabled(true);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        this.mMap.setMyLocationEnabled(true);
        AddGroundOverlay(this.mMap);
        this.mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MapTab.this.mMap.addMarker(new MarkerOptions().position(latLng));
                MapTab.this.LastMarker = new MarkerOptions().position(latLng);
                MapTab.this.LastMarker.title("Point");
                MapTab.this.G.MarkerArray.add(MapTab.this.LastMarker);
                MapTab.this.G.Adapter.notifyDataSetChanged();
            }
        });
        CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.36016771016875d, 40.75285586089982d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(15.0f);
        this.mMap.moveCamera(newLatLng);
        this.mMap.animateCamera(zoomTo);
    }
    public void AddGroundOverlay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
    }
}
