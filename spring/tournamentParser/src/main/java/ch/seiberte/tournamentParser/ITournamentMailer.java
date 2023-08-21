package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

public interface ITournamentMailer {

    void sendMail(ShortTournament st, String email);
}
