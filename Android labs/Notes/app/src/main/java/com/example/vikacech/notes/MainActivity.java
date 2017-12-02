package com.example.vikacech.notes;

import android.app.SearchManager;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";
    private final int NOTE_ADDED = 5;
    SQLiteDatabase db;

    FloatingActionButton fabAdd;
    ArrayList<Note> notes;
    private DBHelper dbHelper;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mCard;
    private MenuItem searchMenuItem;
    private Menu menu;
    private RecyclerView recyclerView;

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
                        Intent intent = ActivityAddNote.newIntent(MainActivity.this, new Note());
//                        Intent intent = ActivityAddNote.newIntent(MainActivity.this, new Note(1, "test", "test test", true, new Date(System.currentTimeMillis())));
                        startActivityForResult(intent, 1);

                        break;
                    default:
                        break;
                }
            }
        });

        dbHelper = new DBHelper(this);
//        final ArrayList<Note> notes = new ArrayList<>();
        notes = new ArrayList<>();


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
                Note note = new Note(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(noteIndex), false, null);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);

        initRecyclerView(notes);

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

    @Override
    protected void onResume() {

//        for(int i=0; i<recyclerView.getSize(); i++) {
//            recyclerView.delete(i);
//        }

        dbHelper = new DBHelper(this);
//        final ArrayList<Note> notes = new ArrayList<>();
        notes = new ArrayList<>();


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
                Note note = new Note(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(noteIndex), false, null);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);

        initRecyclerView(notes);

        super.onResume();
    }

    private void initRecyclerView(ArrayList<Note> array) {

        mAdapter = new RecyclerAdapter(array, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Note item) {
                AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);

                dialogDelete.setCancelable(true)
                        .setTitle("Are you sure?")
                        .setNegativeButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //del


//                                        dbHelper.deleteRow(db, item);//


                                        dialog.cancel();

                                        notes.remove(item);

                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                AlertDialog alert = dialogDelete.create();
                alert.show();

            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }


    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for zh
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu);
    }

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
        if (resultCode != RESULT_CANCELED) {
            if (data != null) {
                super.onActivityResult(requestCode, resultCode, data);

//                db = dbHelper.getWritableDatabase();
//                //вывод всех записей
//                Cursor c = db.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
//
//                int idIndex = 0;
//                int nameIndex = 0;
//                int noteIndex = 0;
//
//                if (c.moveToFirst()) {
//                    idIndex = c.getColumnIndex(DBHelper.KEY_ID);
//                    nameIndex = c.getColumnIndex(DBHelper.KEY_NAME);
//                    noteIndex = c.getColumnIndex(DBHelper.KEY_CONTEXT);
//
//
//                    do {
//                        // Note note = new Note(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(noteIndex), false, null);
//                    } while (c.moveToNext());
//                }
//
//                c.close();
//
//                mAdapter.getNotes().add(new Note(c.getInt(idIndex), c.getString(nameIndex), c.getString(noteIndex), false, null));


                mAdapter.notifyItemInserted(mAdapter.getNotes().size() - 1);//try
                //ifutils
                if (resultCode == NOTE_ADDED) {
                    Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);


        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        ArrayList<Note> searhItems = new ArrayList<>();
                        for (Note item : notes) {
                            if (compareString(item.getName(), s)) {
                                searhItems.add(item);
                            }
                        }

                        initRecyclerView(searhItems);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        ArrayList<Note> searhItems = new ArrayList<>();
                        int i = 0;
                        for (Note item : notes) {
                            if (compareString(item.getName(), s)) {
                                searhItems.add(item);
                            }
                        }


                        ArrayList<String> templist = new ArrayList<String>();
                        for (Note item : searhItems) {

                            templist.add(item.getName());

                        }

                        initRecyclerView(searhItems);
                        mRecyclerView.setAdapter(mAdapter);

                        return false;
                    }

                }
        );
        return true;
    }

    private boolean compareString(String main, String search) {
        if (search.length() > main.length()) {
            return false;
        }
        for (int i = 0; i < search.length(); i++) {
            if (!(main.charAt(i) == search.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.delete_everything) {
            AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);

            dialogDelete.setCancelable(true)
                    .setTitle("Are you sure you want delete everything???")
                    .setNegativeButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    AlertDialog.Builder dialogDelete2 = new AlertDialog.Builder(MainActivity.this);

                                    dialogDelete2.setCancelable(true)
                                            .setTitle("Really?")
                                            .setNegativeButton("Delete",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            //del
                                                            SQLiteDatabase database = dbHelper.getWritableDatabase();
                                                            database.delete(DBHelper.TABLE_CONTACTS, null, null);

                                                            Toast.makeText(MainActivity.this, "You have deleted everything", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                    AlertDialog alert = dialogDelete2.create();
                                    alert.show();


                                }
                            });
            AlertDialog alert = dialogDelete.create();
            alert.show();
        }


//        else {
//            if (id == R.id.search) {
//                Toast.makeText(MainActivity.this, getString(R.string.search), Toast.LENGTH_LONG).show();
//            }
//        }

        return super.onOptionsItemSelected(item);
    }


}
