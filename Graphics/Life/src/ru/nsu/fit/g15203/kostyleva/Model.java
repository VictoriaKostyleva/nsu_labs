package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;
import ru.nsu.fit.g15203.kostyleva.dots.VecDouble;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Model {
    private int width, height;

    private Cell[][] cells;
    private final double cos30 = Math.sqrt(3) / 2.;
    private final double tg30 = 1 / Math.sqrt(3);

    private final VecDouble[] hexagonCoef = {
            new VecDouble(0, -1 / cos30),
            new VecDouble(1, -tg30),
            new VecDouble(1, tg30),
            new VecDouble(0, 1 / cos30),
            new VecDouble(-1, tg30),
            new VecDouble(-1, -tg30),
    };

    private double liveBegin = 2.0, liveEnd = 3.3, birthBegin = 2.3, birthEnd = 2.9, fstImpact = 1.0, sndImpact = 0.3;

    boolean isRun;

    public Model(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[j][i] = new Cell(0, 0, 0.);
            }
        }
    }

    public double getLiveBegin() {
        return liveBegin;
    }

    public double getBirthBegin() {
        return birthBegin;
    }

    public double getLiveEnd() {
        return liveEnd;
    }

    public double getBirthEnd() {
        return birthEnd;
    }

    public double getFstImpact() {
        return fstImpact;
    }

    public double getSndImpact() {
        return sndImpact;
    }

    public boolean checkXY(int x, int y) {
        return ((0 <= x) && (x < getWidthInLine(y))) && ((0 <= y) && (y < height));
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public int getWidthInLine(int y) {//m in line, or m - 1
        return width - ((y % 2 == 0) ? 0 : 1);
    }

    private void updateState(int y, int x, boolean isAlive) {
        cells[y][x].setState(isAlive);
    }

    private void setStatesByImpact() {
        double currImp;
        boolean newState;
        for (int y = 0; y < height; y++) {
            int width_line = getWidthInLine(y);
            for (int x = 0; x < width_line; x++) {
                currImp = cells[y][x].getImpact();
                // Смерть от одиночества или перенаселённости, или продолжение нежития
                if ((currImp < liveBegin) || (currImp > liveEnd)) {
                    newState = false;
                }
                // Рождение если мертвый, продолжение жизни если живой
                else if ((birthBegin <= currImp) && (currImp <= birthEnd)) {
                    newState = true;
                }
                // Продолжение жизни
                else if (checkXY(y, x) && cells[y][x].isState() && (liveBegin <= currImp) && (currImp <= liveEnd)) {
                    newState = true;
                }
                // Иначе смерть
                else newState = false;

                updateState(y, x, newState);
            }
        }
    }

    public void reCalcImpact() {
        // Матрица: dy, dx, distance
        int[][][] matrix = {
                { // Для четной нажатой
                        {-2, 0, 1},
                        {-1, -2, 1}, {-1, -1, 0}, {-1, 0, 0}, {-1, 1, 1},
                        {0, -1, 0}, {0, 1, 0},
                        {1, -2, 1}, {1, -1, 0}, {1, 0, 0}, {1, 1, 1},
                        {2, 0, 1}
                },
                { // Для нечетной нажатой
                        {-2, 0, 1},
                        {-1, -1, 1}, {-1, 0, 0}, {-1, 1, 0}, {-1, 2, 1},
                        {0, -1, 0}, {0, 1, 0},
                        {1, -1, 1}, {1, 0, 0}, {1, 1, 0}, {1, 2, 1},
                        {2, 0, 1}
                }
        };

        for (int y = 0; y < height; y++) {
            int widthLine = getWidthInLine(y);
            for (int x = 0; x < widthLine; x++) {
                int[] height = {0, 0};
                for (int j = 0; j < 12; j++) {
                    int xCount = x + matrix[y % 2][j][1];
                    int yCount = y + matrix[y % 2][j][0];

                    if (checkXY(xCount, yCount) && cells[yCount][xCount].isState()) {
                        height[matrix[y % 2][j][2]] += 1;
                    }
                }
                cells[y][x].setImpact(height[0] * fstImpact + height[1] * sndImpact);
//                System.out.println("impact:" + x + "." + y + "= " + cells[y][x].getImpact());
            }
        }
    }

    public void soutAllStates() {
        System.out.println("states: ");
        System.out.println(height  + "   " + width);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                System.out.print(cells[y][x].isState() + " ");
            }
            System.out.println("");
        }
    }

    public void soutAllImpacts() {
        System.out.println("impacts: ");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                System.out.print(makeDoubleBetter(cells[y][x].getImpact()) + " ");
            }
            System.out.println("");
        }
    }

    public void checkParams(double[] params) throws ChangeParamsException {
        if (params.length != 6) {
            throw new ChangeParamsException("Not enough parameters");
        }
        for (int i = 0; i < 3; i++) {
            if ((params[i] < 0) || (params[i + 1] < 0)) {
                throw new ChangeParamsException("Parameters can not be negative");
            }
            if (params[i] >= params[i + 1]) {
                throw new ChangeParamsException("Parameters should increase");//  LIVE_BEGIN ≤ BIRTH_BEGIN ≤ BIRTH_END ≤ LIVE_END
            }
        }
    }

    public boolean setParams(double[] params) {
        boolean goodParams = true;
        try {
            checkParams(params);
        } catch (ChangeParamsException e) {
            System.out.println(e.getMessage());
            goodParams = false;
        }
        liveBegin = params[0];
        birthBegin = params[1];
        birthEnd = params[2];
        liveEnd = params[3];
        fstImpact = params[4];
        sndImpact = params[5];

        return goodParams;
    }

    public void changeFieldSize(int w, int h) {
        Cell[][] newCells = new Cell[h][w];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                newCells[j][i] = new Cell(0, 0, 0.);
            }
        }

        for (int y = 0; y < min(height, h); y++) {

            for (int x = 0; x < min(getWidthInLine(width), getWidthInLine(w)); x++) {
                try {
                    newCells[y][x] = cells[y][x];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(1);
                }
            }
        }
        cells = newCells;
        this.width = w;
        this.height = h;
    }

    public void clearField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                cells[y][x].setImpact(0.);
                cells[y][x].setState(false);
            }
        }
    }

    public void showImpactValues(int h, int w, Graphics g) {
        String string = String.valueOf((cells[h][w].getImpact()));
        g.setColor(Color.BLUE);
        g.drawString(string, cells[h][w].getX(), cells[h][w].getY());
    }

    public void showAllImpactValues(BufferedImage image, int r) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < getWidthInLine(h); w++) {
                String string = String.valueOf(makeDoubleBetter(cells[h][w].getImpact()));
                drawCenteredString(image, string, cells[h][w].getY(), cells[h][w].getX(), r);
            }
        }
    }

    public void showMyImpact(BufferedImage image, int h, int w, int r) {
        String string = String.valueOf(makeDoubleBetter(cells[h][w].getImpact()));
        drawCenteredString(image, string, cells[h][w].getY(), cells[h][w].getX(), r);
    }

    public void drawCenteredString(BufferedImage image, String text, int h, int w, int r) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.blue);

        int font_size = r - 4;//or 2r or change

        Font font = new Font("Monospace", Font.PLAIN, font_size);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int sx = (int) (w - fm.stringWidth(text) / 2.);
        int sy = (int) (h - fm.getHeight() / 2.) + fm.getAscent();

        g2d.drawString(text, sx, sy);
    }

    public void setCellXY(int h, int w, int resX, int resY) {
        cells[h][w].setX(resX);
        cells[h][w].setY(resY);
    }

    public void testSout() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                System.out.print(cells[y][x].getX() + "." + cells[y][x].getY() + " ");
            }
            System.out.println("\n");
        }
    }

    public void whichHex(int h, int w) {
        int minXcoord = 0;
        int minYcoord = 0;
        double t = 0;
        double tmin = 9999;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                t = Math.sqrt(Math.pow(cells[y][x].getX() - h, 2) + Math.pow(cells[y][x].getY() - w, 2));
                if (t < tmin) {
                    tmin = t;
                    minXcoord = x;
                    minYcoord = y;
                }
            }
        }
