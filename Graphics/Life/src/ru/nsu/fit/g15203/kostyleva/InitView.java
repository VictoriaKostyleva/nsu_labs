package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;
import ru.nsu.fit.g15203.kostyleva.dots.VecDouble;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TimerTask;
import javax.swing.*;

public class InitView extends JPanel {

    private final double cos30 = Math.sqrt(3) / 2.;
    private final double tg30 = 1 / Math.sqrt(3);

    private final int ANGLE_HEXAGON = 6;
    private final int LITTLE_RADIUS = 12;

    private final Color STR_COLOR = Color.blue;

    private final int RADIUS_CONST = 20;

    private BufferedImage bufferedImage;
    private Color colorLife = Color.green;

    private Model model;

    private final VecDouble[] hexagonCoef = {
            new VecDouble(0, -1 / cos30),
            new VecDouble(1, -tg30),
            new VecDouble(1, tg30),
            new VecDouble(0, 1 / cos30),
            new VecDouble(-1, tg30),
            new VecDouble(-1, -tg30),
    };

    private int lastHexh = -1;//for mouse dragged
    private int lastHexw = -1;

    private boolean xor = false;
    private boolean impactValues = false;

    private int radius;
    private int radToFile;
    private int m;
    private int n;

    private boolean isRun = false;

    private java.util.Timer timer;
    private TimerTask timerTask;

