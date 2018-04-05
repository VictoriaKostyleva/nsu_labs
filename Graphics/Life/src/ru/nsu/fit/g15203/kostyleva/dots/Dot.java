package ru.nsu.fit.g15203.kostyleva.dots;

public class Dot {
    private int x = 0;
    private int y = 0;

    public Dot(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Dot(VecDouble v) {
        x = (int)v.getX();
        y = (int)v.getY();
    }
}
