package com.example.myapplication2.fragments;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.example.myapplication2.SuperSaveZone;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.myapplication2.SuperSaveZone.CHECK_TIME_IN;
import static com.example.myapplication2.SuperSaveZone.CHECK_TIME_OUT;
import static java.util.Calendar.HOUR_OF_DAY;

public class MapTab extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private Globals globals;
    public MarkerOptions LastMarker;
    private GoogleMap mMap;
    DBHelper dbHelper;
    public MapTab(Globals globals) {
        this.globals = globals;
    }
    SQLiteDatabase database;
    Cursor cursor;
    Circle mapCircle = null;


    long delta_time = 180;
    Circle[] mapCircle_save = new Circle[96];
    Circle[] mapCircle_save_night = new Circle[12];
    Circle[] mapCircle_save_original = new Circle[4];
    SuperSaveZone[] superSaveZones = new SuperSaveZone[8];

    private static final String TAG = "mapSave";

    public void Create_mapCircles(){
        Arrays.fill(mapCircle_save, null);
        Arrays.fill(mapCircle_save_night, null);
        Arrays.fill(mapCircle_save_original, null);
    }
    public void Create_super_save_zones(){
        superSaveZones[0] = new SuperSaveZone(CHECK_TIME_IN, 0, delta_time, 150d, "stalkers_in");
        superSaveZones[1] = new SuperSaveZone((CHECK_TIME_IN), 0, delta_time, 15d, "military_in");
        superSaveZones[2] = new SuperSaveZone((CHECK_TIME_IN), 0, delta_time, 20d, "stalkers_out");
        superSaveZones[3] = new SuperSaveZone((CHECK_TIME_IN + delta_time / 2), 1, delta_time, 20d, "stalkers_out");
        superSaveZones[4] = new SuperSaveZone(CHECK_TIME_OUT, 0, delta_time, 30d, "stalkers_out");
        superSaveZones[5] = new SuperSaveZone((CHECK_TIME_OUT + delta_time / 2), 1, delta_time, 30d, "stalkers_out");
        superSaveZones[6] = new SuperSaveZone(CHECK_TIME_OUT, 0, delta_time, 20d, "green_out");
        superSaveZones[7] = new SuperSaveZone((CHECK_TIME_OUT + delta_time / 2), 1, delta_time, 20d, "green_out");
    }
    BroadcastReceiver broadcastReceiverCircle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            drawCirceAnomaly();
        }
    };

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_map, viewGroup, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);  // почему здесь не R.row.map? - потому что это не картинка, а layout
        dbHelper = new DBHelper(getActivity());
        create_stalkers_zones_in();
        create_nightZones();
        create_saveZones();
        Create_mapCircles();
        Create_super_save_zones();

        return inflate;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        globals.map = this.mMap;
        mMap.setMapType(0);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        AddGroundOverlay(this.mMap);

        //CircleLocations();
        drawMarkers();
        //create_stalkers_zones_in();

        mMap.setOnMapLongClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions().position(latLng));
            writeMarkerToDB(latLng);
        });
        // экран выставляется по центру майдана
        CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.35342867d, 40.7328d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(13.65f);
        // экран выставляется по центру курятника
        /*CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(64.53203d, 40.151296d));
        CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(14.8f);*/

        mMap.moveCamera(newLatLng);
        mMap.animateCamera(zoomTo);

        drawConstantCircle();

        if (mapCircle!=null) {
            mapCircle.remove();
            mapCircle = null;
        }

    }
    //сюда ставится карта
    public void AddGroundOverlay(GoogleMap googleMap) {
        // карта майдана
        googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2021)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
        //майдан низкого качества
        //googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.map2021g)).positionFromBounds(new LatLngBounds(new LatLng(64.34759866104574d, 40.71273050428501d), new LatLng(64.36016771016875d, 40.75285586089982d))));
        // карта около Адмиралтейской
        //googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.mapadm6)).positionFromBounds(new LatLngBounds(new LatLng(64.573228d, 40.514540d), new LatLng(64.574154d, 40.518798d))));
        // курятник
        //googleMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.kury)).positionFromBounds(new LatLngBounds(new LatLng(64.527070d, 40.142495d), new LatLng(64.536994d, 40.160006d))));
    }

     private void drawCirceAnomaly(){

        if (mapCircle==null){
            mapCircle = mMap.addCircle(globals.circleOptions);
        }
        if (globals.anomalyRadius == 0){
            mapCircle.remove();
            mapCircle = null;
        }
        cal = Calendar.getInstance();
        Hour = this.cal.get(HOUR_OF_DAY);
        AltCheckSuper(true, nightZones, mapCircle_save_night,17d, Color.WHITE);
        AltCheckSuper(false, stalkers_save_zones_in, mapCircle_save, 17d, Color.WHITE);
        AltCheckSuper(false, saveZones, mapCircle_save_original, 40, Color.RED);
        //Check_super_save_zone(CHECK_TIME_IN, 0, (mapCircle_save.length / 2));
        //Check_super_save_zone(CHECK_TIME_OUT, (mapCircle_save.length / 2), mapCircle_save.length);

    }

    public void drawConstantCircle(){
        mMap.addCircle(new CircleOptions().center(new LatLng(64.352410d, 40.739851d)).radius(17).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE));
        mMap.addCircle(new CircleOptions().center(new LatLng(64.352406d, 40.739431d)).radius(17).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE));
        mMap.addCircle(new CircleOptions().center(new LatLng(64.357220d, 40.721517d)).radius(80).strokeColor(Color.RED).strokeWidth(3).zIndex(Float.MAX_VALUE));
    }

    LatLng[] nightZones = new LatLng[12];
    public void create_nightZones(){
        LatLng[] latLngs = new LatLng[12];
        latLngs[0] = new LatLng(64.35635328466024d, 40.740526559484145d);// ночные
        latLngs[1] = new LatLng(64.35648749695677d, 40.74024632672407d);//
        latLngs[2] = new LatLng(64.35662201786853d, 40.7399589942588d);//
        latLngs[3] = new LatLng(64.3567484782622d, 40.73967596773043d);// ночные
        latLngs[4] = new LatLng(64.35190461794156d, 40.740989107818386d);//ночные
        latLngs[5] = new LatLng(64.35186262065666d, 40.74058758789669d);// ночные
        latLngs[6] = new LatLng(64.3517975167356d, 40.74019942527292d);// ночные
        latLngs[7] = new LatLng(64.3548623948148d, 40.73747803747212d);// ночные
        latLngs[8] = new LatLng(64.35479771816406d, 40.73709830414507d);// ночные
        latLngs[9] = new LatLng(64.35279819399817d, 40.73884715079873d);//ночная
        latLngs[10] = new LatLng(64.35597785931614d, 40.73796221719635d);
        latLngs[11] = new LatLng(64.35606751644602d, 40.73759465692494d);
        nightZones = latLngs;
    }
    LatLng[] saveZones = new LatLng[4];
    public void create_saveZones(){
        LatLng[] latLngs = new LatLng[4];
        latLngs[0] = new LatLng(64.35166347320484d, 40.72757875444062d); // гараж
        latLngs[1] = new LatLng(64.349906d, 40.725957d);
        latLngs[2] = new LatLng(64.350906d, 40.719229d);
        latLngs[3] = new LatLng(64.351080d, 40.736224d);// свобода
        saveZones = latLngs;
    }


    LatLng[] stalkers_save_zones_in = new LatLng[96];
    public void create_stalkers_zones_in(){
        LatLng[] latLngs = new LatLng[96];
        latLngs[0] = new LatLng(64.354129d, 40.743913d);// кпп и далее к выходу
        latLngs[1] = new LatLng(64.354235d, 40.744258d);
        latLngs[2] = new LatLng(64.35432521220459d, 40.74460558362928d);
        latLngs[3] = new LatLng(64.35445293046173d, 40.744923831508146d);
        latLngs[4] = new LatLng(64.3545824026996d, 40.745214162825555d);
        latLngs[5] = new LatLng(64.35472209740922d, 40.74546291301759d);
        latLngs[6] = new LatLng(64.35489346502892d, 40.745582845300504d);
        latLngs[7] = new LatLng(64.35500782797686d, 40.74526722512475d);
        latLngs[8] = new LatLng(64.35507067122896d, 40.74488372241825d);
        latLngs[9] = new LatLng(64.35513902594106d, 40.74451641974487d);
        latLngs[10] = new LatLng(64.3552087889606d, 40.74412309924951d);
        latLngs[11] = new LatLng(64.3552827954611d, 40.743742309081476d);
        latLngs[12] = new LatLng(64.35534417931994d, 40.74335453065853d);
        latLngs[13] = new LatLng(64.35541176405508d, 40.74296775537176d);
        latLngs[14] = new LatLng(64.35555348581504d, 40.742687429518696d);
        latLngs[15] = new LatLng(64.35563584135113d, 40.742319967224745d);
        latLngs[16] = new LatLng(64.3557006595403d, 40.74194897608368d);
        latLngs[17] = new LatLng(64.3558133075042d, 40.74161370149263d);
        latLngs[18] = new LatLng(64.35595448110925d, 40.74133608438898d);
        latLngs[19] = new LatLng(64.35609017983464d, 40.74107015979084d);
        latLngs[20] = new LatLng(64.35624275507247d, 40.74082160943004d);

        latLngs[21] = new LatLng(64.35295356108362d, 40.7434670952109d); //толчек у монолита
        latLngs[22] = new LatLng(64.35280265443103d, 40.74370276887028d);
        latLngs[23] = new LatLng(64.3526529104659d, 40.74394533079032d);
        latLngs[24] = new LatLng(64.35247292472532d, 40.74397037100172d);
        latLngs[25] = new LatLng(64.35234879448167d, 40.74365386149042d);
        latLngs[26] = new LatLng(64.35225051500136d, 40.7433202479478d);
        latLngs[27] = new LatLng(64.3521427267776d, 40.74298846111945d);
        latLngs[28] = new LatLng(64.35208781625262d, 40.74256105302851d);
        latLngs[29] = new LatLng(64.35206614369244d, 40.742176031451784d);
        latLngs[30] = new LatLng(64.35203133624847d, 40.74175692849512d);
        latLngs[31] = new LatLng(64.3519613993229d, 40.741380966909865d);

        latLngs[32] = new LatLng(64.35276759841277d, 40.74260360547909d); // путь военных
        latLngs[33] = new LatLng(64.35269821146606d, 40.742199371531434d);
        latLngs[34] = new LatLng(64.35259972068465d, 40.741861999009714d);
        latLngs[35] = new LatLng(64.35251103312895d, 40.741499425573956d);
        latLngs[36] = new LatLng(64.35247640158082d, 40.74108919460003d);
        latLngs[37] = new LatLng(64.35243869370726d, 40.74068559251896d);
        latLngs[38] = new LatLng(64.35242336435232d, 40.740270397903735d);

        latLngs[39] = new LatLng(64.35317903326629d, 40.74211621986506d); // самый жирный
        latLngs[40] = new LatLng(64.3530676855036d, 40.74177850748372d);
        latLngs[41] = new LatLng(64.35307759043815d, 40.74136361752823d);
        latLngs[42] = new LatLng(64.35311559862588d, 40.740947540217554d);
        latLngs[43] = new LatLng(64.35320833036639d, 40.7406033580693d);
        latLngs[44] = new LatLng(64.3533427514016d, 40.74088981008373d);
        latLngs[45] = new LatLng(64.3534273880053d, 40.74125741173395d);
        latLngs[46] = new LatLng(64.3535272506385d, 40.7416104341624d);
        latLngs[47] = new LatLng(64.35369931415698d, 40.74167856221654d);
        latLngs[48] = new LatLng(64.35385106807306d, 40.741420371104475d);
        latLngs[49] = new LatLng(64.35399244102797d, 40.74117024644733d);
        latLngs[50] = new LatLng(64.35399947701931d, 40.740757545914946d);
        latLngs[51] = new LatLng(64.35413600828822d, 40.74047520189392d);
        latLngs[52] = new LatLng(64.35431770759529d, 40.74047530249902d);
        latLngs[53] = new LatLng(64.35448818768883d, 40.74060662377004d);
        latLngs[54] = new LatLng(64.3546205550841d, 40.740366451897d);
        latLngs[55] = new LatLng(64.35463945956784d, 40.73993880157785d);
        latLngs[56] = new LatLng(64.35465628840566d, 40.739523336212606d);
        latLngs[57] = new LatLng(64.35468482593951d, 40.73910560830513d);
        latLngs[58] = new LatLng(64.35483549689444d, 40.738899002714625d);
        latLngs[59] = new LatLng(64.354993892013d, 40.73867466493646d);
        latLngs[60] = new LatLng(64.35498453249937d, 40.7382720979003d);
        latLngs[61] = new LatLng(64.35492709824331d, 40.7378803923697d);

        latLngs[62] = new LatLng(64.35289419918085d, 40.74006914469832d);//самая короткая
        latLngs[63] = new LatLng(64.35282882522654d, 40.73965389027957d);
        latLngs[64] = new LatLng(64.35281241378898d, 40.739259701118684d);

        latLngs[65] = new LatLng(64.35327464552576d, 40.74278657071493d); // самая важная
        latLngs[66] = new LatLng(64.35338909350193d, 40.74310977309313d);
        latLngs[67] = new LatLng(64.35339042102528d, 40.742703076129935d);
        latLngs[68] = new LatLng(64.35357354817818d, 40.74276856634414d);
        latLngs[69] = new LatLng(64.35374926753758d, 40.74276936833783d);
        latLngs[70] = new LatLng(64.35392842206795d, 40.74275131202157d);
        latLngs[71] = new LatLng(64.35406736165523d, 40.74247493169485d);
        latLngs[72] = new LatLng(64.35424611985239d, 40.7425261775552d);
        latLngs[73] = new LatLng(64.35442159914702d, 40.74253051689229d);
        latLngs[74] = new LatLng(64.35459589159377d, 40.74244715268726d);
        latLngs[75] = new LatLng(64.35473228695206d, 40.7421457820547d);
        latLngs[76] = new LatLng(64.35474349130439d, 40.74172442186285d);
        latLngs[77] = new LatLng(64.35474117057692d, 40.741743502867166);
        latLngs[78] = new LatLng(64.35480112579775d, 40.741346431397666d);
        latLngs[79] = new LatLng(64.35492614666789d, 40.741040636954516d);
        latLngs[80] = new LatLng(64.3550648535183d, 40.740787868746104d);
        latLngs[81] = new LatLng(64.35523643165064d, 40.74092887045961d);
        latLngs[82] = new LatLng(64.35535259212864d, 40.740618210093004d);
        latLngs[83] = new LatLng(64.35538887008047d, 40.740199498863824d);
        latLngs[84] = new LatLng(64.35546413114129d, 40.73981468710406d);
        latLngs[85] = new LatLng(64.35556608591283d, 40.739482952991544d);
        latLngs[86] = new LatLng(64.35566787403617d, 40.739141199432446d);
        latLngs[87] = new LatLng(64.35562164086585d, 40.73873399434004d);
        latLngs[88] = new LatLng(64.35566212114979d, 40.7383278893475d);
        latLngs[89] = new LatLng(64.3558329544728d, 40.738182241545026d);
        latLngs[90] = new LatLng(64.35471398517412d, 40.74072987952912d);//отворотки
        latLngs[91] = new LatLng(64.3540625268518d, 40.74008438658268d);//отворотки
        latLngs[92] = new LatLng(64.35388053787214d, 40.74008035258441d);//отворотки
        latLngs[93] = new LatLng(64.35511363345678d, 40.7435522554439d);//отворотки
        latLngs[94] = new LatLng(64.35499368143708d, 40.74324220074001d);//отворотки
        latLngs[95] = new LatLng(64.35366985130402d, 40.74305883740328d);//у монолита
        stalkers_save_zones_in = latLngs;
    }


    public void CircleLocations(/*LatLng[] latLngs*/){
        for (int i = 0; i < 4/*stalkers_save_zones_in.length*/; i++){
            //mapCircle_save[i].setVisible(false);
            mapCircle_save[i] = mMap.addCircle(new CircleOptions().center(stalkers_save_zones_in[i]).radius(150).strokeColor(Color.WHITE).strokeWidth(3).zIndex(Float.MAX_VALUE));

        }
    }

    private Calendar cal = Calendar.getInstance();
    private int Hour = this.cal.get(HOUR_OF_DAY);
    public void AltCheckSuper(boolean timeDepend, LatLng[] latLngs, Circle[] circles, double radius, int color){

        Location location = new Location("GPS");
        if (timeDepend && ((Hour >= 20) | (Hour <= 4))) {

            for (int i = 0; i < latLngs.length; i++){
                circles[i] = mMap.addCircle(new CircleOptions().center(latLngs[i]).radius(radius).strokeColor(color).strokeWidth(3).zIndex(Float.MAX_VALUE));
            }
        }
        if (!timeDepend){
            for (int i = 0; i < latLngs.length; i++){
                circles[i] = mMap.addCircle(new CircleOptions().center(latLngs[i]).radius(radius).strokeColor(color).strokeWidth(3).zIndex(Float.MAX_VALUE));
                location.setLatitude(circles[i].getCenter().latitude);
                location.setLongitude(circles[i].getCenter().longitude);
                double distanceToCircle = location.distanceTo(globals.location);
                if (distanceToCircle > (circles[i].getRadius() + 5) & circles[i] != null){
                    circles[i].remove();
                    circles[i] = null;
                }
            }
        }
    }

    public void Check_super_save_zone(long time, int first_i, int final_i){
        if (true/*((Calendar.getInstance().getTimeInMillis() / 1000) >= time)*/ /*&& ((Calendar.getInstance().getTimeInMillis() / 1000) <= (time + 3600))*/) {
            for(int i = first_i; i < final_i; i++){
                if (mapCircle_save[i] != null){
                    mapCircle_save[i].remove();
                    mapCircle_save[i] = null;
                }

                try {
                    Log.d(TAG, "distance to circle i = " + String.valueOf(globals.location.hashCode()));
                    mapCircle_save[i] = mMap.addCircle(superSaveZones[i].Draw_save_zone(globals.location));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // функция рисует маркеры на карте
    private void drawMarkers(){
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.TABLE_MARKERS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int iconIndex = cursor.getColumnIndex(DBHelper.KEY_ICON);
            int latIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
            int lonIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
            int commentIndex = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            do {
                mMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(latIndex), cursor.getDouble(lonIndex))).title(cursor.getString(idIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }
    // функция записывает маркеры в базу данных
    private void writeMarkerToDB(LatLng latLng){
        /*
         * try/finally призвана для безопасности и быстроты
         * */
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        database.beginTransaction();
        try {
            contentValues.put(DBHelper.KEY_NAME, "name");
            contentValues.put(DBHelper.KEY_ICON, "icon");
            contentValues.put(DBHelper.KEY_LATITUDE, latLng.latitude); //если написать здесь format для округления о 6 знаков после запятой, то всё ломается
            contentValues.put(DBHelper.KEY_LONGITUDE, latLng.longitude);
            contentValues.put(DBHelper.KEY_COMMENT, "comment");
            database.insert(DBHelper.TABLE_MARKERS, null, contentValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        dbHelper.close();
    }

    // эта штука меняла иконки у маркера, но работала не фонтан
    int iconNumber = 0;
    @Override
    public boolean onMarkerClick(Marker marker) {
        /*switch (iconNumber) {
            case 0:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.radsymbol2));
                break;
            case 1:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.biosymbol2));
                break;
            case 2:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.psysymbol2));
                break;
            case 3:
                marker.setIcon(BitmapDescriptorFactory.defaultMarker());
                break;
        }
        iconNumber++;
        if (iconNumber == 4){
            iconNumber = 0;
        }*/

        return false;
    }

    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiverCircle, new IntentFilter("MapTab.Circle"));
        //anomalyIndex[1] = -1;
        try {
            onMapReady(mMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(broadcastReceiverCircle);
        mMap.clear();
    }
}
