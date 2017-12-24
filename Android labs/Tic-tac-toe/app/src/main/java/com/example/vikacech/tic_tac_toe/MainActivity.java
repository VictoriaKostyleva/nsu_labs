package com.example.vikacech.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SIGN_IN_RESULT = 1234;
    Button btnPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button) findViewById(R.id.button_play);
        btnPlay.setOnClickListener(this);
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(this, EmailPasswordActivity.class);
//                startActivity(intent);
//            }
//        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                Intent intent = new Intent(this, EmailPasswordActivity.class);
                startActivityForResult(intent, SIGN_IN_RESULT);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_RESULT) {
            System.out.println("back here");

//            Intent intent = new Intent(this, GameActivity.class);
//            startActivity(intent);
        }

    }
}
