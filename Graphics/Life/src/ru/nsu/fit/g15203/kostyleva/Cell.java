package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;

public class Cell extends Dot {
        private double impact = 0;
        private boolean state = false;//is alive or not
        //+координаты центра

        public Cell(int x, int y, double impact) {
            super(x, y);
            this.impact = impact;
        }

    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }
}