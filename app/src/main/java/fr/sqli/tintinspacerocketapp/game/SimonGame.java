package fr.sqli.tintinspacerocketapp.game;

import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sqli.tintinspacerocketapp.game.ex.GamerAlreadyPlayedException;
import fr.sqli.tintinspacerocketapp.led.LEDColors;
import fr.sqli.tintinspacerocketapp.led.LEDManager;

/**
 * Represents the game
 */
public class SimonGame {

    public static final String TAG = SimonGame.class.getName();
    // TODO implement game
    private LEDManager ledManagerInstance;

    // TODO gestion des journées de jeu
    private Map<Integer, Gamer> gamerMap = new HashMap<>();

    private final Integer DEMO_GAMER_ID = -1;

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

    public final Gamer startNewGame(final Gamer gamer) throws GamerAlreadyPlayedException {
        if (gamer.gamerFirstname != null && gamer.gamerEmail.equalsIgnoreCase("Démo")) {
            gamerMap.remove(DEMO_GAMER_ID);
            gamer.gamerId = DEMO_GAMER_ID;
            gamer.isDemo = true;
        } else {
            if (gamerMap.values().contains(gamer)) {
                throw new GamerAlreadyPlayedException();
            } else {
                gamer.gamerId = generateGamerId(gamerMap.keySet());
            }
        }
        // générer la première séquence
        gamerMap.put(gamer.gamerId, gamer);
        Log.d(TAG, "Initialisation d'une partie pour : " + gamer);
        return gamer;
    }

    protected static int generateGamerId(final Set<Integer> gamersIdsSet) {
        if (gamersIdsSet == null || gamersIdsSet.size() == 0) {
            return 0;
        } else {
            // On trie l'ensemble
            final List<Integer> sortedIdList = new LinkedList<>(gamersIdsSet);
            Collections.sort(sortedIdList);
            return sortedIdList.get(sortedIdList.size() - 1) + 1;
        }
    }

}
