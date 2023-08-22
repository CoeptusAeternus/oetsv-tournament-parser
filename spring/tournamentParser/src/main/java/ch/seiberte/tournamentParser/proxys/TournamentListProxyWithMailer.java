package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.ITournamentMailer;
import ch.seiberte.tournamentParser.NewTournamentService;
import ch.seiberte.tournamentParser.OetsvCalendarDataParser;
import ch.seiberte.tournamentParser.data.ShortTournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TournamentListProxyWithMailer implements IKalenderProxy {
    private static Logger logger = LoggerFactory.getLogger(TournamentListProxyWithMailer.class);

    private List<ShortTournament> cachedTournaments;
    private IKalenderReader baseService;
    private ITournamentMailer mailer;

    public TournamentListProxyWithMailer() {
        this.cachedTournaments = new ArrayList<>();
        this.baseService = new OetsvCalendarDataParser();
        this.mailer = new  NewTournamentService();
    }

    @Override
    public List<ShortTournament> getTournaments() {
        return cachedTournaments;
    }

    @Override
    public void updateTournaments() {
        List<ShortTournament> currentTournaments = baseService.getTournaments();
        if(!cachedTournaments.isEmpty()) //to prevent when reloading all tournaments
            for(ShortTournament st : currentTournaments){
                if(!cachedTournaments.contains(st)) {
                    logger.info("Sending Mail with new Tournament: "+st.getBezeichnung());
                    mailer.sendMail(st, "jaksei.lol@gmail.com");
                    mailer.sendMail(st, "sportwart@schwarzgold.at");
                }
            }

        cachedTournaments = currentTournaments;
        cachedTournaments.sort((o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? -1 : 1);
    }
}
