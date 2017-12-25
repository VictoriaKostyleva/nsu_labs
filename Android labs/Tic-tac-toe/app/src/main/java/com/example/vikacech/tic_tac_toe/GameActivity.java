package com.example.vikacech.tic_tac_toe;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vikacech.tic_tac_toe.models.TurnData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GameActivity extends AppCompatActivity {
    int FIELD_SIZE = 3;
    private Button[] buttons= new Button[FIELD_SIZE*FIELD_SIZE];
    private String GAME_UID;
    private String PLAY_WITH;
    private boolean gameIsOver = false;
    private boolean isMyTurn;

    DatabaseReference ref;

    private int[][] gameField = new int[FIELD_SIZE][FIELD_SIZE];

    private String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database;

    enum GameResult {NOT_FINISHED, SOMEONE_WON, DRAW}

    private String opponent = null;

    private int turnCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String keyOpp = getIntent().getStringExtra("keyOpponent");
        opponent = getIntent().getStringExtra("opponent");
        GAME_UID = getIntent().getStringExtra("GAME_UID");
        PLAY_WITH = getIntent().getStringExtra("PLAY_WITH");

//        Toast.makeText(getApplicationContext(), "GAME_UID: " + GAME_UID, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "You play with " + PLAY_WITH, Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("game").child(GAME_UID);


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
        setButtonsEnabled(isMyTurn);
        if(isMyTurn) {
            Toast.makeText(getApplicationContext(), "You start the game :)", Toast.LENGTH_SHORT).show();
        }

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
                    setButtonsEnabled(isMyTurn);
                    button.setText(PLAY_WITH.equals("X") ? "X" : "0");


                    if (checkIfGameIsOver() == GameResult.SOMEONE_WON) {
                        Toast.makeText(GameActivity.this, "You won the game :)", Toast.LENGTH_SHORT).show();
                        setResult(777);//WON
                        finish();
                    } else if (checkIfGameIsOver() == GameResult.DRAW) {
                        Toast.makeText(GameActivity.this, "Draw", Toast.LENGTH_SHORT).show();
                        setResult(101);//DRAW
                        finish();
                    }
                }
            });
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TurnData turn = dataSnapshot.child(String.valueOf(turnCount)).getValue(TurnData.class);
                if (turn != null) {
                    if (!turn.getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        int coordinates = Integer.parseInt(turn.getTurn());
                        int xCoordinate = coordinates / 10;
                        int yCoordinate = coordinates % 10;
                        turnCount++;
                        Button button = buttons[xCoordinate * 3 + yCoordinate];
                        button.setText(PLAY_WITH.equals("X") ? "0" : "X");//достаем значение из базы
                        isMyTurn = true;
                        gameField[xCoordinate][yCoordinate] = -1;//отмечаем кнопку как посещенную
                        setButtonsEnabled(isMyTurn);

                        if (checkIfGameIsOver() == GameResult.SOMEONE_WON) {
                            Toast.makeText(GameActivity.this, "You lost the game", Toast.LENGTH_SHORT).show();
                            setResult(666);//LOST
                            finish();
                        } else if (checkIfGameIsOver() == GameResult.DRAW) {
                            Toast.makeText(GameActivity.this, "Draw", Toast.LENGTH_SHORT).show();
                            setResult(101);//DRAW
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Your turn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setButtonsEnabled(boolean value) {
        for (Button button : buttons) {
            String resourceName = getResources().getResourceEntryName(button.getId());
            String turn = resourceName.substring(6);
            int coordinates = Integer.parseInt(turn);
            int xCoordinate = coordinates / 10;
            int yCoordinate = coordinates % 10;
            if (gameField[xCoordinate][yCoordinate] != 0) {
                button.setEnabled(false);
            } else {
                button.setEnabled(value);
            }
        }
    }

    private GameResult checkIfGameIsOver() {

        for (int i = 0; i < FIELD_SIZE; ++i) {
            int rowSum = 0;
            for (int j = 0; j < FIELD_SIZE; ++j) {
                rowSum += gameField[i][j];
            }
            if (rowSum == FIELD_SIZE || rowSum == -FIELD_SIZE) {
                gameIsOver = true;
                return GameResult.SOMEONE_WON;
            }
        }

        for (int i = 0; i < FIELD_SIZE; ++i) {
            int columnSum = 0;
            for (int j = 0; j < FIELD_SIZE; ++j) {
                columnSum += gameField[j][i];
            }
            if (columnSum == FIELD_SIZE || columnSum == -FIELD_SIZE) {
                gameIsOver = true;
                return GameResult.SOMEONE_WON;
            }
        }

        int diagonalSum = 0;
        for (int i = 0; i < FIELD_SIZE; ++i) {
            diagonalSum += gameField[i][i];
        }

        if (diagonalSum == FIELD_SIZE || diagonalSum == -FIELD_SIZE) {
            gameIsOver = true;
            return GameResult.SOMEONE_WON;
        }

        diagonalSum = 0;
        for (int i = 0; i < FIELD_SIZE; ++i) {
            diagonalSum += gameField[i][FIELD_SIZE - i - 1];
        }

        if (diagonalSum == FIELD_SIZE || diagonalSum == -FIELD_SIZE) {
            gameIsOver = true;
            return GameResult.SOMEONE_WON;
        }

        boolean isDraw = true;
        for (int i = 0; i < FIELD_SIZE; ++i) {
            for (int j = 0; j < FIELD_SIZE; ++j) {
                if (gameField[i][j] == 0) {
                    isDraw = false;
                    break;
                }
            }
        }
        if (isDraw) {
            gameIsOver = true;
            return GameResult.DRAW;
        }

        return GameResult.NOT_FINISHED;
    }


}
