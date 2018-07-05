package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OkSynch implements HttpResponse {

    public final String message = "OK";

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkSynch.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
