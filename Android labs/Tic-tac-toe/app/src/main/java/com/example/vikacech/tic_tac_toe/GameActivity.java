package com.example.vikacech.tic_tac_toe;

import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {
    int fieldRow = 3;
    int i, j;
    public String playerX = "X", playeyZero = "0";
    private Button[][] buttons= new Button[fieldRow][fieldRow];
    public String playerTurn;//кто ходит следующим
    private TableLayout layout;

    FirebaseDatabase database;

    private String[][] moves= new String[fieldRow + 1][fieldRow + 1];//масиив ходов

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("game");//ТУТ ВРОДЕ МОЖНО ОПРЕДЕЛЯТЬ ВЕТКУ
        ref.child("currentPlayer").setValue("X");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                getData(dataSnapshot);
                //func
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                getData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(childEventListener);


        //сама по себе игра

        playerTurn = playerX;//по умолчанию начинает тот, у кого крестики
        layout = (TableLayout) findViewById(R.id.table);

        for(i = 0; i < fieldRow; i++) {
            TableRow row = new TableRow(this);

            for(j = 0; j < fieldRow; j++) {


                Button button = new Button(this);
                buttons[i][j] = button;
                String id;

                if(i == 0) {
                    id = String.valueOf(j);
                } else {
                    id = String.valueOf(i) + String.valueOf(j);
                }
                button.setId(Integer.parseInt(id.toString()));
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Button button = (Button) view;
                        int viewId = view.getId();
                        String viewIdToString = String.valueOf(viewId);

                        if(viewIdToString.length() == 1) {
                            viewIdToString = "0_" + viewIdToString;
                        } else {
                            viewIdToString = viewIdToString.charAt(0) + "_" + viewIdToString.charAt(1);
                        }
                        setUserMoveToBase(viewIdToString, playerTurn);
                        if(playerTurn == "X") {//TODO ToString
                            changeUser("0");
                        } else {
                            changeUser("X");
                        }
                        button.setText(playerTurn);
                    }
                });
                row.addView(button, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                button.setWidth(107);//TODO
                button.setHeight(107);//TODO
            }
            layout.addView(row, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        }

    }

    private void getData(DataSnapshot dataSnapshot){

        Toast.makeText(getApplicationContext(), "added" + dataSnapshot.getKey().toString() + " : " + dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
        String key = dataSnapshot.getKey().toString();
        if(key != "currentPlayer") {//TODO equals

            String[] key_values = key.split("\\_");//Парсим ключ
            moves[Integer.parseInt(key_values[0])][Integer.parseInt(key_values[1])] = dataSnapshot.getValue().toString();
            String id;
            if(0 == Integer.parseInt(key_values[0])) {//если слева стоит нолик, то мы берем только первый индекс
                id = key_values[1];
            }
            else {
                id = key_values[0] +  key_values[1];//строки складываются как 1 + 1 = 11
            }

            Button btn = (Button) findViewById(Integer.parseInt(id));
            btn.setText(dataSnapshot.getValue().toString());//кладем в кнопку то, что взяли из базы

        }
        else {
            playerTurn = dataSnapshot.getValue().toString();
        }
    }


    private void setUserMoveToBase(String child, String data) {
        DatabaseReference ref = database.getReference();
        ref.child(child).setValue(data);
    }

    private void changeUser(String user) {
        DatabaseReference ref = database.getReference();
        ref.child("currentPlayer").setValue(user);
    }

}

