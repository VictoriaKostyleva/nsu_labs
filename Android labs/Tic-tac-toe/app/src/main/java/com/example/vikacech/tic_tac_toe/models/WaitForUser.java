package com.example.vikacech.tic_tac_toe.models;

public class WaitForUser {
    private String host;//TODO if i need it?
    private String opponent;
    private String uidOpp;

    public WaitForUser(){}
    public WaitForUser(String host, String opponent) {
        this.host = host;
        this.opponent = opponent;
    }

    public WaitForUser(String host, String opponent, String uidOpp) {
        this.host = host;
        this.opponent = opponent;
        this.uidOpp = uidOpp;
    }
    public String getUidOpp() {
        return uidOpp;
    }

    public void setuidOpp(String uidOpp) {
        this.uidOpp = uidOpp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }
}
