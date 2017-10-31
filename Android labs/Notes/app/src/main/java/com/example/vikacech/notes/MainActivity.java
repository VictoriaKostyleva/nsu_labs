package com.example.vikacech.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {// implements View.OnClickListener {

    final String LOG_TAG = "myLogs";

    private DBHelper dbHelper;
    SQLiteDatabase db;

    FloatingActionButton fabAdd;
    private Toolbar toolbar;
    //    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mCard;

    private MenuItem searchMenuItem;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        //fabAdd.setOnClickListener((View.OnClickListener) this);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.floatingActionButtonAdd:
                        Intent intent = ActivityAddNote.newIntent(MainActivity.this, new Note("test", "test test", true, new Date(System.currentTimeMillis())));
                        startActivityForResult(intent, 1);

                        break;
                    default:
                        break;
                }
            }
        });

        dbHelper = new DBHelper(this);
        ArrayList<Note> notes = new ArrayList<>();
        db = dbHelper.getWritableDatabase();
       //вывод всех записей
        Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int noteIndex = cursor.getColumnIndex(DBHelper.KEY_CONTEXT);
//                    int statusIndex = cursor.getColumnIndex(DBHelper.KEY_CHECKED);
//                    int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            do {
                Note note = new Note(cursor.getString(nameIndex), cursor.getString(noteIndex), false, null);
                notes.add(note);
            } while (cursor.moveToNext());
        }




        /*DBHelper.init();
        * ArrayList notes = new ArrayList();
        * Note note = new Note(DBHelper.getAll());
        * notes.add(note);
        *
        * //data
        * //search
        * //sort
        * backpress - передавать результат, чтобы он не выводил тост
        * поменять иконку
        * */


        mRecyclerView = findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);

        // создаем адаптер
        mAdapter = new RecyclerAdapter(notes, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note item) {
                //Toast.makeText(MainActivity.this, "Item clicked!", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);

                dialogDelete.setCancelable(true)
                        .setTitle("Are you sure?")
                        .setNegativeButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = dialogDelete.create();
                alert.show();
//                Intent intent = new Intent(this, ActivityAddNote.class);
//                startActivityForResult(intent, 1);

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        initToolBar();
//        createCards();

//        dbHelper = new DBHelper(this);
//
//        db = dbHelper.getWritableDatabase();
//
//
////вывод всех записей
//        Cursor cursor = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
//
//        if (cursor.getCount() == 0) {
//            Log.d(LOG_TAG, "empty table");
//        } else {
//            Log.d("log", "no rows");
//        }
//        cursor.close();
//        dbHelper.close();

    }


    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for zh
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu);
    }

//    private void createCards() {
//        ArrayList<Note> notes = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            notes.add(new Note("Note" + i, "Empty", false, new Date(System.currentTimeMillis())));
//        }
//        mAdapter.updateNotes(notes);
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem edit = menu.findItem(R.id.action_edit);
        edit.setVisible(false);

        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(data == null) {
            return;
        }
        String name = data.getStringExtra("name");*/
//        Note note = data.getParcelableExtra(ActivityAddNote.KEY_NOTE);
//        mAdapter.getNotes().add(note);
//        mAdapter.updateNotes(mAdapter.getNotes());

        mAdapter.notifyDataSetChanged();
        //ifutils
        Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.settings) {
            Toast.makeText(MainActivity.this, getString(R.string.settings), Toast.LENGTH_LONG).show();
        }
//        else {
//            if (id == R.id.search) {
//                Toast.makeText(MainActivity.this, getString(R.string.search), Toast.LENGTH_LONG).show();
//            }
//        }

        return super.onOptionsItemSelected(item);
    }


}
