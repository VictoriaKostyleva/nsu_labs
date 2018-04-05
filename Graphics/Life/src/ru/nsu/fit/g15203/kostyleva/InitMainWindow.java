package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InitMainWindow extends MainFrame {
    private ImageIcon startIcon;
    private ImageIcon pauseIcon;


    private JMenuItem runMenu;
    private JMenuItem xorMenu;
    private JMenuItem showValuesMenu;

    private JButton runButton;
    private JButton stepButton;
    private JButton xorButton;
    private JButton showValuesButton;

    private final int WIDTH_SIZE = 600;
    private final int HEIGTH_SIZE = 600;
    private final int TEXT_FIELD_SIZE = 6;

    private final int MAX_N = 50;
    private final int MAX_M = 50;
    private final int MAX_R = 40;

    private InitView initView;

    private int m = 8;
    private int n = 10;
    private int r = 15;//radius
    private int lineThickness = 1;

    JScrollPane scrollPane;

    private Object[] options = {"Yes, please", "No, thanks"};

    File file;

    /**
     * Default constructor to create main window
     */
    public InitMainWindow() {
        super(500, 600, "GAME LIFE");
        try {
//            setMinimumSize(new Dimension(500, 600));
            startIcon = new ImageIcon(getClass().getResource("resources/Run.png"), "Run");
            pauseIcon = new ImageIcon(getClass().getResource("resources/Pause.png"), "Pause");

            addSubMenu("File", KeyEvent.VK_F);
            addSubMenu("Edit", KeyEvent.VK_F);
            addSubMenu("Game", KeyEvent.VK_F);
            addSubMenu("Help", KeyEvent.VK_H);

            addMenuItem("File/Empty", "Restart the game", KeyEvent.VK_X, "Empty.png", "onEmpty");
            addToolBarButton("File/Empty");

            addMenuItem("File/Open", "Open field file", KeyEvent.VK_X, "Open.png", "onOpen");
            addToolBarButton("File/Open");

            addMenuItem("File/Save", "Save field to file", KeyEvent.VK_X, "Save.png", "onSave");
            addToolBarButton("File/Save");

            addMenuItem("File/SaveAs", "Save field to file as", KeyEvent.VK_X, "SaveAs.png", "onSaveAs");
            addToolBarButton("File/SaveAs");
            addToolBarSeparator();

            addMenuItem("Game/Next step", "Next step", KeyEvent.VK_A, "Next.png", "onNextStep");
            stepButton = addToolBarButton("Game/Next step");

            addMenuItem("Game/Run", "Run/Pause", KeyEvent.VK_E, "Run.png", "runPauseModel");
            runButton = addToolBarButton("Game/Run");
            addToolBarSeparator();

            addMenuItem("Edit/Model settings", "Model settings", KeyEvent.VK_X, "Options.png", "onSettings");
            addToolBarButton("Edit/Model settings");

            addMenuItem("Edit/View settings", "View settings", KeyEvent.VK_X, "Slider.png", "onSlider");
            addToolBarButton("Edit/View settings");
            addToolBarSeparator();

            addMenuItem("Edit/Show impact values", "Show impact values", KeyEvent.VK_X, "Show.png", "onShow");
            showValuesButton = addToolBarButton("Edit/Show impact values");

            addMenuItem("Edit/Xor or replace", "Xor or replace", KeyEvent.VK_X, "XorOrReplace.png", "onXorOrReplace");
            xorButton = addToolBarButton("Edit/Xor or replace");
            addToolBarSeparator();

            addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.png", "onAbout");

            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.png", "onExit");
            addToolBarButton("File/Exit");

            initView = new InitView(m, n, r, lineThickness);

            initView.setPreferredSize(new Dimension(initView.getBufferedImage().getWidth(), initView.getBufferedImage().getHeight()));
            scrollPane = new JScrollPane(initView);
            add(scrollPane);


            runMenu = (JMenuItem) getMenuElement("Game/Run");
            runMenu.setIcon(startIcon);

            xorMenu = (JMenuItem) getMenuElement("Edit/Xor or replace");
            showValuesMenu = (JMenuItem) getMenuElement("Edit/Show impact values");

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
        int k = JOptionPane.showOptionDialog(this, "Would you like to save this game?",
                "Clear field",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if(k == JOptionPane.YES_NO_OPTION) {
            onSaveAs();
        }

        askFieldParams();

        repaint();
        initView.newField(m, n, r, lineThickness);
        repaint();
        revalidate();
    }

    public void onSlider() {
        JDialog dialog = new JDialog(this, "View settings", true);

        JPanel panel = new JPanel(new GridLayout(3, 3));

        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setResizable(false);

        String lineStr = "Line thickness: ";
        addLabel(panel, lineStr);

        JTextField fieldLine = new JTextField(6);
        fieldLine.setText(((Integer) getLineThickness()).toString());
        panel.add(fieldLine);

        JSlider sliderLine = new JSlider(JSlider.HORIZONTAL, 1, 11, lineThickness);
        sliderLine.setPaintTicks(true);

        sliderLine.setMinorTickSpacing(1);
        sliderLine.setPaintTicks(true);
        sliderLine.setPaintLabels(true);
        sliderLine.setLabelTable(sliderLine.createStandardLabels(2));
        sliderLine.setValue(getLineThickness());
        panel.add(sliderLine);

        sliderLine.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fieldLine.setText(String.valueOf(sliderLine.getValue()));
                int lineTh = sliderLine.getValue();
                setLineThickness(lineTh);

                initView.newFieldNewLine(m, n, r, lineThickness);

                scrollPane.setViewportView(initView);
                initView.setPreferredSize(new Dimension(initView.getBufferedImage().getWidth(), initView.getBufferedImage().getHeight()));
            }
        });

        fieldLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String thickness = fieldLine.getText();
                if (!thickness.matches("\\d+") || Integer.parseInt(thickness) > 11) {
                    sliderLine.setValue(0);
                    return;
                }
                sliderLine.setValue(Integer.parseInt(thickness));
            }
        });

        ButtonGroup group = new ButtonGroup();
        JRadioButton xorMode = new JRadioButton("XOR mode", false);
        group.add(xorMode);
        JRadioButton replaceMode = new JRadioButton("Replace mode", true);
        group.add(replaceMode);

        panel.add(xorMode);
        panel.add(replaceMode);

        xorMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initView.setXor(true);
                System.out.println(initView.isXor());
            }
        });

        replaceMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initView.setXor(false);
                System.out.println(initView.isXor());
            }
        });

        Checkbox impact = new Checkbox("Display impact values");
        panel.add(impact);
        impact.setState(initView.isImpactValues());
        dialog.setContentPane(panel);

        String nullStr = " ";
        addLabel(panel, nullStr);

        JButton buttonApply = new JButton("Apply");
        panel.add(buttonApply);

        buttonApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isImpact = impact.getState();
                initView.setImpactValues(isImpact);



                dialog.dispose();
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    public void onExit() {
        int n = JOptionPane.showOptionDialog(this, "Would you like to save this game?",
                "Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if(n == JOptionPane.YES_NO_OPTION) {
            onSaveAs();
        }
        System.exit(0);
    }

    public void onNextStep() {
        initView.stepController(m, n, r);
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
    }

    public void onSettings() {
        JDialog dialog = new JDialog(this, "Field settings", true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(200, 380);
        dialog.setResizable(false);

        GridLayout layout = new GridLayout(12, 2);
        panel.setLayout(layout);

        addLabel(panel, "Field parameters: ");
        String nullStr = " ";
        addLabel(panel, nullStr);

        addLabel(panel, "Width: ");
        JTextField widthField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(widthField);
        widthField.setText(String.valueOf(m));

        addLabel(panel, "Height: ");
        JTextField heightField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(heightField);
        heightField.setText(String.valueOf(n));

        addLabel(panel, "Radius: ");
        JTextField radiusField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(radiusField);
        radiusField.setText(String.valueOf(r));

        addLabel(panel, "Game options: ");
        addLabel(panel, nullStr);

        addLabel(panel, "Live begin: ");
        JTextField liveBeginField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(liveBeginField);
        liveBeginField.setText(String.valueOf(initView.getModel().getLiveBegin()));

        addLabel(panel, "Live end: ");
        JTextField liveEndField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(liveEndField);
        liveEndField.setText(String.valueOf(initView.getModel().getLiveEnd()));

        addLabel(panel, "Birth begin: ");
        JTextField birthBeginField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(birthBeginField);
        birthBeginField.setText(String.valueOf(initView.getModel().getBirthBegin()));

        addLabel(panel, "Birth end: ");
        JTextField birthEndField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(birthEndField);
        birthEndField.setText(String.valueOf(initView.getModel().getBirthEnd()));

        addLabel(panel, "First impact: ");
        JTextField firstImpField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(firstImpField);
        firstImpField.setText(String.valueOf(initView.getModel().getFstImpact()));

        addLabel(panel, "Second impact: ");
        JTextField secondImpField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(secondImpField);
        secondImpField.setText(String.valueOf(initView.getModel().getSndImpact()));

        JButton buttonApply = new JButton("Apply");
        panel.add(buttonApply);


        dialog.setContentPane(panel);


        buttonApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    int newM = Integer.parseInt(widthField.getText());
                    int newN = Integer.parseInt(heightField.getText());
                    int newR = Integer.parseInt(radiusField.getText());

                    int goodMNR = checkMNR(newM, newN, newR);

                    if (goodMNR == 1) {
                        JOptionPane.showMessageDialog(dialog, "Bad params! m, n, r can not be negative. r can not be less than 15.", "Alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (goodMNR == 2) {
                        JOptionPane.showMessageDialog(dialog, "Too big params!", "Alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    setM(newM);
                    setN(newN);
                    setR(newR);
                } catch (java.lang.NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Bad params! Type numbers here!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                initView.setRadius(r);
                initView.setN(n);
                initView.setM(m);

                repaint();
                initView.newFieldNewMNR(m, n, r, lineThickness);
                repaint();

                scrollPane.setViewportView(initView);
                initView.setPreferredSize(new Dimension(initView.getBufferedImage().getWidth(), initView.getBufferedImage().getHeight()));

                double params[] = new double[6];
                params[0] = Double.parseDouble(liveBeginField.getText());
                params[1] = Double.parseDouble(birthBeginField.getText());
                params[2] = Double.parseDouble(birthEndField.getText());
                params[3] = Double.parseDouble(liveEndField.getText());
                params[4] = Double.parseDouble(firstImpField.getText());
                params[5] = Double.parseDouble(secondImpField.getText());
//                for (int i = 0; i < 6; i++)
//                    System.out.println(params[i]);

                boolean goodParams = initView.workWithParams(params);

                if (!goodParams) {
                    JOptionPane.showMessageDialog(dialog, "Bad params: you need LIVE_BEGIN ≤ BIRTH_BEGIN ≤ BIRTH_END ≤ LIVE_END", "Alert", JOptionPane.ERROR_MESSAGE);
                }
                revalidate();
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private int checkMNR(int m, int n, int r) {//0 - ok 	1 - negative		2 - too big
        if (m < 0 || n < 0 || r < 15) {
            return 1;
        }
        if (m > MAX_M || n > MAX_N || r > MAX_R) {
            return 2;
        }
        return 0;
    }

    private void addLabel(JPanel panel, String str) {
        JLabel label = new JLabel(str);
        label.setText(str);
        panel.add(label);
        return;
    }

    public void runPauseModel() {
        if (initView.getModel().isRun) {
            initView.getModel().isRun = false;
            runMenu.setText("Run");
            runMenu.setIcon(startIcon);
            runButton.setIcon(startIcon);

            stepButton.setEnabled(true);
        } else {
            initView.getModel().isRun = true;
            runMenu.setText("Stop");
            runMenu.setIcon(pauseIcon);
            runButton.setIcon(pauseIcon);

            stepButton.setEnabled(false);
        }
        initView.play();
    }

    public void onOpen() {
        int k = JOptionPane.showOptionDialog(this, "Would you like to save this game?", "Open a new file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if(k == JOptionPane.YES_NO_OPTION) {
            onSaveAs();
        }

        file = getOpenFileName("txt", "Open Life file");

        if (file == null)
            return;

        try {
            FileParser fileParser = new FileParser(file);
            fileParser.parse();

            int goodMNR = checkMNR(fileParser.getWidth(), fileParser.getHeight(), fileParser.getRadius());

            if (goodMNR == 1) {
                JOptionPane.showMessageDialog(null, "Bad params! m, n, r can not be negative. r can not be less than 15.", "Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (goodMNR == 2) {
                JOptionPane.showMessageDialog(null, "Too big params!", "Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setM(fileParser.getWidth());
            setN(fileParser.getHeight());
            setR(fileParser.getRadius());
            lineThickness = fileParser.getLineThickness();

            initView.setRadius(r);
            initView.setN(n);
            initView.setM(m);

            initView.newFieldNewMNR(m, n, r, lineThickness);

            scrollPane.setViewportView(initView);
            initView.setPreferredSize(new Dimension(initView.getBufferedImage().getWidth(), initView.getBufferedImage().getHeight()));

            initView.newField(m, n, r, lineThickness);
            repaint();

            for (Dot cell: fileParser.cells) {
                initView.setAlive(cell);
            }

            initView.reculcImpAfterOpen();
            initView.repaintLives(m, n, r);

        } catch (FileParserException fileParserException) {
            JOptionPane.showMessageDialog(this, fileParserException.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onSave() {
        if(file == null) {
            onSaveAs();
            return;
        }

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
            FileWriter out = new FileWriter(file.getAbsoluteFile());
            out.write(initView.getM() + " " + initView.getN() + "\r\n");
            out.write(lineThickness + "\r\n");
            out.write(initView.getRadToFile() + "\r\n");

            ArrayList<Dot> states = initView.makeArrayFromCelles();

            out.write(states.size() + "\r\n");

            for (Dot state : states) {
                out.write(Integer.toString(state.getX()) + " " + Integer.toString(state.getY()) + "\r\n");
            }

            out.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onSaveAs() {
        file = getSaveFileName("txt", "Save Life file");
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
            FileWriter out = new FileWriter(file.getAbsoluteFile());
            out.write(initView.getM() + " " + initView.getN() + "\r\n");
            out.write(lineThickness + "\r\n");
            out.write(initView.getRadToFile() + "\r\n");

            ArrayList<Dot> states = initView.makeArrayFromCelles();

            out.write(states.size() + "\r\n");

            for (Dot state : states) {
                out.write(Integer.toString(state.getX()) + " " + Integer.toString(state.getY()) + "\r\n");
            }

            out.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void askFieldParams() {
        JDialog dialog = new JDialog(this, "You can choose field params", true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(250, 200);
        dialog.setResizable(false);

        GridLayout layout = new GridLayout(4, 2);
        panel.setLayout(layout);

        addLabel(panel, "Width: ");
        JTextField widthField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(widthField);
        widthField.setText(String.valueOf(m));

        addLabel(panel, "Height: ");
        JTextField heightField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(heightField);
        heightField.setText(String.valueOf(n));

        addLabel(panel, "Radius: ");
        JTextField radiusField = new JTextField(TEXT_FIELD_SIZE);
        panel.add(radiusField);
        radiusField.setText(String.valueOf(r));

        JButton buttonApply = new JButton("Apply");
        panel.add(buttonApply);

        dialog.setContentPane(panel);

        buttonApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    int newM = Integer.parseInt(widthField.getText());
                    int newN = Integer.parseInt(heightField.getText());
                    int newR = Integer.parseInt(radiusField.getText());

                    int goodMNR = checkMNR(newM, newN, newR);

                    if (goodMNR == 1) {
                        JOptionPane.showMessageDialog(dialog, "Bad params! m, n, r can not be negative. r can not be less than 15.", "Alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (goodMNR == 2) {
                        JOptionPane.showMessageDialog(dialog, "Too big params!", "Alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    setM(newM);
                    setN(newN);
                    setR(newR);
                } catch (java.lang.NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Bad params! Type numbers here!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                initView.setRadius(r);
                initView.setN(n);
                initView.setM(m);

                repaint();
                initView.newFieldNewMNR(m, n, r, lineThickness);

                repaint();

                revalidate();
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void onShow() {
        if(!initView.isImpactValues()) {
            initView.setImpactValues(true);
            showValuesMenu.setText("Hide values");
            showValuesButton.setToolTipText("Hide values");
        } else {
            initView.setImpactValues(false);
            showValuesMenu.setText("Show values");
            showValuesButton.setToolTipText("Show values");
        }
    }

    public void onXorOrReplace() {
        if(!initView.isXor()) {
            initView.setXor(true);
            xorMenu.setText("Replace");
            xorButton.setToolTipText("Replace");
        } else {
            initView.setXor(false);
            xorMenu.setText("XOR");
            xorButton.setToolTipText("XOR");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int rAdd = (int)((lineThickness - 1) / 2.);
        return new Dimension(700, 700);
    }
    /**
     * Application main entry point
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }
}




