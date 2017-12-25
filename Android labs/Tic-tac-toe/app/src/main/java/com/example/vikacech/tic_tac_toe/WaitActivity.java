package com.example.vikacech.tic_tac_toe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitActivity extends AppCompatActivity {

    String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();//username
    String opponentName;
    String Uid = UUID.randomUUID().toString();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        myRef.child("WaitPlayers").child(Uid).setValue(Uid);//складываем ид и имя игрока
        myRef.child("WaitPlayers").child(Uid).setValue(name);

        pd = new ProgressDialog(this);
        pd.setTitle("Message:");
        pd.setMessage("We are searching an opponent for you, please wait");
        pd.show();

        myRef.child("WaitPlayers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> waitingPlayers = (HashMap<String, String>) dataSnapshot.getValue();

                if (waitingPlayers != null) {
                    for (Map.Entry<String, String> entry : waitingPlayers.entrySet()) {


                        if (!entry.getValue().equals(name)) {
                            opponentName = entry.getValue();
                            String opponentUid = entry.getKey();
//                            Toast.makeText(getApplicationContext(), "opp name: " + opponentName, Toast.LENGTH_SHORT).show();

                            String gameUid = createGameUid(Uid, opponentUid);


                            Intent intent = new Intent(WaitActivity.this, GameActivity.class);
                            intent.putExtra("opponent", opponentName);
                            intent.putExtra("keyOpponent", opponentUid);
                            intent.putExtra("GAME_UID", gameUid);
                            intent.putExtra("PLAY_WITH", Uid.toString().hashCode() < opponentUid.hashCode() ? "X" : "O");

                            myRef.child("WaitPlayers").removeValue();

                            startActivity(intent);


                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String createGameUid(String uidOne, String uidTwo) {
        String gameUid;
        int a = uidOne.hashCode() - uidTwo.hashCode();
        if (a > 0) {
            gameUid = uidOne + uidTwo;
        } else {
            gameUid = uidTwo + uidOne;
        }

        return gameUid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        finish();

//        if(resultCode == 666) {
//            finish();
//        }
//        if(resultCode == 777) {
//            finish();
//        }
//        if(resultCode == 101) {
//            finish();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        finish();
    }
}
