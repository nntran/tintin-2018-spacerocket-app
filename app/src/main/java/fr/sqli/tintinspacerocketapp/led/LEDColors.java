package fr.sqli.tintinspacerocketapp.led;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public enum LEDColors {
    BLUE(Codes.BLUE),
    YELLOW(Codes.YELLOW),
    RED(Codes.RED),
    GREEN(Codes.GREEN);

    private static final List<LEDColors> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    public final String code;


    LEDColors(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public static List<LEDColors> getRandomColors(final int size) {
        final List<LEDColors> ledColors = new LinkedList<>();
        ledColors.add(getRandomColor());
        for (int i = 1; i < size; i++) {
            ledColors.add(getRandomColor(ledColors.get(i-1)));
        }

        return ledColors;
    }

    public static LEDColors getRandomColor(final LEDColors excludedColor) {
        final LEDColors generatedColor = getRandomColor();
        if (generatedColor.equals(excludedColor)) {
            return getRandomColor(excludedColor);
        }
        return generatedColor;
    }

    public static LEDColors getRandomColor() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static LEDColors getByCode(final String code) {
        LEDColors color = null;
        switch(code) {
            case Codes.BLUE:
                color = BLUE;
                break;
            case Codes.YELLOW:
                color = YELLOW;
                break;
            case Codes.RED:
                color = RED;
                break;
            case Codes.GREEN:
                color = GREEN;
                break;
        }
        return color;
    }

    private static class Codes {
        public static final String GREEN = "0";
        public static final String RED = "3";
        public static final String YELLOW = "1";
        public static final String BLUE = "2";
    }
}
