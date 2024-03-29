package com.example.stalkernet.fragments.childTabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.Globals;
import com.example.stalkernet.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import static com.example.stalkernet.playerCharacter.PlayerCharacter.PREFERENCE_NAME;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PROTECTIONS_AVAILABLE_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.USER_ID_KEY;


public class UserChildFragment extends Fragment {

    private static final String PREFERENCE_USER = "shar_pref_user";
    private static final String CARD_USER_KEY = "card_user_key";
    private static final String CARD_HEALTH_KEY = "card_hp_key";
    private static final String CARD_RAD_KEY = "card_rad_key";
    private static final String CARD_BIO_KEY = "card_bio_key";
    private static final String CARD_PSY_KEY = "card_psy_key";
    private final Globals globals;
    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    TextView txtFaction, txtProtectionsAvailable, tvIsScienceQR, tvIsApplyQR;
    String userName, isScienceQR, isApplyQR, factionName, factionImage;
    private int user_id = 1, protections_available, scienceQR, applyQR;

    CardView cardUser, cardHealth, cardRad, cardBio, cardPsy;

    public UserChildFragment(Globals globals) {
        this.globals = globals;
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle){
        View inflate = layoutInflater.inflate(R.layout.fragment_user_child, viewGroup, false);

        dbHelper = new DBHelper(getActivity());

        globals.tvMessage = inflate.findViewById(R.id.txtMessage);
        setUser();
        setBtnHealth(inflate);
        setBtnRad(inflate);
        setBtnBio(inflate);
        setBtnPsy(inflate);
        setBtnUser(inflate);
        loadStats();
        return inflate;
    }
    /*
    * кнопка и cardView юзера
    * */
    private void setBtnUser(View inflate){
        cardUser = inflate.findViewById(R.id.cardViewUser);
        setUserCard(inflate);
        MaterialButton btnUser = inflate.findViewById(R.id.btnUser);
        btnUser.setText(userName);
        btnUser.setIcon(imageDrawable(factionImage));
        btnUser.setOnClickListener(view -> {
            if (cardUser.getVisibility() == View.VISIBLE){
                cardUser.setVisibility(View.GONE);
            } else {
                cardUser.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setUserCard(View inflate){
        txtFaction = inflate.findViewById(R.id.txtVPlayerFaction);
        txtProtectionsAvailable = inflate.findViewById(R.id.txtVProtectionsAvailable);
        tvIsScienceQR = inflate.findViewById(R.id.tvIsScienceQR);
        tvIsApplyQR = inflate.findViewById(R.id.tvIsApplyQR);

        txtFaction.setText(factionName);
        txtProtectionsAvailable.setText(getString(R.string.protections_available, protections_available));
        tvIsScienceQR.setText(getString(R.string.tvIsScienceQR, scienceQR == 1 ? "включен" : "выключен"));
        tvIsApplyQR.setText(getString(R.string.tvIsApplyQR, applyQR == 1 ? "включен" : "выключен"));
    }
    /*
    * кнопка здоровья
    * */
    private void setBtnHealth(View inflate){
        globals.healthTextUser = inflate.findViewById(R.id.tvHealthUser);
        cardHealth = inflate.findViewById(R.id.cardViewHealth);
        MaterialButton btnHealth = inflate.findViewById(R.id.btnHealth);
        btnHealth.setOnClickListener(view -> {
            if (cardHealth.getVisibility() == View.VISIBLE){
                cardHealth.setVisibility(View.GONE);
            } else {
                cardHealth.setVisibility(View.VISIBLE);
            }
        });
    }
    /*
    * кнопка рад и его cardView
    * */
    private void setBtnRad(View inflate){
        cardRad = inflate.findViewById(R.id.cardViewRad);
        setCardViewRad(inflate);
        MaterialButton btnRad = inflate.findViewById(R.id.btnRad);
        btnRad.setOnClickListener(view -> {
            if (cardRad.getVisibility() == View.VISIBLE){
                cardRad.setVisibility(View.GONE);
            } else {
                cardRad.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setCardViewRad(View inflate){
        globals.radText = inflate.findViewById(R.id.tvRadContamination);
        globals.radTotalProtection = inflate.findViewById(R.id.tvRadTotalProtection);
        globals.radProtectionOut = inflate.findViewById(R.id.txtProtectionRad);
        globals.radCapacityOut = inflate.findViewById(R.id.txtCapacityRad);
    }
    /*
    * кнопка bio и его cardView
    * */
    private void setBtnBio(View inflate){
        cardBio = inflate.findViewById(R.id.cardViewBio);
        setCardViewBio(inflate);
        MaterialButton btnBio = inflate.findViewById(R.id.btnBio);
        btnBio.setOnClickListener(view -> {
            if (cardBio.getVisibility() == View.VISIBLE){
                cardBio.setVisibility(View.GONE);
            } else {
                cardBio.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setCardViewBio(View inflate){
        globals.bioText = inflate.findViewById(R.id.tvBioContamination);
        globals.bioTotalProtection = inflate.findViewById(R.id.tvBioTotalProtection);
        globals.bioProtectionOut = inflate.findViewById(R.id.txtProtectionBio);
        globals.bioCapacityOut = inflate.findViewById(R.id.txtCapacityBio);
    }
    /*
    * кнопка psy и его cardView
    * */
    private void setBtnPsy(View inflate){
        cardPsy = inflate.findViewById(R.id.cardViewPsy);
        setCardViewPsy(inflate);
        MaterialButton btnPsy = inflate.findViewById(R.id.btnPsy);
        btnPsy.setOnClickListener(view -> {
            if (cardPsy.getVisibility() == View.VISIBLE){
                cardPsy.setVisibility(View.GONE);
            } else {
                cardPsy.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setCardViewPsy(View inflate){
        globals.psyText = inflate.findViewById(R.id.tvPsyContamination);
        globals.psyTotalProtection = inflate.findViewById(R.id.tvPsyTotalProtection);
        globals.psyProtectionOut = inflate.findViewById(R.id.txtProtectionPsy);
        globals.psyCapacityOut = inflate.findViewById(R.id.txtCapacityPsy);
    }
    private void setUser(){
        loadServiceStats();
        database = dbHelper.open();
        String query = "SELECT user." + DBHelper.KEY_NAME__USER + " AS user_name, " +
                "user." + DBHelper.KEY_SCIENCE_QR__USER + " AS science_qr, " +
                "user." + DBHelper.KEY_APPLY_QR__USER + " AS apply_qr, " +
                "faction." + DBHelper.KEY_NAME__FACTION + " AS faction_name, " +
                "faction." + DBHelper.KEY_IMAGE__FACTION + " AS faction_image " +
                "FROM " + DBHelper.TABLE_USER + " AS user " +
                "JOIN " + DBHelper.TABLE_FACTION + " AS faction " +
                "ON user." + DBHelper.KEY_FACTION_ID__USER + " = faction." + DBHelper.KEY_ID__FACTION;

        cursor = database.rawQuery(query, null);
        //Log.d(LOG_CHE, String.valueOf(user_id));
        cursor.moveToPosition(user_id-1);
        userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
        factionName = cursor.getString(cursor.getColumnIndexOrThrow("faction_name"));
        factionImage = cursor.getString(cursor.getColumnIndexOrThrow("faction_image"));
        scienceQR = cursor.getInt(cursor.getColumnIndexOrThrow("science_qr"));
        applyQR = cursor.getInt(cursor.getColumnIndexOrThrow("apply_qr"));
        cursor.close();
    }
    /*
    * призывает картинку
    * */
    private Drawable imageDrawable(String image){
        String path = "@drawable/" + image;
        int imageResource = getResources().getIdentifier(path, null,getActivity().getPackageName());
        return getResources().getDrawable(imageResource);
    }
    SharedPreferences sharedPreferences;
    public void loadServiceStats() {
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        user_id = sharedPreferences.getInt(USER_ID_KEY, 1);
        protections_available = sharedPreferences.getInt(PROTECTIONS_AVAILABLE_KEY, 1);

    }

    private void loadStats(){
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_USER,Context.MODE_PRIVATE);
        cardUser.setVisibility(sharedPreferences.getInt(CARD_USER_KEY, 0));
        cardHealth.setVisibility(sharedPreferences.getInt(CARD_HEALTH_KEY, 0));
        cardRad.setVisibility(sharedPreferences.getInt(CARD_RAD_KEY, 0));
        cardBio.setVisibility(sharedPreferences.getInt(CARD_BIO_KEY, 0));
        cardPsy.setVisibility(sharedPreferences.getInt(CARD_PSY_KEY, 0));
    }

    public void saveStats() {
        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(CARD_USER_KEY, cardUser.getVisibility());
        edit.putInt(CARD_HEALTH_KEY, cardHealth.getVisibility());
        edit.putInt(CARD_RAD_KEY, cardRad.getVisibility());
        edit.putInt(CARD_BIO_KEY, cardBio.getVisibility());
        edit.putInt(CARD_PSY_KEY, cardPsy.getVisibility());
        edit.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        database = dbHelper.open();
        loadServiceStats();
        globals.loadStats();
        setUser();

    }
    @Override
    public void onPause() {
        super.onPause();
        database.close();
        cursor.close();
        globals.saveStats();
        saveStats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
        cursor.close();
    }

}