package fr.sqli.tintinspacerocketapp.game.json;

/**
 * Cette classe représente le contrat d'interface entre le client et le serveur
 */
public class GamerJson {
    int gamerId;
    String gamerFirstname;
    String gamerLastname;
    String gamerEmail;
    String gamerCompany;
    String gamerTwitter;
    boolean gamerContact;
    int score;
    long time;

    // Date de participation au format dd/MM/yyyy
    String startDate;

    // Heure participation au format hh:MM:ss
    String startTime;


    public int getGamerId() {
        return gamerId;
    }

    public void setGamerId(int gamerId) {
        this.gamerId = gamerId;
    }

    public String getGamerFirstname() {
        return gamerFirstname;
    }

    public void setGamerFirstname(String gamerFirstname) {
        this.gamerFirstname = gamerFirstname;
    }

    public String getGamerLastname() {
        return gamerLastname;
    }

    public void setGamerLastname(String gamerLastname) {
        this.gamerLastname = gamerLastname;
    }

    public String getGamerEmail() {
        return gamerEmail;
    }

    public void setGamerEmail(String gamerEmail) {
        this.gamerEmail = gamerEmail;
    }

    public String getGamerCompany() {
        return gamerCompany;
    }

    public void setGamerCompany(String gamerCompany) {
        this.gamerCompany = gamerCompany;
    }

    public String getGamerTwitter() {
        return gamerTwitter;
    }

    public void setGamerTwitter(String gamerTwitter) {
        this.gamerTwitter = gamerTwitter;
    }

    public boolean isGamerContact() {
        return gamerContact;
    }

    public void setGamerContact(boolean gamerContact) {
        this.gamerContact = gamerContact;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