    public InitView(int m, int n, int r, int lineThickness) {
        radius = (int) Math.ceil((r)* Math.sqrt(3)/2);
        radToFile = r;
        this.setN(n);
        this.setM(m);

        int deltaX = (r + (int) ((lineThickness - 1) / 2.)) * 2;
        int deltaY = ((int) Math.ceil(Math.sqrt(3) * (r + (int) ((lineThickness - 1) / 2.))));

        bufferedImage = new BufferedImage(m * deltaX + 20 + (int) ((lineThickness - 1) / 2.)*2, (n + 1) * deltaY, BufferedImage.TYPE_INT_ARGB);

        Graphics g = bufferedImage.getGraphics();

        g.setColor(Color.BLACK);

        model = new Model(m, n);

        drawField(m, n, r, lineThickness, bufferedImage);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Graphics g = bufferedImage.getGraphics();
                    if (bufferedImage.getRGB(e.getX(), e.getY()) == (Color.BLACK.getRGB())) {
                        return;
                    }
                    int xPressed = e.getX();
                    int yPressed = e.getY();

                    if (xor) {
                        Dot dot = model.IsHexWhichOne(xPressed, yPressed, radius);

                        if (model.getCells()[dot.getY()][dot.getX()].isState()) {
                            paintHexagon(e.getX(), e.getY(), Color.white, false, radius, false);
                        } else {
                            paintHexagon(e.getX(), e.getY(), colorLife, true, radius, false);
                        }

                    } else {
                        Dot dot = model.IsHexWhichOne(xPressed, yPressed, radius);

                        if (model.getCells()[dot.getY()][dot.getX()].isState()) {
                            return;
                        } else {
                            paintHexagon(e.getX(), e.getY(), colorLife, true, radius, false);
                        }
                    }

                    if (impactValues) {
                        repaintNeighborImpacts(e.getX(), e.getY());
                    }

                }
                catch (NullPointerException ex) {
                    System.out.println(ex.getMessage());
                } catch (ArrayIndexOutOfBoundsException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                try {
                    int xPressed = e.getX();
                    int yPressed = e.getY();

                    if (bufferedImage.getRGB(xPressed, yPressed) == Color.black.getRGB()) {
                        return;
                    }

                    Dot t = model.IsHexWhichOne(xPressed, yPressed, radius);

                    if (xor) {
                        if (lastHexh == t.getX() && lastHexw == t.getY()) {
                            return;
                        } else {
                            lastHexh = t.getX();
                            lastHexw = t.getY();
                        }
                        if (model.getCells()[t.getY()][t.getX()].isState()) {
                            paintHexagon(e.getX(), e.getY(), Color.white, false, radius, false);
                        } else {
                            paintHexagon(e.getX(), e.getY(), colorLife, true, radius, false);
                        }
                    } else {
                        if (model.getCells()[t.getY()][t.getX()].isState()) {
                        } else {
                            paintHexagon(e.getX(), e.getY(), colorLife, true, radius, false);
                        }
                    }

                    if (impactValues) {
                        repaintNeighborImpacts(e.getX(), e.getY());
                    }

                } catch (ArrayIndexOutOfBoundsException exception) {
                    System.out.println(exception.getMessage());
                }
                catch (NullPointerException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public void newFieldNewMNR(int m, int n, int r, int lineThickness) {
        model.changeFieldSize(m, n);

        radius = (int) Math.ceil(r* Math.sqrt(3)/2);
        radToFile = r;

        int deltaX = (r + (int) ((lineThickness - 1) / 2.))*2;
        int deltaY = ((int) Math.ceil(Math.sqrt(3) * (r + (int) ((lineThickness - 1) / 2.))));

        System.out.println("mnr r " + r);
        bufferedImage = new BufferedImage(m*deltaX + 20 + (int) ((lineThickness - 1) / 2.)*2, (n + 1)*deltaY, BufferedImage.TYPE_INT_ARGB);
        drawField(m, n, r, lineThickness, bufferedImage);

        repaintAllField(m, n, r);
        repaintLives(m, n, r);
        repaint();

    }

    public void newFieldNewLine(int m, int n, int r, int lineThickness) {
        int deltaX = (r + (int) ((lineThickness - 1) / 2.))*2;
        int deltaY = ((int) Math.ceil(Math.sqrt(3) * (r + (int) ((lineThickness - 1) / 2.))));
        bufferedImage = new BufferedImage(m*deltaX + 20 + (int) ((lineThickness - 1) / 2.)*2, (n + 1)*deltaY, BufferedImage.TYPE_INT_ARGB);
        drawField(m, n, r, lineThickness, bufferedImage);

        repaintLives(m, n, r);
        repaint();
    }

    public void newField(int m, int n, int r, int lineThickness) {
        model.clearField();
        model.changeFieldSize(m, n);

        int deltaX = (r + (int) ((lineThickness - 1) / 2.))*2;
        int deltaY = ((int) Math.ceil(Math.sqrt(3) * (r + (int) ((lineThickness - 1) / 2.))));

        radius = (int) Math.ceil(r* Math.sqrt(3)/2);
        bufferedImage = new BufferedImage(m*deltaX + 20 + (int) ((lineThickness - 1) / 2.)*2, (n + 1)*deltaY, BufferedImage.TYPE_INT_ARGB);
        drawField(m, n, r, lineThickness, bufferedImage);
        repaintAllField(m, n, r);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).drawImage(bufferedImage, null, null);
    }

    public void drawHexagon(VecDouble center, int radius, int lineThickness, BufferedImage image) {
        List<Dot> dots;
        dots = dotsHexagonal(center, radius);

        if (lineThickness == 1) {
            for (int i = 0; i < ANGLE_HEXAGON - 1; i++) {
                drawBresenhamLine(dots.get(i).getX(), dots.get(i).getY(), dots.get(i + 1).getX(), dots.get(i + 1).getY(), image);
            }
            drawBresenhamLine(dots.get(ANGLE_HEXAGON - 1).getX(), dots.get(ANGLE_HEXAGON - 1).getY(), dots.get(0).getX(), dots.get(0).getY(), image);
        } else {
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(lineThickness));

            int[] xArr = new int[ANGLE_HEXAGON];
            int[] yArr = new int[ANGLE_HEXAGON];

            for (int i = 0; i < ANGLE_HEXAGON; i++) {
                xArr[i] = dots.get(i).getX();
                yArr[i] = dots.get(i).getY();
            }

            g.drawPolygon(xArr, yArr, ANGLE_HEXAGON);
            g.dispose();
        }
    }

    public List<Dot> dotsHexagonal(VecDouble center, int radius) {
        List<Dot> dots = new ArrayList<>();

        for (int i = 0; i < ANGLE_HEXAGON; i++) {
            VecDouble dot = new VecDouble(center);
            dot.takeNewPoint(hexagonCoef[i].multiple(radius));
            dots.add(new Dot(dot));
        }
        return dots;
    }

