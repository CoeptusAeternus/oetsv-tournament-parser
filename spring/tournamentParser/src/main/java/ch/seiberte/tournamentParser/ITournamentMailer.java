package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

public interface ITournamentMailer {

    public void sendMail(ShortTournament st, String email);
}
