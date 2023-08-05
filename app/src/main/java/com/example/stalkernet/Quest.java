package com.example.stalkernet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Quest {

    protected SQLiteDatabase database;
    protected Cursor cursor;

    public Quest(SQLiteDatabase database, Cursor cursor){
        this.database = database;
        this.cursor = cursor;
    }
    /*
    * при изменении id юзера проверяет доступные квесты
    * TODO сделать так, чтобы при смене id недоступные квесты убирались
    * */
    public void isQuestAvailable(String user_id){
        // выбирает нужного юзера
        cursor = database.query(DBHelper.TABLE_USER, new String[]{DBHelper.KEY_ID__USER, DBHelper.KEY_QUEST_ID__USER}, DBHelper.KEY_ID__USER + " =?", new String[]{user_id}, null, null, null);
        if (cursor.moveToFirst()){
            // переводит ячейку с записанными квестами в массив
            String[] quests = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_QUEST_ID__USER)).split(",");
            int numberOfQuests = quests.length;
            // если доступны хоть какие то квесты, то продолжает
            if (!quests[0].equals("0")) {
                // создает нужное количетсво вопросительных знаков, равное количеству доступных квестов
                StringBuilder placeholderBuilder = new StringBuilder();
                for (int i = 0; i < numberOfQuests; i++) {
                    placeholderBuilder.append("?,");
                }
                String placeholders = placeholderBuilder.deleteCharAt(placeholderBuilder.length() - 1).toString();
                // вставляет нужное количество вопросительных знаков в IN
                String selection = DBHelper.KEY_ID__QUEST + " IN (" + placeholders + ")";
                // делает доступные квесты доступными
                cursor = database.query(DBHelper.TABLE_QUEST, new String[]{DBHelper.KEY_ID__QUEST, DBHelper.KEY_ACCESS_QUEST}, selection, quests, null, null, null);
                ContentValues cv;
                int i = 0;
                if (cursor.moveToFirst()){
                    do {
                        cv = new ContentValues();
                        cv.put(DBHelper.KEY_ACCESS_QUEST, "true");
                        database.update(DBHelper.TABLE_QUEST, cv, DBHelper.KEY_ID__QUEST + "=" + (quests[i]), null);
                        i++;
                    } while (cursor.moveToNext());
                }
            }
        }
        cursor.close();
    }
}
