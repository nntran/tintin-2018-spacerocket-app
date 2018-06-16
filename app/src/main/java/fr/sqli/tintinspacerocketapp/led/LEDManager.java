package fr.sqli.tintinspacerocketapp.led;

import com.google.android.things.pio.Gpio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton qui permet de gérer les LEDs
 */
public final class LEDManager {

    // Instance interne du Singleton
    private static LEDManager instance;

    // LED bleue branchée sur le GPIO n°7 (BCM4)
    public LED blueLED;

    // LED jaune branchée sur le GPIO n°11 (BCM17)
    public LED yellowLED;

    // LED rouge branchée sur le GPIO n°15 (BCM22)
    public LED redLED;

    // LED verte branchée sur le GPIO n°17 (BCM27)
    public LED greenLED;

    // Liste de toutes les LED crées
    private List<LED> ledList;

    /**
     * Init les LEDs
     * @throws IOException
     */
    private LEDManager() throws IOException {
        initLEDs();
    }

    /**
     * Crée toutes les LEDs
     * @throws IOException
     */
    private void initLEDs() throws IOException {
        ledList = new ArrayList<>();

        blueLED = new LED("BCM4");
        ledList.add(blueLED);

        yellowLED = new LED("BCM17");
        ledList.add(yellowLED);

        redLED = new LED("BCM27");
        ledList.add(redLED);

        greenLED = new LED("BCM23");
        ledList.add(greenLED);

        // TODO autres LEDs (cf. OneNote)
    }

    /**
     * Retourne l'instance du singleton. La crée si elle n'existe pas;
     * @return instance
     * @throws IOException
     */
    public static LEDManager getInstance() throws IOException {
        if (instance == null) {
            instance = new LEDManager();
        }
        return instance;
    }

    /**
     * Allume toutes les LEDs
     * @throws IOException
     */
    public void turnOnAllLEDs() throws IOException {
        for (LED led : ledList) {
            led.turnOn();
        }
    }

    /**
     * Eteind toutes les LEDs
     * @throws IOException
     */
    public void turnOffAllLEDs() throws IOException {
        for (LED led : ledList) {
            led.turnOff();
        }
    }

    /**
     * Inverse le statut de toutes les LEDs
     * @throws IOException
     */
    public void toggleAllLEDs() throws IOException {
        for (LED led : ledList) {
            led.toggle();
        }
    }

    /**
     * Détruit toutes les LEDs
     * @throws IOException
     */
    public void destroy() throws IOException {
        for (LED led : ledList) {
            led.turnOff();
            led.destroy();
        }
    }
}