    private int sign(int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    public void drawBresenhamLine(int xStart, int yStart, int xEnd, int yEnd, BufferedImage image) {
        int x, y, dx, dy, incX, incY, pdx, pdy, es, el, err;

        dx = xEnd - xStart;//разница по оси икс
        dy = yEnd - yStart;//разница по оси игрек

        incX = sign(dx);
        incY = sign(dy);

        dx = Math.abs(dx);
        dy = Math.abs(dy);

        if (dx > dy) {//вытянута по х
            pdx = incX;
            pdy = 0;
            es = dy;
            el = dx;
        } else {//вытянута по у
            pdx = 0;
            pdy = incY;
            es = dx;
            el = dy;
        }

        x = xStart;
        y = yStart;
        err = el / 2;
        image.setRGB(x, y, Color.black.getRGB());

        for (int i = 0; i < el; i++) {
            err -= es;
            if (err < 0) {
                err += el;
                x += incX;
                y += incY;
            } else {
                x += pdx;
                y += pdy;
            }
            image.setRGB(x, y, Color.black.getRGB());
        }
    }

    public void drawField(int m, int n, int r, int lineThikness, BufferedImage image) {
        int mCount = m;

        r = r + (int) ((lineThikness - 1) / 2.);

        int centerX0 = r + 10 + (int) ((lineThikness - 1) / 2.);//константа
//        int centerY0 = (int) (r * Math.sqrt(3));
        int centerY0 = (int) Math.ceil(Math.sqrt(3) * r);

        int centerX1 = centerX0 + r;
        int deltaX = 2 * r;

        int centerVerticalX;
        int centerVerticalY;

        Graphics g = image.getGraphics();

        for (int i = 0; i < n; i++) {//определяем кол-во фигур в строке

            if (i % 2 == 0) {//строки, в которых четное число шестиугольников
                mCount = m;
                centerVerticalX = centerX0;
            } else {
                mCount = m - 1;
                centerVerticalX = centerX1;
            }
            centerVerticalY = centerY0 + (int) Math.ceil(Math.sqrt(3) * r) * i;

            for (int j = 0; j < mCount; j++) {
                VecDouble centerLine = new VecDouble(centerVerticalX, centerVerticalY);
                drawHexagon(centerLine, r, lineThikness, image);

                model.setCellXY(i, j, ((int) centerLine.getX()), (int) centerLine.getY());

                centerVerticalX += deltaX;
            }
        }

        if (impactValues && radius > LITTLE_RADIUS) {
            repaintAllField(m, n, r);
            model.showAllImpactValues(image, r);
        }
    }

    public void paintHexagon(int xPressed, int yPressed, Color color, boolean alpha, int radius, boolean theSameColor) {
        boolean lockUp;
        boolean lockDown;
        int glass = alpha ? 0xffffffff : 0x00ffffff;

        Dot dot = model.IsHexWhichOne(xPressed, yPressed, radius);

        if (dot == null) {
            return;
        }

        Span span = new Span(new Dot(xPressed, yPressed), bufferedImage);
        Stack<Span> spanStack = new Stack<>();
        spanStack.push(span);

        while (!spanStack.empty()) {
            lockUp = false;
            lockDown = false;

            span = spanStack.pop();
            int j = span.getStart().getY();

            for (int i = span.getStart().getX(); i < span.getEnd().getX(); i++) {

                if (bufferedImage.getRGB(i, j - 1) != Color.BLACK.getRGB() && bufferedImage.getRGB(i, j - 1) != (color.getRGB() & glass)) {
                    if (!lockUp) {
                        lockUp = true;
                        spanStack.push(new Span(new Dot(i, j - 1), bufferedImage));
                    }
                } else {
                    lockUp = false;
                }

                if (bufferedImage.getRGB(i, j + 1) != Color.BLACK.getRGB() && bufferedImage.getRGB(i, j + 1) != (color.getRGB() & glass)) {
                    if (!lockDown) {
                        lockDown = true;
                        spanStack.push(new Span(new Dot(i, j + 1), bufferedImage));
                    }
                } else {
                    lockDown = false;
                }
                bufferedImage.setRGB(i, j, color.getRGB() & glass);
            }
        }
        repaint();

        if (!theSameColor) {
            model.changeState(dot.getX(), dot.getY());
            model.reCalcImpact();
        }
        if (impactValues) {
            model.showMyImpact(bufferedImage, dot.getY(), dot.getX(), RADIUS_CONST);
        }
    }

    public void showValues(int radius) {
        model.showAllImpactValues(bufferedImage, RADIUS_CONST);
    }

    public void paintTheSameColorHex(int x, int y, int r, Color color) {
        if (color.getRGB() == colorLife.getRGB()) {
            paintHexagon(x, y, colorLife, true, r, true);
//            paintHexagon(x, y, color, true, r, true);
        } else {
            paintHexagon(x, y, Color.white, false, r, true);
        }
    }

    public void repaintAllField(int m, int n, int r) {
        for (int x = 0; x < n; x++) {
            int width_line = model.getWidthInLine(x);
            for (int y = 0; y < width_line; y++) {
                if (bufferedImage.getRGB(model.getCells()[x][y].getX(), model.getCells()[x][y].getY()) == colorLife.getRGB()) {
                    paintTheSameColorHex(model.getCells()[x][y].getX(), model.getCells()[x][y].getY(), r, colorLife);
                } else {
                    paintTheSameColorHex(model.getCells()[x][y].getX(), model.getCells()[x][y].getY(), r, Color.white);
                }
            }
        }
    }

    public void stepController(int m, int n, int r) {
        try {
            model.nextStep();

            for (int x = 0; x < n; x++) {
                int width_line = model.getWidthInLine(x);
                for (int y = 0; y < width_line; y++) {
                    if (model.getCells()[x][y].isState()) {
                        paintTheSameColorHex(model.getCells()[x][y].getX(), model.getCells()[x][y].getY(), r, colorLife);
                    } else {
                        paintTheSameColorHex(model.getCells()[x][y].getX(), model.getCells()[x][y].getY(), r, Color.white);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Model getModel() {
        return model;
    }

    public boolean workWithParams(double params[]) {
        return model.setParams(params);
    }

    public void setXor(boolean xor) {
        this.xor = xor;
    }

    public boolean isXor() {
        return xor;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setImpactValues(boolean impactValues) {
        System.out.println("show imp");
        if (radius < LITTLE_RADIUS) {
            this.impactValues = false;
            return;
        }

        this.impactValues = impactValues;
        Graphics g = bufferedImage.getGraphics();

        if (impactValues) {
            showValues(radius);

        } else {
            repaintAllField(m, n, radius);
        }

        repaint();
    }

    public void setRadius(int radius) {
        this.radToFile = radius;
        this.radius = (int) Math.ceil((radius)* Math.sqrt(3)/2);
    }

    private class PlayTask extends TimerTask {
        @Override
        public void run() {
            stepController(m, n, radius);
        }
    }

    public void play() {
        if (isRun) {
            timer.cancel();
            timer.purge();
            isRun = false;
        } else {
            timer = new java.util.Timer();
            timerTask = new PlayTask();
            isRun = true;
            timer.schedule(timerTask, 0, 300);
        }
    }

    public void repaintLives(int w, int h, int r) {// del m, n, r
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (model.getCells()[y][x].isState()) {
                    paintHexagon(model.getCells()[y][x].getX(), model.getCells()[y][x].getY(), colorLife, true, r, true);
                } else {
                    paintHexagon(model.getCells()[y][x].getX(), model.getCells()[y][x].getY(), Color.white, false, r, true);
                }
            }
        }
    }

    public void repaintNeighborImpacts(int xPressed, int yPressed) {

        Dot hex = model.IsHexWhichOne(xPressed, yPressed, radius);

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
        for (int j = 0; j < 12; j++) {
            int yCount = hex.getX() + matrix[hex.getY() % 2][j][1];//y % 2 для четной или нечетной строки
            int xCount = hex.getY() + matrix[hex.getY() % 2][j][0];

            if (model.checkXY(yCount, xCount)) {
                Cell cell = model.getCells()[xCount][yCount];

                if (model.getCells()[xCount][yCount].isState()) {
                    paintTheSameColorHex(cell.getX(), cell.getY(), radius, colorLife);
                } else {
                    paintTheSameColorHex(cell.getX(), cell.getY(), radius, Color.white);
                }
            }
        }
        Cell centerCell = model.getCells()[hex.getY()][hex.getX()];

        if (model.checkXY(hex.getX(), hex.getY())) {
            if (model.getCells()[hex.getY()][hex.getX()].isState()) {
            paintTheSameColorHex(centerCell.getX(), centerCell.getY(), radius, colorLife);
            } else {
            paintTheSameColorHex(centerCell.getX(), centerCell.getY(), radius, Color.white);
            }
        model.showMyImpact(bufferedImage, hex.getY(), hex.getX(), RADIUS_CONST);
        }
    }

    public boolean isImpactValues() {
        return impactValues;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public int getRadius() {
        return radius;
    }

    public int getRadToFile() {
        return radToFile;
    }

    public ArrayList<Dot> makeArrayFromCelles() {
        ArrayList<Dot> array = new ArrayList<>();

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < model.getWidthInLine(m); x++) {//m
                if (model.getCells()[y][x].isState()) {
                    array.add(new Dot(y, x));
                }
            }
        }
        return array;
    }

    public void setAlive(Dot cell) {
        model.getCells()[cell.getY()][cell.getX()].setState(true);
//	    model.soutAllStates();
    }

    public void reculcImpAfterOpen() {
        model.reCalcImpact();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}