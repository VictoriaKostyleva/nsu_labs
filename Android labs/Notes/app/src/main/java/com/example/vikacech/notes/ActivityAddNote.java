package com.example.vikacech.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vikacech.notes.myNotes.Note;

public class ActivityAddNote extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_NOTE = "note";
    private static final int RESULT_OK = 0;
    DBHelper dbHelper;
    private FloatingActionButton fabNoteDone;
    private EditText enterNameNote;
    private EditText enterNote;
    private Toolbar toolbar;
    private Button button_1_read;
    private Button button_2_clear;

    static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, ActivityAddNote.class);
        intent.putExtra("NOTE", note);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        initToolBar();
        dbHelper = new DBHelper(this);

        enterNameNote = (EditText) findViewById(R.id.enter_name_note);
        enterNote = (EditText) findViewById(R.id.enter_note);
        fabNoteDone = (FloatingActionButton) findViewById(R.id.floatingActionButtonNoteDone);
        fabNoteDone.setOnClickListener(this);
//        fabNoteDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent();
////                intent.putExtra(KEY_NOTE, new Note(enterNameNote.getText().toString(), enterNote.getText().toString(), false, new Date(System.currentTimeMillis())));
////                setResult(RESULT_OK, intent);
////                finish();
//                //создаем заметку
//
//                String nameNote = enterNameNote.getText().toString();
//                String contentNote = enterNote.getText().toString();
//
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//
//                ContentValues contentValues = new ContentValues();
//
//                contentValues.put(DBHelper.KEY_NAME, nameNote);
//                contentValues.put(DBHelper.KEY_CONTEXT, contentNote);
//
//                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
//                dbHelper.close();
//
//            }
//        });

        button_1_read = (Button) findViewById(R.id.button_1_read);
        button_1_read.setOnClickListener(this);
        button_2_clear = (Button) findViewById(R.id.button_2_clear);
        button_2_clear.setOnClickListener(this);



    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View view) {

        String nameNote = enterNameNote.getText().toString();
        String contentNote = enterNote.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        switch (view.getId()) {
            case R.id.floatingActionButtonNoteDone:

                contentValues.put(DBHelper.KEY_NAME, nameNote);
                contentValues.put(DBHelper.KEY_CONTEXT, contentNote);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                break;

            case R.id.button_1_read:
//                contentValues.put(DBHelper.KEY_NAME, nameNote);
//                contentValues.put(DBHelper.KEY_CONTEXT, contentNote);
//
//                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                //


                Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int noteIndex = cursor.getColumnIndex(DBHelper.KEY_CONTEXT);

                    do {
                        Log.d("log", "ID = " + cursor.getInt(idIndex) +
                                ", name  = " + cursor.getString(nameIndex) +
                                ", context = " + cursor.getString(noteIndex));

                    } while (cursor.moveToNext());


                } else
                    Log.d("log", "no rows");

                cursor.close();
                break;
            case R.id.button_2_clear:

                database.delete(DBHelper.TABLE_CONTACTS, null, null);

                //
                break;

        }
        dbHelper.close();

    }


//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem edit = menu.findItem(R.id.action_edit);
//        edit.setVisible(true);
//
//        MenuItem delete = menu.findItem(R.id.action_delete);
//        delete.setVisible(true);
//
//        MenuItem search = menu.findItem(R.id.action_search);
//        delete.setVisible(false);
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//dbHelper.close();
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);//
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        item.setVisible(false);
//        return super.onOptionsItemSelected(item);
//    }

}
