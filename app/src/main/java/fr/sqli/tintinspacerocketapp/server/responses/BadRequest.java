package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class BadRequest implements HttpResponse {

    private String message;

    public BadRequest(final String message) {
        this.message = message != null ? message : "";
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(BadRequest.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.BAD_REQUEST;
    }
}
