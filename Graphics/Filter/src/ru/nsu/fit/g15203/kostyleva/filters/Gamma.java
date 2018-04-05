package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Gamma {
    public BufferedImage go(BufferedImage bufferedImage, double gammaParameter) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        double gamma = gammaParameter / 10.;

        for (int i = 0; i < filteredImage.getHeight(); i++) {
            for (int j = 0; j < filteredImage.getWidth(); j++) {
                Color color = new Color(bufferedImage.getRGB(j,i));

                int red = (int)(255 * (Math.pow(color.getRed() / 255., gamma)));
                int green = (int)(255 * (Math.pow(color.getGreen() / 255., gamma)));
                int blue  = (int)(255 * (Math.pow(color.getBlue() / 255., gamma)));

                if (red > 255)
                    red = 255;
                if (red < 0)
                    red = 0;

                if (green > 255)
                    green = 255;
                if (green < 0)
                    green = 0;

                if (blue > 255)
                    blue = 255;
                if (blue < 0)
                    blue = 0;

                color = new Color(red, green, blue);
                filteredImage.setRGB(j, i, color.getRGB());
            }
        }
        return filteredImage;
    }
}
