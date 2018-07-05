package fr.sqli.tintinspacerocketapp.server;

import android.util.Log;

import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fr.sqli.tintinspacerocketapp.game.SimonGame;
import fr.sqli.tintinspacerocketapp.server.responses.Forbidden;
import fr.sqli.tintinspacerocketapp.server.responses.Health;
import fr.sqli.tintinspacerocketapp.server.responses.HttpResponse;
import fr.sqli.tintinspacerocketapp.server.responses.OkAsynch;
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

    public HttpdServer() throws IOException {
        super(PORT);

        // Declare Json adapter for responses
        moshi = new Moshi.Builder().build();
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
                processingResult = processPostRequest(session.getUri(), session.getParameters(), simonGame);

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
     * @return response content or null
     */
    protected HttpResponse processPostRequest(final String uri, final Map<String, List<String>> parameters, final SimonGame simonGame) {
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

            }
        }

        return response;
    }
}
