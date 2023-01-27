package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.Collection;

public interface IKalenderReader {

    public Collection<ShortTournament> getTournaments();

}
