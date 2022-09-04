package com.example.myapplication2.fragments.childTabs;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;


public class DatabaseChildFragment extends Fragment {


    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    ExpandableListView exListLocality;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_database_child, viewGroup, false);

        dbHelper = new DBHelper(getActivity());

        // expendableListView для таблицы locality
        exListLocality = inflate.findViewById(R.id.exListStalkerDBLocality);

        // Inflate the layout for this fragment
        return inflate;
    }


    class LocalityAdapter extends SimpleCursorTreeAdapter {
        Context context;
        Cursor cursor;
        String[] childFrom;

        public LocalityAdapter(Context context, Cursor cursor, int groupLayout,
                               String[] groupFrom, int[] groupTo, int childLayout,
                               String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
            this.cursor = cursor;
            this.childFrom = childFrom;
            this.context = context;

        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // получаем курсор по элементам для конкретной группы
            int idColumn = groupCursor.getColumnIndex(DBHelper.KEY_ID_TABLE_OF_TABLES);
            String stringForCursor = "SELECT * FROM " +
                    "(SELECT l._id, l.name, l.description, l.image_path, l.kostyl, l.access_status FROM locality AS l\n" +
                    "UNION\n" +
                    "SELECT f._id, f.name, f.description, f.image_path, f.kostyl, f.access_status FROM faction AS f)\n" +
                    "WHERE kostyl = ? AND access_status =?";
            return database.rawQuery(stringForCursor, new String[]{String.valueOf(groupCursor.getInt(idColumn)), "true"});
        }
        /*
        * Оформляет родительские вьюшки в expandableView
        * Принимает cursor из expendableLocality, в котором DBHelper.TABLE_TABLE_OF_TABLES
        * В соответветствии с положением в DBHelper.TABLE_TABLE_OF_TABLES выставляет родительским вьюшкам
        * картинку и название
        */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_db_table, null);
            }
            TextView textView = convertView.findViewById(R.id.txtVdbTable);
            ImageView imageView = convertView.findViewById(R.id.iVdbTable);

            cursor.moveToPosition(groupPosition);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_KOSTYL_TABLE_OF_TABLES);
            int imageIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_TABLE_OF_TABLES);

            textView.setText(cursor.getString(nameIndex));
            imageView.setImageDrawable(imageDrawable(cursor, imageIndex));
            return convertView;
        }
        /*
        * Оформляет детские вьюшки в expandableView
        * Принимет getChildrenCursor - курсор, в котором собрана объедененная таблица методом UNION.
        * Не знаю, какие в объединенной таблице будут названия стоблцов, поэтому вместо названий
        * использую константы
        */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_locality, null);
            }
            Cursor localCursor = getChildrenCursor(cursor);
            TextView name = convertView.findViewById(R.id.txtVName);
            TextView description = convertView.findViewById(R.id.txtVDescription);
            ImageView imageView = convertView.findViewById(R.id.imageViewItemLocality);

            int NAME_INDEX = 1;
            int DESCRIPTION_INDEX = 2;
            int IMAGE_INDEX = 3;

            localCursor.moveToPosition(childPosition);
            name.setText(localCursor.getString(NAME_INDEX));
            description.setText(localCursor.getString(DESCRIPTION_INDEX));
            imageView.setImageDrawable(imageDrawable(localCursor, IMAGE_INDEX));

            /*Button button = (Button)convertView.findViewById(R.id.buttonChild);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext,"button is pressed",5000).show();
                }
            });*/

            return convertView;
        }
        /*
        * Служит для того, чтобы с помощью названия картинки из базы данных выбрать картинку для
        * expendableView
        * Принимает cursor и номер столбца в таблице
        */
        private Drawable imageDrawable(Cursor localCursor, int index){
            String path = "@drawable/" + localCursor.getString(index);
            int imageResource = getResources().getIdentifier(path, null,getActivity().getPackageName());
            return getResources().getDrawable(imageResource);
        }

    }


    public void expendableLocality(){
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_TABLE_OF_TABLES, null, null, null, null, null, null);
        // группы - должна быть одна
        String[] groupFrom = { DBHelper.KEY_NAME_TABLE_OF_TABLES};
        int[] groupTo = { android.R.id.text1 };
        // элементы группы
        String[] childFrom = { DBHelper.KEY_NAME_LOCALITY/*,  DBHelper.KEY_NAME_FACTION*/};
        int[] childTo = { android.R.id.text1 };
        // адаптер
        LocalityAdapter sctAdapter = new LocalityAdapter(getActivity(), cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_1, childFrom,
                childTo);
        exListLocality.setAdapter(sctAdapter);
    }




    @Override
    public void onResume() {
        super.onResume();
        expendableLocality();
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.close();
        cursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        cursor.close();
    }
}