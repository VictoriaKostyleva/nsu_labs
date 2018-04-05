package ru.nsu.fit.g15203.kostyleva;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class XorBorder extends AbstractBorder {

    private final int DASH_LENGTH = 5;
    private int x0;
    private int y0;
    private BufferedImage bufferedImage;

    public XorBorder(int x0, int y0, BufferedImage bufferedImage) {
        this.x0 = x0;
        this.y0 = y0;
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        width -= 2;
        height -= 2;
        Graphics2D g2d = (Graphics2D) g;
        int count = 0;
        boolean isDash = false;

//        System.out.println(x);
//        System.out.println(y);

        for (int i = 0; i < width; i++) {
            if (!isDash) {
                Color color = new Color(bufferedImage.getRGB(i + x0, y0));//top
                g2d.setColor(new Color(255 - color.getRed(),255 - color.getGreen(),255 - color.getBlue()));
                g2d.drawLine(i, y, i, y);

                color = new Color(bufferedImage.getRGB(i + x0, width - 1  + y0));//bottom
                g2d.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
                g2d.drawLine(i,  width - 1, i, width - 1);
                isDash = count % DASH_LENGTH == 0;
            } else {
                Color color = new Color(bufferedImage.getRGB(i + x0, y0));
                g2d.setColor(color);
                g2d.drawLine(i, y, i, y);

                color = new Color(bufferedImage.getRGB(i + x0, width - 1  + y0));
                g2d.setColor(color);
                g2d.drawLine(i, width - 1, i, width - 1);
                isDash = count % DASH_LENGTH != 0;
            }
            count++;
        }

        for (int i = 0; i < height; i++) {
            if (!isDash) {
                Color color = new Color(bufferedImage.getRGB(x0, i + y0));
                g2d.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
                g2d.drawLine(x, i, x, i);
                color = new Color(bufferedImage.getRGB(height - 1 + x0, i + y0));
                g2d.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
                g2d.drawLine(height - 1, i, height - 1, i);
                isDash = count % DASH_LENGTH == 0;
            }
            else {
                Color color = new Color(bufferedImage.getRGB(x0, i + y0));
                g2d.setColor(color);
                g2d.drawLine(x, i, x, i);
                color = new Color(bufferedImage.getRGB(height - 1 + x0, i + y0));
                g2d.setColor(color);
                g2d.drawLine(height - 1, i, height - 1, i);
                isDash = count % DASH_LENGTH != 0;
            }
            count++;
        }
    }
}
