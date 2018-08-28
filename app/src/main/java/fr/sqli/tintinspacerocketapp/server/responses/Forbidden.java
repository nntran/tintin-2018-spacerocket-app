package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Forbidden implements HttpResponse {

    private String message;
    private int gamerId;

    public Forbidden(final String message) {
        this.message = message != null ? message : "";
    }

    public Forbidden(final String message, final int gamerId) {
        this.message = message;
        this.gamerId = gamerId;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(Forbidden.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.FORBIDDEN;
    }
}
