package fr.sqli.tintinspacerocketapp.game;

import java.util.Arrays;

import fr.sqli.tintinspacerocketapp.led.LEDColors;

public class Gamer {
    public int gamerId;
    public String gamerFirstname;
    public String gamerLastname;
    public String gamerEmail;
    public String gamerCompany;
    public String gamerTwitter;
    public boolean gamerGenderMale;
    public boolean isDemo = false;
    public LEDColors[] sequence;
    public int score;
    public int time;

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
                ", gamerGenderMale=" + gamerGenderMale +
                ", isDemo=" + isDemo +
                ", sequence=" + Arrays.toString(sequence) +
                ", score=" + score +
                ", time=" + time +
                '}';
    }
}
