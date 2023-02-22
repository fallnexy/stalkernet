package com.example.myapplication2.fragments.childTabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.myapplication2.DBHelper;
import com.example.myapplication2.QuestConfirmDialog;
import com.example.myapplication2.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CreedChildFragment extends Fragment {

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    ExpandableListView exListCreed;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_creed_child, viewGroup, false);

        dbHelper = new DBHelper(getActivity());
        // expendableListView для таблицы quest and quest_step
        exListCreed = inflate.findViewById(R.id.exListStalkerBDCreed);

        // Inflate the layout for this fragment
        return inflate;
    }

    class CreedAdapter extends SimpleCursorTreeAdapter {
        Context context;
        Cursor cursor;
        String[] childFrom;

        public CreedAdapter(Context context, Cursor cursor, int groupLayout,
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
            int idColumn = groupCursor.getColumnIndex(DBHelper.KEY_ID__CREED);
            String stringForCursor = "SELECT * FROM creed_branch WHERE creed_id =? AND access_status IN ('true', 'TRUE')";
            return database.rawQuery(stringForCursor, new String[]{String.valueOf(groupCursor.getInt(idColumn))});
        }
        /*
         * Оформляет родительские вьюшки в expandableView
         * Принимает cursor из expendableQuest, в котором DBHelper.TABLE_QUEST
         * В соответветствии с положением в DBHelper.TABLE_QUEST выставляет родительским вьюшкам
         * название и статус (завершено или не выполнено)
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_creed_parent, null);
            }
            TextView textName = convertView.findViewById(R.id.tVCreedName);
            TextView textDescription= convertView.findViewById(R.id.tVCreedDescription);
            ImageView imageView = convertView.findViewById(R.id.iVParentCreed);

            cursor.moveToPosition(groupPosition);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME__CREED);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__CREED);
            int imageIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE__CREED);

            textName.setText(cursor.getString(nameIndex));
            textDescription.setText(cursor.getString(descriptionIndex));
            imageView.setImageDrawable(imageDrawable(cursor, imageIndex));

            return convertView;
        }
        /*
         * Оформляет детские вьюшки в expandableView
         * Принимет getChildrenCursor - курсор, в котором собрана таблица stringForCursor.
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_creed_child, null);
            }
            Cursor localCursor = getChildrenCursor(cursor);
            TextView name = convertView.findViewById(R.id.tVCreedChildName);
            TextView status = convertView.findViewById(R.id.tVCreedChildStatus);
            TextView bonus = convertView.findViewById(R.id.tVCreedChildBonus);
            TextView description = convertView.findViewById(R.id.tVCreedChildDescription);
            TextView attention = convertView.findViewById(R.id.txtCreedAttention);
            Button confirm = convertView.findViewById(R.id.btnCreedChildConfirm);
            Button creedReset = convertView.findViewById(R.id.btnResetCreed);
            String code;
            String id;
            String branchId;

            int NAME_INDEX = localCursor.getColumnIndex(DBHelper.KEY_NAME__CREED_BRANCH);
            int DESCRIPTION_INDEX = localCursor.getColumnIndex(DBHelper.KEY_DESCRIPTION__CREED_BRANCH);
            int STATUS_INDEX = localCursor.getColumnIndex(DBHelper.KEY_STATUS__CREED_BRANCH);
            int ACCESS_INDEX = localCursor.getColumnIndex(DBHelper.KEY_ACCESS_KEY__CREED_BRANCH);
            int BONUS_INDEX = localCursor.getColumnIndex(DBHelper.KEY_BONUS__CREED_BRANCH);
            int ID_INDEX = localCursor.getColumnIndex(DBHelper.KEY_ID__CREED_BRANCH);
            int BRANCH_ID_INDEX = localCursor.getColumnIndex(DBHelper.KEY_BRANCH_ID__CREED_BRANCH);
            //Log.d("цвет", String.valueOf(BRANCH_ID_INDEX));
            localCursor.moveToPosition(childPosition);
            name.setText(localCursor.getString(NAME_INDEX));
            bonus.setText("Награда: " + localCursor.getString(BONUS_INDEX));
            description.setText(localCursor.getString(DESCRIPTION_INDEX));

            //
            // Делеат видимым предупреждение о невозможности сброса кредо и кнопки сброса
            // только для второго квеста
            //
            if ((localCursor.getInt(ID_INDEX) + 1) % 3 == 0){
                attention.setVisibility(View.VISIBLE);
                creedReset.setVisibility(View.VISIBLE);
            } else {
                attention.setVisibility(View.GONE);
                creedReset.setVisibility(View.GONE);
            }

            // выбор цвета ветки кредо
            switch (localCursor.getInt(BRANCH_ID_INDEX)){
                case 1:
                    convertView.setBackgroundColor(getResources().getColor(R.color.creedOne));
                    break;
                case 2:
                    convertView.setBackgroundColor(getResources().getColor(R.color.creedTwo));
                    break;
            }

            //
            // выставление статуса, завершен ли квест
            //
            if (localCursor.getString(STATUS_INDEX).toLowerCase(Locale.ENGLISH).equals("true")){
                ContentValues cv;
                cv = new ContentValues();
                status.setTextColor(getResources().getColor(R.color.green));
                status.setText("завершен");

                // делает доступной/недоступной кнопку сброс
                creedReset.setEnabled((localCursor.getInt(ID_INDEX) + 1) % 3 != 0);

                bonus.setVisibility(View.VISIBLE);

                //Если квест завершен, то отображает следующий
                // Следующий может быть из другого раздела, но он в данном случае true
                // Если следующего квеста нет, то ничего почему то не вылетает
                if (localCursor.getInt(ID_INDEX) % 3 != 0) {
                    int creedId = localCursor.getInt(ID_INDEX);
                    cv.put(DBHelper.KEY_ACCESS_STATUS__CREED_BRANCH, "true");
                    database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + (creedId+1), null);
                }

                // делает недоступной кнопку подтвердить выполнение
                confirm.setEnabled(false);

                notifyDataSetChanged();
            }else {
                status.setTextColor(getResources().getColor(R.color.design_default_color_error));
                status.setText("не завершен");

                // делает доступной кнопку подтвердить выполнение
                confirm.setEnabled(true);

                bonus.setVisibility(View.INVISIBLE);
            }

            //
            // Далее идет код для кнопки подтверждения выполнения квеста
            //
            code = localCursor.getString(ACCESS_INDEX);
            id = localCursor.getString(ID_INDEX);
            branchId = localCursor.getString(BRANCH_ID_INDEX);
            String finalCode = code; // почему то требует копировать вот так
            String finalID = id; // почему то требует копировать вот так
            String finalBranchID = branchId; // почему то требует копировать вот так

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuestConfirmDialog questConfirmDialog = new QuestConfirmDialog();
                    Bundle argsQuestDialog = new Bundle();
                    argsQuestDialog.putString("type", "creed");
                    argsQuestDialog.putString("code", finalCode);
                    argsQuestDialog.putString("group_position", String.valueOf(finalID)); // id от 1 до 24
                    argsQuestDialog.putString("child_position", String.valueOf(finalBranchID)); // id ветки: 1 или 2
                    questConfirmDialog.setArguments(argsQuestDialog);
                    questConfirmDialog.show(getActivity().getSupportFragmentManager(), "custom");
                }
            });

            //
            // кнопка сброс
            //
            creedReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues cv;
                    cv = new ContentValues();
                    cv.put(DBHelper.KEY_ACCESS_STATUS__CREED_BRANCH, "false");
                    int creedId = localCursor.getInt(ID_INDEX);
                    database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + (creedId), null);

                    cv = new ContentValues();
                    cv.put(DBHelper.KEY_ACCESS_STATUS__CREED_BRANCH, "true");
                    cv.put(DBHelper.KEY_STATUS__CREED_BRANCH, "false");
                    database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + (creedId - 1), null);

                    if (localCursor.getInt(BRANCH_ID_INDEX) == 1){
                        database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + (creedId + 2), null);
                    } else if (localCursor.getInt(BRANCH_ID_INDEX) == 2){
                        database.update(DBHelper.TABLE_CREED_BRANCH, cv, DBHelper.KEY_ID__CREED_BRANCH + "=" + (creedId - 4), null);
                    }

                    notifyDataSetChanged();
                }
            });
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

        // не знаю, помогает это штука в работе notifyDataSetChanged() или нет
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }
    }

    /*
    * заполняет expendableView данными
    */
    public void expendableCreed(){
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_CREED, null, null, null, null, null, null);
        // группы - должна быть одна
        String[] groupFrom = { DBHelper.KEY_NAME__CREED};
        int[] groupTo = { android.R.id.text1 };
        // элементы группы
        String[] childFrom = { DBHelper.KEY_BONUS__CREED_BRANCH};
        int[] childTo = { android.R.id.text1 };
        // адаптер
        CreedAdapter creedAdapter = new CreedAdapter(getActivity(), cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_1, childFrom,
                childTo);
        exListCreed.setAdapter(creedAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        expendableCreed();
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