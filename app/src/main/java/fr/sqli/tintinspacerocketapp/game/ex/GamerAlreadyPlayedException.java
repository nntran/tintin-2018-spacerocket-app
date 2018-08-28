package fr.sqli.tintinspacerocketapp.game.ex;

public class GamerAlreadyPlayedException extends Exception {


    public int gamerId;

    public GamerAlreadyPlayedException(final int gamerId) {
        super();
        this.gamerId = gamerId;
    }
}
