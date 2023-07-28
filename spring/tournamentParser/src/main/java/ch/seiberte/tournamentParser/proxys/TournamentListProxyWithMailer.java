package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.ITournamentMailer;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.List;

public class TournamentListProxyWithMailer implements IKalenderProxy {

    private List<ShortTournament> cachedTournaments;
    private IKalenderReader baseService;
    private ITournamentMailer mailer;


    @Override
    public List<ShortTournament> getTournaments() {
        return cachedTournaments;
    }

    @Override
    public void updateTournaments() {
        List<ShortTournament> currentTournaments = baseService.getTournaments();
        for(ShortTournament st : currentTournaments){
            if(!cachedTournaments.contains(st))
                mailer.sendMail(st,"jaksei.lol@gmail.com");
        }

        cachedTournaments = currentTournaments;
        cachedTournaments.sort((o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? -1 : 1);
    }
}
