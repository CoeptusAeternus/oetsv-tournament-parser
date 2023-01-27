package ch.seiberte.tournamentParser.data;

import ch.seiberte.tournamentParser.IKalenderReader;

import java.util.Collection;

public class KalenderParser implements IKalenderReader {
    private static String url = "https://www.tanzsportverband.at/kalender/daten.html";

    @Override
    public Collection<ShortTournament> getTournaments() {
        return null;
    }
}
