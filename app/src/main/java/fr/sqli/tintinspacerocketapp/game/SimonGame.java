package fr.sqli.tintinspacerocketapp.game;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import fr.sqli.tintinspacerocketapp.game.ex.GameFinishedException;
import fr.sqli.tintinspacerocketapp.game.ex.GameNotFinishedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerAlreadyPlayedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerNotFoundException;
import fr.sqli.tintinspacerocketapp.led.LEDColors;
import fr.sqli.tintinspacerocketapp.led.LEDManager;

/**
 * Represents the game
 */
public class SimonGame {

    public static final String TAG = SimonGame.class.getName();

    private LEDManager ledManagerInstance;

    // Map des journées de jeu par jour
    private Map<Integer, Gamer> gamerMap;

    private final Integer DEMO_GAMER_ID = -1;

    // The current Simon instance (singleton)
    // only one instance game for all gamers
    private static SimonGame currentSimonGame = null;

    /**
     *
     * @throws IOException
     */
    private SimonGame() throws IOException {

        ledManagerInstance = LEDManager.getInstance();

        // Chargement des joueurs de la journée
        gamerMap = StorageHelper.read(new Date());
    }

    /**
     * Return the current instance of the Simon Game
     * @return
     */
    public static SimonGame getInstance() throws IOException {
        if (currentSimonGame == null) {
            currentSimonGame = new SimonGame();
        }
        return currentSimonGame;
    }

