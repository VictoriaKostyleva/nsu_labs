package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Negative {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        for (int h = 0; h < filteredImage.getHeight(); h++) {
            for (int w = 0; w < filteredImage.getWidth(); w++) {
                Color color = new Color(bufferedImage.getRGB(w, h));
                color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                filteredImage.setRGB(w, h, color.getRGB());
            }
        }
        return filteredImage;
    }
}
