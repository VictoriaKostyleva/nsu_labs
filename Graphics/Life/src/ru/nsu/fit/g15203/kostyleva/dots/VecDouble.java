package ru.nsu.fit.g15203.kostyleva.dots;

public class VecDouble {

    private double x = 0;
    private double y = 0;

    public VecDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public VecDouble(VecDouble v) {
        x = v.getX();
        y = v.getY();
    }

    public VecDouble(Dot v) {//TODO del?
        x = v.getX();
        y = v.getY();
    }

    public void takeNewPoint(VecDouble other) {
        x += other.x;
        y += other.y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public VecDouble multiple(double other) {
        return new VecDouble((x * other), (y * other));
    }
}
