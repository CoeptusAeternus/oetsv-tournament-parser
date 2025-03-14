package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;

public interface ITournamentReader {
    LongTournament readTournament(Long id);
}
