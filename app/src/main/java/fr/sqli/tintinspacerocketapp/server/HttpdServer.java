package fr.sqli.tintinspacerocketapp.server;

import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD;
import fr.sqli.tintinspacerocketapp.game.SimonGame;
import fr.sqli.tintinspacerocketapp.server.responses.Health;
import fr.sqli.tintinspacerocketapp.server.responses.HttpResponse;

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

    private Moshi moshi;

    public HttpdServer() {
        super(PORT);

        // Declare Json adapter for responses
        moshi = new Moshi.Builder().build();
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
                processingResult = processGetRequest(session.getUri());

                if (null != processingResult) {
                    responseContent = processingResult.toJson(moshi);
                    responseStatus = Response.Status.OK;
                    responseMimeType = "application/json";
                }

                // add others methods

                break;
            case POST:
                processingResult = processPostRequest(session.getUri(), simonGame);

                if (null != processingResult) {
                    responseContent = processingResult.toJson(moshi);
                    responseStatus = Response.Status.CREATED;
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
    protected HttpResponse processGetRequest(final String uri) {
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
     * @return response content or null
     */
    protected HttpResponse processPostRequest(final String uri, final SimonGame simonGame) {
        HttpResponse response = null;

        // TODO implement the game with GameSimon.java

        switch (uri) {
            default:
                break;
        }

        return response;
    }
}
