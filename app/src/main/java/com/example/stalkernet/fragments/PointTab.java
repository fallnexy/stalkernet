package com.example.stalkernet.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.Globals;
import com.example.stalkernet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class PointTab extends Fragment {

    public static final String INTENT_POINT_TAB = "point_tab";
    public static final String INTENT_POINT_TAB_RENEW = "renew";
    private Globals globals;
    DBHelper dbHelper;
    SQLiteDatabase databasePoint;
    Cursor cursor;
    PointSimpleCursorAdapter userAdapter;
    ListView listViewPoints;
    ContentValues contentValues;
    public static final int IDM_DELETE = 1102;

    public PointTab(Globals globals) {
        this.globals = globals;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String input = intent.getStringExtra(INTENT_POINT_TAB_RENEW);
            if (input != null){
                applyUserAdapter();
            }
        }
    };


    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_point, viewGroup, false);

        listViewPoints = inflate.findViewById(R.id.listViewPoints);

        dbHelper = new DBHelper(getActivity());
        registerForContextMenu(listViewPoints);
        return inflate;
    }

    private void applyUserAdapter(){
        try {
            databasePoint = dbHelper.getWritableDatabase();
            cursor = databasePoint.rawQuery("select * from " + DBHelper.TABLE_MARKERS, null);
            String[] headers = new String[]{DBHelper.KEY_ID__MARKER, DBHelper.KEY_NAME__MARKER, DBHelper.KEY_LATITUDE__MARKER, DBHelper.KEY_LONGITUDE__MARKER, DBHelper.KEY_COMMENT__MARKER};
            userAdapter = new PointSimpleCursorAdapter(getActivity(), R.layout.item_frag_point,
                    cursor, headers, new int[]{R.id.txtV_key, R.id.txtV_name, R.id.txtV_coordinate, R.id.txtV_comment}, 0);

            // устанавливаем провайдер фильтрации
            userAdapter.setFilterQueryProvider(constraint -> {

                if (constraint == null || constraint.length() == 0) {
                    return databasePoint.rawQuery("select * from " + DBHelper.TABLE_MARKERS, null);
                }
                else {
                    return databasePoint.rawQuery("select * from " + DBHelper.TABLE_MARKERS + " where " +
                            DBHelper.KEY_ID__MARKER + " like ?", new String[]{constraint.toString() + "%"});
                }
            });

            listViewPoints.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listViewPoints.setAdapter(userAdapter);


        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        applyUserAdapter();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(INTENT_POINT_TAB));

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
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MARKER);
            databasePoint.delete(DBHelper.TABLE_MARKERS, DBHelper.KEY_ID__MARKER + "= ?", new String[] {String.valueOf(cursor.getInt(idIndex))});;
            /*
            * просто пересобираем весь список точек
            * */
            applyUserAdapter();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        databasePoint.close();
        cursor.close();
        getActivity().unregisterReceiver(broadcastReceiver);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        databasePoint.close();
        cursor.close();
    }
    /*
    * вложеный класс, который позволяет настраивать макет отображения точек
    * */
    class PointSimpleCursorAdapter extends SimpleCursorAdapter{

        private Context context;
        private Cursor cursor;
        private int[] to;
        private String[] from;

        public PointSimpleCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
            super(context, layout, cursor, from, to, flags);
            this.context = context;
            this.cursor = cursor;
            this.to = to;
            this.from = from;

        }

        @Override
        public void bindView (View view, Context context, Cursor cursor){

            int idIndex = cursor.getColumnIndex(from[0]);
            TextView tvId = view.findViewById(to[0]);
            String textId = "№ " + cursor.getString(idIndex);
            tvId.setText(textId );

            int nameIndex = cursor.getColumnIndex(from[1]);
            TextView tvName = view.findViewById(to[1]);
            tvName.setText(cursor.getString(nameIndex));

            int latitudeIndex = cursor.getColumnIndex(from[2]);
            int longitudeIndex = cursor.getColumnIndex(from[3]);
            TextView tvCoordinate = view.findViewById(to[2]);
            String textCoordinate = "координаты: " + cursor.getString(latitudeIndex) + " - " + cursor.getString(longitudeIndex);
            tvCoordinate.setText(textCoordinate);

            int commentIndex = cursor.getColumnIndex(from[4]);
            TextView tvComment = view.findViewById(to[3]);
            tvComment.setText(cursor.getString(commentIndex));

            tvName.setOnClickListener(view1 -> {
                editText(from[1], "имя");
            });

            tvComment.setOnClickListener(view1 -> {
                editText(from[4], "комментарий");
            });


        }

        private void editText(String from, String s) {
            // Create a dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String title = "Введите " + s;
            builder.setTitle(title);

            // Create an EditText view for user input
            final EditText input = new EditText(context);
            builder.setView(input);

            // Set positive button to save the entered text
            builder.setPositiveButton("Сохранить", (dialog, which) -> {
                String text = input.getText().toString();
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID__MARKER);
                ContentValues contentValues = new ContentValues();
                contentValues.put(from, text);
                databasePoint.update(DBHelper.TABLE_MARKERS, contentValues, DBHelper.KEY_ID__MARKER + " = " + cursor.getString(idIndex), null);
                applyUserAdapter();
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

}


