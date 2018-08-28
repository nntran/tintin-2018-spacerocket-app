package fr.sqli.tintinspacerocketapp.server;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fr.sqli.tintinspacerocketapp.game.Attemp;
import fr.sqli.tintinspacerocketapp.game.AttempResult;
import fr.sqli.tintinspacerocketapp.game.Gamer;
import fr.sqli.tintinspacerocketapp.game.Score;
import fr.sqli.tintinspacerocketapp.game.SimonGame;
import fr.sqli.tintinspacerocketapp.game.ex.GameFinishedException;
import fr.sqli.tintinspacerocketapp.game.ex.GameNotFinishedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerAlreadyPlayedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerNotFoundException;
import fr.sqli.tintinspacerocketapp.led.LEDColors;
import fr.sqli.tintinspacerocketapp.server.requests.Try;
import fr.sqli.tintinspacerocketapp.server.responses.BadRequest;
import fr.sqli.tintinspacerocketapp.server.responses.Forbidden;
import fr.sqli.tintinspacerocketapp.server.responses.Health;
import fr.sqli.tintinspacerocketapp.server.responses.HttpResponse;
import fr.sqli.tintinspacerocketapp.server.responses.NotFound;
import fr.sqli.tintinspacerocketapp.server.responses.OkAsynch;
import fr.sqli.tintinspacerocketapp.server.responses.OkPlay;
import fr.sqli.tintinspacerocketapp.server.responses.OkScore;
import fr.sqli.tintinspacerocketapp.server.responses.OkStart;
import fr.sqli.tintinspacerocketapp.server.responses.OkSynch;
import fr.sqli.tintinspacerocketapp.server.responses.OkTry;

/**
 * Represents the embedded HTTP server
 */
public final class HttpdServer extends NanoHTTPD {
    private static final String TAG = HttpdServer.class.getSimpleName();

    /**
     * Default HTTP PORT
     */
    private static final int PORT = 8888;

    /**
     * Business code
     */
    private SimonGame simonGame;

    private static final String URI_SIMON_GAME_PREFIX = "/simon/";

    private Moshi moshi;

    private JsonAdapter<Gamer> moshiGamer;

    private JsonAdapter<Try> moshiTry;

    public HttpdServer() throws IOException {
        super(PORT);

        // Declare Json adapter for responses
        moshi = new Moshi.Builder().build();
        moshiGamer = new Moshi.Builder().build().adapter(Gamer.class);
        moshiTry = new Moshi.Builder().build().adapter(Try.class);
        simonGame = new SimonGame();
    }

    /**
     * Process HTTP request
     * @param session request infos
     * @return response infos
     */
    @Override
    public Response serve(IHTTPSession session) {
        Log.d(TAG, "New Request : " + session.getMethod() + " " + session.getUri());

        // Default : code 501
        Response.Status responseStatus = Response.Status.NOT_IMPLEMENTED;
        String responseContent = responseStatus.getDescription();
        String responseMimeType = "text/plain";

        HttpResponse processingResult = null;

        switch (session.getMethod()) {
            case GET:
                processingResult = processGetRequest(session.getUri(), simonGame);

                if (null != processingResult) {
                    responseContent = processingResult.toJson(moshi);
                    responseStatus = processingResult.getStatus();
                    responseMimeType = "application/json";
                }

                // add others methods

                break;
            case POST:
                try {
                    final Map<String, String> map = new HashMap<>();
                    session.parseBody(map);
                    final String jsonBodyContent = map.get("postData");
                    Log.d(TAG, "Request body : " + jsonBodyContent);
                    processingResult = processPostRequest(session.getUri(), session.getParameters(), jsonBodyContent, simonGame);
                } catch (IOException e) {
                    processingResult = new BadRequest("Corps de la requête incorrect");
                    e.printStackTrace();
                } catch (ResponseException e) {
                    processingResult = new BadRequest("Corps de la requête incorrect");
                    e.printStackTrace();
                }

                if (null != processingResult) {
                    responseContent = processingResult.toJson(moshi);
                    responseStatus = processingResult.getStatus();
                    responseMimeType = "application/json";
                }

                break;
            default:
                responseStatus = Response.Status.METHOD_NOT_ALLOWED;
                responseContent = responseStatus.getDescription();
                responseMimeType = "text/plain";
                break;
        }

        return newFixedLengthResponse(responseStatus, responseMimeType,responseContent);
    }

    /**
     * Process HTTP GET request
     * @param uri requested API URI
     * @return response content or null
     */
    protected HttpResponse processGetRequest(final String uri, final SimonGame simonGame) {
        HttpResponse response = null;

        if (uri.equals("/health")) {
            response = new Health();
        } else if (uri.endsWith("/score")) {
            try {
                final Score score = simonGame.getScore(getGamerIdFromUri(uri));
                response = new OkScore(score.score, score.time);
            } catch (GameNotFinishedException gnfe) {
                response = new Forbidden("Partie en cours, score non consultable");
            } catch (GamerNotFoundException gnfe) {
                response = new NotFound("Aucun joueur trouvé");
            } catch (Exception e) {
                response = new BadRequest("Mauvais format de requête (id du joueur non trouvé)");
            }
        }

        return response;
    }

