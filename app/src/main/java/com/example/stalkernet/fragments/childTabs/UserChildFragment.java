package com.example.stalkernet.fragments.childTabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.Globals;
import com.example.stalkernet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.stalkernet.playerCharacter.PlayerCharacter.PREFERENCE_NAME;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.PROTECTIONS_AVAILABLE_KEY;
import static com.example.stalkernet.playerCharacter.PlayerCharacter.USER_ID_KEY;


public class UserChildFragment extends Fragment {

    private Globals globals;
    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    TextView txtName, txtFaction, txtProtectionsAvailable;
    private int user_id = 1, protections_available;

    public UserChildFragment(Globals globals) {
        this.globals = globals;
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle){
        View inflate = layoutInflater.inflate(R.layout.fragment_user_child, viewGroup, false);

        dbHelper = new DBHelper(getActivity());

        txtName = inflate.findViewById(R.id.txtVPlayerName);
        txtFaction = inflate.findViewById(R.id.txtVPlayerFaction);
        txtProtectionsAvailable = inflate.findViewById(R.id.txtVProtectionsAvailable);

        globals.tvMessage = inflate.findViewById(R.id.txtMessage);
        return inflate;
    }


    private void setUser(){
        loadStats();
        database = dbHelper.open();
        String query = "SELECT user." + DBHelper.KEY_NAME__USER + " AS user_name, faction." + DBHelper.KEY_NAME_FACTION + " AS faction_name " +
                "FROM " + DBHelper.TABLE_USER + " AS user " +
                "JOIN " + DBHelper.TABLE_FACTION + " AS faction " +
                "ON user." + DBHelper.KEY_FACTION_ID__USER + " = faction." + DBHelper.KEY_ID_FACTION;

        cursor = database.rawQuery(query, null);
        //Log.d(LOG_CHE, String.valueOf(user_id));
        cursor.moveToPosition(user_id-1);
        String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
        String factionName = cursor.getString(cursor.getColumnIndexOrThrow("faction_name"));
        cursor.close();


        txtName.setText(userName);
        txtFaction.setText(factionName);
        txtProtectionsAvailable.setText(getString(R.string.protections_available, protections_available));
    }

    SharedPreferences sharedPreferences;
    public void loadStats() {
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        user_id = sharedPreferences.getInt(USER_ID_KEY, 1);
        protections_available = sharedPreferences.getInt(PROTECTIONS_AVAILABLE_KEY, 1);

    }

    @Override
    public void onResume() {
        super.onResume();
        database = dbHelper.open();
        loadStats();
        globals.loadStats();
        setUser();
    }
    @Override
    public void onPause() {
        super.onPause();
        database.close();
        cursor.close();
        globals.saveStats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
        cursor.close();
    }
}