package ru.nsu.fit.g15203.kostyleva;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class MainWindow extends MainFrame {

    private File file;
    private GraphicsPanel graphicsPanel;
    private boolean gridFlag = false;
    private boolean isolinesFlag  = false;

    public MainWindow() {
        super(800, 720, "ISOLINES");
        try {
//            setMinimumSize(new Dimension(650, 600));
            addSubMenu("File", KeyEvent.VK_F);
            addSubMenu("Edit", KeyEvent.VK_F);
            addSubMenu("Help", KeyEvent.VK_H);

//            addMenuItem("File/Open", "Load image", KeyEvent.VK_X, "Open.png", "onOpen");
//            addToolBarButton("File/Open");
//
//            addMenuItem("File/Save", "Save image", KeyEvent.VK_X, "Save.png", "onSave");
//            addToolBarButton("File/Save");
//            addToolBarSeparator();

            addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.png", "onAbout");

//            addMenuItem("Edit/Settings", "Settings", KeyEvent.VK_X, "Settings.png", "onSettings");
//            addToolBarButton("Edit/Settings");

            addMenuItem("Edit/Grid", "Show/hide grid", KeyEvent.VK_X, "Grid.png", "onGrid");
            addToolBarButton("Edit/Grid");

//            addMenuItem("Edit/Isolines", "Show/hide isolines", KeyEvent.VK_X, "Isolines.png", "onIsolines");
//            addToolBarButton("Edit/Isolines");
            addToolBarSeparator();

            addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "Exit.png", "onExit");
            addToolBarButton("File/Exit");

            graphicsPanel = new GraphicsPanel(600, 600, new FunctionToDraw(0, 40, 0, 40));
            add(graphicsPanel.initWindow(), BorderLayout.SOUTH);

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

    public void onOpen() {
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

    }

    public void onGrid() {
        if(!gridFlag) {
            graphicsPanel.showGrid();
            gridFlag = true;
        }
        else {
            graphicsPanel.hideGrid();
            gridFlag = false;
        }
    }

    public void onIsolines() {
        if(!isolinesFlag) {
            isolinesFlag = true;
        }
//        else {
//            isolinesFlag = false;
//        }
    }

    public void onSettings() {

    }


    public static void main(String[] args) {
        MainWindow mainFrame = new MainWindow();
        mainFrame.setVisible(true);
    }



}




