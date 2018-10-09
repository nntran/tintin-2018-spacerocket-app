package fr.sqli.tintinspacerocketapp.server.responses;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import fr.sqli.tintinspacerocketapp.game.Gamer;

public abstract class HtmlResponse {

    public abstract String asHtml();


    /**
     *
     * @param gamers
     * @return
     */
    public static HtmlResponse ranking(Gamer[] gamers) {
        return new HtmlResponse() {
            public String asHtml() {

                List<Gamer> gamersList = Arrays.asList(gamers);
                Collections.sort(
                        gamersList, new Comparator<Gamer>() {
                            @Override
                            public int compare(Gamer g1, Gamer g2) {
                                if (g1.score > g2.score) return -1;
                                if (g1.score < g2.score) return 1;
                                if (g1.time < g2.time) return -1;
                                if (g1.time > g2.time) return 1;
                                return 0;
                            }
                        });

                StringBuilder body = new StringBuilder();
                body.append("<table summary=\"Ranking\" cellpadding=\"0\" cellspacing=\"0\">");
	            body.append("<thead><tr><th>Rank</th><th>Player name</th><th>Score</th><th>Time</th></tr></thead>");
                body.append("<tbody>");

                for (int i = 0; i < gamersList.size(); i++) {
                    Gamer player = gamersList.get(i);
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

    public static Gamer[] getGamersTest() {
        Gamer[] gamers = new Gamer[20];

        for (int i = 0; i < 20; i++) {
            gamers[i] = new Gamer();
            gamers[i].gamerFirstname = "Gamer " + i;
            gamers[i].gamerLastname = "Test";
            gamers[i].time = 5000 + (long)(Math.random() * ((120000 - 5000) + 1));
            gamers[i].score = 2 + (int)(Math.random() * ((30 - 2) + 1));
        }

        return gamers;
    }
}
