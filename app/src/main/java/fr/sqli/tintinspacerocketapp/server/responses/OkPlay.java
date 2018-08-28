package fr.sqli.tintinspacerocketapp.server.responses;

import android.util.Log;

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
        String toJson = moshi.adapter(OkPlay.class).toJson(this);
        Log.d(getClass().getName(), "Response body : " + toJson);
        return toJson;
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
