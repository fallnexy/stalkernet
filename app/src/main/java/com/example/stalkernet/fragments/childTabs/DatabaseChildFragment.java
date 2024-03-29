package com.example.stalkernet.fragments.childTabs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


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
                    "(SELECT l._id, l.name, l.description, l.image, l.kostyl, l.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM locality AS l\n" +
                    "UNION\n" +
                    "SELECT f._id, f.name, f.description, f.image, f.kostyl, f.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM faction AS f\n" +
                    "UNION\n" +
                    "SELECT p._id, p.name, p.description, p.image, p.kostyl, p.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM person AS p\n" +
                    "UNION\n" +
                    "SELECT m._id, m.name, m.description, m.image, m.kostyl, m.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM monster AS m\n" +
                    "UNION\n" +
                    "SELECT a._id, a.name, a.description, a.image, a.kostyl, a.access_status, a.feature, a.apply_level, a.is_dangerous FROM artefact AS a\n" +
                    "UNION\n" +
                    "SELECT ms._id, ms.name, ms.description, ms.image, ms.kostyl, ms.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM milestone AS ms\n" +
                    "UNION\n" +
                    "SELECT c._id, c.name, c.description, c.image, c.kostyl, c.access_status, NULL AS feature, NULL AS apply_level, NULL AS is_dangerous FROM checkpoint AS c)\n" +
                    "WHERE kostyl = ? AND access_status !=?";
            return database.rawQuery(stringForCursor, new String[]{String.valueOf(groupCursor.getInt(idColumn)), "false"});
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
                convertView = inflater.inflate(R.layout.item_db_child, null);
            }
            Cursor localCursor = getChildrenCursor(cursor);
            TextView name = convertView.findViewById(R.id.txtVName);
            TextView description = convertView.findViewById(R.id.txtVDescription);
            ImageView imageView = convertView.findViewById(R.id.imageViewItemLocality);
            TextView artFeature = convertView.findViewById(R.id.tvDBFeature);
            TextView artFeaturePre = convertView.findViewById(R.id.tvDBFeaturePreText);
            TextView addInfo = convertView.findViewById(R.id.tvAddInfo);

            int NAME_INDEX = 1;
            int DESCRIPTION_INDEX = 2;
            int IMAGE_INDEX = 3;
            int featureIndex = localCursor.getColumnIndex(DBHelper.KEY_FEATURE__ARTEFACT);
            int kostylIndex = localCursor.getColumnIndex(DBHelper.KEY_KOSTYL__ARTEFACT);
            int applyLevelIndex = localCursor.getColumnIndex(DBHelper.KEY_APPLY_LEVEL__ARTEFACT);
            int accessStatusIndex = localCursor.getColumnIndex(DBHelper.KEY_ACCESS_STATUS__ARTEFACT);
            int addInfoIndex;

            localCursor.moveToPosition(childPosition);

            name.setText(localCursor.getString(NAME_INDEX));
            description.setText(localCursor.getString(DESCRIPTION_INDEX));
            imageView.setImageDrawable(imageDrawable(localCursor, IMAGE_INDEX));

            /*
            * случай артефакта
            * */
            if (localCursor.getInt(kostylIndex) == 5){
                addInfoIndex = localCursor.getColumnIndex(DBHelper.KEY_IS_DANGEROUS__ARTEFACT);
                if (localCursor.getString(addInfoIndex).equals("true")){
                    addInfo.setText("Опасный");
                } else {
                    addInfo.setText("Безопасный");
                }

                String feature = localCursor.getString(featureIndex);
                artFeature.setText(feature);
                if (localCursor.getString(accessStatusIndex).equals("partial")){
                    description.setVisibility(View.GONE);
                    artFeature.setVisibility(View.GONE);
                } else {
                    artFeature.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                }
                artFeaturePre.setVisibility(View.VISIBLE);
                artFeaturePre.setText(colorChanger(localCursor.getInt(applyLevelIndex)));

                if (feature.equals("none")){
                    artFeature.setVisibility(View.GONE);
                    artFeaturePre.setVisibility(View.GONE);
                } else {
                    artFeaturePre.setVisibility(View.VISIBLE);
                }
            } else {
                addInfo.setText("");
                artFeaturePre.setVisibility(View.GONE);
                artFeature.setVisibility(View.GONE);
            }


            localCursor.close();
            /*
            * позволяет увеличивать картинки в бестиарии
            * */
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.expanded_image, null);
                    builder.setView(dialogView);
                    ImageView expandedImageView = dialogView.findViewById(R.id.expanded_image_view);
                    expandedImageView.setImageDrawable(imageView.getDrawable());
                    builder.show();
                }
            });

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
        String[] childFrom = { DBHelper.KEY_NAME__LOCALITY/*,  DBHelper.KEY_NAME_FACTION*/};
        int[] childTo = { android.R.id.text1 };
        // адаптер
        LocalityAdapter sctAdapter = new LocalityAdapter(getActivity(), cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_1, childFrom,
                childTo);
        exListLocality.setAdapter(sctAdapter);
    }
    /*
    * закраска нужной стадии у артоса
    * */
    private SpannableString colorChanger(int level){
        String feature = "Свойства:\nСтадия I\nСтадия II\nСтадия III";
        SpannableString spannableString = new SpannableString(feature);
        int colorForStageI = Color.GREEN;
        int startStageI;
        int endStageI;
        switch (level){
            default:
            case 1:
                startStageI = feature.indexOf("Стадия I");
                endStageI = startStageI + "Стадия I".length();
                break;
            case 2:
                startStageI = feature.indexOf("Стадия II");
                endStageI = startStageI + "Стадия II".length();
                break;
            case 3:
                startStageI = feature.indexOf("Стадия III");
                endStageI = startStageI + "Стадия III".length();
                break;
        }
        spannableString.setSpan(new ForegroundColorSpan(colorForStageI), startStageI, endStageI, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    @Override
    public void onResume() {
        super.onResume();
        expendableLocality();
    }

    @Override
    public void onPause() {
        super.onPause();
        database.close();
        cursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
        cursor.close();
    }
}