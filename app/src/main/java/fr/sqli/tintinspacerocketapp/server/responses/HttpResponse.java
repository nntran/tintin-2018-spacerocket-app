package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

public interface HttpResponse {

    String toJson(final Moshi moshi);
}
