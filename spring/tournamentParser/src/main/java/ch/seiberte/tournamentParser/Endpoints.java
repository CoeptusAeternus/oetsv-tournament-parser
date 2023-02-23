package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.proxys.ListProxy;
import ch.seiberte.tournamentParser.proxys.TournamentProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Endpoints {

    ITournamentReader tr = new TournamentProxy();
    IKalenderReader kr = new ListProxy();
    private static Logger logger = LoggerFactory.getLogger(Endpoints.class);

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String mainPage() {
        logger.info("request at /");
        return "<p>api at <a href=/oetsv_kalender title=api>/oetsv_kalender</a></p>";
    }

    @RequestMapping(value = "/oetsv_kalender", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String linkTreePage() {
        logger.info("request at /oetsv_kalender");
        return """
                Overview List for all current Tournaments under <a href=/list>/list</a><br>
                Details for single tournament with /&lt;id&gt;
                """;
    }

    List

    @Scheduled(fixedRate = 1000)//TODO update to 3600000 (once per hour)
    public static void updateCollectionAndMap() {
        IKalenderReader kr = new OetsvCalendarDataParser();
        ITournamentReader tr = new OetsvTournamentDataParser();

        shortyCollection = kr.getTournaments();

        for (ShortTournament st : shortyCollection) {
            if (!longMap.containsKey(st.getId().toString())) {
                LongTournament lt = tr.readTournament(st.getId());
                longMap.put(lt.getId().toString(),lt);
            }
        }
    }
}
