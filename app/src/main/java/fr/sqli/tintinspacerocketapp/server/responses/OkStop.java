package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.sqli.tintinspacerocketapp.game.Score;

public class OkStop implements HttpResponse {

    private Score score;

    public OkStop(Score score) {
        this.score = score;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkStop.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
