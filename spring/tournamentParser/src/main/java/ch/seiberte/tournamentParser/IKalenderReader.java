package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.Collection;

public interface IKalenderReader {

    Collection<ShortTournament> getTournaments();

}
