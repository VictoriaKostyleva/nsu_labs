package com.example.vikacech.tic_tac_toe.models;

import java.util.List;

public class GameOnline {
    private WaitForUser game;
    private int turn = 1;
    private String field[][];

    public GameOnline() {
    }

    public GameOnline(WaitForUser game) {
        this.game = game;
        for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 3; ++j) {
            field = null;
        }
        }
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public WaitForUser getGame() {
        return game;
    }

    public void setGame(WaitForUser game) {
        this.game = game;
    }

    public String[][] getField() {
        return field;
    }

    public void setField(String[][] field) {
        this.field = field;
    }
}