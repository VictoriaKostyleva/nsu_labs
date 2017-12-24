package com.example.vikacech.tic_tac_toe;

import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikacech.tic_tac_toe.models.GameOnline;
import com.example.vikacech.tic_tac_toe.models.TurnData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GameActivity extends AppCompatActivity {
    int fieldRow = 3;
    int i, j;
    public String playerX = "X";//, playeyZero = "0";
    private Button[] buttons= new Button[fieldRow*fieldRow];
    public String playerTurn;//кто ходит следующим
    private TableLayout layout;
    private int myTurn;
    private String GAME_UID;
    private String PLAY_WITH;
    private boolean gameIsOver = false;
    private boolean isMyTurn;

    DatabaseReference ref;

    private String[][] moves= new String[fieldRow + 1][fieldRow + 1];//масиив ходов
    private int[][] gameField = new int[fieldRow][fieldRow];

    private String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database;


    private String opponent = null;


    private int turnCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String keyOpp = getIntent().getStringExtra("keyOpponent");//достаем информацию о противнике
        opponent = getIntent().getStringExtra("opponent");
        GAME_UID = getIntent().getStringExtra("GAME_UID");
        PLAY_WITH = getIntent().getStringExtra("PLAY_WITH");

        Toast.makeText(getApplicationContext(), "GAME_UID: " + GAME_UID, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "PLAY_WITH " + PLAY_WITH, Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("game").child(GAME_UID);//TODO это имя моей ветки!!!


        buttons[0] = (Button) findViewById(R.id.button00);
        buttons[1] = (Button) findViewById(R.id.button01);
        buttons[2] = (Button) findViewById(R.id.button02);

        buttons[3] = (Button) findViewById(R.id.button10);
        buttons[4] = (Button) findViewById(R.id.button11);
        buttons[5] = (Button) findViewById(R.id.button12);

        buttons[6] = (Button) findViewById(R.id.button20);
        buttons[7] = (Button) findViewById(R.id.button21);
        buttons[8] = (Button) findViewById(R.id.button22);

        isMyTurn = PLAY_WITH.equals("X");
//        setAllButtonsEnabled(isMyTurn);//TODO если fal, то неактивна

        ref.child("curr" + PLAY_WITH).setValue(PLAY_WITH);


        for (final Button button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String resourceName = getResources().getResourceEntryName(button.getId());
                    String turn = resourceName.substring(6);
                    int coordinates = Integer.parseInt(turn);
                    int xCoordinate = coordinates / 10;
                    int yCoordinate = coordinates % 10;
                    gameField[xCoordinate][yCoordinate] = 1;
                    TurnData turnData = new TurnData(Uid, PLAY_WITH, turn);

                    ref.child(String.valueOf(turnCount)).setValue(turnData);
                    turnCount++;
                    isMyTurn = false;
                    setAllButtonsEnabled(isMyTurn);
//                    turnCountView.setText(String.valueOf(turnCount));
                    button.setText(PLAY_WITH.equals("X") ? "X" : "0");//(getResources().getDrawable(playFor.equals("X") ? R.drawable.cross : R.drawable.zero));
//                    if (checkIfGameIsOver() == GameResult.SOMEONE_WON) {
//                        Toast.makeText(GameFieldActivity.this, "You won the game", Toast.LENGTH_SHORT).show();
//                        setResult(Constants.WON);
//                        finish();
//                    } else if (checkIfGameIsOver() == GameResult.DRAW) {
//                        Toast.makeText(GameFieldActivity.this, "Draw", Toast.LENGTH_SHORT).show();
//                        setResult(Constants.DRAW);
//                        finish();
//                    }


                }
            });
        }


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                TurnData turn = dataSnapshot.child(String.valueOf(turnCount)).getValue(TurnData.class);
//                System.out.println(turn);
                if (turn != null) {
                    if (!turn.getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        int coordinates = Integer.parseInt(turn.getTurn());
                        int xCoordinate = coordinates / 10;
                        int yCoordinate = coordinates % 10;
                        turnCount++;
                        Button button = buttons[xCoordinate * 3 + yCoordinate];
                        button.setText(PLAY_WITH.equals("X") ? "0" : "X");//достаем значение из базы
//                        button.setBackground(getResources().getDrawable(playFor.equals(Constants.X) ? R.drawable.zero : R.drawable.cross));
                        isMyTurn = true;
                        gameField[xCoordinate][yCoordinate] = -1;
                        setAllButtonsEnabled(isMyTurn);

//                        if (checkIfGameIsOver() == GameResult.SOMEONE_WON) {
//                            Toast.makeText(GameFieldActivity.this, "You lost the game", Toast.LENGTH_SHORT).show();
//                            setResult(Constants.LOST);
//                            finish();
//                        } else if (checkIfGameIsOver() == GameResult.DRAW) {
//                            Toast.makeText(GameFieldActivity.this, "Draw", Toast.LENGTH_SHORT).show();
//                            setResult(Constants.DRAW);
//                            finish();
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public boolean checkWin(String[][] moves) {//TODO make equals
        Toast.makeText(getApplicationContext(), "checked", Toast.LENGTH_SHORT).show();

        String temp;
        for (int i = 0; i < fieldRow; ++i) {//по строкам
            temp = moves[i][0];
            if (temp != null && moves[i][1] == temp && moves[i][2] == temp) {
                return true;
            }

            temp = moves[0][i];//по столбцам
            if (temp != null && moves[1][i] == temp && moves[2][i] == temp) {
                return true;
            }
        }
        temp = moves[0][0];
        if (temp != null && moves[1][1] == temp && moves[2][2] == temp) {
            return true;
        }
        temp = moves[0][2];
        if (temp != null && moves[1][1] == temp && moves[2][0] == temp) {
            return true;
        }
        return false;
    }

    private void setUserMoveToBase(String child, String data) {
//        DatabaseReference ref = database.getReference();
        ref.child(child).setValue(data);
    }

    private void changeUser(String user) {//changes current player
//        DatabaseReference ref = database.getReference();
        ref.child("currentPlayer").setValue(user);
    }

    private void setAllButtonsEnabled(boolean value) {
        int i, j;
        for (Button button : buttons) {
                i = 0;
//                if (moves[i] != null) {
//                    button.setEnabled(false);
//                } else {
//                    button.setEnabled(value);
//                }
//                i++;


        }
    }














//    private void cleadBD(){
//        if(key.equals(Uid)){
//            ref.child(Uid).setValue(null);
//        }
//    }

}