    /**
     * Retourner la liste des joueurs triés par son score + temps
     * @return
     */
    public List<Gamer> getSortGamersList() {

        Gamer gamers[] = gamerMap.values().toArray(new Gamer[gamerMap.size()]);
        List<Gamer> gamersList = Arrays.asList(gamers);

        Collections.sort(
                gamersList, new Comparator<Gamer>() {
            @Override
            public int compare(Gamer g1, Gamer g2) {
                if (g1.score > g2.score) return 1;
                if (g1.score < g2.score) return -1;
                if (g1.time < g2.time) return 1;
                if (g1.time > g2.time) return -1;
                return 0;
            }
        });

        return gamersList;
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

    /**
     * Start new game
     * @param gamer
     * @return
     * @throws GamerAlreadyPlayedException
     */
    public final Gamer startNewGame(final Gamer gamer) throws GamerAlreadyPlayedException {
        Gamer newGamer = gamer;
        if (gamer.gamerFirstname != null && gamer.gamerEmail.equalsIgnoreCase("Démo")) {
            gamerMap.remove(DEMO_GAMER_ID);
            gamer.gamerId = DEMO_GAMER_ID;
            gamer.isDemo = true;
            gamerMap.put(gamer.gamerId, gamer);
            Log.d(TAG, "Initialisation d'une partie démo");
        } else {
            final Gamer possibleGamer = findGamer(gamer, gamerMap);
            if (possibleGamer != null) {
                if (possibleGamer.remainingAttemps > 0) {
                    possibleGamer.gamerResume = true;
                    newGamer = possibleGamer;
                    Log.d(TAG, "Reprise de la partie pour : " + newGamer);
                } else {
                    newGamer = possibleGamer;
                    Log.d(TAG, "Ce joueur a déjà joué : " + newGamer);
                    throw new GamerAlreadyPlayedException(possibleGamer.gamerId);
                }
            } else {
                gamer.gamerId = generateGamerId(gamerMap.keySet());
                gamerMap.put(gamer.gamerId, gamer);
                Log.d(TAG, "Initialisation d'une partie pour : " + gamer);
            }
        }

        return newGamer;
    }

    public Gamer playSequence(int gamerId) throws GamerNotFoundException, GameFinishedException {
        final Gamer gamer = getGamerAndCheckGame(gamerId);
        if (!gamer.gamerResume) {
            Log.d(TAG, "Génération prochaine séquence");
            if (gamer.sequence.size() == 0) {
                // début de la partie
                gamer.sequence.addAll(LEDColors.getRandomColors(3));
            } else {
                // suite de la partie
                gamer.sequence.add(LEDColors.getRandomColor(gamer.sequence.get(gamer.sequence.size()-1)));
            }
        } else {
            // reprise d'une partie, on rejoue la dernière séquence
            Log.d(TAG, "Reprise d'une partie, pas de génération prochaine séquence");
            gamer.gamerResume = false;
        }
        ledManagerInstance.launchSequence(gamer.sequence.toArray(new LEDColors[gamer.sequence.size()]), true);
        return gamer;
    }

    public AttempResult trySequence(int gamerId, final Attemp attemp) throws GameFinishedException, GamerNotFoundException {
        final Gamer gamer = getGamerAndCheckGame(gamerId);
        boolean isAttempInError = false;
        for (int i=0; i < gamer.sequence.size() && !isAttempInError; i++) {
            if (!gamer.sequence.get(i).equals(attemp.sequence.get(i))) {
                isAttempInError = true;
            }
        }

        final AttempResult attempResult = new AttempResult();

        gamer.time += attemp.time;

        if (isAttempInError) {
            // Séquence incorrecte
            gamer.remainingAttemps--;

            if (gamer.remainingAttemps == 0) {
                // Nombre d'essais max atteint
                // Mise à jour du score finale
                internalUpdateScore(gamer);
                ledManagerInstance.startWelcomeSequence();
                Log.d(TAG, "Partie terminée avec un score de " + gamer.score + " et une durée totale de " + gamer.time);

                // Sauvegarde le socore ainsi que les infos du joueur
                try {
                    StorageHelper.write(gamer);
                }
                catch (IOException ex) {
                    Log.e(TAG, "Erreur de sauvegarde du joueur: " + gamer, ex);
                }

                throw new GameFinishedException();
            } else {
                attempResult.result = false;
                attempResult.remainingAttemps = gamer.remainingAttemps;
                attempResult.correctSequence = gamer.sequence;
                Log.d(TAG, "Tentative manquée");
            }
        } else {
            internalUpdateScore(gamer);
            attempResult.result = true;
            Log.d(TAG, "Tentative réussie");
        }

        return attempResult;
    }

    private void internalUpdateScore(Gamer gamer) {
        // Séquence correcte
        // Calcul du score
        if (gamer.sequence.size() <= 3) {
            gamer.score = 0;
        } else {
            // Mise à jour du score
            // -1 car la séquence enregistrée représente la dernière séquence tentée donc forcément loupé
            gamer.score = gamer.sequence.size() - 1;
        }
    }


    public Score getScore(int gamerId) throws GamerNotFoundException, GameNotFinishedException {
        final Score score = new Score();
        try {
            final Gamer gamer = getGamerAndCheckGame(gamerId);
            if (gamer.remainingAttemps > 0) {
                throw new GameNotFinishedException();
            }
            score.score = gamer.score;
            score.time = gamer.time;
        } catch (GameFinishedException e) {
            final Gamer gamer = gamerMap.get(gamerId);
            score.score = gamer.score;
            score.time = gamer.time;
        }
        return score;
    }

    private Gamer getGamerAndCheckGame(int gamerId) throws GamerNotFoundException, GameFinishedException {
        final Gamer gamer = gamerMap.get(gamerId);
        if (gamer == null) {
            Log.d(TAG, "Joueur non trouvé");
            throw new GamerNotFoundException();
        } else if (gamer.remainingAttemps == 0) {
            Log.d(TAG, "Partie déjà terminée");
            throw new GameFinishedException();
        }
        return gamer;
    }

    private Gamer findGamer(final Gamer gamer, final Map<Integer, Gamer> gamers) {
        Gamer foundGamer = null;
        for (final Gamer existingGamer : gamers.values()) {
            if (existingGamer.equals(gamer)) {
                foundGamer = existingGamer;
                Log.d(TAG, "Joueur existant trouvé : " + foundGamer);
                break;
            }
        }

        return foundGamer;
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
