package com.example.vikacech.tic_tac_toe.models;

public class TurnData {
    private String userUid;
    private String playsFor;
    private String turn;

    public TurnData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public TurnData(String userUid, String playsFor, String turn) {
        this.userUid = userUid;
        this.playsFor = playsFor;
        this.turn = turn;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getPlaysFor() {
        return playsFor;
    }

    public void setPlaysFor(String playsFor) {
        this.playsFor = playsFor;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "TurnData{" +
                "userUid='" + userUid + '\'' +
                ", playsFor='" + playsFor + '\'' +
                ", turn='" + turn + '\'' +
                '}';
    }
}