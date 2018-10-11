package fr.sqli.tintinspacerocketapp.led;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton qui permet de gérer les LEDs
 */
public final class LEDManager {

    private static final String TAG = LEDManager.class.getSimpleName();

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
    private Map<LEDColors, LED> ledMap;

    // Indique si une séquence est en cours
    private boolean isSequenceRuning = false;

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
        ledMap = new HashMap<>();

        blueLED = new LED("BCM4");
        ledMap.put(LEDColors.BLUE, blueLED);

        yellowLED = new LED("BCM17");
        ledMap.put(LEDColors.YELLOW, yellowLED);

        redLED = new LED("BCM22");
        ledMap.put(LEDColors.RED, redLED);

        greenLED = new LED("BCM27");
        ledMap.put(LEDColors.GREEN, greenLED);
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
        for (LED led : ledMap.values()) {
            led.turnOn();
        }
    }

    /**
     * Eteind toutes les LEDs
     * @throws IOException
     */
    public void turnOffAllLEDs() throws IOException {
        for (LED led : ledMap.values()) {
            led.turnOff();
        }
    }

    /**
     * Inverse le statut de toutes les LEDs
     * @throws IOException
     */
    public void toggleAllLEDs() throws IOException {
        for (LED led : ledMap.values()) {
            led.toggle();
        }
    }

    /**
     * Lance une séquence d'affichage de LEDs
     * @param ledColors liste des LEDs de la séquence
     * @param isSynchrone indique si le traitement doit être asynchrone ou non
     * @return vrai si une séquence est déjà en cours, false sinon
     */
    public boolean launchSequence(final LEDColors[] ledColors, boolean isSynchrone) {
        Log.d(TAG,"Lancement de la séquence " + ledColors);
        if (!isSequenceRuning) {
            if (isSynchrone) {
                isSequenceRuning = true;
                internalLaunchSequence(ledColors);
                isSequenceRuning = false;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        internalLaunchSequence(ledColors);
                    }
                }).start();
            }
        }
        return isSequenceRuning;
    }

    private void internalLaunchSequence(LEDColors[] ledColors) {
        try {
            isSequenceRuning = true;

            // On éteind toutes les LEDs avant de lancer la séquence
            turnOffAllLEDs();
            Thread.currentThread().sleep(1000);
            int timingSequence = 350;

            for (int i=0; i < ledColors.length; i++) {
                Thread.currentThread().sleep(timingSequence);
                ledMap.get(ledColors[i]).turnOn();
                Thread.currentThread().sleep(timingSequence);
                ledMap.get(ledColors[i]).turnOff();
            }

            isSequenceRuning = false;
        } catch (IOException io) {
            Log.e(TAG, "", io);
        } catch (InterruptedException ie) {
            Log.e(TAG, "", ie);
        }
    }

    /**
     * Lance la séquence de démarrage de manière asynchrone
     */
    public boolean startWelcomeSequence() {
        // A faire obligatoirement quand on fait une séquence aussi longue
        if (!isSequenceRuning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isSequenceRuning = true;

                        int waitingTime = 300;

                        for (int i = 0; i < 10; i++) {
                            toggleAllLEDs();
                            Thread.currentThread().sleep(waitingTime);
                        }
                        turnOnAllLEDs();

                        isSequenceRuning = false;
                    } catch (IOException io) {
                        Log.e(TAG, "", io);
                    } catch (InterruptedException ie) {
                        Log.e(TAG, "", ie);
                    }
                }
            }).start();
        }

        return isSequenceRuning;
    }

    /**
     * Détruit toutes les LEDs
     * @throws IOException
     */
    public void destroy() throws IOException {
        for (LED led : ledMap.values()) {
            led.turnOff();
            led.destroy();
        }
    }
}
