package fr.sqli.tintinspacerocketapp.server.responses;

import com.squareup.moshi.Moshi;

import fi.iki.elonen.NanoHTTPD.Response.Status;
import fr.sqli.tintinspacerocketapp.led.LEDColors;

public class OkPlay implements HttpResponse {

    private final int remainingAttempts;
    private final LEDColors[] ledColors;

    public OkPlay(int remainingAttempts, LEDColors[] ledColors) {
        this.remainingAttempts = remainingAttempts;
        this.ledColors = ledColors;
    }

    @Override
    public String toJson(final Moshi moshi) {
        return moshi.adapter(OkPlay.class).toJson(this);
    }

    @Override
    public Status getStatus() {
        return Status.CREATED;
    }
}
