package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class OkStart implements HttpResponse {

    private final int gamerId;
    private boolean gamerResume = false;

    public OkStart(int gamerId, boolean gamerResume) {
        this.gamerId = gamerId;
        this.gamerResume = gamerResume;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkStart.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
