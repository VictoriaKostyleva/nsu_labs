package com.example.vikacech.tic_tac_toe.models;

public class TurnData {
    private String userUid;
    private String playsWith;
    private String turn;

    public TurnData() {
    }

    public TurnData(String userUid, String playsFor, String turn) {
        this.userUid = userUid;
        this.playsWith = playsFor;
        this.turn = turn;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getTurn() {
        return turn;
    }

}