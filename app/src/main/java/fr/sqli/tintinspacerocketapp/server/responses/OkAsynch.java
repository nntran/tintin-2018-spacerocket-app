package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OkAsynch implements HttpResponse {

    public final String message = "OK Accepted";

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkAsynch.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.ACCEPTED;
    }
}
