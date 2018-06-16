package fr.sqli.tintinspacerocketapp.led;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class LED {
    // Android Things
    private PeripheralManager manager = PeripheralManager.getInstance();

    // Internal implementation
    private Gpio led;

    /**
     * Statut allumée
     */
    private static final boolean ON = true;

    /**
     * Statut éteinte
     */
    private static final boolean OFF = false;

    /**
     * Init une LED à l'état éteinte
     * @param gpioName nom du port GPIO
     * @throws IOException
     */
    public LED(final String gpioName) throws IOException {
        this.led = manager.openGpio(gpioName);
        led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
    }

    /**
     * Inverse le satut de la LED
     * @throws IOException
     */
    public void toggle() throws IOException {
        led.setValue(!led.getValue());
    }

    /**
     * Allume la LED
     * @throws IOException
     */
    public void turnOn() throws IOException {
        if (led.getValue() == OFF) {
            led.setValue(ON);
        }
    }

    /**
     * Eteind la LED
     * @throws IOException
     */
    public void turnOff() throws IOException {
        if (led.getValue() == ON) {
            led.setValue(OFF);
        }
    }

    /**
     * Détruit l'objet de la LED
     * @throws IOException
     */
    public void destroy() throws IOException {
        led.close();
        led = null;
    }
}
