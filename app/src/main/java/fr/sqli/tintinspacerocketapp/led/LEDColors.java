package fr.sqli.tintinspacerocketapp.led;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum LEDColors {
    BLUE("2"),
    YELLOW("1"),
    RED("3"),
    GREEN("0");

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

    public static LEDColors getRandomColor() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
