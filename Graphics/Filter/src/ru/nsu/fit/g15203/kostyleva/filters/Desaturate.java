package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Desaturate {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        for (int h = 0; h < filteredImage.getHeight(); h++) {
            for (int w = 0; w < filteredImage.getWidth(); w++) {
                Color color = new Color(bufferedImage.getRGB(w, h));

//                int newColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                int newColor = (int)(color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114);

                color = new Color(newColor, newColor, newColor);
                filteredImage.setRGB(w, h, color.getRGB());
            }
        }
        return filteredImage;
    }
}