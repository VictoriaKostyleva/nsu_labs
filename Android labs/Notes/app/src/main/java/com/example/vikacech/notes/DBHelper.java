package com.example.vikacech.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactDb";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CONTEXT = "context";
    public static final String KEY_CHECKED = "checked";
    public static final String KEY_DATE = "date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID + " integer primary key," + KEY_NAME + "  text," + KEY_CONTEXT + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS);

        onCreate(db);
    }

    public void deleteRow(SQLiteDatabase db, Note note)//TODO
    {
        int a = db.delete(TABLE_CONTACTS, KEY_ID + "= " + note.getId(), null);

    }

    public String getData(SQLiteDatabase db, int id, String str) {
        String res = null;
        String query = "SELECT " + str + " FROM " + TABLE_CONTACTS + " WHERE " + KEY_ID + "=" + id;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        res = cursor.getString(cursor.getColumnIndex(str));

        System.out.println(res);
        return res;
    }

    public static void addNote(SQLiteDatabase db, ContentValues contentValues) {
        db.insert(TABLE_CONTACTS, null, contentValues);
    }

    public static void updateNote(SQLiteDatabase db, ContentValues contentValues, String str) {
        db.update(TABLE_CONTACTS, contentValues, str ,null);
    }


}
