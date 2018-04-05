package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DoubleSize {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        BufferedImage littleImage = new BufferedImage(bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2, bufferedImage.getType());

        int[][] coordinates = {{-1, -1}, {0, -1},
                {-1, 0}, {0, 0}};

        int w = 0;
        for (int i = bufferedImage.getHeight() / 4; i < 3 *bufferedImage.getHeight() / 4 - 1; i++) {
            int h = 0;
            for (int j = bufferedImage.getWidth() / 4; j < 3 * bufferedImage.getWidth() / 4 - 1; j++) {
                Color color = new Color(bufferedImage.getRGB(j, i));
                    littleImage.setRGB(h, w, color.getRGB());
                h++;
            }
            w++;
        }

        w = 0;
        for (int i = 1; i < filteredImage.getHeight()- 1; i+=2) {
            int h = 0;
            for (int j = 1; j < filteredImage.getWidth() - 1; j+=2) {
                Color color = new Color(littleImage.getRGB(h, w));
                for(int y = 0; y < 4; y++)
                    filteredImage.setRGB(j + coordinates[y][0], i + coordinates[y][1], color.getRGB());

                h++;
            }
            w++;
        }

        return filteredImage;
    }
}