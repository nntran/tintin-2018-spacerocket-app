package fr.sqli.tintinspacerocketapp.led;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum LEDColors {
    BLUE,
    YELLOW,
    RED,
    GREEN;

    private static final List<LEDColors> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static LEDColors getRandomColor() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
