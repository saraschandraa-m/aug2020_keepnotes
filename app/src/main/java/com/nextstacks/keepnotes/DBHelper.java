package com.nextstacks.keepnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "task_details";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "task_title";
    private static final String COL_ITEMS = "task_items";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_TITLE + " TEXT," +
            COL_ITEMS + " TEXT)";


    public DBHelper(@Nullable Context context) {
        super(context, "keeptask.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertDataToDatabase(SQLiteDatabase database, String title, String items) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_ITEMS, items);
        database.insert(TABLE_NAME, null, cv);
    }

    public void updateDataToDatabase(SQLiteDatabase database, String title, String items, int id) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_ITEMS, items);
        database.update(TABLE_NAME, cv, COL_ID + "=" + id, null);
    }


    public ArrayList<TaskDetail> getDataFromDatabase(SQLiteDatabase database) {
        ArrayList<TaskDetail> taskDetails = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                String itemsArr = cursor.getString(cursor.getColumnIndex(COL_ITEMS));

                TaskDetail item = new TaskDetail();
                item.id = id;
                item.taskTitle = title;
                item.taskItemsArrayValue = itemsArr;

                taskDetails.add(item);
            } while (cursor.moveToNext());
        }


        return taskDetails;
    }
}
