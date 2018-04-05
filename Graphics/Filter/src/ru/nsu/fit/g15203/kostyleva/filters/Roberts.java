package ru.nsu.fit.g15203.kostyleva.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Roberts {
    public BufferedImage go(BufferedImage bufferedImage) {
        BufferedImage filteredImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        int[][] coordinates = {{0, 0}, {1, 1},
                               {1, 0}, {0, -1}};

        Color[] colors = new Color[4];

        for (int i = 1; i < filteredImage.getHeight() - 1; i++) {
            for (int j = 0; j < filteredImage.getWidth() - 1; j++) {
                for (int y = 0; y < 4; y++)
                    colors[y] = new Color(bufferedImage.getRGB(j + coordinates[y][0], i + coordinates[y][1]));

                int r1 = colors[0].getRed() - colors[1].getRed();
                int r2 = colors[2].getRed() - colors[3].getRed();
                int res = (int)Math.sqrt(r2 * r2 + r1 * r1);

                if (res < 0)
                    res = 0;
                if (res > 255)
                    res = 255;

                Color color = new Color(res, res, res);
                filteredImage.setRGB(j, i, color.getRGB());
            }
        }
        return filteredImage;
    }
}