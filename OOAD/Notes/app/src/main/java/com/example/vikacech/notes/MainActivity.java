package com.example.vikacech.notes;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
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

    private final int NOTE_ADDED = 5;
//    SQLiteDatabase db;

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
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.floatingActionButtonAdd:
                        Intent intent = ActivityAddNote.newIntent(MainActivity.this, new Note());
                        startActivityForResult(intent, 1);

                        break;
                    default:
                        break;
                }
            }
        });

        dbHelper = new DBHelper(this);
        notes = new ArrayList<>();

        dbHelper.showNotes(notes);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);

        initRecyclerView(notes);

        initToolBar();

    }

    @Override
    protected void onResume() {

        dbHelper = new DBHelper(this);
        notes = new ArrayList<>();

//        db = dbHelper.getWritableDatabase();
        //вывод всех записей


        dbHelper.showNotes(notes);




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
                        .setTitle("Settings")
//                        .setNegativeButton("Delete",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                        dbHelper.deleteRow(db, item);//TODO
//                                        Toast.makeText(getApplicationContext(), "Id: " + item.getId(), Toast.LENGTH_SHORT).show();
//
//
//                                        dialog.cancel();
//
//                                        notes.remove(item);
//
//                                        mAdapter.notifyDataSetChanged();
//                                    }
//                                });


                .setItems(new CharSequence[]
                                {"Open", "Delete"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        Intent intent = ActivityAddNote.newIntent(MainActivity.this, item);
                                        startActivityForResult(intent, 1);
//                                        Toast.makeText(getApplicationContext(), "Opened", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        dbHelper.deleteRow(item);//TODO
//                                        Toast.makeText(getApplicationContext(), "Id: " + item.getId(), Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                        notes.remove(item);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                }
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
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (data != null) {
                super.onActivityResult(requestCode, resultCode, data);

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

}
