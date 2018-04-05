package ru.nsu.fit.g15203.kostyleva;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel {

    private ArrayList<Color> colors;

    private Function functionToDraw;
    private Function legendFunction;
    private int width;//of panel
    private int height;
    private BufferedImage bufferedImage;
    private BufferedImage legendImage;
    private BufferedImage stringImage;
    private BufferedImage gridImage;
    private BufferedImage isolinesImage;

    private double kX;
    private double kY;

    private JPanel panel;
    private JLayeredPane colorMap;
    private JPanel legendPanel;
    private JPanel stringPanel;
    private JPanel GridPanel;
    private JPanel isolinesPanel;

    private double colorDiapason;

    GraphicsPanel(int x, int y, Function functionToDraw) {
        this.width = x;
        this.height = y;
        this.functionToDraw = functionToDraw;

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);//TODO
        legendImage = new BufferedImage(width * 10 / 100, height, BufferedImage.TYPE_INT_ARGB);//TODO
        stringImage = new BufferedImage(width * 15 / 100, height, BufferedImage.TYPE_INT_ARGB);//TODO
        Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.BLACK);

        colors = new ArrayList<>();

        colors.add(Color.red);
        colors.add(Color.orange);
        colors.add(Color.yellow);
        colors.add(Color.green);
        colors.add(Color.cyan);
        colors.add(Color.blue);
        colors.add(new Color(90, 0, 158));
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));
        legendFunction = new LegendFunction(0, 20, 0, 20);

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                changeSize(e.getComponent().getWidth(), e.getComponent().getHeight());
                System.out.println("kokoko");
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        setVisible(true);

        repaint();
    }

    public JPanel initWindow() {
        panel = new JPanel();

        panel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                changeSize(e.getComponent().getWidth(), e.getComponent().getHeight());
                System.out.println("kokoko");
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        Container container = new Container();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        colorMap = new JLayeredPane();
        colorMap.setPreferredSize(new Dimension(width, height));

        CustomMouseListener cms = new CustomMouseListener();
        colorMap.addMouseListener(cms);
        colorMap.addMouseMotionListener(cms);

        System.out.println("1");

        //TODO make better
        kX = (functionToDraw.getB() - functionToDraw.getA())/(double) width;
        System.out.println("kX " + kX);
        kY = (functionToDraw.getD() - functionToDraw.getC())/(double) height;
        System.out.println("kY " + kY);
        findMinMax(functionToDraw);
        drawStringLegend();

        stringPanel = new JPanel();
        stringPanel.setLayout(null);
        stringPanel.setPreferredSize(new Dimension(width * 10 / 100, height));

        legendPanel = new JPanel();
        legendPanel.setLayout(null);
        legendPanel.setPreferredSize(new Dimension(width * 10 / 100, height));

        System.out.println("2");

        panel.add(colorMap, container);
        panel.add(stringPanel, container);
        panel.add(legendPanel, container);

        changeSize(width, height);
        changeSizeLegend(width * 10 / 100, height);

        return panel;
    }

    class CustomMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("mouse cl");
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            System.out.println("mouse dr");
        }
    }

    public void changeSize(int width, int height) {
        kX = (functionToDraw.getB() - functionToDraw.getA())/(double) width;
        System.out.println("kX " + kX);
        kY = (functionToDraw.getD() - functionToDraw.getC())/(double) height;
        System.out.println("kY " + kY);

        findMinMax(functionToDraw);

        colorDiapason = (Math.abs(functionToDraw.getMax()) + Math.abs(functionToDraw.getMin()))/(double)colors.size();
        System.out.println("colorDiapason " + colorDiapason);

        double goX = functionToDraw.getA();
        double goY = functionToDraw.getC();

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                double fZ = functionToDraw.func(goX, goY);

                Color color = takeColor(fZ, functionToDraw);

                bufferedImage.setRGB(j, i, color.getRGB());
                goX += kX;
            }
            goY += kY;
            goX = functionToDraw.getA();
        }

        Image image = new ImageIcon(bufferedImage).getImage();
        image = new ImageIcon(image.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT)).getImage();
        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        JLabel sel = new JLabel(new ImageIcon(bufferedImage));
        sel.setBounds(0, 0, image.getWidth(null), image.getHeight(null));
        colorMap.add(sel);
        colorMap.repaint();


        drawStringLegend();
        Image strImage = new ImageIcon(stringImage).getImage();
        strImage = new ImageIcon(strImage.getScaledInstance(width * 10 / 100, height, BufferedImage.SCALE_DEFAULT)).getImage();
        stringImage = new BufferedImage(strImage.getWidth(null), strImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        g2 = stringImage.createGraphics();
        g2.drawImage(strImage, 0, 0, null);
        g2.dispose();
        sel = new JLabel(new ImageIcon(stringImage));
        sel.setBounds(0, 0, strImage.getWidth(null), strImage.getHeight(null));
        stringPanel.add(sel);
        stringPanel.repaint();
    }


    public void changeSizeLegend(int width, int height) {
        legendFunction = new LegendFunction(0, height, 0, height);//TODO
        kX = (legendFunction.getB() - legendFunction.getA())/(double) width;
        System.out.println("kXl " + kX);
        kY = (legendFunction.getD() - legendFunction.getC())/(double) height;
        System.out.println("kYl " + kY);

        findMinMax(legendFunction);

        colorDiapason = (Math.abs(legendFunction.getMax()) + Math.abs(legendFunction.getMin()))/(double)colors.size();
        System.out.println("colorDiapason " + colorDiapason);

        double goX = legendFunction.getA();
        double goY = legendFunction.getC();

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                double fZ = legendFunction.func(goX, goY);

                Color color = takeColor(fZ, legendFunction);

                legendImage.setRGB(j, i, color.getRGB());
                goX += kX;
            }
            goY += kY;
            goX = legendFunction.getA();
        }

        Image image = new ImageIcon(legendImage).getImage();
        image = new ImageIcon(image.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT)).getImage();
        legendImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = legendImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        JLabel sel = new JLabel(new ImageIcon(legendImage));
        sel.setBounds(0, 0, image.getWidth(null), image.getHeight(null));
        legendPanel.add(sel);
        legendPanel.repaint();
    }

    public Color takeColor(double fZ, Function function) {
        for(int i = 0; i < colors.size(); i++) {
            if(fZ >= function.getMin() + colorDiapason * i && fZ <= function.getMin() + colorDiapason * (i + 1)) {
                return colors.get(i);
            }
        }
        return Color.black;//it means a bug
    }

    public void findMinMax(Function function) {
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        double goX = function.getA();
        double goY = function.getC();

        for(double i = goY; i < function.getD(); i+= kY) {
            for(double j = goX; j < function.getB(); j+=kX) {
                double fZ = function.func(goX, goY);

                if(fZ > max) {
                    max = fZ;
                }

                if(fZ < min) {
                    min = fZ;
                }

                goX += kX;
            }
            goX = function.getA();
            goY += kY;

        }

        System.out.println("min " + min);
        System.out.println("max " + max);
        function.setMax(max);
        function.setMin(min);
    }

    public void drawStringLegend() {
        double deltaStep = (functionToDraw.getMax() - functionToDraw.getMin()) / colors.size();
        int deltaY = height / colors.size();

        System.out.println("delta "+ deltaStep);
        double str = functionToDraw.getMax();
        Graphics g = stringImage.getGraphics();

        int fontSize = Math.min(width / 25, height / 25);

        Graphics2D g2 = (Graphics2D) g;
        Font font = new Font("Monospace", Font.PLAIN, fontSize);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();


        for(int i = 0; i < colors.size() - 1; i++) {
            String string = String.valueOf(makeDoubleBetter(str - (i + 1) * deltaStep));
            g2.setColor(Color.BLACK);
//            g2.drawString(string, stringImage.getWidth() - 25, (i + 1) * deltaY + 10);

            int legendOffsetX = width * 14 / 100;
            int sx = legendOffsetX - fm.stringWidth(string);
            g2.drawString(string, sx, (i + 1) * deltaY + 10);

        }

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

    public void showGrid() {
        GridPanel = new JPanel(new BorderLayout());
        GridPanel.setBounds(0, 0, width, height);

        GridPanel.setOpaque(false);
        colorMap.add(GridPanel, JLayeredPane.DRAG_LAYER);

        gridImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        GridPanel.setLocation(0, 0);

        Graphics g = gridImage.getGraphics();
        g.setColor(Color.BLACK);

        int k = 10;
        int n = 10;//15

        int stepH = height / k;
        int stepW = width / n;

        for(int i = 1; i < n; i ++) {//vertical
            g.drawLine(i*stepW, 0, i*stepW, height);
        }

        for(int i = 1; i < k; i ++) {//horizontal
            g.drawLine(0, i*stepH, width, i*stepH);
        }

        Image image = new ImageIcon(gridImage).getImage();
        image = new ImageIcon(image.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT)).getImage();
        gridImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = gridImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        JLabel sel = new JLabel(new ImageIcon(gridImage));
        sel.setBounds(0, 0, image.getWidth(null), image.getHeight(null));
        GridPanel.add(sel);
        GridPanel.repaint();
    }

    public void hideGrid() {
        GridPanel.removeAll();
        GridPanel.repaint();
        GridPanel.revalidate();
    }

}
