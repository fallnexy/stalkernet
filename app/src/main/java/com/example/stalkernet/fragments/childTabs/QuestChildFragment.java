package com.example.stalkernet.fragments.childTabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.stalkernet.DBHelper;
import com.example.stalkernet.QuestConfirmDialog;
import com.example.stalkernet.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class QuestChildFragment extends Fragment {

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    ExpandableListView exListQuest;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_quest_child, viewGroup, false);

        dbHelper = new DBHelper(getActivity());
        // expendableListView для таблицы quest and quest_step
        exListQuest = inflate.findViewById(R.id.exListStalkerDBQuest);

        // Inflate the layout for this fragment
        return inflate;
    }

    class QuestAdapter extends SimpleCursorTreeAdapter  {
        Context context;
        Cursor cursor;
        String[] childFrom;

        public QuestAdapter(Context context, Cursor cursor, int groupLayout,
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
            int idColumn = groupCursor.getColumnIndex(DBHelper.KEY_ID__QUEST);
            String stringForCursor = "SELECT _id, quest_id, description, status, access_key FROM quest_step " +
                    "WHERE quest_id =? AND access_status =?";
            return database.rawQuery(stringForCursor, new String[]{String.valueOf(groupCursor.getInt(idColumn)), "true"});
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
                convertView = inflater.inflate(R.layout.item_quest_parent, null);
            }
            TextView textName = convertView.findViewById(R.id.tVParentQuestName);
            TextView textStatus= convertView.findViewById(R.id.tVParentQuestStatus);
            TextView textDescription= convertView.findViewById(R.id.tVParentQuestDescription);
            //ImageView imageView = convertView.findViewById(R.id.iVdbTable);

            cursor.moveToPosition(groupPosition);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME_QUEST);
            int statusIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS__QUEST);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_QUEST);
            //int imageIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_TABLE_OF_TABLES);

            textName.setText(cursor.getString(nameIndex));
            textDescription.setText(cursor.getString(descriptionIndex));
            if (cursor.getString(statusIndex).toLowerCase(Locale.ENGLISH).equals("true")){
                textStatus.setTextColor(getResources().getColor(R.color.green));
                textStatus.setText("КВЕСТ ЗАВЕРШЕН");
            } else if(cursor.getString(statusIndex).toLowerCase(Locale.ENGLISH).equals("const")){
                textStatus.setTextColor(getResources().getColor(R.color.green));
                textStatus.setText("ЗАГРУЗ или\nПОСТОЯННОЕ ЗАДАНИЕ");
            } else {
                textStatus.setTextColor(getResources().getColor(R.color.design_default_color_error));
                textStatus.setText("КВЕСТ НЕ ВЫПОЛНЕН");
            }
            //imageView.setImageDrawable(imageDrawable(cursor, imageIndex));
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
                convertView = inflater.inflate(R.layout.item_quest_child, null);
            }

            Cursor localCursor = getChildrenCursor(cursor);
            TextView description = convertView.findViewById(R.id.tvQuestStepDescription);
            TextView status = convertView.findViewById(R.id.tVquestStepStatus);
            Button button = convertView.findViewById(R.id.btnConfirmQuestStep);
            String code;

            int DESCRIPTION_INDEX = localCursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_QUEST_STEP);
            int STATUS_INDEX = localCursor.getColumnIndex(DBHelper.KEY_STATUS_QUEST_STEP);
            int ACCESS_INDEX = localCursor.getColumnIndex(DBHelper.KEY_ACCESS_KEY_QUEST_STEP);

            localCursor.moveToPosition(childPosition);
            description.setText(localCursor.getString(DESCRIPTION_INDEX));

            //выставляет статус завершенности квеста
            if (localCursor.getString(STATUS_INDEX).toLowerCase(Locale.ENGLISH).equals("true")){
                ContentValues cv;
                status.setTextColor(getResources().getColor(R.color.green));
                status.setText("Статус:\nПодквест завершен");
                button.setEnabled(false);
                /*
                * Далее идет определение того, выполненный подквест - последний в квесте или нет,
                * и что делать в обоих случаях
                */
                int idIndex = localCursor.getColumnIndex(DBHelper.KEY_ID__QUEST_STEP);
                int questStepId = localCursor.getInt(idIndex); //id выполненного подквеста
                Cursor isNextStepCursor = database.rawQuery("SELECT _id, quest_id FROM quest_step WHERE quest_id =?", new String[]{String.valueOf(groupPosition + 1)});
                isNextStepCursor.moveToLast();
                int questId = isNextStepCursor.getInt(idIndex); //id последнего подквеста в курсоре
                /*
                * Если выполненный подквест последний в квесте, то ставит статус родительскому квесту TRUE.
                * Иначе если id выполненного подквеста меньше id последнего подквеста в квесте, то отображеет
                * в детской вьешке следующий подквест
                */
                if (questStepId == questId){
                    cv = new ContentValues();
                    cv.put(DBHelper.KEY_STATUS__QUEST, "true");
                    database.update(DBHelper.TABLE_QUEST, cv, DBHelper.KEY_ID__QUEST + "=" + (groupPosition+1), null);
                } else if(questStepId < questId){
                    cv = new ContentValues();
                    cv.put(DBHelper.KEY_ACCESS_STATUS__QUEST_STEP, "true");
                    database.update(DBHelper.TABLE_QUEST_STEP, cv, DBHelper.KEY_ID__QUEST_STEP + "=" + (questStepId+1), null);
                }
                isNextStepCursor.close();
                notifyDataSetChanged();
            }else {
                status.setTextColor(getResources().getColor(R.color.design_default_color_error));
                status.setText("Статус:\nПодквест не завершен");
                button.setEnabled(true);
            }
            /*
            * Далее идет код для кнопки подтверждения выполнения квеста
            */
            code = localCursor.getString(ACCESS_INDEX);
            String finalCode = code; // почему то требует копировать вот так


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuestConfirmDialog questConfirmDialog = new QuestConfirmDialog();
                    Bundle argsQuestDialog = new Bundle();
                    argsQuestDialog.putString("type", "quest");
                    argsQuestDialog.putString("code", finalCode);
                    argsQuestDialog.putString("group_position", String.valueOf(groupPosition + 1));
                    argsQuestDialog.putString("child_position", String.valueOf(childPosition));
                    questConfirmDialog.setArguments(argsQuestDialog);
                    questConfirmDialog.show(getActivity().getSupportFragmentManager(), "custom");
                }
            });

            return convertView;
        }

        /*
         * Служит для того, чтобы с помощью названия картинки из базы данных выбрать картинку для
         * expendableView
         * Принимает cursor и номер столбца в таблице
         */
        /*private Drawable imageDrawable(Cursor localCursor, int index){
            String path = "@drawable/" + localCursor.getString(index);
            int imageResource = getResources().getIdentifier(path, null,getActivity().getPackageName());
            return getResources().getDrawable(imageResource);
        }*/

        // не знаю, помогает это штука в работе notifyDataSetChanged() или нет
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }
    }




    public void expendableQuest(){
        database = dbHelper.open();
        cursor = database.query(DBHelper.TABLE_QUEST, null, DBHelper.KEY_ACCESS_QUEST + " =?", new String[]{"true"}, null, null, null);
        // группы - должна быть одна
        String[] groupFrom = { DBHelper.KEY_NAME_QUEST};
        int[] groupTo = { android.R.id.text1 };
        // элементы группы
        String[] childFrom = { DBHelper.KEY_DESCRIPTION_QUEST_STEP};
        int[] childTo = { android.R.id.text1 };
        // адаптер
        QuestAdapter questAdapter = new QuestAdapter(getActivity(), cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, android.R.layout.simple_list_item_1, childFrom,
                childTo);
        exListQuest.setAdapter(questAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        expendableQuest();
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