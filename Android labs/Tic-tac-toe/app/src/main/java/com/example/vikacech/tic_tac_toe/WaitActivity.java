package com.example.vikacech.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.vikacech.tic_tac_toe.models.GameOnline;
import com.example.vikacech.tic_tac_toe.models.WaitForUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaitActivity  extends AppCompatActivity {

    String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("WaitPlayers");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), Uid, Toast.LENGTH_SHORT).show();

        myRef.child(Uid).setValue(new WaitForUser(name, null), Uid);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Toast.makeText(getApplicationContext(), "here we are", Toast.LENGTH_SHORT).show();

                WaitForUser wait = dataSnapshot.getValue(WaitForUser.class);
                boolean itsMe = dataSnapshot.getKey().equals(Uid);


//                if (wait == null || wait.getOpponent() != null || itsMe) {//пусто/с ним кто-то играет/это я сам
//                    return;
//                }


                wait.setOpponent(name);
                wait.setuidOpp(Uid);

//                Toast.makeText(getApplicationContext(), name + "   empty name", Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), wait.getOpponent(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), Uid, Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), wait.getUidOpp(), Toast.LENGTH_SHORT).show();


                myRef.getParent().child("games").child(Uid)
                        .setValue(new GameOnline(new WaitForUser(wait.getOpponent(), wait.getHost(), dataSnapshot.getKey())));// первый зашёл


                myRef.child(dataSnapshot.getKey()).setValue(wait);//строка эта заполняет оппонента
                myRef.removeEventListener(this);

                Toast.makeText(getApplicationContext(), "new game", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(WaitActivity.this, GameActivity.class);
                intent.putExtra("opponent", wait.getHost());
                intent.putExtra("keyOpponent", dataSnapshot.getKey());
                startActivity(intent);



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });

    }
}
