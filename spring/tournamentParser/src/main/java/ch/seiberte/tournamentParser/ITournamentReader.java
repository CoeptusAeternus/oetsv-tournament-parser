package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.LongTournament;

public interface ITournamentReader {
    public LongTournament readTournament(Long id);
}