//        System.out.println(minXcoord + "." + minYcoord);
    }

    public List<Dot> dotsOfHexagon(Dot center, int radius) {//the same
        List<Dot> dots = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            VecDouble dot = new VecDouble(center);
            dot.takeNewPoint(hexagonCoef[i].multiple(radius));
            dots.add(new Dot(dot));
        }
        return dots;
    }

    private int IsPointInsidePolygon(List<Dot> dots, int Number, int x, int y) {//TODO change to bool
        int i1, i2, n, N, S, S1, S2, S3, flag;
        N = Number;
        flag = 0;
        for (n = 0; n < N; n++) {
            flag = 0;
            i1 = n < N - 1 ? n + 1 : 0;
            while (flag == 0) {
                i2 = i1 + 1;
                if (i2 >= N)
                    i2 = 0;
                if (i2 == (n < N - 1 ? n + 1 : 0))
                    break;
                S = Math.abs(dots.get(i1).getX() * (dots.get(i2).getY() - dots.get(n).getY()) +
                        dots.get(i2).getX() * (dots.get(n).getY() - dots.get(i1).getY()) +
                        dots.get(n).getX() * (dots.get(i1).getY() - dots.get(i2).getY()));
                S1 = Math.abs(dots.get(i1).getX() * (dots.get(i2).getY() - y) +
                        dots.get(i2).getX() * (y - dots.get(i1).getY()) +
                        x * (dots.get(i1).getY() - dots.get(i2).getY()));
                S2 = Math.abs(dots.get(n).getX() * (dots.get(i2).getY() - y) +
                        dots.get(i2).getX() * (y - dots.get(n).getY()) +
                        x * (dots.get(n).getY() - dots.get(i2).getY()));
                S3 = Math.abs(dots.get(i1).getX() * (dots.get(n).getY() - y) +
                        dots.get(n).getX() * (y - dots.get(i1).getY()) +
                        x * (dots.get(i1).getY() - dots.get(n).getY()));
                if (S == S1 + S2 + S3) {
                    flag = 1;
                    break;
                }
                i1 = i1 + 1;
                if (i1 >= N)
                    i1 = 0;
            }
            if (flag == 0)
                break;
        }
        return flag;
    }

    public Dot IsHexWhichOne(int h, int w, int radius) {
        int xCoord = 0;
        int yCoord = 0;
        int temp = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthInLine(y); x++) {
                List<Dot> dotsHex = dotsOfHexagon(cells[y][x], radius);
                temp = IsPointInsidePolygon(dotsHex, 6, h, w);
                if (temp == 1) {
                    xCoord = x;
                    yCoord = y;
//                    System.out.println(xCoord + "." + yCoord);
                    return new Dot(xCoord, yCoord);
                }
            }
        }

        if (temp == 0) {
//            System.out.println("outside hex");
        }
        return null;
    }

    public void changeState(int w, int h) {
        if (cells[h][w].isState()) {
            cells[h][w].setState(false);
        } else {
            cells[h][w].setState(true);
        }
    }

    public void nextStep() {
        setStatesByImpact();
        reCalcImpact();
//        System.out.println("next step");
//        soutAllStates();
    }

    private String makeDoubleBetter(Double d) {
        String newD;
        double ost = d - d.intValue();
        if (ost != 0) {
            newD = String.format("%.1f%n", d);
        } else {
            newD = ((Integer) d.intValue()).toString();
        }
        return newD;
    }

    private int min(int a, int b) {
        return ((a < b) ? a : b);
    }
}