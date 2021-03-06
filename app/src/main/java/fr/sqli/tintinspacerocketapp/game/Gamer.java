package fr.sqli.tintinspacerocketapp.game;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.sqli.tintinspacerocketapp.led.LEDColors;

public class Gamer {
    public int gamerId;
    public String gamerFirstname;
    public String gamerLastname;
    public String gamerEmail;
    public String gamerCompany;
    public String gamerTwitter;
    public boolean gamerContact;
    public boolean gamerResume = false;
    public boolean isDemo = false;
    public final List<LEDColors> sequence = new LinkedList<>();
    public int score;
    public long time;
    public int remainingAttemps = 3;
    //public Date startDateTime = new Date();
    public String startDateTime;

    public Gamer() {

    }

    @Override
    public boolean equals(Object obj) {
        final Gamer gamer = (Gamer) obj;
        return this.gamerFirstname.trim().equalsIgnoreCase(gamer.gamerFirstname.trim())
                && this.gamerLastname.trim().equalsIgnoreCase(gamer.gamerLastname.trim())
                && this.gamerEmail.trim().equalsIgnoreCase(gamer.gamerEmail.trim());
    }

    @Override
    public String toString() {
        return "Gamer{" +
                "gamerId=" + gamerId +
                ", gamerFirstname='" + gamerFirstname + '\'' +
                ", gamerLastname='" + gamerLastname + '\'' +
                ", gamerEmail='" + gamerEmail + '\'' +
                ", gamerCompany='" + gamerCompany + '\'' +
                ", gamerTwitter='" + gamerTwitter + '\'' +
                ", gamerContact=" + gamerContact +
                ", isDemo=" + isDemo +
                ", sequence=" + sequence +
                ", score=" + score +
                ", time=" + time +
                ", startDateTime=" + startDateTime +
                '}';
    }
}
