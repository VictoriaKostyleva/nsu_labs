package com.example.vikacech.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.vikacech.notes.myNotes.Constants;
import com.example.vikacech.notes.myNotes.Note;

public class ActivityAddNote extends AppCompatActivity {

    public static final String KEY_NOTE = "note";
    private static final int RESULT_OK = 0;
    final int NOTE_ADDED = 5;

    int id_check = -1; //TODO not static
    private DBHelper dbHelper;
    private FloatingActionButton fabNoteDone;
    private EditText enterNameNote;
    private EditText enterNote;
    private Toolbar toolbar;

    static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, ActivityAddNote.class);
        intent.putExtra("NOTE", note);
        intent.putExtra("ID", note.getId());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        id_check = -1;

        initToolBar();
        dbHelper = new DBHelper(this);


        id_check = getIntent().getIntExtra("ID", -1);
//        Toast.makeText(getApplicationContext(), "Id: " + id_check, Toast.LENGTH_SHORT).show();


        enterNameNote = (EditText) findViewById(R.id.enter_name_note);
        enterNote = (EditText) findViewById(R.id.enter_note);

        if ((id_check != -1) && (id_check != 0)) {

//            db = dbHelper.getWritableDatabase();
            enterNameNote.setText(dbHelper.getData(id_check, Constants.KEY_NAME));
            enterNote.setText(dbHelper.getData(id_check, Constants.KEY_CONTEXT));
        }

        fabNoteDone = (FloatingActionButton) findViewById(R.id.floatingActionButtonNoteDone);
        fabNoteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameNote = enterNameNote.getText().toString();
                String contentNote = enterNote.getText().toString();

//                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.KEY_NAME, nameNote);
                contentValues.put(Constants.KEY_CONTEXT, contentNote);

                if ((id_check == -1) || (id_check == 0)) {
                    DBHelper.addNote(contentValues);
                } else {
                    String str = Constants.KEY_ID + "=" + id_check;

                    DBHelper.updateNote(contentValues, str);
//                    db.update(DBHelper.TABLE_CONTACTS, contentValues, str ,null);
                }

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
