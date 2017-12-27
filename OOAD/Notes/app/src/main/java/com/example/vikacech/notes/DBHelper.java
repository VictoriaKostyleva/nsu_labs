package com.example.vikacech.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vikacech.notes.myNotes.Constants;
import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper implements DataBase{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "contactDb";

//    public static final String TABLE_CONTACTS = "contacts";

//    public static final String KEY_ID = "_id";
//    public static final String KEY_NAME = "name";
//    public static final String KEY_CONTEXT = "context";


    static SQLiteDatabase db;



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.TABLE_CONTACTS + "(" + Constants.KEY_ID + " integer primary key," + Constants.KEY_NAME + "  text," + Constants.KEY_CONTEXT + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void deleteRow(Note note)//TODO
    {
        initBase();
//        db = getWritableDatabase();
        int a = db.delete(Constants.TABLE_CONTACTS, Constants.KEY_ID + "= " + note.getId(), null);

    }

    public String getData(int id, String str) {
        initBase();
        String res = null;
        String query = "SELECT " + str + " FROM " + Constants.TABLE_CONTACTS + " WHERE " + Constants.KEY_ID + "=" + id;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        res = cursor.getString(cursor.getColumnIndex(str));

        System.out.println(res);
        return res;
    }

    public static void addNote(ContentValues contentValues) {

        db.insert(Constants.TABLE_CONTACTS, null, contentValues);
    }

    public static void updateNote(ContentValues contentValues, String str) {
        db.update(Constants.TABLE_CONTACTS, contentValues, str ,null);
    }

    public void showNotes(ArrayList<Note> notes) {
//        db = getWritableDatabase();
        initBase();
        Cursor cursor = db.query(Constants.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(Constants.KEY_ID);
            int nameIndex = cursor.getColumnIndex(Constants.KEY_NAME);
            int noteIndex = cursor.getColumnIndex(Constants.KEY_CONTEXT);
            do {
                Note note = new Note(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(noteIndex), false, null);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public void initBase() {
        db = getWritableDatabase();
    }


}
