package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Span {
    private Dot start;
    private Dot end;

    public Span(Dot dotPressed, BufferedImage bufferedImage) {

        int startX = dotPressed.getX();
        int endX = dotPressed.getX();
        int Y = dotPressed.getY();

        while(bufferedImage.getRGB(startX,Y) != Color.BLACK.getRGB()){
            startX--;
        }
        startX+=1;

        while(bufferedImage.getRGB(endX,Y) != Color.BLACK.getRGB()){
            endX++;
        }

        this.start = new Dot(startX, Y);
        this.end = new Dot(endX, Y);
    }

    public Dot getStart() {
        return start;
    }

    public Dot getEnd() {
        return end;
    }
}
