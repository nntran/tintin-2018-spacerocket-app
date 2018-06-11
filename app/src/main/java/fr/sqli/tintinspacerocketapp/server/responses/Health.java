package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

public final class Health implements HttpResponse {

    public final String status = "alive";

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(Health.class).toJson(this);
    }
}
