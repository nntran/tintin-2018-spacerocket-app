package fr.sqli.tintinspacerocketapp.server;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fr.sqli.tintinspacerocketapp.game.Gamer;
import fr.sqli.tintinspacerocketapp.game.SimonGame;
import fr.sqli.tintinspacerocketapp.game.ex.GameFinishedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerAlreadyPlayedException;
import fr.sqli.tintinspacerocketapp.game.ex.GamerNotFoundException;
import fr.sqli.tintinspacerocketapp.led.LEDColors;
import fr.sqli.tintinspacerocketapp.server.responses.BadRequest;
import fr.sqli.tintinspacerocketapp.server.responses.Forbidden;
import fr.sqli.tintinspacerocketapp.server.responses.Health;
import fr.sqli.tintinspacerocketapp.server.responses.HttpResponse;
import fr.sqli.tintinspacerocketapp.server.responses.NotFound;
import fr.sqli.tintinspacerocketapp.server.responses.OkAsynch;
import fr.sqli.tintinspacerocketapp.server.responses.OkPlay;
import fr.sqli.tintinspacerocketapp.server.responses.OkStart;
import fr.sqli.tintinspacerocketapp.server.responses.OkSynch;

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

    public HttpdServer() throws IOException {
        super(PORT);

        // Declare Json adapter for responses
        moshi = new Moshi.Builder().build();
        moshiGamer = new Moshi.Builder().build().adapter(Gamer.class);
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

        switch (uri) {
            case "/health":
                response = new Health();
                break;
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

            } else if (uri.contains("/start")) {
                final Gamer gamer;
                try {
                    gamer = simonGame.startNewGame(moshiGamer.fromJson(jsonBodyContent));
                    response = new OkStart(gamer.gamerId);
                } catch (GamerAlreadyPlayedException e) {
                    response = new Forbidden("Ce joueur a déjà joué aujourd'hui");
                }
            } else if (uri.contains("/play")) {
                final String[] uriSplit = uri.split("/");
                try {
                    int gamerId = Integer.parseInt(uriSplit[uriSplit.length - 2]);
                    final Gamer gamer = simonGame.playSequence(gamerId);

                    String[] ledColors = new String[gamer.sequence.size()];
                    for (int i = 0; i < gamer.sequence.size(); i++) {
                        ledColors[i] = gamer.sequence.get(i).code;
                    }

                    response = new OkPlay(gamer.remainingAttemps, ledColors);
                } catch (GamerNotFoundException gnfe) {
                    response = new NotFound("Aucun joueur trouvé");
                } catch (GameFinishedException gfe) {
                    response = new Forbidden("La partie est terminée (nombre de tentatives max atteint)");
                } catch (Exception e) {
                    response = new BadRequest("Mauvais format de requête (id du joueur non trouvé)");
                }
            }
        }

        return response;
    }
}
