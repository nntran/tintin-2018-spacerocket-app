package fr.sqli.tintinspacerocketapp.game;

import java.io.IOException;

import fr.sqli.tintinspacerocketapp.led.LEDColors;
import fr.sqli.tintinspacerocketapp.led.LEDManager;

/**
 * Represents the game
 */
public class SimonGame {

    // TODO implement game
    private LEDManager ledManagerInstance;

    public SimonGame() throws IOException {
        this.ledManagerInstance = LEDManager.getInstance();
    }

    /**
     * Lance une séquence aléatoire de 5 couleurs
     * @param isSynchrone
     * @return vrai si une séquence est déjà en cours, false sinon
     */
    public final boolean launchRandomSequence(final boolean isSynchrone) {
        LEDColors[] ledColors = {
                LEDColors.getRandomColor(),
                LEDColors.getRandomColor(),
                LEDColors.getRandomColor(),
                LEDColors.getRandomColor(),
                LEDColors.getRandomColor()
        };
        return ledManagerInstance.launchSequence(ledColors, isSynchrone);
    }
}
