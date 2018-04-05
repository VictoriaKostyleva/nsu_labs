package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.filters.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow extends MainFrame {
    private final int NEGATIVE = 1;
    private final int DESATURATE = 2;
    private final int SHARPNESS = 3;
    private final int BLUR = 4;
    private final int BORDER_ROBERTS = 5;
    private final int WATERCOLOR = 6;
    private final int DOUBLE_SIZE = 7;
    private final int GAMMA = 8;
    private final int STAMPING = 9;
    private final int TURN = 10;
    private final int FLOYD_STEINBERG = 11;

    private int gammaParameter = 1;
    private int floydRed = 2;
    private int floydGreen = 2;
    private int floydBlue = 2;


    private JButton jButtonSelect;

    private JLayeredPane zoneA;
    private JPanel zoneB;
    private JPanel zoneC;
    private JPanel panel;
    private JPanel selectedZoneA;

    private BufferedImage bufferedImage;
    private BufferedImage kost;
    private BufferedImage selectedImage;
    private BufferedImage effectImage;
    private BufferedImage cancelImage;

    private Image resImage;

    private int xPressed;
    private int yPressed;

    private double loadedImageWCoef;
    private double loadedImageHCoef;

    private final int DEFAULT_SIZE = 350;

    private int frameSize = 100;

    private boolean isSelectPressed = false;

    private File file;

    public MainWindow() {
        super(1150, 500, "FILTER");
        try {

            setMinimumSize(new Dimension(1150, 500));
//            startIcon = new ImageIcon(getClass().getResource("resources/Run.png"), "Run");
//            pauseIcon = new ImageIcon(getClass().getResource("resources/Pause.png"), "Pause");

            addSubMenu("File", KeyEvent.VK_F);
            addSubMenu("Edit", KeyEvent.VK_F);
//            addSubMenu("Game", KeyEvent.VK_F);
            addSubMenu("Help", KeyEvent.VK_H);

            addMenuItem("File/Empty", "New", KeyEvent.VK_X, "Empty.png", "onEmpty");
            addToolBarButton("File/Empty");

            addMenuItem("File/Open", "Load image", KeyEvent.VK_X, "Open.png", "onOpen");
            addToolBarButton("File/Open");

            addMenuItem("File/Save", "Save image", KeyEvent.VK_X, "Save.png", "onSave");
            addToolBarButton("File/Save");
            addToolBarSeparator();

            addMenuItem("Edit/Choose zone", "Choose zone", KeyEvent.VK_K, "Frame.png", "OnChoose");
            jButtonSelect = addToolBarButton("Edit/Choose zone");

            addMenuItem("File/Copy", "Copy C to B", KeyEvent.VK_X, "CopyLeft.png", "onCopyCtoB");
            addToolBarButton("File/Copy");
            addToolBarSeparator();

            addMenuItem("Edit/Negative", "Negative", KeyEvent.VK_X, "Negative.png", "onNegative");
            addToolBarButton("Edit/Negative");

            addMenuItem("Edit/Desaturate", "Desaturate", KeyEvent.VK_X, "ChB.png", "onDesaturate");
            addToolBarButton("Edit/Desaturate");

            addMenuItem("Edit/Sharpness", "Sharpness", KeyEvent.VK_X, "Sharpness.png", "onSharpness");
            addToolBarButton("Edit/Sharpness");

            addMenuItem("Edit/Blur", "Blur", KeyEvent.VK_X, "Blur.png", "onBlur");
            addToolBarButton("Edit/Blur");

            addMenuItem("Edit/Border", "Border Roberts", KeyEvent.VK_X, "BorderRoberts.png", "onBorder");
            addToolBarButton("Edit/Border");

            addMenuItem("Edit/Gamma", "Brightness Gamma", KeyEvent.VK_X, "Gamma.png", "onGamma");
            addToolBarButton("Edit/Gamma");

            addMenuItem("Edit/Watercolor", "Watercolorization", KeyEvent.VK_X, "Water.png", "onWatercolor");
            addToolBarButton("Edit/Watercolor");

            addMenuItem("Edit/Stamping", "Stamping", KeyEvent.VK_X, "Stamping.png", "onStamping");
            addToolBarButton("Edit/Stamping");

//            addMenuItem("Edit/Floyd-Steinberg", "Floyd-Steinberg", KeyEvent.VK_X, "FloydSteinberg.png", "onFloydSteinberg");
//            addToolBarButton("Edit/Floyd-Steinberg");

            addMenuItem("Edit/Double", "Double size", KeyEvent.VK_X, "Double.png", "onDoubleSize");
            addToolBarButton("Edit/Double");

//            addMenuItem("Edit/Turn", "Turn image", KeyEvent.VK_X, "Turn.png", "onTurn");
//            addToolBarButton("Edit/Turn");
            addToolBarSeparator();

            addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.png", "onAbout");

            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.png", "onExit");
            addToolBarButton("File/Exit");

            add(initWindow());

            jButtonSelect.addActionListener(e -> {
                if (bufferedImage != null) {
                    if(!isSelectPressed) {

//                        System.out.println("select true");
//                        isSelectPressed = true;

                        selectedZoneA = new JPanel(new BorderLayout());//отрисовка бегающего окна\
                        selectedZoneA.setBounds(0, 0, frameSize, frameSize);

                        selectedZoneA.setOpaque(false);
                        zoneA.add(selectedZoneA, JLayeredPane.DRAG_LAYER);
                    } else {
//                        System.out.println("select false");
//                        isSelectPressed = false;
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onAbout() {
        ImageIcon icon = new ImageIcon("src/ru/nsu/fit/g15203/kostyleva/resources/Me.png");
        JOptionPane.showMessageDialog(
                this,
                "Author:\nKostyleva Victoria\nFIT, group 15203\nYear 2018",
                "About Init", JOptionPane.INFORMATION_MESSAGE, icon);
    }


    public void onEmpty() {
        if (bufferedImage != null) {
            if(selectedZoneA != null) {
                selectedZoneA.removeAll();
                selectedZoneA.repaint();
                selectedZoneA.setVisible(false);
                isSelectPressed = false;
            }
            if(selectedImage != null) {
                zoneB.removeAll();
                zoneB.repaint();
                selectedImage = null;
            }

            if(effectImage != null) {
                zoneC.removeAll();
                zoneC.repaint();
                effectImage = null;
            }
        }
    }

    public void onOpen() {
        loadImage();
        isSelectPressed = false;
    }

    public void OnChoose() {
        isSelectPressed = true;
        selectedZoneA.setVisible(true);
    }

    public void onExit() {
        System.exit(0);
    }

    public void onSave() {
        file = getSaveFileName("png", "Save Filter file");
        if (file == null)
            return;

        try {
            file.createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Can not make a new file", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!file.canWrite()) {
            JOptionPane.showMessageDialog(this, "Can not write to a file", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ImageIO.write(effectImage, "png", file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onCopyCtoB() {
        if(selectedImage != null && effectImage != null)
        zoneB.removeAll();
        JLabel tmp = new JLabel(new ImageIcon(resImage));
        tmp.setBounds(1, 1, resImage.getWidth(null), resImage.getHeight(null));
        zoneB.add(tmp);
        zoneB.repaint();
        selectedImage = effectImage;
    }

    public JPanel initWindow() {
        panel = new JPanel();
        Container container = new Container();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        zoneA = new JLayeredPane();
        zoneA.setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
        zoneA.setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));

        CustomMouseListener cms = new CustomMouseListener();
        zoneA.addMouseListener(cms);
        zoneA.addMouseMotionListener(cms);

        zoneB = new JPanel();
        zoneB.setLayout(null);
        zoneB.setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
        zoneB.setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));

        zoneC = new JPanel();
        zoneC.setLayout(null);
        zoneC.setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
        zoneC.setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1, 5, 3, true));

        panel.add(zoneA, container);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(zoneB, container);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(zoneC, container);

        selectedZoneA = new JPanel();
        return panel;
    }

    class CustomMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(bufferedImage != null) {
                if(isSelectPressed) {
                    drawSelectedZoneA(e);
                    drawZoneB();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(bufferedImage != null) {
                if(isSelectPressed) {
                    drawSelectedZoneA(e);
                    drawZoneB();
                }
            }
        }
    }

    private void drawSelectedZoneA(MouseEvent e) {//TODO about -1
        int x = e.getX();
        int y = e.getY();

        int xSize = (int) (DEFAULT_SIZE / loadedImageHCoef);
        int ySize = (int) (DEFAULT_SIZE / loadedImageWCoef);

        frameSize = xSize > ySize ? xSize : ySize;

        int x0 = x - frameSize / 2;
        int y0 = y - frameSize / 2;

        int xMax = x + frameSize / 2;
        int yMax = y + frameSize / 2;

        if(x0 < 0){
            x0 = 1;
        }
        if(y0 < 0){
            y0 = 1;
        }
        if(xMax >= DEFAULT_SIZE){
            x0 = DEFAULT_SIZE - frameSize;
            System.out.println(x0);
        }
        if(yMax >= DEFAULT_SIZE){
            y0 = DEFAULT_SIZE - frameSize;
        }

        xPressed = x;
        yPressed = y;

        selectedZoneA.removeAll();
//        BufferedImage im = new BufferedImage(frameSize, frameSize, BufferedImage.TYPE_INT_ARGB);

//        System.out.println("buf im : " + kost.getHeight());
        selectedZoneA.setOpaque(false);
        selectedZoneA.setSize(new Dimension(frameSize, frameSize));
//        System.out.println("Sel A size: " + selectedZoneA.getHeight());
        selectedZoneA.setBorder(new XorBorder(x0, y0, kost));
        selectedZoneA.setLocation(x0, y0);
        selectedZoneA.repaint();
        selectedZoneA.revalidate();

    }

    private void drawZoneB() {
        if (selectedImage != null) {
            zoneB.removeAll();
        }

        selectedImage = new BufferedImage(frameSize, frameSize, BufferedImage.TYPE_INT_ARGB);
        cancelImage = new BufferedImage(frameSize, frameSize, BufferedImage.TYPE_INT_ARGB);

        int x0 = xPressed - frameSize / 2;
        int y0 = yPressed - frameSize / 2;

        if(x0 < 0) {
            x0 = 1;
        }
        if(y0 < 0) {
            y0 = 1;
        }
        if(x0 + frameSize >= DEFAULT_SIZE) {
            x0 = DEFAULT_SIZE - frameSize;
        }
        if(y0 + frameSize >= DEFAULT_SIZE) {
            y0 = DEFAULT_SIZE - frameSize;
        }

        int temp = x0;
        for (int i = 0; i < selectedImage.getHeight(); i++) {
            for (int j = 0; j < selectedImage.getWidth(); j++) {
                int color = bufferedImage.getRGB(x0 , y0);
                selectedImage.setRGB(j, i, color);
                x0++;
            }
            y0++;
            x0 = temp;
        }

        Image image = new ImageIcon(selectedImage).getImage();
        image = new ImageIcon(image.getScaledInstance(DEFAULT_SIZE-2, DEFAULT_SIZE-2, BufferedImage.SCALE_DEFAULT)).getImage();
//        System.out.println(image.getWidth(null));
//        System.out.println(image.getWidth(null));
        selectedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = selectedImage.createGraphics();//TODO
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        JLabel sel = new JLabel(new ImageIcon(selectedImage));
        sel.setBounds(1, 1, image.getWidth(null), image.getHeight(null));
        zoneB.add(sel);
        zoneB.repaint();
    }

    private void loadImage() {
        file = getOpenFileName("bmp", "Open Filter file");

        if(file == null) {
            System.out.println("hello, null");
            return;
        }

        zoneA.removeAll();
        zoneB.removeAll();
        zoneC.removeAll();

        try {
            Image image = ImageIO.read(file);

            int imWidth = image.getWidth(null);
            int imHeight = image.getHeight(null);

            if (imWidth > DEFAULT_SIZE || imHeight > DEFAULT_SIZE) {

                if (imWidth > DEFAULT_SIZE) {
                    loadedImageWCoef = imWidth / 350.;
//                    System.out.println("load w " + loadedImageWCoef);
                }
                if (imHeight > DEFAULT_SIZE) {
                    loadedImageHCoef = imHeight / 350.;
//                    System.out.println("load h " + loadedImageHCoef);
                }

                if (imWidth >= imHeight) {
                    int width = (int) (imHeight / loadedImageWCoef) > DEFAULT_SIZE - 2 ? DEFAULT_SIZE - 2 : (int) (imHeight / loadedImageWCoef);
//                    System.out.println("width " + width);
                    image = new ImageIcon(image.getScaledInstance(DEFAULT_SIZE - 2, width, BufferedImage.SCALE_DEFAULT)).getImage();
                } else {
                    int height = (int) (imWidth / loadedImageHCoef) > DEFAULT_SIZE - 2 ? DEFAULT_SIZE - 2 : (int) (imWidth / loadedImageHCoef);
//                    System.out.println("height " + height);
                    image = new ImageIcon(image.getScaledInstance(height, DEFAULT_SIZE - 2, BufferedImage.SCALE_DEFAULT)).getImage();
                }
            }

//            System.out.println(image.getHeight(null));
            kost = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bGr = kost.createGraphics();
            bGr.drawImage(image, 0, 0, null);
            bGr.dispose();

            bufferedImage = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();

            JLabel tmpImage = new JLabel(new ImageIcon(bufferedImage));
            tmpImage.setBounds(1, 1, imWidth, imHeight);
            zoneA.add(tmpImage, JLayeredPane.DEFAULT_LAYER);
            zoneA.repaint();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void useFilter(int type) {
        if (selectedImage != null) {
            if (effectImage != null) {
                zoneC.removeAll();
            }

            switch (type) {
                case NEGATIVE:
                    Negative negative = new Negative();
                    effectImage = negative.go(selectedImage);
                    break;

                case DESATURATE:
                    Desaturate desaturate = new Desaturate();
                    effectImage = desaturate.go(selectedImage);
                    break;

                case SHARPNESS:
                    Sharpness sharpness = new Sharpness();
                    effectImage = sharpness.go(selectedImage);
                    break;

                case BLUR:
                    BlurGauss blur = new BlurGauss();
                    effectImage = blur.go(selectedImage);
                    break;

                case BORDER_ROBERTS:
                    Roberts border = new Roberts();
                    effectImage = border.go(selectedImage);
                    break;

                case GAMMA:
                    Gamma gamma = new Gamma();
                    effectImage = gamma.go(selectedImage, gammaParameter);
                    break;

                case WATERCOLOR:
                    Median median = new Median();
                    effectImage = median.go(selectedImage);
                    Sharpness water = new Sharpness();
                    effectImage = water.go(effectImage);
                    break;

                case STAMPING:
                    Stamping stamping = new Stamping();
                    effectImage = stamping.go(selectedImage);
                    break;

                case DOUBLE_SIZE:
                    DoubleSize doubleSize = new DoubleSize();
                    effectImage = doubleSize.go(selectedImage);
                    break;

                case FLOYD_STEINBERG:
//                    FloydSteinberg floydSteinberg = new FloydSteinberg();
//                    effectImage = floydSteinberg.go(selectedImage);
                        return;
//                    Desaturate kost = new Desaturate();
//                    effectImage = kost.go(effectImage);
//                    break;
            }

            resImage = new ImageIcon(effectImage).getImage();
            resImage = new ImageIcon(resImage.getScaledInstance(DEFAULT_SIZE - 2, DEFAULT_SIZE - 2, BufferedImage.SCALE_DEFAULT)).getImage();
            JLabel res = new JLabel(new ImageIcon(resImage));
            res.setBounds(1, 1, resImage.getWidth(null), resImage.getHeight(null));
            zoneC.add(res);
            zoneC.repaint();
        }
    }

    public void onNegative() {
        useFilter(NEGATIVE);
    }

    public void onDesaturate() {
        useFilter(DESATURATE);
    }

    public void onSharpness() {
        useFilter(SHARPNESS);
    }

    public void onBlur() {
        useFilter(BLUR);
    }

    public void onBorder() {
        useFilter(BORDER_ROBERTS);
    }

    public void onGamma() {
        if (selectedImage != null) {

            cancelImage = effectImage;

            useFilter(GAMMA);

            JDialog dialog = new JDialog(this, "Choose gamma/10", true);
            JPanel panel = new JPanel(new GridLayout(2, 2));

            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            dialog.setSize(400, 200);
            dialog.setResizable(false);

            JTextField field = new JTextField(3);
            field.setText(((Integer) gammaParameter).toString());
            panel.add(field);

            JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 10, gammaParameter);
            slider.setPaintTicks(true);

            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setLabelTable(slider.createStandardLabels(1));
            slider.setValue(gammaParameter);
            panel.add(slider);

            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    field.setText(String.valueOf(slider.getValue()));
                    gammaParameter = slider.getValue();
                    useFilter(GAMMA);

                }
            });

            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String thickness = field.getText();
                    if (!thickness.matches("\\d+") || Integer.parseInt(thickness) > 10 || Integer.parseInt(thickness) < 1) {
                        slider.setValue(0);
                        return;
                    }
                    slider.setValue(gammaParameter);
                }
            });

            JButton buttonCancel = new JButton("Cancel");
            panel.add(buttonCancel);

            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(cancelImage != null) {
                        zoneC.removeAll();
                        JLabel sel = new JLabel(new ImageIcon(cancelImage));
                        sel.setBounds(1, 1, cancelImage.getWidth(null), cancelImage.getHeight(null));
                        zoneC.add(sel);
                        zoneC.repaint();
                        effectImage = cancelImage;
                    }
                    dialog.dispose();
                }
            });

            dialog.setLocationRelativeTo(this);
            dialog.setContentPane(panel);
            dialog.setVisible(true);
        }
    }

    public void onWatercolor() {
        useFilter(WATERCOLOR);
    }

    public void onStamping() {
        useFilter(STAMPING);
    }

    public void onDoubleSize() {
        useFilter(DOUBLE_SIZE);
    }

    public void onTurn() {
        useFilter(TURN);
    }

    public void onFloydSteinberg() {
//        useFilter(FLOYD_STEINBERG);

        if (selectedImage != null) {
            cancelImage = effectImage;

            JDialog dialog = new JDialog(this, "Choose color gamma", true);
            JPanel panel = new JPanel(new GridLayout(4, 2));

            dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            dialog.setSize(400, 200);
            dialog.setResizable(false);

            JTextField fieldRed = new JTextField(3);
            fieldRed.setText(((Integer) floydRed).toString());
            panel.add(fieldRed);

            JSlider sliderRed = new JSlider(JSlider.HORIZONTAL, 2, 256, floydRed);
            sliderRed.setPaintTicks(true);

            sliderRed.setMinorTickSpacing(10);
            sliderRed.setValue(floydRed);
            panel.add(sliderRed);

            sliderRed.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    fieldRed.setText(String.valueOf(sliderRed.getValue()));
                    floydRed = sliderRed.getValue();
                    useFilter(FLOYD_STEINBERG);
                }
            });

            fieldRed.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String thickness = fieldRed.getText();
                    if (!thickness.matches("\\d+") || Integer.parseInt(thickness) > 2 || Integer.parseInt(thickness) < 256) {
                        sliderRed.setValue(0);
                        return;
                    }
                    sliderRed.setValue(floydRed);
                }
            });

            //-------------------

            JTextField fieldGreen = new JTextField(3);
            fieldGreen.setText(((Integer) floydGreen).toString());
            panel.add(fieldGreen);

            JSlider sliderGreen = new JSlider(JSlider.HORIZONTAL, 2, 256, floydGreen);
            sliderGreen.setPaintTicks(true);

            sliderGreen.setMinorTickSpacing(10);
            sliderGreen.setValue(floydGreen);
            panel.add(sliderGreen);

            sliderGreen.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    fieldGreen.setText(String.valueOf(sliderGreen.getValue()));
                    floydGreen = sliderGreen.getValue();
                    useFilter(FLOYD_STEINBERG);
                }
            });

            fieldRed.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String thickness = fieldGreen.getText();
                    if (!thickness.matches("\\d+") || Integer.parseInt(thickness) > 2 || Integer.parseInt(thickness) < 256) {
                        sliderGreen.setValue(0);
                        return;
                    }
                    sliderGreen.setValue(floydGreen);
                }
            });
            //-------------------
            JTextField fieldBlue = new JTextField(3);
            fieldBlue.setText(((Integer) floydBlue).toString());
            panel.add(fieldBlue);

            JSlider sliderBlue = new JSlider(JSlider.HORIZONTAL, 2, 256, floydBlue);
            sliderBlue.setPaintTicks(true);

            sliderBlue.setMinorTickSpacing(10);
            sliderBlue.setValue(floydBlue);
            panel.add(sliderBlue);

            sliderBlue.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    fieldBlue.setText(String.valueOf(sliderBlue.getValue()));
                    floydBlue = sliderBlue.getValue();
                    useFilter(FLOYD_STEINBERG);
                }
            });

            fieldRed.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String thickness = fieldBlue.getText();
                    if (!thickness.matches("\\d+") || Integer.parseInt(thickness) > 2 || Integer.parseInt(thickness) < 256) {
                        sliderBlue.setValue(0);
                        return;
                    }
                    sliderBlue.setValue(floydBlue);
                }
            });
            //-------------------

            JButton buttonCancel = new JButton("Cancel");
            panel.add(buttonCancel);

            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(cancelImage != null) {
                        zoneC.removeAll();
                        JLabel sel = new JLabel(new ImageIcon(cancelImage));
                        sel.setBounds(1, 1, cancelImage.getWidth(null), cancelImage.getHeight(null));
                        zoneC.add(sel);
                        zoneC.repaint();
                        effectImage = cancelImage;
                    }
                    dialog.dispose();
                }
            });

            dialog.setLocationRelativeTo(this);
            dialog.setContentPane(panel);
            dialog.setVisible(true);
        }

    }

    public static void main(String[] args) {
        MainWindow mainFrame = new MainWindow();
        mainFrame.setVisible(true);
    }
}




