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

public class ActivityAddNote extends AppCompatActivity {

    public static final String KEY_NOTE = "note";
    private static final int RESULT_OK = 0;
    private DBHelper dbHelper;
    private FloatingActionButton fabNoteDone;
    private EditText enterNameNote;
    private EditText enterNote;
    private Toolbar toolbar;
    private Button button_1_read;
    private Button button_2_clear;
    final int NOTE_ADDED = 5;

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
        fabNoteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameNote = enterNameNote.getText().toString();
                String contentNote = enterNote.getText().toString();

                SQLiteDatabase database = dbHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, nameNote);
                contentValues.put(DBHelper.KEY_CONTEXT, contentNote);
                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);


                setResult(NOTE_ADDED);
                finish();
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);

    }
}
