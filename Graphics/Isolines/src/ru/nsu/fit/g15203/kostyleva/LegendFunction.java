package ru.nsu.fit.g15203.kostyleva;

public class LegendFunction extends Function {
    LegendFunction(double a, double b, double c, double d) {
        super(a, b, c, d);
    }

    @Override
    public double func(double x, double y) {
        return -y;
    }

}
