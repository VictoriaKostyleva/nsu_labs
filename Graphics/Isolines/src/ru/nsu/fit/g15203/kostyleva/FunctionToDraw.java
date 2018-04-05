package ru.nsu.fit.g15203.kostyleva;

public class FunctionToDraw extends Function {

    FunctionToDraw(double a, double b, double c, double d) {
        super(a, b, c, d);
    }

    @Override
    public double func(double x, double y) {//TODO here you can change the function
        return x * x - y * y;//Math.sin(x) + Math.cos(y) x*x + y*y
    }

}