package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.FileReader.CachedTournamentsReader;
import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.mailers.ITournamentMailer;
import ch.seiberte.tournamentParser.mailers.NewTournamentService;
import ch.seiberte.tournamentParser.OetsvCalendarDataParser;
import ch.seiberte.tournamentParser.data.ShortTournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TournamentListProxyWithMailer implements IKalenderProxy {
    private static final Logger logger = LoggerFactory.getLogger(TournamentListProxyWithMailer.class);

    private List<ShortTournament> cachedTournaments;
    private final IKalenderReader baseService;
    private final ITournamentMailer mailer;

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
        CachedTournamentsReader reader = new CachedTournamentsReader();
        List<ShortTournament> currentTournaments = baseService.getTournaments();
        boolean fileIsEmpty = reader.fileIsEmpty();
        for(ShortTournament st : currentTournaments){
            if( !reader.isNotified(st.getId()) ){
                reader.addNotified(st.getId());
                logger.info("Sending Mail with new Tournament: "+st.getBezeichnung());
                mailer.sendMail(st, "jaksei.lol@gmail.com");
                if(!fileIsEmpty)
                    mailer.sendMail(st, "sportwart@schwarzgold.at");
            }
        }

        cachedTournaments = currentTournaments;
        cachedTournaments.sort(Comparator.comparing(ShortTournament::getStart));
    }
}
