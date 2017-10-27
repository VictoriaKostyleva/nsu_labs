package com.example.vikacech.notes;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ActivityAddNote extends AppCompatActivity{

    FloatingActionButton fabNoteDone;
    EditText enterNameNote;
    EditText enterNote;

    static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ActivityAddNote.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        enterNameNote = (EditText) findViewById(R.id.enter_name_note);
        enterNote = (EditText) findViewById(R.id.enter_note);

        fabNoteDone = (FloatingActionButton) findViewById(R.id.floatingActionButtonNoteDone);
        fabNoteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //создаем заметку
            }
        });

    }




}
