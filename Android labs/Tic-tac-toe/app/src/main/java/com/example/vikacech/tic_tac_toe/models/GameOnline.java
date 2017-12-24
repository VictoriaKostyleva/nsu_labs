package com.example.vikacech.tic_tac_toe.models;

import java.util.ArrayList;
import java.util.List;

public class GameOnline {
    private WaitForUser game;
    private int turn = 1;
    private List<Integer> list = new ArrayList<>();

    public GameOnline() {
    }

    public GameOnline(WaitForUser game) {
        this.game = game;
        for (int i = 0; i < 9; ++i) {
            list.add(0);
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

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }
}