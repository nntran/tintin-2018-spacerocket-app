package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD;

public class OkTry implements HttpResponse {
    private final int remainingAttempts;
    private final String[] sequence;
    private final boolean result;

    public OkTry(final int remainingAttempts, final String[] sequence, final boolean result) {
        this.remainingAttempts = remainingAttempts;
        this.sequence = sequence;
        this.result = result;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkTry.class).toJson(this);
    }

    @Override
    public NanoHTTPD.Response.Status getStatus() {
        return NanoHTTPD.Response.Status.CREATED;
    }
}
