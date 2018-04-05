package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sharpness {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        double[][] core = {{-1, -1, -1},
                            {-1, 9, -1},
                            {-1, -1, -1}};

        int[][] coordinates = {{-1, -1}, {0, -1}, {1, -1},
                              {-1, 0}, {0, 0}, {1, 0},
                              {-1, 1}, {0, 1}, {1, 1}};

        Color[] colors = new Color[9];

        for (int i = 1; i < filteredImage.getHeight() - 1; i++) {
            for (int j = 1; j < filteredImage.getWidth() - 1; j++) {
                for (int y = 0; y < 9; y++)
                    colors[y] = new Color(bufferedImage.getRGB(j + coordinates[y][0], i + coordinates[y][1]));

                int red = (int) (
                                core[0][0] * colors[0].getRed() +
                                core[0][1] * colors[1].getRed() +
                                core[0][2] * colors[2].getRed() +
                                core[1][0] * colors[3].getRed() +
                                core[1][1] * colors[4].getRed() +
                                core[1][2] * colors[5].getRed() +
                                core[2][0] * colors[6].getRed() +
                                core[2][1] * colors[7].getRed() +
                                core[2][2] * colors[8].getRed()
                );

                int green = (int) (
                                core[0][0] * colors[0].getGreen() +
                                core[0][1] * colors[1].getGreen() +
                                core[0][2] * colors[2].getGreen() +
                                core[1][0] * colors[3].getGreen() +
                                core[1][1] * colors[4].getGreen() +
                                core[1][2] * colors[5].getGreen() +
                                core[2][0] * colors[6].getGreen() +
                                core[2][1] * colors[7].getGreen() +
                                core[2][2] * colors[8].getGreen()
                );

                int blue = (int) (
                                core[0][0] * colors[0].getBlue() +
                                core[0][1] * colors[1].getBlue() +
                                core[0][2] * colors[2].getBlue() +
                                core[1][0] * colors[3].getBlue() +
                                core[1][1] * colors[4].getBlue() +
                                core[1][2] * colors[5].getBlue() +
                                core[2][0] * colors[6].getBlue() +
                                core[2][1] * colors[7].getBlue() +
                                core[2][2] * colors[8].getBlue()
                );

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

                Color color = new Color(red, green, blue);
                filteredImage.setRGB(j, i, color.getRGB());
            }
        }
        return filteredImage;
    }
}