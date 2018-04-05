package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.util.Arrays.sort;

public class Median {
        public BufferedImage go(BufferedImage bufferedImage) {
            BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

            int[][] coordinates = {{-1, -1}, {0, -1}, {1, -1},
                                    {-1, 0}, {0, 0}, {1, 0},
                                    {-1, 1}, {0, 1}, {1, 1}};

            Color[] colors = new Color[9];
            int[] brightness = new int[9];

            for (int i = 1; i < filteredImage.getHeight() - 1; i++) {
                for (int j = 1; j < filteredImage.getWidth() - 1; j++) {
                    for (int y = 0; y < 9; y++) {
                        colors[y] = new Color(bufferedImage.getRGB(j + coordinates[y][0], i + coordinates[y][1]));
                        brightness[y] = (int)(0.299 * colors[y].getRed() + 0.587 * colors[y].getGreen() + 0.114 * colors[y].getBlue());
                    }

                    sort(brightness);

                    int temp = 0;
                    //which color has [4] (median) brightness
                    for (int y = 0; y < 9; y++) {
                        if(brightness[4] == (int)(0.299 * colors[y].getRed() + 0.587 * colors[y].getGreen() + 0.114 * colors[y].getBlue()))
                        temp = y;
                    }

                    filteredImage.setRGB(j, i, colors[temp].getRGB());
                }
            }
            return filteredImage;

        }

}
