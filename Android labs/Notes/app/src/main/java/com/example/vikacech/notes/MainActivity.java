package com.example.vikacech.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        fabAdd.setOnClickListener((View.OnClickListener) this);

        ArrayList<Note> notes = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);


        // создаем адаптер
        mAdapter = new RecyclerAdapter(notes, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note item) {
                Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(this, ActivityAddNote.class);
//                startActivityForResult(intent, 1);

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        initToolBar();
        createCards();

    }


    private void initToolBar() {
        //
//        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);//del
//        collapsingToolbarLayout.setTitle("It works!");

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu);


//        MenuItemCompat.setOnActionExpandListener(searchMenuItem.OnActionExpandListener);
//        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);

    }

    private void createCards() {
        ArrayList<Note> notes = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            notes.add(new Note("Note" + i, "Empty", false, new Date(System.currentTimeMillis())));
        }
        mAdapter.updateNotes(notes);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonAdd:
                Intent intent = ActivityAddNote.newIntent(this, new Note("test", "test test", true, new Date(System.currentTimeMillis())));


                startActivityForResult(intent, 1);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if(data == null) {
            return;
        }
        String name = data.getStringExtra("name");*/
        Note note = data.getParcelableExtra(ActivityAddNote.KEY_NOTE);
        mAdapter.getNotes().add(note);
        mAdapter.updateNotes(mAdapter.getNotes());

        Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
//        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem menuItem) {
//                Toast.makeText(MainActivity.this, "Search!!!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                Toast.makeText(MainActivity.this, "Return from search!!!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);//
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
