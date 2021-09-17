package com.example.myapplication2.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PointTab extends Fragment {
    private Globals globals;

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    SimpleCursorAdapter userAdapter;
    ListView listViewPoints;
    ContentValues contentValues;
    public static final int IDM_DELETE = 1102;

    public PointTab(Globals globals) {
        this.globals = globals;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_point, viewGroup, false);

        listViewPoints = inflate.findViewById(R.id.listViewPoints);

        dbHelper = new DBHelper(getActivity());
        registerForContextMenu(listViewPoints);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            database = dbHelper.getWritableDatabase();
            cursor = database.rawQuery("select * from " + DBHelper.TABLE_MARKERS, null);
            String[] headers = new String[]{DBHelper.KEY_ID, DBHelper.KEY_NAME, DBHelper.KEY_LATITUDE, DBHelper.KEY_LONGITUDE, DBHelper.KEY_COMMENT};
            userAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_frag_point,
                    cursor, headers, new int[]{R.id.txtV_key, R.id.txtV_name, R.id.txtV_lat, R.id.txtV_long, R.id.txtV_comment}, 0);

            // установка слушателя изменения текста
           /* enterSurname.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    userAdapter.getFilter().filter(s.toString());
                }
            });*/

            // устанавливаем провайдер фильтрации
            userAdapter.setFilterQueryProvider(constraint -> {

                if (constraint == null || constraint.length() == 0) {
                    return database.rawQuery("select * from " + DBHelper.TABLE_MARKERS, null);
                }
                else {
                    return database.rawQuery("select * from " + DBHelper.TABLE_MARKERS + " where " +
                            DBHelper.KEY_ID + " like ?", new String[]{constraint.toString() + "%"});
                }
            });

            /*listViewPoints.setOnItemClickListener(onItemClickListener);*/
            listViewPoints.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listViewPoints.setAdapter(userAdapter);


        }
        catch (SQLException ex){}
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, IDM_DELETE, 0, "Удалить запись");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == IDM_DELETE) {
            // удаляем
            database.delete(DBHelper.TABLE_MARKERS, DBHelper.KEY_ID + "= ?", new String[] {String.valueOf(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID)))});;
            // уведомляем, что данные изменились, но че т ничего не убновлется
            userAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        dbHelper.close();
        cursor.close();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dbHelper.close();
        cursor.close();
    }
}
