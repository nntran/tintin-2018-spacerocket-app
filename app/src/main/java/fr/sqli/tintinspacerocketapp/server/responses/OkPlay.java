package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OkPlay implements HttpResponse {

    private final int remainingAttempts;
    private final String[] sequence;

    public OkPlay(final int remainingAttempts, final String[] sequence) {
        this.remainingAttempts = remainingAttempts;
        this.sequence = sequence;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkPlay.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
