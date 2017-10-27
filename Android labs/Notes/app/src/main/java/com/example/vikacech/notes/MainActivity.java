package com.example.vikacech.notes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    FloatingActionButton fabAdd;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mCard;

//    TextView delMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        fabAdd.setOnClickListener((View.OnClickListener) this);

        ArrayList<Note> notes = new ArrayList<>();

//        String[] myDataset = getDataSet();


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // используем linear layout manager
        mCard = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCard);


        // создаем адаптер
        mAdapter = new RecyclerAdapter(notes, new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note item) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);

//        delMe = (TextView) findViewById(R.id.del_me);

        initToolBar();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu);

    }

//    private String[] getDataSet() {
//
//        String[] mDataSet = new String[30];
//        for (int i = 0; i < 1; i++) {
//            mDataSet[i] = "item" + i;
//        }
//        return mDataSet;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonAdd:
                Intent intent = new Intent(this, ActivityAddNote.class);
                startActivityForResult(intent, 1);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            return;
        }
        String name = data.getStringExtra("name");


//        delMe.setText("The name of the note is " +  name );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

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
