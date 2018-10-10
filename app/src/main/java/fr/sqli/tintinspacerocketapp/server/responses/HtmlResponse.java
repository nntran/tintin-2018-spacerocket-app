package fr.sqli.tintinspacerocketapp.server.responses;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import fr.sqli.tintinspacerocketapp.game.Gamer;
import fr.sqli.tintinspacerocketapp.game.SimonGame;

public abstract class HtmlResponse {

    public abstract String asHtml();


    /**
     *
     * @param players
     * @return
     */
    public static HtmlResponse ranking(Gamer[] players) {
        return new HtmlResponse() {
            public String asHtml() {

                List<Gamer> playersList = SimonGame.sortPlayersByScore(players);

                StringBuilder body = new StringBuilder();
                body.append("<table summary=\"Ranking\" cellpadding=\"0\" cellspacing=\"0\">");
	            body.append("<thead><tr><th>Rank</th><th>Player name</th><th>Score</th><th>Time</th></tr></thead>");
                body.append("<tbody>");

                for (int i = 0; i < playersList.size(); i++) {
                    Gamer player = playersList.get(i);
                    body.append("<tr>");
                    body.append("<td>").append(i + 1).append("</td>");
                    body.append("<td>").append(player.gamerFirstname + " " + player.gamerLastname).append("</td>");
                    body.append("<td><b>").append(player.score).append("</b></td>");

                    String strTime = String.format(Locale.FRANCE, "%01d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(player.time),
                            TimeUnit.MILLISECONDS.toSeconds(player.time) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.time)));

                    body.append("<td><i>").append(strTime).append("</i></td>");
                    body.append("</tr>");
                }
                body.append("</tbody></table>");

                return HtmlBuilder.html().title("Ranking")
                        .defaultTableStyle()
                        .body(body.toString()).build();
            }
        };
    }
}