    /**
     * Process HTTP POST request
     * @param uri requested API URI
     * @param parameters
     * @param jsonBodyContent
     * @return response content or null
     */
    protected HttpResponse processPostRequest(final String uri, final Map<String, List<String>> parameters, final String jsonBodyContent, final SimonGame simonGame) throws IOException {
        HttpResponse response = null;

        // TODO refactor constantes
        if (uri.startsWith(URI_SIMON_GAME_PREFIX)) {
            if (uri.contains("/sequence")) {
                response = internalPostSequence(parameters, simonGame);
            } else if (uri.contains("/start")) {
                response = internalPostSimonStart(jsonBodyContent, simonGame);
            } else if (uri.contains("/play")) {
                response = internalPostPlay(uri, simonGame);
            } else if (uri.contains("/try")) {
                response = internalPostTry(uri, jsonBodyContent, simonGame);
            }
        }

        return response;
    }

    @NonNull
    private HttpResponse internalPostPlay(final String uri, final SimonGame simonGame) {
        HttpResponse response;
        try {
            final Gamer gamer = simonGame.playSequence(getGamerIdFromUri(uri));
            response = new OkPlay(gamer.remainingAttemps, convertLEDColorsListToArray(gamer.sequence));
        } catch (GamerNotFoundException gnfe) {
            response = new NotFound("Aucun joueur trouvé");
        } catch (GameFinishedException gfe) {
            response = new Forbidden("La partie est terminée (nombre de tentatives max atteint)");
        } catch (Exception e) {
            response = new BadRequest("Mauvais format de requête (id du joueur non trouvé)");
        }
        return response;
    }

    @NonNull
    private HttpResponse internalPostTry(final String uri, final String jsonBodyContent, final SimonGame simonGame) {
        HttpResponse response;
        try {
            final Attemp attemp = new Attemp();
            final Try aTry = moshiTry.fromJson(jsonBodyContent);

            for (final String ledColorCode : aTry.sequence) {
                attemp.sequence.add(LEDColors.getByCode(ledColorCode));
            }

            attemp.time = aTry.time;

            final AttempResult attempResult = simonGame.trySequence(getGamerIdFromUri(uri), attemp);
            response = new OkTry(attempResult.remainingAttemps, convertLEDColorsListToArray(attempResult.correctSequence), attempResult.result);

        } catch (GamerNotFoundException gnfe) {
            response = new NotFound("Aucun joueur trouvé");
        } catch (GameFinishedException gfe) {
            response = new Forbidden("La partie est terminée (nombre de tentatives max atteint)");
        } catch (Exception e) {
            response = new BadRequest("Mauvais format de requête (id du joueur non trouvé ou contenu incorrect)");
        }
        return response;
    }

    @NonNull
    private HttpResponse internalPostSimonStart(final String jsonBodyContent, final SimonGame simonGame) throws IOException {
        HttpResponse response;
        try {
            final Gamer gamer = simonGame.startNewGame(moshiGamer.fromJson(jsonBodyContent));
            response = new OkStart(gamer.gamerId, gamer.gamerResume);
        } catch (GamerAlreadyPlayedException e) {
            response = new Forbidden("La partie est terminée (nombre de tentatives max atteint)", e.gamerId);
        }
        return response;
    }


    @NonNull
    private String[] convertLEDColorsListToArray(final List<LEDColors> sequence) {
        String[] ledColors = null;
        if (sequence != null) {
            ledColors = new String[sequence.size()];
            for (int i = 0; i < sequence.size(); i++) {
                ledColors[i] = sequence.get(i).code;
            }
        }
        return ledColors;
    }

    private int getGamerIdFromUri(String uri) {
        final String[] uriSplit = uri.split("/");
        return Integer.parseInt(uriSplit[uriSplit.length - 2]);
    }

    @NonNull
    private HttpResponse internalPostSequence(final Map<String, List<String>> parameters, final SimonGame simonGame) {
        HttpResponse response;
        boolean isSynchrone = false;

        if (parameters.get("isSynchrone") != null
                && "true".equals(parameters.get("isSynchrone").get(0))) {
            Log.d("Request", "isSynchrone=true");
            isSynchrone = true;
        }

        if (simonGame.launchRandomSequence(isSynchrone)) {
            response = new Forbidden("Séquence déjà en cours d'affichage !");
        } else {
            if (isSynchrone) {
                response = new OkSynch();
            } else {
                response = new OkAsynch();
            }
        }
        return response;
    }
}
