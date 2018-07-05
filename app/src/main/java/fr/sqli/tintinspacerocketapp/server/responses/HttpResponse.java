package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD;

public interface HttpResponse {

    String toJson(final Moshi moshi);

    NanoHTTPD.Response.Status getStatus();
}
