package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD;

public class OkScore implements HttpResponse {
    private final int score;
    private final long time;

    public OkScore(int score, long time) {
        this.score = score;
        this.time = time;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkScore.class).toJson(this);
    }

    @Override
    public NanoHTTPD.Response.Status getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }
}
