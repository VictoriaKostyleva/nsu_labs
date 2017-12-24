package com.example.vikacech.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.vikacech.tic_tac_toe.models.Constants;
import com.example.vikacech.tic_tac_toe.models.GameOnline;
import com.example.vikacech.tic_tac_toe.models.WaitForUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitActivity  extends AppCompatActivity {

    String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();//username
    String opponentName;
//    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String Uid = UUID.randomUUID().toString();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

//        Toast.makeText(getApplicationContext(),"my name: " + name, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), "my uid: " + Uid, Toast.LENGTH_SHORT).show();

        myRef.child("WaitPlayers").child(Uid).setValue(Uid);//складываем ид и имя игрока
        myRef.child("WaitPlayers").child(Uid).setValue(name);

        myRef.child("WaitPlayers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> waitingPlayers = (HashMap<String, String>) dataSnapshot.getValue();

                if (waitingPlayers != null) {
                    for (Map.Entry<String, String> entry : waitingPlayers.entrySet()) {


                        if (!entry.getValue().equals(name)) {
                            opponentName = entry.getValue();
                            String opponentUid = entry.getKey();
                            Toast.makeText(getApplicationContext(), "opp name: " + opponentName, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), "opp uid:" + opponentUid, Toast.LENGTH_SHORT).show();
//
                            String gameUid = createGameUid(Uid, opponentUid);
//                            Toast.makeText(getApplicationContext(), "game uid: " + gameUid, Toast.LENGTH_SHORT).show();



//

//                            myRef.child(gameUid);

                            Intent intent = new Intent(WaitActivity.this, GameActivity.class);
                            intent.putExtra("opponent", opponentName);
                            intent.putExtra("keyOpponent", opponentUid);
                            intent.putExtra("GAME_UID", gameUid);
                            intent.putExtra("PLAY_WITH", Uid.toString().hashCode() < opponentUid.hashCode() ? "X" : "O");

                            myRef.child("WaitPlayers").removeValue();

                            startActivity(intent);




//                            WaitForUser wait = dataSnapshot.getValue(WaitForUser.class);
//                             boolean itsMe = dataSnapshot.getKey().equals(Uid);
//                            Intent intent = new Intent(WaitActivity.this, GameActivity.class);
//                            intent.putExtra("opponent", opponentName);
//                            intent.putExtra("keyOpponent", opponentUid);
//                            startActivity(intent);
//                            myRef.child("waitingPlayers").child(gameUid.toString()).setValue(gameUid);



//                            myRef.child(gameUid.toString()).setValue(gameUid);//создаю игру

//
//                    if (waitingPlayers != null) {
//                        for (Map.Entry<String, String> entry : waitingPlayers.entrySet()) {
//                            if (!entry.getValue().equals(username)) {
//                                opponentName = entry.getValue();
//                                System.out.println("OPPONENT_NAME : " + opponentName);
//                                String opponentUid = entry.getKey();
//                                String gameUid = createGameUid(gameUuid.toString(), opponentUid);
//                            myRef.child("waitingPlayers").child(gameUuid.toString()).removeValue();
//                                Intent game = new Intent(ChooseOpponent.this, GameFieldActivity.class);
//                                game.putExtra(Constants.GAME_UID, gameUid);
//                                game.putExtra(Constants.OPPONENT_NAME, opponentName);
//                                game.putExtra(Constants.PLAY_FOR, gameUuid.toString().hashCode() < opponentUid.hashCode() ? "X" : "O");
//                                isPlaying = true;
//                                startActivityForResult(game, Constants.GAME_RESULT);
//                                break;
//                            }
//                        }}
//


                        }}}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        firebaseDatabaseReference.child("waitingPlayers").addValueEventListener(new ValueEventListener() {

//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
////                WaitForUser wait = dataSnapshot.getValue(WaitForUser.class);
////                boolean itsMe = dataSnapshot.getKey().equals(Uid);
////
////
////                if (wait == null || wait.getOpponent() != null || itsMe) {//пусто/с ним кто-то играет/это я сам
////                    return;
////                }
////
////
////                wait.setOpponent(name);
////                wait.setuidOpp(Uid);
////
////
////                myRef.getParent().child("games").child(Uid)
////                        .setValue(new GameOnline(new WaitForUser(wait.getOpponent(), wait.getHost(), dataSnapshot.getKey())));// первый зашёл
////
////
////                myRef.child(dataSnapshot.getKey()).setValue(wait);//строка эта заполняет оппонента
////                myRef.removeEventListener(this);
////
////                Intent intent = new Intent(WaitActivity.this, GameActivity.class);
////                intent.putExtra("opponent", wait.getHost());
////                intent.putExtra("keyOpponent", dataSnapshot.getKey());
////                startActivity(intent);
////
//
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    private String createGameUid(String uidOne, String uidTwo) {
        String gameUid;
        int a = uidOne.hashCode() - uidTwo.hashCode();
        if(a > 0) {
            gameUid = uidOne + uidTwo;
        }
        else {
            gameUid = uidTwo + uidOne;
        }

        return gameUid;
    }
}
