package com.example.vikacech.notes;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

import java.util.Date;

public class ActivityAddNote extends AppCompatActivity{

    private FloatingActionButton fabNoteDone;
    private EditText enterNameNote;
    private EditText enterNote;
    private Toolbar toolbar;

    private Button button_1;
    private Button button_2;


    public static final String KEY_NOTE = "note";
    private static final int RESULT_OK = 0;

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

        enterNameNote = (EditText) findViewById(R.id.enter_name_note);
        enterNote = (EditText) findViewById(R.id.enter_note);
        fabNoteDone = (FloatingActionButton) findViewById(R.id.floatingActionButtonNoteDone);
        fabNoteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(KEY_NOTE, new Note(enterNameNote.getText().toString(), enterNote.getText().toString(), false, new Date(System.currentTimeMillis())));
                setResult(RESULT_OK, intent);
                finish();
                //создаем заметку
            }
        });

        button_1 = (Button) findViewById(R.id.button_1);
//        button_1.setOnClickListener();
        button_2 = (Button) findViewById(R.id.button_2);
//        button_2.setOnClickListener(this);

    }



    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);

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
//
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
