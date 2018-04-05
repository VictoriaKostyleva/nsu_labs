package ru.nsu.fit.g15203.kostyleva;

import ru.nsu.fit.g15203.kostyleva.dots.Dot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {
    private File file;
    private BufferedReader in;
    private int width, height;
    private int lineThickness;
    private int radius;
    public Dot[] cells;

    FileParser(File file) throws FileParserException {
        this.file = file;
        if (!file.canRead()) {
            throw new FileParserException("Can not read the file");
        }
    }

    public void parse() throws FileParserException {
        try {
            in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            int[] buf;

            buf = getDataFromFile(2);
            width = buf[0];
            height = buf[1];

            buf = getDataFromFile(1);
            lineThickness = buf[0];

            buf = getDataFromFile(1);
            radius = buf[0];

            buf = getDataFromFile(1);
            int cellsCount = buf[0];

            cells = new Dot[cellsCount];

            for (int i = 0; i < cellsCount; i++) {
                buf = getDataFromFile(2);
                cells[i] = new Dot(buf[1], buf[0]);
            }

            in.close();

        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new FileParserException("Number format exception: " + e.getMessage());
        }
    }

    private int[] getDataFromFile(int count) throws IOException, FileParserException {
        String s;
        if ((s = in.readLine()) == null) {
            throw new FileParserException("File is over");
        }

        int[] buf = parseTheLine(s);

//        if (buf.length != count)
//            throw new FileParserException("Error in parsing");


        while(buf == null) {
            s = in.readLine();

            if (s == null) {
                throw new FileParserException("File is over");
            }

            buf = parseTheLine(s);
        }
//            throw new FileParserException("Error in parsing");

        return buf;
    }

    private int[] parseTheLine(String s) {
        int comment = s.indexOf("//");
        if (comment != -1) {
            s = s.substring(0, comment);
        }

        s = removeTabulation(s);

        String[] data = s.split(" ");

        if(data[0].equals(""))
            return null;

        int[] numbers = new int[data.length];
        int i = 0;

        for (String number: data) {
            numbers[i] = Integer.parseInt(number);
            i++;
        }
        return numbers;
    }

    public static String removeTabulation(String str) {
        StringBuilder result = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String cc = String.valueOf(c);

            if (!cc.equals("\t")) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRadius() {
        return radius;
    }

    public int getLineThickness() {
        return lineThickness;
    }
}