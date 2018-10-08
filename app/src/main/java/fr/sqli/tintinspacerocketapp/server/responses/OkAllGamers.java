package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import fr.sqli.tintinspacerocketapp.game.Gamer;

public class OkAllGamers implements HttpResponse {

    private List<Gamer> scores;

    public OkAllGamers(List<Gamer> scores) {
        this.scores = scores;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkAllGamers.class).toJson(this);
    }

    @Override
    public NanoHTTPD.Response.Status getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }
}
