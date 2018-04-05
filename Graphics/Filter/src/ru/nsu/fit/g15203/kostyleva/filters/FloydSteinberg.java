package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FloydSteinberg {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        double[][] core = {{0, 2},
                        {3, 1}};

        int[][] coordinates = {{-1, -1}, {0, -1},
                {-1, 0}, {0, 0}};

        Color color;

        for (int i = 1; i < filteredImage.getHeight() - 1; i++) {
            for (int j = 1; j < filteredImage.getWidth() - 1; j++) {
                color = new Color(bufferedImage.getRGB(j, i));

                int oldpixel  = bufferedImage.getRGB(j, i);
                int newpixel  = find_closest_palette_color(oldpixel);

                filteredImage.setRGB(j, i, newpixel);
                int quant_error  = oldpixel - newpixel;

                filteredImage.setRGB(j + 1, i, bufferedImage.getRGB(j + 1, i) + quant_error * 7 / 16);
                filteredImage.setRGB(j - 1, i + 1, bufferedImage.getRGB(j - 1, i + 1) + quant_error * 3 / 16);
                filteredImage.setRGB(j, i + 1, bufferedImage.getRGB(j, i + 1) + quant_error * 5 / 16);
                filteredImage.setRGB(j + 1, i + 1, bufferedImage.getRGB(j + 1, i + 1) + quant_error * 1 / 16);

            }
        }




        return filteredImage;
    }

    public int find_closest_palette_color(int oldpixel) {
       return Math.round(oldpixel / 255);
    }
}
